package com.example.monopolymanager

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Size
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.example.monopolymanager.databinding.ActivityQrscannerBinding
import com.example.monopolymanager.entities.QRData
import com.example.monopolymanager.entities.QrCodeAnalyzer
import com.example.monopolymanager.fragments.LoginFragmentDirections
import com.example.monopolymanager.viewmodels.QRScannerViewModel
import com.google.android.material.snackbar.Snackbar

class QRScanner : AppCompatActivity() {

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 10
    }
    private lateinit var binding : ActivityQrscannerBinding
    private lateinit var viewModel : QRScannerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrscannerBinding.inflate(layoutInflater)
        viewModel = QRScannerViewModel(binding.root.context)
        setContentView(binding.root)
        viewModel.isLoading.observe(this) { stillLoading ->
            if (!stillLoading) {
                if (isCameraPermissionGranted()) {
                    binding.textureView.post { startCamera() }
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.CAMERA),
                        REQUEST_CAMERA_PERMISSION
                    )
                }
                binding.payBtn.setOnClickListener {
                    if (viewModel.hasFoundValid()) {
                        val amount = binding.payAmtTxt.text.toString().toInt()
                        val userDest = binding.toPlayerTxt.text.toString()
                        if (viewModel.pay(amount, userDest)) {
                            this.finish()
                        } else {
                            Snackbar.make(binding.root, getString(R.string.notEnoughCash), Snackbar.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        Snackbar.make(binding.root, getString(R.string.findQR), Snackbar.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        val cameraSelector =
            CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
        val previewConfig = Preview.Builder()
            .setTargetResolution(Size(400,400))
            .build()

        previewConfig.setSurfaceProvider(binding.textureView.createSurfaceProvider())


        val imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setTargetResolution(Size(400,400))
            .build()
        val executor = ContextCompat.getMainExecutor(this)
        val imageAnalyzer = ImageAnalysis.Builder().build().also { it ->
            it.setAnalyzer(executor, QrCodeAnalyzer { qrCodes ->
                qrCodes?.forEach {
                    val qrData = QRData(it.rawValue!!)
                    if(qrData.isValid && viewModel.isValidGame("Game1", qrData.username)) {
                        binding.payAmtTxt.text = qrData.cash.toString()
                        binding.toPlayerTxt.text = qrData.username
                        viewModel.setHasFoundValid()
                    } else {
                        binding.scanQRTxt.text = getString(R.string.notValidQR)
                    }
                }
            })
        }

        cameraProviderFuture.addListener(Runnable{
            val cameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()
            val camera = cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                previewConfig,
                imageCapture,
                imageAnalyzer
            )
            camera.cameraControl.enableTorch(false)
        }, executor)
    }

    private fun isCameraPermissionGranted(): Boolean {
        val selfPermission =
            ContextCompat.checkSelfPermission(baseContext, Manifest.permission.CAMERA)
        return selfPermission == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (isCameraPermissionGranted()) {
                binding.textureView.post { startCamera() }
            } else {
                Toast.makeText(this, "Camera permission is required.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}