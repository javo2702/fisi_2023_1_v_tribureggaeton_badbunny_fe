package com.example.akinms.data.source.remote.dto.Tarjeta

import com.example.akinms.data.source.remote.dto.pedido.DetallesPedido

data class Tarjeta(
    var amount: Double,
    var currency: String,
    var cardNumber: String,
    var expMonth: Int,
    var expYear: Int,
    var cvv: String
)
