import { useEffect, useState } from "react"
import { useDispatch, useSelector } from "react-redux"
import { fetchInvestments, deleteInvestment } from "@/features/investments/investmentSlice"
import AddInvestmentModal from "@/components/investments/AddInvestmentModal"
import UploadCsvModal from "@/components/investments/UploadCsvModal"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Plus, Search, Trash2, FileUp } from "lucide-react"

const Investments = () => {
  const dispatch = useDispatch()
  const { investments, loading } = useSelector((state) => state.investments)
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [isCsvModalOpen, setIsCsvModalOpen] = useState(false)
  const [searchTerm, setSearchTerm] = useState("")

  useEffect(() => {
    dispatch(fetchInvestments({ page: 0, size: 50, search: searchTerm }))
  }, [dispatch, searchTerm])

  const handleDelete = (id) => {
    if (window.confirm("Are you sure you want to delete this investment?")) {
      dispatch(deleteInvestment(id))
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-3xl font-bold tracking-tight text-foreground">Investments</h1>
          <p className="text-muted-foreground">Manage your portfolio holdings.</p>
        </div>
        <div className="flex items-center gap-3">
          <Button onClick={() => setIsCsvModalOpen(true)} variant="outline" className="gap-2">
            <FileUp size={16} /> Import CSV
          </Button>
          <Button onClick={() => setIsModalOpen(true)} className="gap-2">
            <Plus size={16} /> Add Asset
          </Button>
        </div>
      </div>

      <Card className="bg-card shadow-sm border-border">
        <div className="p-4 border-b flex items-center gap-4 bg-muted/30">
          <div className="relative w-full max-w-sm">
            <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
            <Input
              type="search"
              placeholder="Search by name or symbol..."
              className="w-full pl-8 bg-background border-border"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>
          {/* We could add generic filters for TYPE here */}
        </div>
        <CardContent className="p-0">
          {loading && investments.length === 0 ? (
            <div className="p-8 text-center text-muted-foreground">Loading investments...</div>
          ) : investments.length === 0 ? (
            <div className="p-12 text-center flex flex-col items-center">
              <div className="h-12 w-12 rounded-full bg-primary/10 flex items-center justify-center text-primary mb-4">
                <Plus size={24} />
              </div>
              <h3 className="font-semibold text-lg">No investments found</h3>
              <p className="text-muted-foreground text-sm mt-1 mb-4">You have not added any assets yet.</p>
              <Button onClick={() => setIsModalOpen(true)} variant="outline">Add Your First Asset</Button>
            </div>
          ) : (
            <Table>
              <TableHeader className="bg-muted/50">
                <TableRow>
                  <TableHead className="w-[200px]">Asset</TableHead>
                  <TableHead>Type</TableHead>
                  <TableHead className="text-right">Quantity</TableHead>
                  <TableHead className="text-right">Buy Price</TableHead>
                  <TableHead className="text-right">Current Price</TableHead>
                  <TableHead className="text-right">P/L %</TableHead>
                  <TableHead className="text-right">Total Value</TableHead>
                  <TableHead className="w-[80px] text-center">Actions</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {investments.map((inv) => (
                  <TableRow key={inv.id} className="hover:bg-muted/50 transition-colors">
                    <TableCell className="font-medium">
                      <div className="flex flex-col">
                        <span>{inv.name}</span>
                        {inv.symbol && <span className="text-xs text-muted-foreground">{inv.symbol}</span>}
                      </div>
                    </TableCell>
                    <TableCell>
                      <Badge variant="outline" className="bg-background text-[10px] tracking-wide">
                        {inv.type}
                      </Badge>
                    </TableCell>
                    <TableCell className="text-right tabular-nums">{inv.quantity.toFixed(4)}</TableCell>
                    <TableCell className="text-right tabular-nums">${inv.purchasePrice.toLocaleString()}</TableCell>
                    <TableCell className="text-right tabular-nums font-medium">${inv.currentPrice.toLocaleString()}</TableCell>
                    <TableCell className="text-right tabular-nums">
                      <span className={inv.profitLossPct >= 0 ? "text-green-500 font-medium" : "text-red-500 font-medium"}>
                        {inv.profitLossPct >= 0 ? "+" : ""}{inv.profitLossPct}%
                      </span>
                    </TableCell>
                    <TableCell className="text-right tabular-nums font-semibold">${inv.currentValue.toLocaleString()}</TableCell>
                    <TableCell className="text-center">
                      <button 
                        onClick={() => handleDelete(inv.id)} 
                        className="p-2 rounded-md hover:bg-destructive/10 text-muted-foreground hover:text-destructive transition-colors"
                        title="Delete asset"
                      >
                        <Trash2 size={16} />
                      </button>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          )}
        </CardContent>
      </Card>

      <AddInvestmentModal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} />
      <UploadCsvModal isOpen={isCsvModalOpen} onClose={() => setIsCsvModalOpen(false)} />
    </div>
  )
}

export default Investments
