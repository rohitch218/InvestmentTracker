import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import { api } from "@/services/api";

const initialState = {
  investments: [],
  selectedInvestment: null,
  page: 0,
  totalPages: 0,
  loading: false,
  error: null,
};

export const fetchInvestments = createAsyncThunk(
  "investments/fetchAll",
  async ({ page = 0, size = 50, type = "", search = "" }, { rejectWithValue }) => {
    try {
      const response = await api.get("/investments", {
        params: { page, size, ...(type && { type }), ...(search && { search }) },
      });
      return response.data; // Spring Data REST Page<T>
    } catch (err) {
      return rejectWithValue(err.response?.data?.detail || "Failed to load investments");
    }
  }
);

export const uploadCsv = createAsyncThunk(
  "investments/uploadCsv",
  async (file, { dispatch, rejectWithValue }) => {
    try {
      const formData = new FormData();
      formData.append("file", file);
      
      const response = await api.post("/investments/upload", formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });
      
      // Refresh investments and dashboard eagerly
      dispatch(fetchInvestments({ page: 0, size: 50 }));
      return response.data;
    } catch (err) {
      return rejectWithValue(err.response?.data?.message || "Failed to upload CSV");
    }
  }
);

export const createInvestment = createAsyncThunk(
  "investments/create",
  async (data, { rejectWithValue }) => {
    try {
      const response = await api.post("/investments", data);
      return response.data;
    } catch (err) {
      return rejectWithValue(err.response?.data?.detail || "Failed to create investment");
    }
  }
);

export const deleteInvestment = createAsyncThunk(
  "investments/delete",
  async (id, { rejectWithValue }) => {
    try {
      await api.delete(`/investments/${id}`);
      return id; // Return ID so we can remove it from state locally
    } catch (err) {
      return rejectWithValue(err.response?.data?.detail || "Failed to delete investment");
    }
  }
);

const investmentSlice = createSlice({
  name: "investments",
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      // Fetch All
      .addCase(fetchInvestments.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchInvestments.fulfilled, (state, { payload }) => {
        state.loading = false;
        state.investments = payload.content;
        state.totalPages = payload.totalPages;
        state.page = payload.number;
      })
      .addCase(fetchInvestments.rejected, (state, { payload }) => {
        state.loading = false;
        state.error = payload;
      })
      // Create
      .addCase(createInvestment.fulfilled, (state, { payload }) => {
        state.investments.unshift(payload); // Add to top of list
      })
      // Delete
      .addCase(deleteInvestment.fulfilled, (state, { payload }) => {
        state.investments = state.investments.filter((inv) => inv.id !== payload);
      });
  },
});

export default investmentSlice.reducer;
