import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { RootState } from '../store';

interface StatsData {
  bufferSize: number;
  bufferCapacity: number;
  totalEmissions: number;
  waitingItems: number;
  suspensions: number;
  memoryUsage: number;
  memoryPerItem: number;
}

interface BatchConfigData {
  bufferCapacity: number;
  producerItems: number;
}

interface StatsState {
  stats: StatsData;
  batchConfig: BatchConfigData;
  updateStatus: 'active' | 'preparing';
  configVisible: boolean;
}

const initialState: StatsState = {
  stats: {
    bufferSize: 0,
    bufferCapacity: 0,
    totalEmissions: 0,
    waitingItems: 0,
    suspensions: 0,
    memoryUsage: 0,
    memoryPerItem: 0
  },
  batchConfig: {
    bufferCapacity: 0,
    producerItems: 0
  },
  updateStatus: 'active', // 'active' or 'preparing'
  configVisible: false,
};

const statsSlice = createSlice({
  name: 'stats',
  initialState,
  reducers: {
    updateStats: (state, action: PayloadAction<any>) => {
      const data = action.payload;
      if (data) {
        state.stats = {
          bufferSize: data.bufferSize || 0,
          bufferCapacity: data.bufferCapacity || 0,
          totalEmissions: data.totalEmissions || 0,
          waitingItems: data.waitingItems || 0,
          suspensions: data.suspensions || 0,
          memoryUsage: data.memoryUsage || 0,
          memoryPerItem: data.memoryPerItem || 0
        };
      }
    },
    updateBatchConfig: (state, action: PayloadAction<any>) => {
      const batchConfig = action.payload;
      if (batchConfig) {
        state.batchConfig = {
          bufferCapacity: batchConfig.bufferCapacity || 0,
          producerItems: batchConfig.producerItems || 0
        };
        state.configVisible = true;
      }
    },
    setUpdateStatus: (state, action: PayloadAction<'active' | 'preparing'>) => {
      state.updateStatus = action.payload;
    },
    setConfigVisible: (state, action: PayloadAction<boolean>) => {
      state.configVisible = action.payload;
    },
  },
});

export const {
  updateStats,
  updateBatchConfig,
  setUpdateStatus,
} = statsSlice.actions;

// Selectors
export const selectStats = (state: RootState) => state.stats.stats;
export const selectBatchConfig = (state: RootState) => state.stats.batchConfig;
export const selectConfigVisible = (state: RootState) => state.stats.configVisible;

export default statsSlice.reducer;
