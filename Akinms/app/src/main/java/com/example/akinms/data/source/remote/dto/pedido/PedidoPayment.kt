package com.example.akinms.data.source.remote.dto.pedido

data class PedidoPayment(
    var bodega: Bodega2,
    var cliente: Cliente,
    var detallesPedido: List<DetallesPedido>,
    var estado: String,
    var fecha: String,
    //var idpedido: Int? = 0,
    var metodo_pago: String,
    var monto_total: Double,
    var tipo_entrega: String,
    var payment:Payment
)
data class Payment(
    var token:String,
    var amount:Int,
    var currency:String,
    var cardNumber:String,
    var expMonth:Int,
    var expYear:Int,
    var cvv:String,
)