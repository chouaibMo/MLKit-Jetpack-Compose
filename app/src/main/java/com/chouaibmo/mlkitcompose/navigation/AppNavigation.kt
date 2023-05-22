package com.chouaibmo.mlkitcompose.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import com.chouaibmo.mlkitcompose.presentation.screens.barcode_scanning.BarcodeScanningScreen
import com.chouaibmo.mlkitcompose.presentation.screens.face_mesh_detection.FaceMeshDetectionScreen
import com.chouaibmo.mlkitcompose.presentation.screens.home.HomeScreen
import com.chouaibmo.mlkitcompose.presentation.screens.image_labeling.ImageLabelingScreen
import com.chouaibmo.mlkitcompose.presentation.screens.object_detection.ObjectDetectionScreen
import com.chouaibmo.mlkitcompose.presentation.screens.text_recognition.TextRecognitionScreen
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

internal sealed class Screen(val route: String) {
    object Home : Screen("home")
    object FaceMeshDetection : Screen("face_mesh_detection")
    object TextRecognition : Screen("text_recognition")
    object ObjectDetection : Screen("object_detection")
    object BarcodeScanning : Screen("barcode_scanning")
    object ImageLabeling : Screen("image_labeling")

}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation() {
    val navController = rememberAnimatedNavController()
    AnimatedNavHost(navController, startDestination = Screen.Home.route) {
        composable(route = Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(route = Screen.TextRecognition.route) {
            TextRecognitionScreen(navController)
        }
        composable(route = Screen.ObjectDetection.route) {
            ObjectDetectionScreen(navController)
        }
        composable(route = Screen.FaceMeshDetection.route) {
            FaceMeshDetectionScreen(navController)
        }
        composable(route = Screen.BarcodeScanning.route) {
            BarcodeScanningScreen(navController)
        }
        composable(route = Screen.ImageLabeling.route) {
            ImageLabelingScreen(navController)
        }
    }
}