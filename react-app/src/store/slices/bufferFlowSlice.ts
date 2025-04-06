import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { RootState } from '../store';

interface BufferInfo {
  size: number;
  capacity: number;
}

interface BufferFlowState {
  bufferData: {
    string: BufferInfo;
    bytearray: BufferInfo;
    int: BufferInfo;
  };
}

const initialState: BufferFlowState = {
  bufferData: {
    string: {
      size: 0,
      capacity: 20
    },
    bytearray: {
      size: 0,
      capacity: 20
    },
    int: {
      size: 0,
      capacity: 20
    }
  }
};

const bufferFlowSlice = createSlice({
  name: 'bufferFlow',
  initialState,
  reducers: {
    updateBufferData: (state, action: PayloadAction<any>) => {
      const data = action.payload;
      if (data && data.buffers) {
        state.bufferData = {
          string: {
            size: data.buffers.string?.bufferSize || 0,
            capacity: data.buffers.string?.bufferCapacity || 20
          },
          bytearray: {
            size: data.buffers.bytearray?.bufferSize || 0,
            capacity: data.buffers.bytearray?.bufferCapacity || 20
          },
          int: {
            size: data.buffers.int?.bufferSize || 0,
            capacity: data.buffers.int?.bufferCapacity || 20
          }
        };
      }
    }
  }
});

export const { updateBufferData } = bufferFlowSlice.actions;

// Selectors
export const selectBufferData = (state: RootState) => state.bufferFlow.bufferData;

export default bufferFlowSlice.reducer;
