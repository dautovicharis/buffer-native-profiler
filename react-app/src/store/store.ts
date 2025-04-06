import { configureStore } from '@reduxjs/toolkit';
import websocketReducer from './slices/websocketSlice';
import statsReducer from './slices/statsSlice';
import bufferFlowReducer from './slices/bufferFlowSlice';
import backpressureReducer from './slices/backpressureSlice';
import memoryReducer from './slices/memorySlice';
import outputLogReducer from './slices/outputLogSlice';
import sessionReducer from './slices/sessionSlice';
import websocketMiddleware from './middleware/websocketMiddleware';

const store = configureStore({
  reducer: {
    websocket: websocketReducer,
    stats: statsReducer,
    bufferFlow: bufferFlowReducer,
    backpressure: backpressureReducer,
    memory: memoryReducer,
    outputLog: outputLogReducer,
    session: sessionReducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        // Ignore non-serializable values in the websocket state
        ignoredActions: ['websocket/setSocket'],
        ignoredPaths: ['websocket.socket'],
      },
    }).concat(websocketMiddleware),
});

// Export the store's state and dispatch types
export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;

export default store;
