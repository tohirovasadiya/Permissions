package uz.ttpu.permissions.ui.screens

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect

@Composable
fun CameraScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val activity = context as? Activity

    // minSdk < 23 bo'lsa runtime permission talab etilmaydi
    val isSdkBelowM = Build.VERSION.SDK_INT < Build.VERSION_CODES.M

    var isGranted by remember {
        mutableStateOf(
            isSdkBelowM || ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var showRationale by remember {
        mutableStateOf(
            if (!isSdkBelowM && activity != null) {
                ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)
            } else false
        )
    }

    var denyCount by remember { mutableIntStateOf(0) }

    fun updatePermissionState() {
        if (isSdkBelowM) {
            isGranted = true
            showRationale = false
            return
        }
        val status = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        isGranted = status == PackageManager.PERMISSION_GRANTED
        showRationale = if (activity != null && !isGranted) {
            ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)
        } else false
    }

    // Settings'dan yoki ilovaga qaytganda (ON_RESUME) holatni avtomatik yangilash
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        updatePermissionState()
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        isGranted = granted
        if (!granted) {
            denyCount++
        }
        updatePermissionState()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "3-topshiriq: Kamera Ruxsatnomasi (3 Holat)",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        when {
            // 1-HOLAT: Ruxsat berilgan (Granted)
            isGranted -> {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "📷 Ruxsat berilgan",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Kameradan to'liq foydalanishingiz mumkin.",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // 2-HOLAT: Rationale ko'rsatiladi (Bir marta rad etilgan)
            showRationale -> {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "⚠️ Rationale ko'rsatilmoqda",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Rasmga olish funksiyasi ishlashi uchun kamera ruxsati zarur. Iltimos, ruxsat bering.",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                if (!isSdkBelowM) {
                                    launcher.launch(Manifest.permission.CAMERA)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100))
                        ) {
                            Text("Qayta ruxsat so'rash")
                        }
                    }
                }
            }

            // 3-HOLAT: Ruxsat yo'q (Boshlang'ich yoki 2 marta rad etilgan)
            else -> {
                val isPermanentlyDenied = denyCount >= 2 || (denyCount > 0)

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isPermanentlyDenied) Color(0xFFFFEBEE) else Color(0xFFF5F5F5)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isPermanentlyDenied) "🚫 2 marta rad etildi (Denied Twice)" else "📷 Ruxsat yo'q",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isPermanentlyDenied) Color(0xFFC62828) else Color.DarkGray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isPermanentlyDenied)
                                "Ruxsat ikki marta rad etilganligi sababli tizim muloqot oynasi ko'rsatilmaydi. Sozlamalardan ruxsat bering."
                            else
                                "Kameradan foydalanish uchun ruxsat so'rang.",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        if (isPermanentlyDenied) {
                            // Ikki marta rad etilgandan keyin "Sozlamalarni ochish" tugmasi chiqadi
                            Button(
                                onClick = {
                                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                        data = Uri.fromParts("package", context.packageName, null)
                                    }
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                            ) {
                                Text("Sozlamalarni ochish")
                            }
                        } else {
                            // Dastlabki so'rov tugmasi
                            OutlinedButton(
                                onClick = {
                                    if (!isSdkBelowM) {
                                        launcher.launch(Manifest.permission.CAMERA)
                                    }
                                }
                            ) {
                                Text("Ruxsat so'rash")
                            }
                        }
                    }
                }
            }
        }
    }
}
