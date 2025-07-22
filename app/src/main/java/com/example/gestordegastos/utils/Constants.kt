package com.example.gestordegastos.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

data class CategoryIcon(
    val name: String,
    val icon: ImageVector
)

fun getIconForName(name: String): ImageVector {
    return iconsCategory.find { it.name == name }?.icon ?: Icons.AutoMirrored.Filled.Help
}

val iconsCategory = listOf(
    CategoryIcon("Food", Icons.Filled.Restaurant),
    CategoryIcon("Transport", Icons.Filled.DirectionsCar),
    CategoryIcon("Health", Icons.Filled.LocalHospital),
    CategoryIcon("Home", Icons.Filled.Home),
    CategoryIcon("Entertainment", Icons.Filled.Movie),
    CategoryIcon("Shopping", Icons.Filled.ShoppingCart),
    CategoryIcon("Education", Icons.Filled.School),
    CategoryIcon("Travel", Icons.Filled.Flight),
    CategoryIcon("Pets", Icons.Filled.Pets),
    CategoryIcon("Gifts", Icons.Filled.CardGiftcard),
    CategoryIcon("Technology", Icons.Filled.Computer),
    CategoryIcon("Phone", Icons.Filled.Phone),
    CategoryIcon("Bills", Icons.Filled.Receipt),
    CategoryIcon("Internet", Icons.Filled.Wifi),
    CategoryIcon("Savings", Icons.Filled.Savings),
    CategoryIcon("Investments", Icons.AutoMirrored.Filled.TrendingUp),
    CategoryIcon("Clothing", Icons.Filled.Checkroom),
    CategoryIcon("Sports", Icons.Filled.SportsSoccer),
    CategoryIcon("Children", Icons.Filled.ChildCare),
    CategoryIcon("Cinema", Icons.Filled.LocalMovies),
    CategoryIcon("Books", Icons.AutoMirrored.Filled.MenuBook),
    CategoryIcon("Coffee", Icons.Filled.LocalCafe),
    CategoryIcon("Bar", Icons.Filled.LocalBar),
    CategoryIcon("Taxi", Icons.Filled.LocalTaxi),
    CategoryIcon("Bike", Icons.AutoMirrored.Filled.DirectionsBike),
    CategoryIcon("Fuel", Icons.Filled.LocalGasStation),
    CategoryIcon("Repairs", Icons.Filled.Build),
    CategoryIcon("Work", Icons.Filled.Work),
    CategoryIcon("Rent", Icons.Filled.Apartment),
    CategoryIcon("Credit", Icons.Filled.CreditCard),
    CategoryIcon("Taxes", Icons.Filled.RequestQuote),
    CategoryIcon("Other", Icons.Filled.Category)
)
