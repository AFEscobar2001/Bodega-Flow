package com.example.bodega_flow.util

object QrParser {
    fun parseBodegaId(raw: String): Long? {
        val s = raw.trim()

        // formatos soportados:
        // "2"
        // "BOD:2"
        // "BODEGA:2"
        val numOnly = s.toLongOrNull()
        if (numOnly != null) return numOnly

        val parts = s.split(":")
        if (parts.size == 2) {
            val prefix = parts[0].uppercase()
            val value = parts[1].trim().toLongOrNull()
            if (value != null && (prefix == "BOD" || prefix == "BODEGA")) return value
        }
        return null
    }
}
