package com.chouaibmo.mlkitcompose.presentation.screens.barcode_scanning

import android.Manifest
import android.graphics.PointF
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.chouaibmo.mlkitcompose.R
import com.chouaibmo.mlkitcompose.analyzer.BarcodeScanningAnalyzer
import com.chouaibmo.mlkitcompose.presentation.common.components.CameraView
import com.chouaibmo.mlkitcompose.presentation.common.utils.adjustPoint
import com.chouaibmo.mlkitcompose.presentation.common.utils.adjustSize
import com.chouaibmo.mlkitcompose.presentation.common.utils.drawBounds
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.barcode.common.Barcode

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BarcodeScanningScreen(navController: NavController) {
    val context = LocalContext.current
    val cameraPermissionState =
        rememberPermissionState(permission = Manifest.permission.CAMERA)

    PermissionRequired(
        permissionState = cameraPermissionState,
        permissionNotGrantedContent = {
            LaunchedEffect(Unit) {
                cameraPermissionState.launchPermissionRequest()
            }
        },
        permissionNotAvailableContent = {
            Column {
                Toast.makeText(context, "Permission denied.", Toast.LENGTH_LONG).show()
            }
        },
        content = {
            ScanSurface(navController = navController)
        }
    )
}

@Composable
fun ScanSurface(navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val detectedBarcode = remember { mutableStateListOf<Barcode>() }

    val screenWidth = remember { mutableStateOf(context.resources.displayMetrics.widthPixels) }
    val screenHeight = remember { mutableStateOf(context.resources.displayMetrics.heightPixels) }

    val imageWidth = remember { mutableStateOf(0) }
    val imageHeight = remember { mutableStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        CameraView(
            context = context,
            lifecycleOwner = lifecycleOwner,
            analyzer = BarcodeScanningAnalyzer { barcodes, width, height ->
                detectedBarcode.clear()
                detectedBarcode.addAll(barcodes)
                imageWidth.value = width
                imageHeight.value = height
            }
        )
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxHeight()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "back",
                        tint = Color.White
                    )
                }
                Text(
                    text = stringResource(id = R.string.barcode_detection_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }

            Card(
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(vertical = 10.dp),
            ) {
                Text(
                    text = detectedBarcode.joinToString(separator = "\n") { it.displayValue.toString() },
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
        DrawBarcode(
            barcodes = detectedBarcode,
            imageWidth = imageWidth.value,
            imageHeight = imageHeight.value,
            screenWidth = screenWidth.value,
            screenHeight = screenHeight.value
        )
    }
}

@Composable
fun DrawBarcode(barcodes : List<Barcode>, imageWidth: Int, imageHeight: Int, screenWidth: Int, screenHeight: Int) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        barcodes.forEach { barcode ->
            barcode.boundingBox?.toComposeRect()?.let {
                val topLeft = adjustPoint(PointF(it.topLeft.x, it.topLeft.y), imageWidth, imageHeight, screenWidth, screenHeight)
                val size = adjustSize(it.size, imageWidth, imageHeight, screenWidth, screenHeight)
                drawBounds(topLeft, size, Color.Yellow, 10f)
            }
        }
    }
}