import { configureStore } from '@reduxjs/toolkit';
import authReducer from '../features/auth/authSlice';
import { pharmaApi } from './pharmaApi';

export const store = configureStore({
  reducer: {
    auth: authReducer,
    [pharmaApi.reducerPath]: pharmaApi.reducer
  },
  middleware: (getDefault) => getDefault().concat(pharmaApi.middleware)
});
