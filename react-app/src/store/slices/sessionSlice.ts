import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { RootState } from '../store';

interface SessionState {
  sessionCount: number;
  completedBatches: number;
}

const initialState: SessionState = {
  sessionCount: 0,
  completedBatches: 0
};

const sessionSlice = createSlice({
  name: 'session',
  initialState,
  reducers: {
    updateSessionCount: (state, action: PayloadAction<number>) => {
      state.sessionCount = action.payload;
    },
    updateCompletedBatches: (state, action: PayloadAction<number>) => {
      state.completedBatches = action.payload;
    }
  }
});

export const {
  updateSessionCount,
  updateCompletedBatches
} = sessionSlice.actions;

// Selectors
export const selectSessionCount = (state: RootState) => state.session.sessionCount;
export const selectCompletedBatches = (state: RootState) => state.session.completedBatches;

export default sessionSlice.reducer;
