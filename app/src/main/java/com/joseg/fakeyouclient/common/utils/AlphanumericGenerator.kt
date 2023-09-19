package com.joseg.fakeyouclient.common.utils

object AlphanumericGenerator {
    private val charSet = ('a'..'z') + ('A'..'Z') + ('1'..'9')
    fun generate(length: Int): String = (1..length)
        .map { charSet.random() }
        .joinToString("")
}