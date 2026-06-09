package com.example.bichimovil.core

import java.text.NumberFormat
import java.util.Locale
/**
 * Convierte Long (centavos) a String con formato MXN
 * Ejemplo: 123456 → "$1,234.56 MXN"
 */
fun Long.toCurrencyMXN(): String {
    val fmt = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    return fmt.format(this / 100.0)
}
/**
 * Convierte String de dinero a centavos (Long)
 * Ejemplo: "250.00" → 25000
 */
fun String.toMoneyCents(): Long {
    return ((this.toDoubleOrNull() ?: 0.0) * 100).toLong()
}