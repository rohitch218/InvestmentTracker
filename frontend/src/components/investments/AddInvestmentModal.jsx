import { useState } from "react"
import { useDispatch, useSelector } from "react-redux"
import { createInvestment } from "@/features/investments/investmentSlice"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { X, Loader2 } from "lucide-react"

const AddInvestmentModal = ({ isOpen, onClose }) => {
  const dispatch = useDispatch()
  const { loading, error } = useSelector((state) => state.investments)

  const [formData, setFormData] = useState({
    name: "",
    type: "STOCK",
    symbol: "",
    quantity: "",
    purchasePrice: "",
    currentPrice: "",
    purchaseDate: new Date().toISOString().split("T")[0],
    notes: "",
  })

  if (!isOpen) return null

  const handleChange = (e) => setFormData({ ...formData, [e.target.name]: e.target.value })

  const handleSubmit = (e) => {
    e.preventDefault()
    dispatch(createInvestment(formData)).then((res) => {
      if (!res.error) onClose()
    })
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm">
      <div className="w-full max-w-lg overflow-hidden rounded-xl border border-border bg-card shadow-2xl animate-in fade-in zoom-in-95 duration-200">
        <div className="flex items-center justify-between border-b px-6 py-4">
          <h2 className="text-lg font-semibold text-foreground">Add New Investment</h2>
          <button onClick={onClose} className="rounded-full p-1 hover:bg-muted text-muted-foreground transition-colors">
            <X size={20} />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="px-6 py-4 space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="name">Asset Name</Label>
              <Input id="name" name="name" required value={formData.name} onChange={handleChange} placeholder="e.g. Apple Inc." />
            </div>
            <div className="space-y-2">
              <Label htmlFor="type">Asset Type</Label>
              <select
                id="type"
                name="type"
                value={formData.type}
                onChange={handleChange}
                className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
              >
                <option value="STOCK">Stock</option>
                <option value="MUTUAL_FUND">Mutual Fund</option>
                <option value="CRYPTO">Crypto</option>
                <option value="FIXED_DEPOSIT">Fixed Deposit</option>
              </select>
            </div>
          </div>

          <div className="grid grid-cols-3 gap-4">
            <div className="space-y-2">
              <Label htmlFor="symbol">Symbol</Label>
              <Input id="symbol" name="symbol" value={formData.symbol} onChange={handleChange} placeholder="AAPL" />
            </div>
            <div className="space-y-2">
              <Label htmlFor="quantity">Quantity</Label>
              <Input id="quantity" name="quantity" type="number" step="any" min="0" required value={formData.quantity} onChange={handleChange} placeholder="10" />
            </div>
            <div className="space-y-2">
              <Label htmlFor="purchaseDate">Date</Label>
              <Input id="purchaseDate" name="purchaseDate" type="date" required value={formData.purchaseDate} onChange={handleChange} />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="purchasePrice">Buy Price ($)</Label>
              <Input id="purchasePrice" name="purchasePrice" type="number" step="any" min="0" required value={formData.purchasePrice} onChange={handleChange} placeholder="150" />
            </div>
            <div className="space-y-2">
              <Label htmlFor="currentPrice">Current Price ($)</Label>
              <Input id="currentPrice" name="currentPrice" type="number" step="any" min="0" required value={formData.currentPrice} onChange={handleChange} placeholder="155" />
            </div>
          </div>

          <div className="space-y-2">
            <Label htmlFor="notes">Notes (Optional)</Label>
            <Input id="notes" name="notes" value={formData.notes} onChange={handleChange} placeholder="Retirement fund..." />
          </div>

          {error && <p className="text-sm text-destructive font-medium">{error}</p>}

          <div className="flex justify-end gap-3 pt-4 border-t">
            <Button variant="outline" type="button" onClick={onClose}>Cancel</Button>
            <Button type="submit" disabled={loading}>
              {loading && <Loader2 className="mr-2 h-4 w-4 animate-spin" />} Save Investment
            </Button>
          </div>
        </form>
      </div>
    </div>
  )
}

export default AddInvestmentModal
