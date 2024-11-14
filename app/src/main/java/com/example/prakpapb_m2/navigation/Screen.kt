package com.example.prakpapb_m2.navigation

sealed class Screen (val route: String) {
    object Matkul : Screen("Matkul")
    object Tugas : Screen("Tugas")
    object Profil : Screen("Profil")
}