package com.chouaibmo.mlkitcompose.analyzer

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

@SuppressLint("UnsafeOptInUsageError")
class BarcodeScanningAnalyzer(
    private val onBarcodeDetected: (barcodes: MutableList<Barcode>, width: Int, height: Int) -> Unit
) : ImageAnalysis.Analyzer {

    private val options = BarcodeScannerOptions.Builder().build()

    private val scanner = BarcodeScanning.getClient(options)

    override fun analyze(imageProxy: ImageProxy) {
        imageProxy.image?.let {
            val imageValue = InputImage.fromMediaImage(it, imageProxy.imageInfo.rotationDegrees)
            scanner.process(imageValue)
                .addOnSuccessListener { barcodes ->
                    onBarcodeDetected(barcodes, imageValue.height, imageValue.width)
                }
                .addOnFailureListener { failure ->
                    failure.printStackTrace()
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }
}