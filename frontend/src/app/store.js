import { configureStore } from "@reduxjs/toolkit";
import authReducer from "@/features/auth/authSlice";
import investmentReducer from "@/features/investments/investmentSlice";
import dashboardReducer from "@/features/dashboard/dashboardSlice";

export const store = configureStore({
  reducer: {
    auth: authReducer,
    investments: investmentReducer,
    dashboard: dashboardReducer,
  },
});
