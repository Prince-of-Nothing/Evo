import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BrowserChooserDialog(
    apps: List<ResolveInfo>,
    url: String,
    context: Context,
    onClose: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onClose,
        title = { Text("Open link with:") },
        text = {
            Column {
                apps.forEach { app ->
                    Text(
                        text = app.loadLabel(context.packageManager).toString(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

                                intent.setPackage(app.activityInfo.packageName)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(intent)
                                onClose()
                            }
                            .padding(8.dp)
                    )
                }
            }
        },
        confirmButton = {}
    )
}