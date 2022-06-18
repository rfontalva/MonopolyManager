package com.example.monopolymanager.entities

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter

//import io.github.g0dkar.qrcode.render.QRCodeGraphics


class QRData(csv: String) {
    lateinit var game: String
    lateinit var username: String
    var cash: Int = 0
    var isValid: Boolean

    companion object {
        fun createQRData(game: String, user: String, cash: Int) : Bitmap {
            val text = "monopolyManager:${game},${user},${cash.toString()}"
            val writer = QRCodeWriter()
            val bitMatrix: BitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 400, 400)
            val w = bitMatrix.width
            val h = bitMatrix.height
            val pixels = IntArray(w * h)
            for (y in 0 until h) {
                for (x in 0 until w) {
                    pixels[y * w + x] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                }
            }

            val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, w, 0, 0, w, h)
            return bitmap
        }
    }

    init {
        val regex = "^monopolyManager:.*".toRegex()
        isValid = regex.matches(csv) && csv.count { it == ',' } == 2
        if (isValid) {
            val filtered = csv.substring("monopolyManager:".length, csv.length)
            val parsed = filtered.split(',')
            game = parsed[0]
            username = parsed[1]
            cash = parsed[2].toInt()
        }
    }


}