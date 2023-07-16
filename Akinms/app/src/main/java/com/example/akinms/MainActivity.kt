package com.example.akinms

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.runtime.*

import androidx.compose.ui.tooling.preview.Preview

import com.example.akinms.ui.AkinmsApp
import com.example.akinms.ui.theme.AkinmsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            /*val navController = rememberNavController()
            AkinmsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                   NavHost(navController = navController, startDestination = "mainMinimarket") {
                        composable("mainMinimarket") {
                            HomeView(navController)
                        }
                        composable("allCategoriesMinimarket") {
                            AllCategoriesView(navController)
                        }
                       composable("allProductsMinimarket") {
                           ProductsView(navController)
                       }
                       composable("CartMinimarket/{cant}", listOf(navArgument("cant"){type = NavType.IntType})) {
                           val id = it.arguments?.getInt("cant")
                           requireNotNull(id)
                           CartView(navController,id)
                       }
                        /*...*/
                    }

                }
            }*/
            AkinmsApp()
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AkinmsTheme {
    }
}