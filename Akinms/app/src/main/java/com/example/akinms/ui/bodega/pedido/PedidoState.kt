package com.example.akinms.ui.bodega.pedido

import com.example.akinms.data.source.remote.dto.pedido.Pedido
import com.example.akinms.data.source.remote.dto.pedido.PedidoX

data class PedidoState(
    val pedidos: PedidoX? = null,
    val isLoading: Boolean? = null,

)
