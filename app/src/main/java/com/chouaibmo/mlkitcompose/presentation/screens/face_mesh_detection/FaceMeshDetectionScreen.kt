package com.chouaibmo.mlkitcompose.presentation.screens.face_mesh_detection

import android.graphics.PointF
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
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
import com.chouaibmo.mlkitcompose.analyzer.FaceMeshDetectionAnalyzer
import com.chouaibmo.mlkitcompose.presentation.common.*
import com.chouaibmo.mlkitcompose.presentation.common.components.CameraView
import com.chouaibmo.mlkitcompose.presentation.common.utils.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.facemesh.FaceMesh

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FaceMeshDetectionScreen(navController: NavController) {
    val context = LocalContext.current
    val cameraPermissionState =
        rememberPermissionState(permission = android.Manifest.permission.CAMERA)

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
    val faces = remember { mutableStateListOf<FaceMesh>() }

    val screenWidth = remember { mutableStateOf(context.resources.displayMetrics.widthPixels) }
    val screenHeight = remember { mutableStateOf(context.resources.displayMetrics.heightPixels) }

    val imageWidth = remember { mutableStateOf(0) }
    val imageHeight = remember { mutableStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        CameraView(
            context = context,
            lifecycleOwner = lifecycleOwner,
            analyzer = FaceMeshDetectionAnalyzer { meshes, width, height ->
                faces.clear()
                faces.addAll(meshes)
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
                    text = stringResource(id = R.string.face_mesh_detection_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }
        }
        DrawFaces(faces = faces, imageHeight.value, imageWidth.value, screenWidth.value, screenHeight.value)
    }
}


@Composable
fun DrawFaces(faces: List<FaceMesh>, imageWidth: Int, imageHeight: Int, screenWidth: Int, screenHeight: Int) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        faces.forEach { face ->
            val boundingBox = face.boundingBox.toComposeRect()
            val topLeft = adjustPoint(PointF(boundingBox.topLeft.x, boundingBox.topLeft.y), imageWidth, imageHeight, screenWidth, screenHeight)
            val size = adjustSize(boundingBox.size, imageWidth, imageHeight, screenWidth, screenHeight)
            drawBounds(topLeft, size, Color.Yellow, 5f)

            face.allPoints.forEach {
                val landmark = adjustPoint(PointF(it.position.x, it.position.y), imageWidth, imageHeight, screenWidth, screenHeight)
                drawLandmark(landmark, Color.Cyan, 3f)
            }

            face.allTriangles.forEach { triangle ->
                val points = triangle.allPoints.map {
                    adjustPoint(PointF(it.position.x, it.position.y), imageWidth, imageHeight, screenWidth, screenHeight)
                }
                drawTriangle(points, Color.Cyan, 1f)
            }
        }
    }
}