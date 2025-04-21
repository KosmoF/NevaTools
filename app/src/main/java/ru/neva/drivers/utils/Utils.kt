package ru.neva.drivers.utils

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.util.Patterns
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.charset.Charset
import java.util.regex.Pattern
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object Utils {
    fun isValidIpAddress(ipAddress: String): Boolean {
        if (ipAddress == "") {
            return false
        }
        val pattern = Pattern.compile(
            "^(25[0-4]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                    "(25[0-4]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                    "(25[0-4]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                    "(25[0-4]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"
        )

        return pattern.matcher(ipAddress).matches()
    }

    fun isValidURL(URL: String): Boolean {
        if (URL == "") {
            return false
        }
        return Patterns.WEB_URL.matcher(URL).matches()
    }

    fun isValidEmail(email: String): Boolean {
        if (email == "") {
            return false
        }
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun booleanToString(param: Boolean): String {
        when (param) {
            true -> return "Да"
            false -> return "Нет"
        }
    }

    fun padWithZeros(input: String, length: Int): String {
        var paddedString = input
        while (paddedString.length < length) {
            paddedString = "0" + paddedString
        }
        return paddedString
    }

    fun calculateCCITT(data: String): Int {
        val bytes = data.toByteArray(Charsets.ISO_8859_1)
        var crc = 0xFFFF

        for (i in data.indices) {
            crc = crc xor (data[i].toInt() shl 8)
            for (j in 0 until 8) {
                if (crc and 0x8000 != 0) {
                    crc = (crc shl 1) xor 0x1021
                } else {
                    crc = crc shl 1
                }
                crc = crc and 0xFFFF
            }
        }

        return crc
    }

    fun calcINN10(INN: String): String {
        val inn = INN.substring(0, 9)
        val coefficients = listOf(2, 4, 10, 3, 5, 9, 4, 6, 8)
        var ctrl = 0
        for (i in 0..8) {
            ctrl += coefficients[i] * inn[i].toString().toInt()
        }
        ctrl = (ctrl % 11) % 10
        return inn + ctrl.toString()
    }

    fun calcINN12(INN: String): String {
        var inn = INN.substring(0, 10)
        val coefficients11 = listOf(7, 2, 4, 10, 3, 5, 9, 4, 6, 8)
        val coefficients12 = listOf(3, 7, 2, 4, 10, 3, 5, 9, 4, 6, 8)
        var ctrl = 0

        for (i in 0..9) {
            ctrl += coefficients11[i] * inn[i].toString().toInt()
        }
        ctrl = (ctrl % 11) % 10
        inn += ctrl.toString()

        ctrl = 0
        for (i in 0..10) {
            ctrl += coefficients12[i] * inn[i].toString().toInt()
        }
        ctrl = (ctrl % 11) % 10
        inn += ctrl.toString()

        return inn
    }

    fun calcINNControlDigits(INN: String): String {
        val inn = INN.trim()
        return when (inn.length) {
            10 -> calcINN10(inn)
            12 -> calcINN12(inn)
            else -> ""
        }
    }

    fun checkINN(INN: String): Boolean {
        if (INN.length != 10 && INN.length != 12) {
            return false
        }
        val correctINN = calcINNControlDigits(INN)
        return correctINN == INN
    }

    fun markingCodeFromViewType(kmTextVal: String, kmTypeVal: Int): String {
        return when (kmTypeVal) {
            0 -> kmTextVal.replace("{FNC1}", "\u001D")
            1 -> {
                val decodedBytes = Base64.decode(kmTextVal, Base64.DEFAULT)
                val utf16String = String(decodedBytes, Charset.forName("UTF-16"))
                return utf16String
            }

            2 -> {
                val markingCode: MutableList<UInt> = mutableListOf()
                val bytes: List<String> = kmTextVal.split(" ")
                for (byte in bytes) {
                    markingCode.add(byte.toLong(16).toUInt())
                }
                return markingCode.joinToString(separator = "", transform = { it.toString(16) })
                    .toByteArray().toString()
            }

            3 -> kmTextVal.replace("\u001d", "\u001D")
            else -> return kmTextVal
        }
    }

    fun zipFolder(folderPath: String, zipFilePath: String) {
        val sourceFolder = File(folderPath)
        val zipFile = File(zipFilePath)
        val zipOutputStream = ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile)))
        compressFolder(sourceFolder, sourceFolder.name, zipOutputStream)
        zipOutputStream.close()
    }

    fun compressFolder(folder: File, parentFolderName: String, zipOut: ZipOutputStream) {
        val files = folder.listFiles()
        for (file in files) {
            if (file.isDirectory) {
                val entry = ZipEntry("${parentFolderName}/${file.name}/")
                zipOut.putNextEntry(entry)
                zipOut.closeEntry()
                compressFolder(file, "${parentFolderName}/${file.name}", zipOut)
            } else {
                val data = ByteArray(1024)
                val fi = FileInputStream(file)
                val origin = BufferedInputStream(fi, 1024)
                val entry = ZipEntry("${parentFolderName}/${file.name}")
                zipOut.putNextEntry(entry)
                var count: Int
                while (origin.read(data, 0, 1024).also { count = it } != -1) {
                    zipOut.write(data, 0, count)
                }
                origin.close()
                zipOut.closeEntry()
            }
        }
    }

    fun parseStatsFromJson(jsonString: String): String {
        val jsonObject = JSONObject(jsonString)
        val stats = jsonObject.getJSONObject("stats")

        val kktCallsCount = stats.getInt("kkt_calls_count")
        val kktErrorsCount = stats.getInt("kkt_errors_count")
        val kktErrorsCountByTypes = stats.getJSONArray("kkt_errors_count_by_types")

        val stringBuilder = StringBuilder()

        stringBuilder.appendLine("--------------------------------------------------")
        stringBuilder.appendLine("Статистика по подключённой ККТ за последние 30 дней:")

        stringBuilder.appendLine("Общее кол-во выполненых функций: $kktCallsCount")
        stringBuilder.appendLine("Общее кол-во ошибок: $kktErrorsCount")
        stringBuilder.appendLine("Кол-во ошибок по кодам:")

        for (i in 0 until kktErrorsCountByTypes.length()) {
            val error = kktErrorsCountByTypes.getJSONArray(i)
            val errorCode = error.getInt(0)
            val errorMessage = error.getString(1)
            val errorCount = error.getInt(2)
            stringBuilder.appendLine("$errorCode - $errorMessage : $errorCount")
        }

        stringBuilder.appendLine("--------------------------------------------------")
        stringBuilder.appendLine("Общая статистика за последние 30 дней:")

        val totalCallsCount = stats.getInt("total_calls_count")
        val totalErrorsCount = stats.getInt("total_errors_count")
        val totalErrorsCountByTypes = stats.getJSONArray("total_errors_count_by_types")

        stringBuilder.appendLine("Общее кол-во выполненых функций: $totalCallsCount")
        stringBuilder.appendLine("Общее кол-во ошибок: $totalErrorsCount")
        stringBuilder.appendLine("Кол-во ошибок по кодам:")

        for (i in 0 until totalErrorsCountByTypes.length()) {
            val error = totalErrorsCountByTypes.getJSONArray(i)
            val errorCode = error.getInt(0)
            val errorMessage = error.getString(1)
            val errorCount = error.getInt(2)
            stringBuilder.appendLine("$errorCode - $errorMessage : $errorCount")
        }

        stringBuilder.appendLine("--------------------------------------------------")
        stringBuilder.appendLine("Последние вызовы функций ККТ (до 50):")

        val kktLastCalls = stats.getJSONArray("kkt_last_calls")
        for (i in 0 until kktLastCalls.length()) {
            val call = kktLastCalls.getJSONArray(i)
            val datetime = call.getString(0)
            val functionName = call.getString(1)
            val errorCode = call.optInt(2, 0)
            if (errorCode == 0){
                stringBuilder.appendLine("$datetime - Функция: $functionName Код ошибки: Ошибок нет")
            }
            else{
                stringBuilder.appendLine("$datetime - Функция: $functionName Код ошибки: $errorCode")
            }
        }

        stringBuilder.appendLine("--------------------------------------------------")

        return stringBuilder.toString()
    }

    fun getPixelsArray(bitmap: Bitmap): ByteArray {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        val byteArray = ByteArray(pixels.size)
        for (i in pixels.indices) {
            val pixel = pixels[i]
            val alpha = Color.alpha(pixel)

            // Пропускаем полностью прозрачные пиксели
            if (alpha == 0) {
                byteArray[i] = 0x00
                continue
            }

            val red = Color.red(pixel)
            val green = Color.green(pixel)
            val blue = Color.blue(pixel)

            // Среднее значение цвета для определения яркости
            val brightness = (red + green + blue) / 3

            // Условие для определения, является ли пиксель светлым
            byteArray[i] = if (brightness > 128) 0x00 else 0xFF.toByte()
        }
        return byteArray
    }


}