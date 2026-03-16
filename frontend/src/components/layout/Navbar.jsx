import { Bell } from "lucide-react"

const Navbar = () => {
  return (
    <header className="flex h-16 items-center justify-between border-b bg-background px-6 shadow-sm">
      <div className="flex items-center gap-4">
        {/* Breadcrumb or Title could go here based on route */}
        <h2 className="text-lg font-semibold tracking-tight text-foreground">
          Welcome back
        </h2>
      </div>

      <div className="flex items-center gap-4">
        <button className="relative rounded-full p-2 text-muted-foreground transition-colors hover:bg-muted hover:text-foreground focus:outline-none focus:ring-2 focus:ring-ring">
          <Bell size={20} />
          <span className="absolute right-2 top-2 flex h-2 w-2 rounded-full bg-destructive"></span>
        </button>
      </div>
    </header>
  )
}

export default Navbar
