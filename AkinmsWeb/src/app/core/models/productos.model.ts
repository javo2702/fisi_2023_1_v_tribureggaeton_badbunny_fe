export interface Producto {
    nombre: String;
    descripcion: String;
    precio: number;
    descuento: number;
    stock: number;
    img: String;
    idProducto: number;
}
export interface Respuesta {
    mensaje: String;
    productos: Producto[];
}
export interface Productos {
    productos: Producto[];
}
export interface DetallePedido{
    cantidad: Number,
    producto:Producto
}
export interface Pedido{
    fecha:string,
    monto_total:number,
    estado:String,
    tipo_entrega:string,
    metodo_pago:string,
    idpedido:number,
    detallesPedido: DetallePedido[]
}
export interface PedidoRespuesta{
    mensaje: String;
    pedidos: Pedido[];
}
