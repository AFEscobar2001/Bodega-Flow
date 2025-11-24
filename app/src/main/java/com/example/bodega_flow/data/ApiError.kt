package com.example.bodega_flow.data

import retrofit2.HttpException

fun parseHttpError(e: HttpException): String {
    return e.response()
        ?.errorBody()
        ?.string()
        ?.takeIf { it.isNotBlank() }
        ?: "Error desconocido"
}