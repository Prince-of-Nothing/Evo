import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun BlockedLinkDialogComposable(url: String, context: Context, onClose: () -> Unit) {
    AlertDialog(
        onDismissRequest = onClose,
        title = { Text("Link Blocked!") },
        text = { Text("The link is unsafe:\n$url") },
        confirmButton = {
            TextButton(onClick = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("Blocked URL", url))
                onClose()
            }) {
                Text("Copy URL")
            }
        },
        dismissButton = {
            TextButton(onClick = onClose) { Text("Close") }
        }
    )
}