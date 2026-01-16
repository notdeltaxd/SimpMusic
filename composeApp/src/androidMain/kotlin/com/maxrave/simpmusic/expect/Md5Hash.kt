package com.maxrave.simpmusic.expect

import java.security.MessageDigest

/**
 * Android implementation of MD5 hash using java.security.MessageDigest.
 */
actual fun md5Hash(input: String): String {
    val md = MessageDigest.getInstance("MD5")
    val digest = md.digest(input.toByteArray())
    return digest.joinToString("") { "%02x".format(it) }
}
