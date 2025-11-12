package com.example.ticketscan.ui.feedback

import java.net.ConnectException
import java.net.SocketTimeoutException
import java.sql.SQLException

object ErrorMessageHelper {
    fun getFriendlyErrorMessage(exception: Exception): String {
        return when (exception) {
            is ConnectException -> "No se pudo conectar al servidor. Por favor verifica tu conexión a internet."
            is SocketTimeoutException -> "Tiempo de espera agotado. El servidor está tardando demasiado en responder."
            is SQLException -> "Error al acceder a la base de datos. Por favor intenta nuevamente."
            is IllegalArgumentException -> "Datos inválidos: ${exception.message ?: "Por favor verifica la información ingresada."}"
            is IllegalStateException -> "Operación no permitida: ${exception.message ?: "Por favor intenta nuevamente."}"
            is NullPointerException -> "Error inesperado. Por favor intenta nuevamente."
            else -> exception.message ?: "Ocurrió un error inesperado. Por favor intenta nuevamente."
        }
    }
}

