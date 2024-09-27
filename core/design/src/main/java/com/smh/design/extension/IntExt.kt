package com.smh.design.extension

import java.util.Locale

fun Int.sOrEs(text: String, suffix: String = "s"): String {
    return if (this == 1) text else text + suffix
}

fun Int.formatByteSize(): String {
    val sizeInMb = this / (1024.0 * 1024.0)
    val sizeInGb = this / (1024.0 * 1024.0 * 1024.0)

    return if (sizeInMb >= 1000) {
        String.format(Locale.getDefault(),"%.2f GB", sizeInGb)
    } else {
        String.format(Locale.getDefault(),"%.2f MB", sizeInMb)
    }
}

fun Int.formatMSecondTime(): String {
    if (this <= 0) return "00:00"

    val seconds = this / 1000
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val remainingSeconds = seconds % 60

    return if (hours > 0) {
        String.format(Locale.getDefault(),"%d:%02d:%02d", hours, minutes, remainingSeconds)
    } else {
        String.format(Locale.getDefault(),"%02d:%02d", minutes, remainingSeconds)
    }
}