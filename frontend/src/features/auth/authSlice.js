import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import { api } from "@/services/api";

const initialState = {
  user: JSON.parse(localStorage.getItem("user")) || null,
  token: localStorage.getItem("accessToken") || null,
  isAuthenticated: !!localStorage.getItem("accessToken"),
  loading: false,
  error: null,
};

export const loginUser = createAsyncThunk(
  "auth/login",
  async (credentials, { rejectWithValue }) => {
    try {
      const response = await api.post("/auth/login", credentials);
      return response.data; // AuthResponse DTO from backend
    } catch (err) {
      return rejectWithValue(err.response?.data?.detail || "Login failed");
    }
  }
);

export const registerTenant = createAsyncThunk(
  "auth/register",
  async (tenantData, { rejectWithValue }) => {
    try {
      const response = await api.post("/auth/register", tenantData);
      return response.data;
    } catch (err) {
      return rejectWithValue(err.response?.data?.detail || "Registration failed");
    }
  }
);

export const logoutUser = createAsyncThunk("auth/logout", async () => {
  await api.post("/auth/logout");
  return true;
});

const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      // Login
      .addCase(loginUser.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(loginUser.fulfilled, (state, { payload }) => {
        state.loading = false;
        state.token = payload.accessToken;
        state.user = payload.user;
        state.isAuthenticated = true;
        localStorage.setItem("accessToken", payload.accessToken);
        localStorage.setItem("user", JSON.stringify(payload.user));
      })
      .addCase(loginUser.rejected, (state, { payload }) => {
        state.loading = false;
        state.error = payload;
      })
      // Register
      .addCase(registerTenant.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(registerTenant.fulfilled, (state, { payload }) => {
        state.loading = false;
        state.token = payload.accessToken;
        state.user = payload.user;
        state.isAuthenticated = true;
        localStorage.setItem("accessToken", payload.accessToken);
        localStorage.setItem("user", JSON.stringify(payload.user));
      })
      .addCase(registerTenant.rejected, (state, { payload }) => {
        state.loading = false;
        state.error = payload;
      })
      // Logout
      .addCase(logoutUser.fulfilled, (state) => {
        state.user = null;
        state.token = null;
        state.isAuthenticated = false;
        localStorage.removeItem("accessToken");
        localStorage.removeItem("user");
      });
  },
});

export const { clearError } = authSlice.actions;
export default authSlice.reducer;
