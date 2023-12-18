package com.bobi.vendorium.models

data class User(
    val userUID: String = "",
    val email: String = "",
    var username: String = "",
    var name: String = "",
    var surname: String = "",
    var contactNumber: String = "",
    var address: String = "",
    var listings: List<String> = emptyList(),
    var favouriteListings: List<String> = emptyList()
)

/**
 * User:
 * janez.kricej@gmail.com
 * geslo:
 * tone1234
 *
 *
 *
 * */