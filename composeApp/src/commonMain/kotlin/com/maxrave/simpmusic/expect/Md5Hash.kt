package com.maxrave.simpmusic.expect

/**
 * Platform-specific MD5 hash function.
 * Used for Last.fm API signature generation.
 */
expect fun md5Hash(input: String): String
