import { useState } from "react"
import { useDispatch, useSelector } from "react-redux"
import { loginUser, registerTenant } from "@/features/auth/authSlice"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { motion } from "framer-motion"
import { BarChart3, Loader2 } from "lucide-react"

const Login = () => {
  const [isLogin, setIsLogin] = useState(true)
  const dispatch = useDispatch()
  const { loading, error } = useSelector((state) => state.auth)

  // Form State
  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    username: "",
    companyName: "",
  })

  const handleChange = (e) => setFormData({ ...formData, [e.target.name]: e.target.value })

  const handleSubmit = (e) => {
    e.preventDefault()
    if (isLogin) {
      dispatch(loginUser({ 
        email: formData.email, 
        password: formData.password
      }))
    } else {
      dispatch(registerTenant(formData))
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-zinc-950 px-4 py-12 sm:px-6 lg:px-8 relative overflow-hidden">
      {/* Background gradients for premium feel */}
      <div className="absolute inset-0 z-0">
        <div className="absolute -top-[30%] -left-[10%] h-[70%] w-[50%] rounded-full bg-indigo-600/20 blur-[120px]" />
        <div className="absolute -bottom-[30%] -right-[10%] h-[70%] w-[50%] rounded-full bg-rose-600/20 blur-[120px]" />
      </div>

      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="z-10 w-full max-w-md"
      >
        <div className="rounded-2xl border border-white/10 bg-black/40 p-8 shadow-2xl backdrop-blur-xl">
          <div className="mb-8 flex flex-col items-center">
            <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-xl bg-indigo-500 shadow-lg shadow-indigo-500/30">
              <BarChart3 className="h-6 w-6 text-white" />
            </div>
            <h2 className="text-2xl font-bold tracking-tight text-white">
              {isLogin ? "Welcome back" : "Investment Tracker"}
            </h2>
            <p className="mt-2 text-sm text-zinc-400">
              {isLogin ? "Enter your credentials to access your portfolio" : "Start managing your investments today"}
            </p>
          </div>

          <form className="space-y-4" onSubmit={handleSubmit}>
            {!isLogin && (
              <motion.div initial={{ opacity: 0, height: 0 }} animate={{ opacity: 1, height: "auto" }} className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="companyName" className="text-zinc-300">Company Name</Label>
                  <Input
                    id="companyName"
                    name="companyName"
                    type="text"
                    required={!isLogin}
                    value={formData.companyName}
                    onChange={handleChange}
                    className="border-white/10 bg-white/5 text-white placeholder:text-zinc-500 focus-visible:ring-indigo-500"
                    placeholder="Acme Corp"
                  />
                </div>
                <div className="flex gap-4">
                  <div className="space-y-2 flex-1">
                    <Label htmlFor="firstName" className="text-zinc-300">First Name</Label>
                    <Input
                      id="firstName"
                      name="firstName"
                      type="text"
                      required={!isLogin}
                      value={formData.firstName}
                      onChange={handleChange}
                      className="border-white/10 bg-white/5 text-white placeholder:text-zinc-500 focus-visible:ring-indigo-500"
                      placeholder="Jane"
                    />
                  </div>
                  <div className="space-y-2 flex-1">
                    <Label htmlFor="lastName" className="text-zinc-300">Last Name</Label>
                    <Input
                      id="lastName"
                      name="lastName"
                      type="text"
                      required={!isLogin}
                      value={formData.lastName}
                      onChange={handleChange}
                      className="border-white/10 bg-white/5 text-white placeholder:text-zinc-500 focus-visible:ring-indigo-500"
                      placeholder="Doe"
                    />
                  </div>
                </div>
                <div className="space-y-2">
                  <Label htmlFor="username" className="text-zinc-300">Admin Username</Label>
                  <Input
                    id="username"
                    name="username"
                    type="text"
                    required={!isLogin}
                    value={formData.username}
                    onChange={handleChange}
                    className="border-white/10 bg-white/5 text-white placeholder:text-zinc-500 focus-visible:ring-indigo-500"
                    placeholder="John Doe"
                  />
                </div>
              </motion.div>
            )}

            <div className="space-y-2">
              <Label htmlFor="email" className="text-zinc-300">Email address</Label>
              <Input
                id="email"
                name="email"
                type="email"
                required
                value={formData.email}
                onChange={handleChange}
                className="border-white/10 bg-white/5 text-white placeholder:text-zinc-500 focus-visible:ring-indigo-500"
                placeholder="name@company.com"
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="password" className="text-zinc-300">Password</Label>
              <Input
                id="password"
                name="password"
                type="password"
                required
                value={formData.password}
                onChange={handleChange}
                className="border-white/10 bg-white/5 text-white placeholder:text-zinc-500 focus-visible:ring-indigo-500"
                placeholder="••••••••"
              />
            </div>

            {error && (
              <div className="rounded-md bg-red-500/10 p-3 text-sm text-red-500 border border-red-500/20">
                {error}
              </div>
            )}

            <Button
              type="submit"
              disabled={loading}
              className="w-full bg-indigo-500 text-white hover:bg-indigo-600 focus-visible:ring-indigo-500 disabled:bg-indigo-500/50"
            >
              {loading ? (
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
              ) : isLogin ? (
                "Sign in"
              ) : (
                "Create Account"
              )}
            </Button>
          </form>

          <div className="mt-6 text-center text-sm">
            <span className="text-zinc-400">
              {isLogin ? "Don't have an account? " : "Already have an account? "}
            </span>
            <button
              type="button"
              onClick={() => setIsLogin(!isLogin)}
              className="font-medium text-indigo-400 hover:text-indigo-300"
            >
              {isLogin ? "Sign up" : "Log in"}
            </button>
          </div>
        </div>
      </motion.div>
    </div>
  )
}

export default Login
