package com.maxrave.simpmusic.expect

import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.dataUsingEncoding
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.usePinned
import platform.CoreCrypto.CC_MD5
import platform.CoreCrypto.CC_MD5_DIGEST_LENGTH

/**
 * iOS implementation of MD5 hash using CommonCrypto.
 */
@OptIn(ExperimentalForeignApi::class)
actual fun md5Hash(input: String): String {
    val data = (input as NSString).dataUsingEncoding(NSUTF8StringEncoding) ?: return ""
    val digest = UByteArray(CC_MD5_DIGEST_LENGTH)
    
    digest.usePinned { pinnedDigest ->
        data.bytes?.let { bytes ->
            CC_MD5(bytes, data.length.convert(), pinnedDigest.addressOf(0))
        }
    }
    
    return digest.joinToString("") { it.toString(16).padStart(2, '0') }
}
