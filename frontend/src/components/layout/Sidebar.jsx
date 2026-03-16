import { NavLink, useNavigate } from "react-router-dom"
import { useDispatch, useSelector } from "react-redux"
import { logoutUser } from "@/features/auth/authSlice"
import { LayoutDashboard, PieChart, LogOut, BarChart3 } from "lucide-react"

const Sidebar = () => {
  const dispatch = useDispatch()
  const navigate = useNavigate()
  const { user } = useSelector((state) => state.auth)

  const handleLogout = () => {
    dispatch(logoutUser())
    navigate("/login")
  }

  const links = [
    { name: "Dashboard", path: "/dashboard", icon: LayoutDashboard },
    { name: "Investments", path: "/investments", icon: PieChart },
  ]

  return (
    <div className="flex h-screen w-64 flex-col border-r bg-card px-4 py-8 shadow-sm">
      <div className="flex items-center gap-2 px-2 pb-8">
        <div className="flex h-8 w-8 items-center justify-center rounded-md bg-primary text-primary-foreground">
          <BarChart3 size={20} />
        </div>
        <span className="text-xl font-bold tracking-tight text-foreground">
          Investment Tracker
        </span>
      </div>

      <nav className="flex-1 space-y-2">
        {links.map((link) => {
          const Icon = link.icon
          return (
            <NavLink
              key={link.name}
              to={link.path}
              className={({ isActive }) =>
                `flex items-center gap-3 rounded-md px-3 py-2 text-sm font-medium transition-colors ${
                  isActive
                    ? "bg-primary text-primary-foreground shadow-sm"
                    : "text-muted-foreground hover:bg-muted hover:text-foreground"
                }`
              }
            >
              <Icon size={18} />
              {link.name}
            </NavLink>
          )
        })}
      </nav>

      <div className="mt-auto border-t pt-4">
        <div className="mb-4 px-3">
          <p className="text-sm font-medium text-foreground">{user?.username}</p>
          <p className="text-xs text-muted-foreground truncate">{user?.email}</p>
          <div className="mt-1 inline-flex items-center rounded-full bg-secondary px-2 py-0.5 text-xs font-semibold text-secondary-foreground">
            {user?.role}
          </div>
        </div>
        <button
          onClick={handleLogout}
          className="flex w-full items-center gap-3 rounded-md px-3 py-2 text-sm font-medium text-destructive transition-colors hover:bg-destructive/10"
        >
          <LogOut size={18} />
          Logout
        </button>
      </div>
    </div>
  )
}

export default Sidebar
