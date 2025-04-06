import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { RootState } from '../store';

interface BackpressureInfo {
  utilizationPct: number;
  status: string;
}

interface BackpressureState {
  bufferData: {
    string: BackpressureInfo;
    bytearray: BackpressureInfo;
    int: BackpressureInfo;
  };
}

const initialState: BackpressureState = {
  bufferData: {
    string: {
      utilizationPct: 0,
      status: 'NORMAL'
    },
    bytearray: {
      utilizationPct: 0,
      status: 'NORMAL'
    },
    int: {
      utilizationPct: 0,
      status: 'NORMAL'
    }
  }
};

const backpressureSlice = createSlice({
  name: 'backpressure',
  initialState,
  reducers: {
    updateBackpressureData: (state, action: PayloadAction<any>) => {
      const data = action.payload;
      if (data && data.buffers) {
        state.bufferData = {
          string: {
            utilizationPct: data.buffers.string?.utilizationPct || 0,
            status: data.buffers.string?.status || 'NORMAL'
          },
          bytearray: {
            utilizationPct: data.buffers.bytearray?.utilizationPct || 0,
            status: data.buffers.bytearray?.status || 'NORMAL'
          },
          int: {
            utilizationPct: data.buffers.int?.utilizationPct || 0,
            status: data.buffers.int?.status || 'NORMAL'
          }
        };
      }
    }
  }
});

export const { updateBackpressureData } = backpressureSlice.actions;

// Selectors
export const selectBackpressureData = (state: RootState) => state.backpressure.bufferData;

export default backpressureSlice.reducer;
