package com.example.akinms.ui.bodega.products

import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.akinms.domain.model.Categoria
import com.example.akinms.viewsborrar.FullScreenLoading
import com.example.akinms.viewsborrar.ProductsView
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ProductsScreen(
    navController: NavHostController,
    viewModel: ProductsViewModel = hiltViewModel(),
    bodegaNombre: String,
    idBodega: Int,
    categorias: List<Categoria>,
    filterCategory: Boolean = false,
    nombreSearch: Boolean = false,
    nombre:String = ""
){
    var state: ProductsState = viewModel.state

    if(!filterCategory && !nombreSearch){
        viewModel.getProducts()
        state.isLoading = false
    }else if(filterCategory){
        viewModel.getProductsCategoria()
        state.isLoading = false
    } else if(nombreSearch){
        viewModel.getProductsNombre(idBodega.toLong(),nombre)
        state.isLoading = false
    }
    //val state = viewModel.state
    val eventFlow = viewModel.eventFlow
    val scaffoldState = rememberScaffoldState()
    LaunchedEffect(key1 = true){
        eventFlow.collectLatest { event ->
            when(event) {
                is ProductsViewModel.UIEvent.ShowSnackBar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message
                    )
                }
            }
        }
    }
    if(state.isLoading){
        FullScreenLoading()
    } else{
        ProductsView(
            navController = navController,
            isLoading = state.isLoading,
            productos = state.productos,
            bodegaNombre = bodegaNombre,
            idBodega = idBodega,
            categorias = categorias
        )
    }

}