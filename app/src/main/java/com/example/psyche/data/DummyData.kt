package com.example.psyche.data

data class DummyData(
    val email: String,
    val password: String,
    val name: String,
    val phone: String,
    val address: String
)

val dummyLoginData = DummyData(
    email = "dhikifauzan97@gmail.com",
    password = "@Pass123",
    name = "Dhiki Fauzan",
    phone = "085813800158",
    address = "Jl. Buah"
)