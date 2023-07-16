package com.example.akinms

import com.google.gson.annotations.SerializedName

data class Minimarket(val nombre:String = "Don Gilberto",val direccion:String = "San Martin de Porres")
data class Category(val nombre:String = "Bebidas", val img:String = "bebidas")
data class DogsResponse (@SerializedName("status") var status:String, @SerializedName("message") var images: List<String>)

data class Producto(var name:String, var descripcion:String, var precio:Double, var img:String)
data class CartItemEntity(var name:String, var descripcion:String, var precio:Double, var img:String, var cantidad:Int)


