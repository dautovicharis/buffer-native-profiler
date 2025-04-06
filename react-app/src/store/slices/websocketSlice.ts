import { createSlice, PayloadAction } from '@reduxjs/toolkit';

interface WebsocketState {
  socket: WebSocket | null;
  isConnected: boolean;
  isConnecting: boolean;
  error: string | null;
  reconnectAttempts: number;
  maxReconnectAttempts: number;
  url: string | null;
}

const initialState: WebsocketState = {
  socket: null,
  isConnected: false,
  isConnecting: false,
  error: null,
  reconnectAttempts: 0,
  maxReconnectAttempts: 20,
  url: null,
};

const websocketSlice = createSlice({
  name: 'websocket',
  initialState,
  reducers: {
    connect: (state, action: PayloadAction<string | undefined>) => {
      state.isConnecting = true;
      if (action.payload) {
        state.url = action.payload;
      }
      state.error = null;
    },
    connectSuccess: (state) => {
      state.isConnected = true;
      state.isConnecting = false;
      state.reconnectAttempts = 0;
      state.error = null;
    },
    connectError: (state, action: PayloadAction<string>) => {
      state.isConnected = false;
      state.isConnecting = false;
      state.error = action.payload;
    },
    disconnect: (state) => {
      state.isConnected = false;
      state.isConnecting = false;
    },
    disconnected: (state, action: PayloadAction<string | null>) => {
      state.isConnected = false;
      state.error = action.payload;
    },
    setSocket: (state, action: PayloadAction<WebSocket | null>) => {
      state.socket = action.payload;
    },
    incrementReconnectAttempts: (state) => {
      state.reconnectAttempts += 1;
    }
  },
});

export const {
  connect,
  connectSuccess,
  connectError,
  disconnect,
  disconnected,
  setSocket,
  incrementReconnectAttempts
} = websocketSlice.actions;

export default websocketSlice.reducer;
