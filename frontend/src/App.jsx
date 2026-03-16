import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom"
import { useSelector } from "react-redux"
import MainLayout from "@/components/layout/MainLayout"
import Login from "@/pages/Login"
import Dashboard from "@/pages/Dashboard"
import Investments from "@/pages/Investments"
import ProtectedRoute from "@/routes/ProtectedRoute"

function App() {
  const { isAuthenticated } = useSelector((state) => state.auth)

  return (
    <Router>
      <Routes>
        <Route path="/login" element={!isAuthenticated ? <Login /> : <Navigate to="/dashboard" />} />
        
        <Route element={<ProtectedRoute />}>
          <Route element={<MainLayout />}>
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/investments" element={<Investments />} />
            <Route path="/" element={<Navigate to="/dashboard" replace />} />
          </Route>
        </Route>
      </Routes>
    </Router>
  )
}

export default App
