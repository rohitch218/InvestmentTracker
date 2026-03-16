import { useState } from "react"
import { useDispatch } from "react-redux"
import { uploadCsv } from "@/features/investments/investmentSlice"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { X, Loader2, UploadCloud } from "lucide-react"

const UploadCsvModal = ({ isOpen, onClose }) => {
  const dispatch = useDispatch()
  const [file, setFile] = useState(null)
  const [isUploading, setIsUploading] = useState(false)
  const [error, setError] = useState(null)
  const [success, setSuccess] = useState(null)

  if (!isOpen) return null

  const handleFileChange = (e) => {
    setError(null)
    setSuccess(null)
    if (e.target.files && e.target.files.length > 0) {
      setFile(e.target.files[0])
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!file) {
      setError("Please select a file first.")
      return
    }

    setIsUploading(true)
    setError(null)

    try {
      const resultAction = await dispatch(uploadCsv(file)).unwrap()
      setSuccess(resultAction.message)
      setTimeout(() => {
        onClose()
        setSuccess(null)
        setFile(null)
      }, 2000)
    } catch (err) {
      setError(err || "Failed to upload file")
    } finally {
      setIsUploading(false)
    }
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm">
      <div className="w-full max-w-md overflow-hidden rounded-xl border border-border bg-card shadow-2xl animate-in fade-in zoom-in-95 duration-200">
        <div className="flex items-center justify-between border-b px-6 py-4">
          <h2 className="text-lg font-semibold text-foreground flex items-center gap-2">
            <UploadCloud size={20} className="text-primary" />
            Bulk Import CSV
          </h2>
          <button onClick={onClose} className="rounded-full p-1 hover:bg-muted text-muted-foreground transition-colors">
            <X size={20} />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="px-6 py-6 space-y-6">
          <div className="space-y-4">
            <Label htmlFor="csvFile" className="text-muted-foreground font-medium">
              Upload an Apache Commons compatible CSV with columns:
              <br/>
               Name, Type, Symbol, Quantity, PurchasePrice, CurrentPrice, PurchaseDate, Notes
            </Label>
            <Input 
              id="csvFile" 
              type="file" 
              accept=".csv"
              onChange={handleFileChange} 
              className="mt-2 cursor-pointer file:cursor-pointer file:font-semibold file:text-primary file:bg-primary/10 file:rounded-md file:border-0 file:px-4 file:py-1 hover:file:bg-primary/20" 
            />
          </div>

          {error && <p className="text-sm text-destructive font-medium bg-destructive/10 p-3 rounded-md border border-destructive/20">{error}</p>}
          {success && <p className="text-sm text-green-500 font-medium bg-green-500/10 p-3 rounded-md border border-green-500/20">{success}</p>}

          <div className="flex justify-end gap-3 pt-2">
            <Button variant="outline" type="button" onClick={onClose}>Cancel</Button>
            <Button type="submit" disabled={isUploading || !file}>
              {isUploading ? <><Loader2 className="mr-2 h-4 w-4 animate-spin" /> Uploading...</> : 'Import Data'}
            </Button>
          </div>
        </form>
      </div>
    </div>
  )
}

export default UploadCsvModal
