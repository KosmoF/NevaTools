package ru.neva.drivers.utils

import android.content.Context
import android.icu.util.Calendar
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class Logger(context: Context) {
    enum class LogLevel {
        SUCCESS,
        ERROR
    }

    private val folder = File(context.applicationInfo.dataDir, "logs")
    private val file = File(folder, "AppLogs.txt")
    private val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    init {
        if (!folder.exists()) {
            folder.mkdirs()
        }
        if (!file.exists()) {
            file.createNewFile()
        }
    }

    fun log(level: LogLevel, functionName: String, params: String, result: String) {
        val logMessage = sdf.format(Calendar.getInstance().time) + " " + level.toString() + " " + functionName + "(" + params + ") : " + result + "\n"
        file.appendText(logMessage)
    }
}