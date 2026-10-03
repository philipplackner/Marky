package com.plcoding.marky

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform