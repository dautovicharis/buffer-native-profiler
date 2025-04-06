import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { RootState } from '../store';

interface MemoryData {
  utilizationPct: number;
  status: string;
  memoryUsage: number;
}

interface MemoryState {
  memoryData: MemoryData;
  memoryHistory: number[];
}

const initialState: MemoryState = {
  memoryData: {
    utilizationPct: 0,
    status: 'normal',
    memoryUsage: 0
  },
  memoryHistory: []
};

const memorySlice = createSlice({
  name: 'memory',
  initialState,
  reducers: {
    updateMemoryData: (state, action: PayloadAction<any>) => {
      const data = action.payload;
      if (data) {
        // Ensure memoryUsage is a number
        const memoryUsage = typeof data.memoryUsage === 'number' ? data.memoryUsage : parseInt(data.memoryUsage, 10) || 0;
        state.memoryData = {
          utilizationPct: data.memoryUtilizationPct || 0,
          status: data.status?.toLowerCase() || 'normal',
          memoryUsage: memoryUsage
        };

        // Add new memory usage to history
        state.memoryHistory.push(memoryUsage);

        // Keep only the last 10 data points
        if (state.memoryHistory.length > 100) {
          state.memoryHistory = state.memoryHistory.slice(-100);
        }
      }
    },

  }
});

export const { updateMemoryData } = memorySlice.actions;

// Selectors
export const selectMemoryData = (state: RootState) => state.memory.memoryData;
export const selectMemoryHistory = (state: RootState) => state.memory.memoryHistory;

export default memorySlice.reducer;
