import { useEffect } from "react"
import { useDispatch, useSelector } from "react-redux"
import { fetchDashboardData } from "@/features/dashboard/dashboardSlice"
import StatCard from "@/components/ui/StatCard"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, PieChart, Pie, Cell } from "recharts"
import { Wallet, TrendingUp, PieChart as PieChartIcon, Activity } from "lucide-react"

const COLORS = ["#6366f1", "#10b981", "#f59e0b", "#ef4444", "#8b5cf6"]

const Dashboard = () => {
  const dispatch = useDispatch()
  const { data, loading } = useSelector((state) => state.dashboard)
  const { user } = useSelector((state) => state.auth)

  useEffect(() => {
    dispatch(fetchDashboardData())
  }, [dispatch])

  if (loading || !data) {
    return (
      <div className="flex h-full items-center justify-center">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent shadow-md"></div>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold tracking-tight text-foreground">Dashboard</h1>
        <p className="text-muted-foreground">Welcome back, {user?.username}. Here's your portfolio overview.</p>
      </div>

      {/* Stats Grid */}
      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-4">
        <StatCard
          title="Total Invested"
          value={`$${data.totalInvested?.toLocaleString() || '0'}`}
          icon={Wallet}
          delay={0.1}
        />
        <StatCard
          title="Current Value"
          value={`$${data.currentValue?.toLocaleString() || '0'}`}
          icon={Activity}
          delay={0.2}
        />
        <StatCard
          title="Total Profit / Loss"
          value={`$${data.profitLoss?.toLocaleString() || '0'}`}
          subValue={data.profitLossPct ? `${data.profitLossPct > 0 ? '+' : ''}${data.profitLossPct}% return` : null}
          trend={data.profitLoss >= 0 ? "up" : "down"}
          icon={TrendingUp}
          delay={0.3}
        />
        <StatCard
          title="Active Holdings"
          value={data.investmentCount || 0}
          icon={PieChartIcon}
          delay={0.4}
        />
      </div>

      {/* Charts Row */}
      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-7 border-t border-border pt-6">
        {/* Line Chart */}
        <Card className="col-span-4 bg-card shadow-sm transition-all hover:shadow-md">
          <CardHeader>
            <CardTitle className="text-lg">Portfolio Growth</CardTitle>
          </CardHeader>
          <CardContent className="pl-0">
            <div className="h-[300px] w-full">
              <ResponsiveContainer width="100%" height="100%">
                <AreaChart data={data.growth} margin={{ top: 10, right: 30, left: 0, bottom: 0 }}>
                  <defs>
                    <linearGradient id="colorValue" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="5%" stopColor="#6366f1" stopOpacity={0.3}/>
                      <stop offset="95%" stopColor="#6366f1" stopOpacity={0}/>
                    </linearGradient>
                  </defs>
                  <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="hsl(var(--border))" />
                  <XAxis dataKey="date" stroke="hsl(var(--muted-foreground))" fontSize={12} tickLine={false} axisLine={false} />
                  <YAxis stroke="hsl(var(--muted-foreground))" fontSize={12} tickLine={false} axisLine={false} tickFormatter={(val) => `$${val}`} />
                  <Tooltip 
                    contentStyle={{ backgroundColor: 'hsl(var(--card))', borderColor: 'hsl(var(--border))', borderRadius: '8px' }}
                    itemStyle={{ color: 'hsl(var(--foreground))' }}
                  />
                  <Area type="monotone" dataKey="value" stroke="#6366f1" strokeWidth={2} fillOpacity={1} fill="url(#colorValue)" />
                </AreaChart>
              </ResponsiveContainer>
            </div>
          </CardContent>
        </Card>

        {/* Pie Chart */}
        <Card className="col-span-3 bg-card shadow-sm transition-all hover:shadow-md">
          <CardHeader>
            <CardTitle className="text-lg">Asset Allocation</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="h-[300px] w-full flex items-center justify-center">
              {data.allocation?.length > 0 ? (
                <ResponsiveContainer width="100%" height="100%">
                  <PieChart>
                    <Pie
                      data={data.allocation}
                      cx="50%"
                      cy="50%"
                      innerRadius={60}
                      outerRadius={80}
                      paddingAngle={5}
                      dataKey="value"
                      nameKey="type"
                    >
                      {data.allocation.map((entry, index) => (
                        <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                      ))}
                    </Pie>
                    <Tooltip 
                       contentStyle={{ backgroundColor: 'hsl(var(--card))', borderColor: 'hsl(var(--border))', borderRadius: '8px' }}
                       formatter={(value, name, props) => [`$${value}`, `${name} (${props.payload.percentage}%)`]}
                    />
                  </PieChart>
                </ResponsiveContainer>
              ) : (
                <p className="text-muted-foreground text-sm">No allocation data to display.</p>
              )}
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Top Performers */}
      <Card className="mt-6 bg-card shadow-sm">
        <CardHeader>
          <CardTitle className="text-lg">Top Performing Assets</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            {data.topPerformers?.length > 0 ? (
              data.topPerformers.map((asset) => (
                <div key={asset.id} className="flex items-center justify-between rounded-lg border p-4 transition-colors hover:bg-muted/50">
                  <div className="flex items-center gap-4">
                    <div className="flex h-10 w-10 flex-col items-center justify-center rounded-full bg-primary/10 font-bold text-primary text-xs">
                      {asset.symbol ? asset.symbol.substring(0, 3) : asset.type.substring(0, 3)}
                    </div>
                    <div>
                      <p className="font-medium leading-none">{asset.name}</p>
                      <p className="mt-1 text-sm text-muted-foreground">{asset.type}</p>
                    </div>
                  </div>
                  <div className="text-right">
                    <p className="font-medium">${asset.currentValue?.toLocaleString()}</p>
                    <Badge variant={asset.profitLossPct >= 0 ? "success" : "destructive"} className="mt-1">
                      {asset.profitLossPct >= 0 ? "+" : ""}{asset.profitLossPct}%
                    </Badge>
                  </div>
                </div>
              ))
            ) : (
              <p className="text-muted-foreground text-sm py-4 text-center">No assets found. Add investments to see your top performers.</p>
            )}
          </div>
        </CardContent>
      </Card>
    </div>
  )
}

export default Dashboard
