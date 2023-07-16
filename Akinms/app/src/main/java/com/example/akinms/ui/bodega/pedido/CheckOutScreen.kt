package com.example.akinms.ui.bodega.pedido

import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.akinms.R
import com.example.akinms.data.source.remote.dto.Tarjeta.Tarjeta
import com.example.akinms.data.source.remote.dto.pedido.*
import com.example.akinms.data.source.remote.dto.pedido.Producto
import com.example.akinms.domain.model.CartItem
import com.example.akinms.ui.bodega.cart.CartViewModel
import com.example.akinms.ui.profile.ProfileViewModel
import com.example.akinms.ui.theme.PrimaryColor
import com.example.akinms.util.githubCreditCardMasker.CardNumberMask
import com.example.akinms.util.githubCreditCardMasker.ExpirationDateMask
import com.example.akinms.util.navigationGraph.Graph
import okhttp3.internal.notify
import java.time.LocalDate
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun CheckOutScreen(
    navController: NavHostController,
    viewModel: CartViewModel = hiltViewModel(),
    idBodega: Int,
    idCliente: Int,
    pedidoViewModel: PedidoViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel(),
){
    //var id = navController.getBackStackEntry(Graph.HOME+"/{idcliente}").arguments?.getLong("idcliente")
    //println("ID DEL CLIENTE: "+id)
    var pedidoState = pedidoViewModel.state
    var state = profileViewModel.state
    profileViewModel.getUsuario(idCliente.toLong())
    var fechaActual = LocalDateTime.now().toString().substring(0,11)
    val cartItems by viewModel.items.collectAsState(initial = emptyList())
    val showDialog = remember{ mutableStateOf(false) }
    val showLoading = remember{ mutableStateOf(false) }
    val showDialogError = remember{ mutableStateOf(false) }
    var cartBodega = mutableListOf<CartItem>()
    for(item in cartItems){
        if(item.idBodega == idBodega)
            cartBodega.add(item)
    }
    /*if(pedidoViewModel.state.pedidos?.idpedido!=0){
                                cartViewModel.deleteCartList(idBodega)
                                showDialog.value = true
                                //navController.navigate(Graph.BODEGA+"/"+idBodega)

                            }else{
                                showDialogError.value = true
                            }*/
    LaunchedEffect(pedidoState){
        if(pedidoState.isLoading!=null){
            println(pedidoState.isLoading!!)
            if(pedidoState.isLoading!!){
                if(pedidoState.pedidos?.idpedido!=0){
                    showLoading.value = true
                    cartViewModel.deleteCartList(idBodega)
                } else{
                    showDialogError.value = true
                }
            } else{
                showLoading.value = false
                showDialog.value = true
            }
        }

    }
    if(showDialog.value){
        com.example.akinms.ui.login.Alert(
            texto = "Se ha registrado el pedido con exito",
            msg = "Continuar",
            imageId = R.drawable.checked,
            showDialog = showDialog.value,
            onDismiss = { showDialog.value = false },
            onLoginClick = {
                navController.popBackStack()
                navController.navigate(Graph.BODEGA+"/cliente/"+idCliente+"/"+idBodega)
            }
        )
    }
    if(showLoading.value){
        AlertDialog(
            onDismissRequest = { /*TODO*/ },
            buttons = {},
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Registrando pedido\n", textAlign = TextAlign.Center)
                    CircularProgressIndicator(
                        color = Color.Yellow
                    )
                }

            }
        )
    }
    if(showDialogError.value){
        com.example.akinms.ui.login.Alert(
            texto = "Hubo un error al registrar el pedido",
            msg = "Volver",
            imageId = R.drawable.cancelar,
            showDialog = showDialogError.value,
            onDismiss = { showDialogError.value = false },
            onLoginClick = { navController.popBackStack() }
        )
    }
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        val openStatePago: MutableState<Boolean> = remember { mutableStateOf(false) }
        val openStateDetallesPedido: MutableState<Boolean> = remember { mutableStateOf(false) }
        val openStateCliente: MutableState<Boolean> = remember { mutableStateOf(false) }
        val openStateEnrega: MutableState<Boolean> = remember { mutableStateOf(false) }
        var tarjeta by remember { mutableStateOf("") }
        var codigo by remember { mutableStateOf("") }
        var date by remember { mutableStateOf("") }
        var formaPago by remember { mutableStateOf("efectivo") }
        var formaEntrega by remember { mutableStateOf("Recojo en Tienda") }
        var costoEntrega by remember { mutableStateOf("0.0") }
        var monto: Double = 0.0
        val igv: Double = 0.0
        val delivery: Double = 3.0
        val recojo: Double = 0.0
        for(ite in cartBodega){
            monto = monto + (ite.precio * ite.cantidad)
        }
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            text = "Confirmación de Pedido",
            color = Color.Green,
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item{
                Column(
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .fillMaxWidth(.95f)
                        .border(
                            width = .5.dp,
                            color = PrimaryColor,
                            shape = RoundedCornerShape(5.dp)
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .padding(top = 5.dp)
                            .fillMaxWidth(1f)
                            .height(30.dp)
                            .padding(start = 10.dp, end = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Detalles del pedido",
                            color = PrimaryColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        IconButton(onClick = { openStateDetallesPedido.value = !openStateDetallesPedido.value }) {
                            Icon(
                                Icons.Rounded.ArrowDropDown,
                                contentDescription = "arrow down",
                                tint = PrimaryColor
                            )
                        }
                    }
                    Column(
                        modifier = Modifier
                            .padding(bottom = 5.dp)
                            .fillMaxWidth(1f)
                            .height(45.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(.9f)
                                .padding(bottom = 1.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(text = "Cantidad de productos:", fontWeight = FontWeight.Bold)
                            Text(text = cartBodega.size.toString())
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(.9f)
                                .padding(bottom = 1.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Monto Total:", fontWeight = FontWeight.Bold)
                            Text(text = "S/. "+monto.toString())
                        }
                    }
                    if(openStateDetallesPedido.value){
                        Divider(
                            modifier = Modifier
                                .padding(vertical = 5.dp)
                                .fillMaxWidth(.9f),
                            thickness = 1.dp, color = PrimaryColor
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(.9f),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Cant", fontWeight = FontWeight.Bold, color = PrimaryColor)
                            Text(text = "Producto", fontWeight = FontWeight.Bold, color = PrimaryColor)
                            Text(text = "Total", fontWeight = FontWeight.Bold, color = PrimaryColor)
                        }
                        for(pedido in cartBodega){
                            Row(
                                modifier = Modifier.fillMaxWidth(.9f),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = pedido.cantidad.toString())
                                Text(text = pedido.nombre)
                                Text(text = (pedido.cantidad*pedido.precio).toString())
                            }
                        }
                        Divider(
                            modifier = Modifier
                                .padding(vertical = 5.dp)
                                .fillMaxWidth(.9f),
                            thickness = 1.dp, color = PrimaryColor
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(.9f),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "SubTotal:")
                            Text(text = "S/. "+monto.toString())
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(.9f),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "IGV:")
                            Text(text = "S/. "+igv.toString())
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(.9f),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Total:", fontWeight = FontWeight.Bold)
                            Text(text = "S/."+(monto+igv).toString(),fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            item{
                Column(
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .fillMaxWidth(.95f)
                        .border(
                            width = .5.dp,
                            color = PrimaryColor,
                            shape = RoundedCornerShape(5.dp)
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .padding(start = 10.dp, end = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Detalles de la cuenta",
                            color = PrimaryColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        IconButton(onClick = { openStateCliente.value = !openStateCliente.value }) {
                            Icon(
                                Icons.Rounded.ArrowDropDown,
                                contentDescription = "arrow down",
                                tint = PrimaryColor
                            )
                        }
                    }
                    Column(
                        modifier = Modifier
                            .padding(start = 20.dp)
                            .fillMaxWidth(1f)
                            .height(35.dp)
                            .padding(bottom = 10.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(.9f)
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Cliente: " + state.cliente?.nombres + " " + state.cliente?.apellidos,
                                fontWeight = FontWeight.Bold
                            )
                            Text("")
                        }
                    }
                        if(openStateCliente.value){
                            Divider(
                                modifier = Modifier
                                    .padding(vertical = 5.dp)
                                    .fillMaxWidth(.9f),
                                thickness = 1.dp, color = PrimaryColor
                            )
                            Column(
                                modifier = Modifier
                                    .padding(start = 20.dp)
                                    .fillMaxWidth(1f)
                                    .padding(bottom = 10.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(text = "Nombre: "+state.cliente!!.nombres)
                                Text(text = "Apellidos: "+state.cliente!!.apellidos)
                                Text(text = "Direccion: "+state.cliente!!.direccion)
                                Text(text = "Telefono: "+state.cliente!!.telefono)
                            }

                        }
                }
            }
            item{
                Column(
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .fillMaxWidth(.95f)
                        .border(
                            width = .5.dp,
                            color = PrimaryColor,
                            shape = RoundedCornerShape(5.dp)
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .padding(top = 5.dp)
                            .fillMaxWidth(1f)
                            .height(30.dp)
                            .padding(start = 10.dp, end = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Modo de Entrega",
                            color = PrimaryColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        IconButton(onClick = { openStateEnrega.value = !openStateEnrega.value }) {
                            Icon(
                                Icons.Rounded.ArrowDropDown,
                                contentDescription = "arrow down",
                                tint = PrimaryColor
                            )
                        }
                    }
                    Column(
                        modifier = Modifier
                            .padding(bottom = 5.dp)
                            .fillMaxWidth(1f)
                            .height(45.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(.9f)
                                .padding(bottom = 1.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = formaEntrega, fontWeight = FontWeight.Bold)
                            Text("")
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(.9f)
                                .padding(bottom = 1.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Costo:", fontWeight = FontWeight.Bold)
                            Text(text = costoEntrega)
                        }
                    }
                    if(openStateEnrega.value){
                        Divider(
                            modifier = Modifier
                                .padding(vertical = 5.dp)
                                .fillMaxWidth(.9f),
                            thickness = 1.dp, color = PrimaryColor
                        )
                        Column(
                        ) {
                            Text(text = "Seleccione un modo de entrega",color = PrimaryColor, fontSize = 15.sp)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(.9f)
                                    .padding(bottom = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.padding(top = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(modifier = Modifier.height(20.dp),selected = formaEntrega == "Recojo en Tienda",
                                        onClick = {
                                            formaEntrega = "Recojo en Tienda"; costoEntrega = recojo.toString()
                                                  },
                                        colors = RadioButtonDefaults.colors(selectedColor = PrimaryColor))
                                    Text(text = "Recojo en Tienda")
                                }
                                Text(text = "S/. "+recojo.toString())
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(.9f)
                                    .padding(bottom = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(modifier = Modifier.height(20.dp),selected = formaEntrega == "Entrega a Domicilio",
                                        onClick = {
                                            formaEntrega = "Entrega a Domicilio"; costoEntrega = delivery.toString()
                                                  },
                                        colors = RadioButtonDefaults.colors(selectedColor = PrimaryColor))
                                    Text(text = "Entrega a Domicilio")
                                }
                                Text(text = "S/. "+delivery.toString())
                            }
                        }
                    }
                }
            }
            item {
                Column( modifier = Modifier
                    .fillMaxWidth(.95f)
                    .border(width = .5.dp, color = PrimaryColor, shape = RoundedCornerShape(5.dp)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(modifier = Modifier
                        .padding(top = 5.dp)
                        .fillMaxWidth(1f)
                        .height(30.dp)
                        .padding(start = 10.dp, end = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Metodo de Pago",
                            color = PrimaryColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        IconButton(onClick = { openStatePago.value = !openStatePago.value }) {
                            Icon(
                                Icons.Rounded.ArrowDropDown,
                                contentDescription = "arrow down",
                                tint = PrimaryColor
                            )
                        }
                    }
                    Column(
                        modifier = Modifier
                            .padding(bottom = 5.dp)
                            .fillMaxWidth(1f)
                            .height(35.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(.9f),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            if (formaPago.equals("efectivo")) {
                                Text("Pago contraentrega", fontWeight = FontWeight.Bold)
                                Text("")
                            } else {
                                Text("Pago con tarjeta", fontWeight = FontWeight.Bold)
                                Text(tarjeta, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Column(
                        modifier = Modifier
                            .padding(vertical = 5.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if(openStatePago.value){
                            Divider(
                                modifier = Modifier
                                    .padding(vertical = 5.dp)
                                    .fillMaxWidth(.9f),
                                thickness = 1.dp, color = PrimaryColor
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 20.dp),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Column(
                                ) {
                                    Text(text = "Seleccione un metodo de pago",color = PrimaryColor, fontSize = 15.sp)
                                    Row(
                                        modifier = Modifier.padding(top = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(modifier = Modifier.height(20.dp),selected = formaPago == "efectivo", onClick = { formaPago = "efectivo" },colors = RadioButtonDefaults.colors(selectedColor = PrimaryColor))
                                        Text(text = "Pago contraentrega")
                                    }
                                    Row(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(modifier = Modifier.height(20.dp),selected = formaPago == "tarjeta", onClick = { formaPago = "tarjeta" },colors = RadioButtonDefaults.colors(selectedColor = PrimaryColor))
                                        Text(text = "Pago con tarjeta")
                                    }
                                }
                            }
                            if(formaPago.equals("tarjeta")){
                                Divider(
                                    modifier = Modifier
                                        .padding(vertical = 5.dp)
                                        .fillMaxWidth(.9f),
                                    thickness = 1.dp, color = PrimaryColor
                                )
                                OutlinedTextField(
                                    value = tarjeta,
                                    onValueChange = {
                                        if(it.length<=16) tarjeta = it
                                    },
                                    label = {Text("Número Tarjeta",fontSize = 15.sp)},
                                    visualTransformation = CardNumberMask("-"),
                                    colors = TextFieldDefaults.outlinedTextFieldColors(
                                        focusedBorderColor = PrimaryColor,
                                        unfocusedBorderColor = PrimaryColor,
                                        disabledBorderColor = Color.Gray,
                                        disabledTextColor = Color.Black,
                                        focusedLabelColor = Color.Gray
                                    ),
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 15.dp),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    OutlinedTextField(
                                        modifier = Modifier
                                            .width(140.dp)
                                            .padding(end = 10.dp),
                                        value = codigo,
                                        onValueChange = {
                                            if(it.length<=3) codigo = it
                                        },
                                        label = {Text("CVV", fontSize = 15.sp)},
                                        visualTransformation = PasswordVisualTransformation('*'),
                                        colors = TextFieldDefaults.outlinedTextFieldColors(
                                            focusedBorderColor = PrimaryColor,
                                            unfocusedBorderColor = PrimaryColor,
                                            disabledBorderColor = Color.Gray,
                                            disabledTextColor = Color.Black,
                                            focusedLabelColor = Color.Gray
                                        ),
                                    )
                                    OutlinedTextField(
                                        modifier = Modifier.width(140.dp),
                                        value = date,
                                        onValueChange = {
                                            if(it.length<=4) date = it
                                        },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        label = {Text("Expiracion",fontSize = 15.sp)},
                                        visualTransformation = ExpirationDateMask(),
                                        colors = TextFieldDefaults.outlinedTextFieldColors(
                                            focusedBorderColor = PrimaryColor,
                                            unfocusedBorderColor = PrimaryColor,
                                            disabledBorderColor = Color.Gray,
                                            disabledTextColor = Color.Black,
                                            focusedLabelColor = Color.Gray
                                        ),
                                    )

                                }
                            }
                        }
                    }
                }
            }
            item{
                Column(
                    modifier = Modifier
                        .padding(top = 10.dp, bottom = 10.dp)
                        .height(50.dp)
                        .fillMaxWidth(.95f)
                        .border(
                            width = .5.dp,
                            color = PrimaryColor,
                            shape = RoundedCornerShape(5.dp)
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .padding(start = 10.dp, end = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total a pagar",
                            color = PrimaryColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(text = "s/. "+(monto+costoEntrega.toDouble()).toString())
                    }
                }
            }
            item {
                Row(modifier = Modifier.fillMaxWidth(1f), horizontalArrangement = Arrangement.Center) {
                    Button(
                        modifier = Modifier.width(250.dp),
                        onClick = {
                            var detalles: MutableList<DetallesPedido> = mutableListOf()
                            for(detalle in cartBodega){
                                println("PRODUCTO ELEGIDO:        "+detalle.nombre+" con ID: "+detalle.idproducto)
                                detalles.add(DetallesPedido(detalle.cantidad,Producto(detalle.idproducto)))
                            }
                            /*var expMont = date.substring(0,1)
                            var expY = date.substring(2,3)
                            var expYSend = ("20"+expY).toInt()
                            var card: Tarjeta = Tarjeta(
                                amount = monto+costoEntrega.toDouble(),
                                currency =  "usd",
                                cardNumber = tarjeta,
                                expMonth= expMont.toInt(),
                                expYear= expYSend,
                                cvv = codigo
                            )*/
                            var payment: Payment = Payment(
                                "",
                                0,
                                "",
                                "",
                                0,
                                0,
                                ""
                            )
                            var pedido: PedidoPayment = PedidoPayment(
                                Bodega2(idBodega),
                                Cliente(idCliente),
                                detalles,
                                "enviado",
                                LocalDateTime.now().toString(),
                                //0,
                                formaPago,
                                (monto+costoEntrega.toDouble()),
                                formaEntrega,
                                payment
                            )
                            pedidoViewModel.setPedido(pedido)
                            /*if(pedidoViewModel.state.pedidos?.idpedido!=0){
                                cartViewModel.deleteCartList(idBodega)
                                showDialog.value = true
                                //navController.navigate(Graph.BODEGA+"/"+idBodega)

                            }else{
                                showDialogError.value = true
                            }*/

                        }) {
                        Text(text = "Confirmar Pedido")
                    }
                }
            }
        }
    }
}

@Composable
fun Alert(
    texto: String,
    msg : String,
    imageId: Int,
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onLoginClick: () -> Unit
) {

    if (showDialog) {
        val colorAlerta = Color(0xFF70D68C)
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = onLoginClick ) {
                    Text(msg, color = PrimaryColor, textAlign = TextAlign.Right)
                }
            },
            title={
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ){
                    //Icon(Modifier.size(26.dp),
                    //painterResource(R.drawable.check_icon))
                    Image(
                        painterResource(id = imageId),
                        contentDescription = null,
                        Modifier
                            .align(Alignment.Center)
                            .size(70.dp, 70.dp)
                    )
                }
            },
            text = {
                if(texto.contains("Error")){
                    Text(text = texto,
                        textAlign = TextAlign.Center,
                        color = Color(0xFFBB2424), fontWeight = FontWeight.SemiBold, fontSize = 18.sp,
                        modifier = Modifier.fillMaxWidth())
                } else{
                    Text(text = texto,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF32BA7C), fontWeight = FontWeight.SemiBold, fontSize = 18.sp,
                        modifier = Modifier.fillMaxWidth())
                }

            },
            backgroundColor = Color.White,
            shape = RoundedCornerShape(5.dp)


        )
    }
}