package com.chouaibmo.mlkitcompose.analyzer

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.facemesh.FaceMesh
import com.google.mlkit.vision.facemesh.FaceMeshDetection

@SuppressLint("UnsafeOptInUsageError")
class FaceMeshDetectionAnalyzer(
    private val onFaceMeshDetected: (faces: MutableList<FaceMesh>, width: Int, height: Int) -> Unit
) : ImageAnalysis.Analyzer {

    private val meshDetector = FaceMeshDetection.getClient()

    override fun analyze(imageProxy: ImageProxy) {
        imageProxy.image?.let {
            val imageValue = InputImage.fromMediaImage(it, imageProxy.imageInfo.rotationDegrees)
            meshDetector.process(imageValue)
                .addOnSuccessListener { meshes ->
                    onFaceMeshDetected(meshes, imageProxy.width, imageProxy.height)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }
}