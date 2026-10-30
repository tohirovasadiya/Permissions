package uz.ttpu.permissions.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect

enum class LocationStatus {
    PRECISE, // Aniq joylashuv (FINE)
    COARSE,  // Taxminiy joylashuv (COARSE)
    NONE     // Ruxsat yo'q
}

@Composable
fun LocationScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var locationStatus by remember { mutableStateOf(LocationStatus.NONE) }

    fun checkLocationPermissions(): LocationStatus {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return when {
            fineGranted -> LocationStatus.PRECISE
            coarseGranted -> LocationStatus.COARSE
            else -> LocationStatus.NONE
        }
    }

    // MUHIM TALAB: Ruxsat holatini har safar ekran qaytganda (ON_RESUME) qayta tekshiramiz.
    // "Faqat bu safar" (Only this time) ruxsati berilganda app fonga o'tib qaytsa, ruxsat avtomatik ravishda bekor qilinadi.
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        locationStatus = checkLocationPermissions()
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap: Map<String, Boolean> ->
        val fineGranted = permissionsMap[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissionsMap[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        locationStatus = when {
            fineGranted -> LocationStatus.PRECISE
            coarseGranted -> LocationStatus.COARSE
            else -> LocationStatus.NONE
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "5-topshiriq: Bir Martalik Joylashuv Ruxsatnomasi",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        val (cardBg, statusTitle, statusDesc, textColor) = when (locationStatus) {
            LocationStatus.PRECISE -> Quadruple(
                Color(0xFFE8F5E9),
                "🎯 Aniq joylashuv (Precise)",
                "FINE va COARSE ruxsatnomalari berilgan.",
                Color(0xFF2E7D32)
            )
            LocationStatus.COARSE -> Quadruple(
                Color(0xFFFFF8E1),
                "📍 Taxminiy joylashuv (Coarse)",
                "Faqat COARSE ruxsatnomasi berilgan (taxminiy hudud).",
                Color(0xFFF57F17)
            )
            LocationStatus.NONE -> Quadruple(
                Color(0xFFFFEBEE),
                "🚫 Ruxsat yo'q (None)",
                "Joylashuv ruxsatnomasi berilmagan yoki bir martalik ruxsat muddati tugagan.",
                Color(0xFFC62828)
            )
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = cardBg)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = statusTitle,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = statusDesc,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        launcher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                ) {
                    Text("Joylashuv ruxsatini so'rash")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Sinov: 'Only this time' tanlab, ilovani fonga olib chiqing va qayting. ON_RESUME ruxsat bekor qilinganini avtomatik anqlaydi.",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
