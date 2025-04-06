import { Middleware } from 'redux';
import {
  connect,
  connectSuccess,
  connectError,
  disconnect,
  disconnected,
  setSocket,
  incrementReconnectAttempts,
} from '../slices/websocketSlice';
import { updateStats, updateBatchConfig, setUpdateStatus } from '../slices/statsSlice';
import { updateSessionCount, updateCompletedBatches } from '../slices/sessionSlice';
import { updateBufferData } from '../slices/bufferFlowSlice';
import { updateBackpressureData } from '../slices/backpressureSlice';
import { updateMemoryData } from '../slices/memorySlice';
import { addLog, setConnected } from '../slices/outputLogSlice';

const websocketMiddleware: Middleware = (store) => (next) => (action) => {
  const { dispatch, getState } = store;
  const state = getState();

  // Handle actions related to WebSocket
  if (connect.match(action)) {
    // Close existing socket if it exists
    if (state.websocket.socket) {
      state.websocket.socket.close();
    }

    // Determine WebSocket URL
    let url = action.payload;
    if (!url) {
      // Use the same host as the page for production, or localhost:8080 for development
      const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
      const host = process.env.NODE_ENV === 'development' ? 'localhost:8080' : window.location.host;
      url = `${protocol}//${host}/stats`;
    }

    try {
      // Create new WebSocket connection
      const socket = new WebSocket(url);
      dispatch(setSocket(socket));

      // Set up WebSocket event handlers
      socket.onopen = () => {
        console.log('WebSocketMiddleware: WebSocket connected');
        dispatch(connectSuccess());
        dispatch(setConnected(true));
        dispatch(addLog('WebSocket connected'));
      };

      socket.onclose = (event) => {
        console.log(`WebSocketMiddleware: WebSocket disconnected: ${event.code} ${event.reason}`);
        dispatch(disconnected(event.reason || null));
        dispatch(setConnected(false));
        dispatch(addLog(`WebSocket disconnected: ${event.code} ${event.reason}`));

        // Attempt to reconnect
        const reconnectAttempts = getState().websocket.reconnectAttempts;
        const maxReconnectAttempts = getState().websocket.maxReconnectAttempts;

        if (reconnectAttempts < maxReconnectAttempts) {
          dispatch(incrementReconnectAttempts());
          const delay = Math.min(1000 * Math.pow(2, reconnectAttempts + 1), 30000);

          console.log(`Attempting to reconnect in ${delay}ms (attempt ${reconnectAttempts + 1}/${maxReconnectAttempts})`);
          dispatch(addLog(`Attempting to reconnect in ${delay}ms (attempt ${reconnectAttempts + 1}/${maxReconnectAttempts})`));

          setTimeout(() => {
            dispatch(connect(url));
          }, delay);
        } else {
          console.error('Maximum reconnect attempts reached');
          dispatch(addLog('Maximum reconnect attempts reached'));
        }
      };

      socket.onerror = (error) => {
        console.error('WebSocketMiddleware: WebSocket error:', error);
        dispatch(connectError('WebSocket connection error'));
        dispatch(addLog(`WebSocket error: ${error.toString()}`));
      };

      socket.onmessage = (event) => {
        console.log('WebSocketMiddleware: Received message:', event.data);
        try {
          const data = JSON.parse(event.data);

          // Process different types of messages
          if (data.type === 'sessions') {
            // Handle session updates
            console.log('WebSocketMiddleware: Received session update:', data);
            if (data.count !== undefined) {
              dispatch(updateSessionCount(data.count));
            }
          } else {
            // Assume it's stats data if no type is specified
            console.log('WebSocketMiddleware: Received stats update:', data);

            // Update stats
            dispatch(updateStats(data));

            // Update batch config if available
            if (data.batchInfo) {
              if (data.batchInfo.batchConfig) {
                dispatch(updateBatchConfig(data.batchInfo.batchConfig));
              }

              // Update completed batches if available
              if (data.batchInfo.completedBatches !== undefined) {
                dispatch(updateCompletedBatches(data.batchInfo.completedBatches));
              }
            }

            // Update status
            dispatch(setUpdateStatus(data.status === 'preparing' ? 'preparing' : 'active'));

            // Update buffer flow data
            dispatch(updateBufferData(data));

            // Update backpressure data
            dispatch(updateBackpressureData(data));

            // Update memory data
            dispatch(updateMemoryData(data));

            // Log data occasionally to avoid flooding
            if (Math.random() < 0.2) {
              dispatch(addLog(`Received data update`));
            }
          }
        } catch (error) {
          console.error('Error parsing WebSocket message:', error);
          dispatch(addLog(`Error parsing WebSocket message: ${error}`));
        }
      };
    } catch (error) {
      console.error('Failed to connect to WebSocket:', error);
      dispatch(connectError('Failed to connect to WebSocket'));
      dispatch(addLog(`Failed to connect to WebSocket: ${error}`));
    }
  }

  if (disconnect.match(action)) {
    // Close the WebSocket connection
    if (state.websocket.socket) {
      state.websocket.socket.close();
      dispatch(setSocket(null));
    }
  }

  return next(action);
};

export default websocketMiddleware;
