import { Card, CardContent } from "@/components/ui/card"
import { motion } from "framer-motion"

const StatCard = ({ title, value, subValue, icon: Icon, trend, delay = 0 }) => {
  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.4, delay }}
    >
      <Card className="overflow-hidden bg-card transition-all hover:shadow-md hover:border-primary/20 hover:-translate-y-1 duration-300">
        <CardContent className="p-6">
          <div className="flex items-center justify-between">
            <div className="space-y-1">
              <p className="text-sm font-medium text-muted-foreground">{title}</p>
              <div className="flex items-baseline gap-2">
                <h2 className="text-3xl font-bold tracking-tight text-foreground">{value}</h2>
              </div>
              {subValue && (
                <p className={`text-xs font-medium ${trend === "up" ? "text-green-500" : trend === "down" ? "text-red-500" : "text-muted-foreground"}`}>
                  {subValue}
                </p>
              )}
            </div>
            <div className={`flex h-12 w-12 items-center justify-center rounded-full ${trend === "up" ? "bg-green-500/10 text-green-500" : trend === "down" ? "bg-red-500/10 text-red-500" : "bg-primary/10 text-primary"}`}>
              <Icon size={24} strokeWidth={2} />
            </div>
          </div>
        </CardContent>
      </Card>
    </motion.div>
  )
}

export default StatCard
