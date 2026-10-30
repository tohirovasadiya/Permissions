package uz.ttpu.permissions.ui.screens

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import uz.ttpu.permissions.R

private const val CHANNEL_ID = "permissions_lab_channel"
private const val NOTIFICATION_ID = 1001

@Composable
fun NotificationScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    // API 33 (Tiramisu) dan boshlab POST_NOTIFICATIONS runtime ruxsatnomasi talab qilinadi
    val isApi33OrHigher = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    fun checkPermission(): Boolean {
        return if (!isApi33OrHigher) {
            true // API < 33 versiyalarda runtime ruxsat so'ralmaydi
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    var isGranted by remember { mutableStateOf(checkPermission()) }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        isGranted = checkPermission()
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        isGranted = granted
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "6-topshiriq: Bildirishnoma Ruxsatnomasi",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Qurilma API darajasi: ${Build.VERSION.SDK_INT}",
            style = MaterialTheme.typography.titleMedium,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isGranted) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isGranted) "🔔 Bildirishnoma Ruxsati Berilgan" else "🔕 Bildirishnoma Ruxsati Yo'q",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isGranted) Color(0xFF2E7D32) else Color(0xFFC62828)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (!isApi33OrHigher) {
                        "Android 12 va undan past (API < 33) versiyalarda bildirishnomalar avtomatik ruxsat etilgan."
                    } else if (isGranted) {
                        "API 34/33: POST_NOTIFICATIONS ruxsatnomasi muvaffaqiyatli berildi."
                    } else {
                        "API 34/33: POST_NOTIFICATIONS runtime ruxsatnomasi so'ralishi shart."
                    },
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (!isGranted && isApi33OrHigher) {
                    Button(
                        onClick = {
                            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                    ) {
                        Text("Bildirishnoma Ruxsatini So'rash")
                    }
                } else {
                    Button(
                        onClick = {
                            sendTestNotification(context)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Text("Test Bildirishnomasini Yuborish")
                    }
                }
            }
        }
    }
}

private fun sendTestNotification(context: Context) {
    createNotificationChannel(context)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
    }

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("PermissionsLab Test Notification")
        .setContentText("Bildirishnoma ruxsatnomasi muvaffaqiyatli ishlamoqda!")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)

    val manager = NotificationManagerCompat.from(context)
    manager.notify(NOTIFICATION_ID, builder.build())
}

private fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "PermissionsLab Notifications"
        val descriptionText = "PermissionsLab test bildirishnoma kanali"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
