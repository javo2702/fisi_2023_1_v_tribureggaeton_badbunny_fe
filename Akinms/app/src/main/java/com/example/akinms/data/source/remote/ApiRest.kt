package com.example.akinms.data.source.remote

import com.example.akinms.data.source.remote.dto.bodega.BodegaDto
import com.example.akinms.data.source.remote.dto.bodega.BodegasDto
import com.example.akinms.data.source.remote.dto.categoria.CategoriasDto
import com.example.akinms.data.source.remote.dto.pedido.*
import com.example.akinms.data.source.remote.dto.producto.ProductDto
import com.example.akinms.data.source.remote.dto.cliente.Cliente
import com.example.akinms.data.source.remote.dto.cliente.ClienteDto
import com.example.akinms.util.BODEGAS_BASE_URL
import com.example.akinms.util.CATEGORIAS_BASE_URL
import com.example.akinms.util.CLIENTES_BASE_URL
import com.example.akinms.util.PEDIDOS_BASE_URL
import com.example.akinms.util.PRODUCTOS_BASE_URL
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiRest {
    @Headers("Ocp-Apim-Subscription-Key:216cf1bc91394a43a28bd73ecce8641f")
    @GET("${PRODUCTOS_BASE_URL}/listar-productos/{id}")
    //suspend fun getAllProducts(): List<Products>
    suspend fun getAllProducts(
        @Path("id") id: Long
    ): ProductDto

    @Headers("Ocp-Apim-Subscription-Key:216cf1bc91394a43a28bd73ecce8641f")
    @GET("${BODEGAS_BASE_URL}/listar-bodegas")
    suspend fun getBodegas(): BodegasDto

    @Headers("Ocp-Apim-Subscription-Key:216cf1bc91394a43a28bd73ecce8641f")
    @GET("${BODEGAS_BASE_URL}/listar-bodegas/premium")
    suspend fun getBodegasPremium(): BodegasDto

    @Headers("Ocp-Apim-Subscription-Key:216cf1bc91394a43a28bd73ecce8641f")
    @GET("${BODEGAS_BASE_URL}/consultar/{id}")
    suspend fun getBodega(
        @Path("id") id:Long
    ): BodegaDto

    @Headers("Ocp-Apim-Subscription-Key:216cf1bc91394a43a28bd73ecce8641f")
    @GET("${CATEGORIAS_BASE_URL}/listar-categorias/{id}")
    suspend fun getAllCategoriesBodega(
        @Path("id") id: Long
    ): CategoriasDto

    @Headers("Ocp-Apim-Subscription-Key:216cf1bc91394a43a28bd73ecce8641f")
    @GET("${PRODUCTOS_BASE_URL}/consultar-productos/{id1}/{id2}")
    suspend fun getProductsCategoria(
        @Path("id1") id1: Long,
        @Path("id2") id2: Long
    ): ProductDto

    @Headers("Ocp-Apim-Subscription-Key:216cf1bc91394a43a28bd73ecce8641f")
    @GET("${PRODUCTOS_BASE_URL}/consultar-productos/{id}/buscar?")
    suspend fun getProductsByName(
        @Path("id") id: Long,
        @Query("nombre") nombre: String
    ): ProductDto

    @Headers("Ocp-Apim-Subscription-Key:216cf1bc91394a43a28bd73ecce8641f")
    @GET("${PEDIDOS_BASE_URL}/consultar-cliente/{id}")
    suspend fun getPedidoByClient(
        @Path("id") id: Long
    ): PedidoDto2

    @Headers("Ocp-Apim-Subscription-Key:216cf1bc91394a43a28bd73ecce8641f")
    @POST("${PEDIDOS_BASE_URL}/registrar")
    suspend fun setPedido(
        @Body pedido: PedidoPayment
    ): PedidoDto3

    @Headers("Ocp-Apim-Subscription-Key:216cf1bc91394a43a28bd73ecce8641f")
    @GET("${PEDIDOS_BASE_URL}/consultar-cliente/{id_cliente}/{id_pedido}")
    suspend fun getDetallePedidoCliente(
        @Path("id_cliente") id_cliente: Long,
        @Path("id_pedido") id_pedido: Long
    ): PedidoDto4

    @POST("${CLIENTES_BASE_URL}/buscar-cliente")
    @Headers("Ocp-Apim-Subscription-Key:216cf1bc91394a43a28bd73ecce8641f")
    suspend fun buscarCliente(
        @Query("correo") correo: String,
        @Query("pass") pass: String,
        //@Body cliente: Cliente
    ): ClienteDto

    @GET("${CLIENTES_BASE_URL}/consultar/{id}")
    @Headers("Ocp-Apim-Subscription-Key:216cf1bc91394a43a28bd73ecce8641f")
    suspend fun getCliente(
        @Path("id") id: Long
    ): ClienteDto
}