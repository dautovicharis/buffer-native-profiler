import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { RootState } from '../store';

interface OutputLogState {
  logs: string[];
  connected: boolean;
}

const initialState: OutputLogState = {
  logs: ['Waiting for data...'],
  connected: true
};

const outputLogSlice = createSlice({
  name: 'outputLog',
  initialState,
  reducers: {
    addLog: (state, action: PayloadAction<string>) => {
      const timestamp = new Date().toLocaleTimeString();
      const newLog = `[${timestamp}] ${action.payload}`;

      // Keep only the last 50 logs to prevent memory issues
      state.logs.push(newLog);
      if (state.logs.length > 50) {
        state.logs = state.logs.slice(state.logs.length - 50);
      }
    },
    setConnected: (state, action: PayloadAction<boolean>) => {
      state.connected = action.payload;
    }
  }
});

export const { addLog, setConnected} = outputLogSlice.actions;

// Selectors
export const selectLogs = (state: RootState) => state.outputLog.logs;
export const selectConnected = (state: RootState) => state.outputLog.connected;

export default outputLogSlice.reducer;
