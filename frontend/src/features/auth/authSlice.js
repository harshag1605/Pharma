import { createSlice } from '@reduxjs/toolkit';

const saved = JSON.parse(localStorage.getItem('pharma.auth') || 'null');

const authSlice = createSlice({
  name: 'auth',
  initialState: saved || { user: null, accessToken: null, refreshToken: null },
  reducers: {
    setCredentials: (state, action) => {
      state.user = {
        id: action.payload.userId,
        fullName: action.payload.fullName,
        email: action.payload.email,
        roles: action.payload.roles
      };
      state.accessToken = action.payload.accessToken;
      state.refreshToken = action.payload.refreshToken;
      localStorage.setItem('pharma.auth', JSON.stringify(state));
    },
    logout: (state) => {
      state.user = null;
      state.accessToken = null;
      state.refreshToken = null;
      localStorage.removeItem('pharma.auth');
    }
  }
});

export const { setCredentials, logout } = authSlice.actions;
export default authSlice.reducer;
