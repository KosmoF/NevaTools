package ru.neva.drivers.utils

import android.util.Base64
import org.json.JSONObject
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.net.URLDecoder
import java.nio.charset.Charset

object Requests {

    fun String.unescape(): String {
        val regex = "\\\\u([0-9A-Fa-f]{4})".toRegex()
        return regex.replace(this) { matchResult ->
            val charCode = matchResult.groupValues[1].toInt(16)
            charCode.toChar().toString()
        }
    }
    fun addKKT(url: String, username: String, password: String, serialNumber: String, kktModel: String, softwareVersion: String): Pair<Int, String> {
        try {
            val urlConnection = URL("$url/create_kkt").openConnection() as HttpURLConnection
            urlConnection.requestMethod = "POST"

            // Add basic auth header
            val authString = "$username:$password"
            val authHeaderValue = "Basic " + Base64.encodeToString(authString.toByteArray(), Base64.DEFAULT)
            urlConnection.setRequestProperty("Authorization", authHeaderValue)

            // Set content type to JSON
            urlConnection.setRequestProperty("Content-Type", "application/json")

            // Send JSON data
            urlConnection.doOutput = true
            val jsonObject = JSONObject()
            jsonObject.put("serialNumber", serialNumber)
            jsonObject.put("kktModel", kktModel)
            jsonObject.put("softwareVersion", softwareVersion)
            val jsonString = jsonObject.toString()
            val outputStream: OutputStream = urlConnection.outputStream
            outputStream.write(jsonString.toByteArray(Charsets.UTF_8))
            outputStream.close()

            val responseCode = urlConnection.responseCode
            var responseString: String = ""
            if (responseCode == HttpURLConnection.HTTP_OK) {
                responseString = urlConnection.inputStream.bufferedReader().readText()
            } else {
                val stream = urlConnection.errorStream ?: urlConnection.inputStream
                responseString = stream.bufferedReader().readText()
                responseString = "Код ошибки: $responseCode Текст ошибки: $responseString"
            }
            urlConnection.disconnect()
            return Pair(responseCode, responseString)
        } catch (e: MalformedURLException) {
            return Pair(404, "Неверный URL: ${e.message}")
        } catch (e: Exception) {
            return Pair(409, "Неверные данные для подключения: ${e.message}")
        }
    }

    fun writeLog(url: String, username: String, password: String, errorCode: Int?, serialNumber: String, dateTime: String, functionName: String): Pair<Int,String> {
        val urlConnection = URL("$url/write_log_kkt").openConnection() as HttpURLConnection
        urlConnection.requestMethod = "POST"

        // Add basic auth header
        val authString = "$username:$password"
        val authHeaderValue = "Basic " + Base64.encodeToString(authString.toByteArray(), Base64.DEFAULT)
        urlConnection.setRequestProperty("Authorization", authHeaderValue)

        // Set content type to JSON
        urlConnection.setRequestProperty("Content-Type", "application/json")

        // Send JSON data
        urlConnection.doOutput = true
        val jsonObject = JSONObject()
        jsonObject.put("errorCode", errorCode)
        jsonObject.put("serialNumber", serialNumber)
        jsonObject.put("dateTime", dateTime)
        jsonObject.put("functionName", functionName)
        val jsonString = jsonObject.toString()

        val outputStream: OutputStream = urlConnection.outputStream
        outputStream.write(jsonString.toByteArray(Charsets.UTF_8))
        outputStream.close()

        val responseCode = urlConnection.responseCode
        var responseString: String = ""
        if (responseCode == HttpURLConnection.HTTP_OK) {
            responseString = urlConnection.inputStream.bufferedReader().readText()
        } else {
            val stream = urlConnection.errorStream ?: urlConnection.inputStream
            val responseText = stream.bufferedReader().readText() // Под вопросом
            responseString = "Код ошибки: $responseCode Текст ошибки: $responseText"
        }
        urlConnection.disconnect()
        return Pair(responseCode, responseString)
    }

    fun getKKTStat(url: String, username: String, password: String, serialNumber: String): Pair<Int,String> {
        val urlConnection = URL("$url/get_kkt_stats/$serialNumber").openConnection() as HttpURLConnection
        urlConnection.requestMethod = "GET"

        // Add basic auth header
        val authString = "$username:$password"
        val authHeaderValue = "Basic " + Base64.encodeToString(authString.toByteArray(), Base64.DEFAULT)
        urlConnection.setRequestProperty("Authorization", authHeaderValue)

        val responseCode = urlConnection.responseCode
        var responseString: String = ""
        if (responseCode == HttpURLConnection.HTTP_OK) {
            responseString = urlConnection.inputStream.bufferedReader().readText()
            responseString = responseString.unescape()
        } else {
            val stream = urlConnection.errorStream ?: urlConnection.inputStream
            val responseText = stream.bufferedReader().readText().unescape() // Под вопросом
            responseString = "Код ошибки: $responseCode Текст ошибки: $responseText"
        }
        urlConnection.disconnect()
        return Pair(responseCode, responseString)
    }

//    fun getKKTStat(url: String, username: String, password: String, serialNumber: String): Pair<Int,String> {
//        val urlConnection = URL("$url/get_kkt_stats/$serialNumber").openConnection() as HttpURLConnection
//        urlConnection.requestMethod = "GET"
//
//        // Add basic auth header
//        val authString = "$username:$password"
//        val authHeaderValue = "Basic " + Base64.encodeToString(authString.toByteArray(), Base64.NO_WRAP)
//        urlConnection.setRequestProperty("Authorization", authHeaderValue)
//
//        val responseCode = urlConnection.responseCode
//        var responseString: String = ""
//        if (responseCode == HttpURLConnection.HTTP_OK) {
//            responseString = urlConnection.inputStream.bufferedReader(charset = Charsets.UTF_8).readText()
//        } else {
//            val errorStream = urlConnection.errorStream ?: urlConnection.inputStream
//            val responseText = errorStream.bufferedReader(charset = Charsets.UTF_16).readText()
//            responseString = "Код ошибки: $responseCode Текст ошибки: $responseText"
//        }
//        urlConnection.disconnect()
//        return Pair(responseCode, responseString)
//    }
}