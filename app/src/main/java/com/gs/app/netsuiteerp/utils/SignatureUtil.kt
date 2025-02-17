// Copyright 2025 Grandstream
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//     https://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.gs.app.netsuiteerp.utils

import android.os.Build
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Base64
import java.util.Locale
import java.util.Random
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


object SignatureUtil {
    fun generateTimeStamp(): String {
        return "${System.currentTimeMillis() / 1000L}"
    }

    fun generateNonce(length: Int): String {
        var text = ""
        val possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        for (i in 0..< length) {
            val random = Random()
            val randomCharsets = random.nextInt(possible.length)
            text += possible.substring(randomCharsets, randomCharsets + 1)
        }
        return text
    }

    fun generateSignatureOAuthV1(httpMethod: String, url: String, parameters: Map<String, String>, secrets: Array<String>): String {
        val parametersString = StringBuilder()
        var first = true
        parameters.entries.stream()
            .sorted(java.util.Map.Entry.comparingByKey())
            .forEachOrdered { (key, value): Map.Entry<String, String> ->
                try {
                    if (!first) {
                        parametersString.append("&")
                    }
//                    val keyEncode = URLEncoder.encode(key, StandardCharsets.UTF_8)
//                    val valueEncode = URLEncoder.encode(value, StandardCharsets.UTF_8)
                    parametersString.append(key).append("=").append(value)
                    first = false
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        val encodedParameters = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            URLEncoder.encode(parametersString.toString(), StandardCharsets.UTF_8)
        } else {
            URLEncoder.encode(parametersString.toString())
        }
        val encodedUrl = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            URLEncoder.encode(url, StandardCharsets.UTF_8)
        } else {
            URLEncoder.encode(url)
        }

        val rawBaseString = httpMethod.uppercase(Locale.getDefault()) + "&" + encodedUrl + "&" + encodedParameters

        first = true
        val secretKeyBuilder = StringBuilder()
        for (secret in secrets) {
            if (!first) {
                secretKeyBuilder.append("&").append(secret)
            } else {
                first = false
                secretKeyBuilder.append(secret)
            }
        }
        val secretKey = SecretKeySpec(secretKeyBuilder.toString().toByteArray(StandardCharsets.UTF_8), "HmacSHA256")
        val sha256Hmac = Mac.getInstance("HmacSHA256")
        sha256Hmac.init(secretKey)
        val signatureBytes = sha256Hmac.doFinal(rawBaseString.toByteArray(StandardCharsets.UTF_8))
        val base64Signature = String(Base64.getEncoder().encode(signatureBytes), StandardCharsets.UTF_8)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            URLEncoder.encode(base64Signature.replace( "\r\n", ""), StandardCharsets.UTF_8)
        } else {
            URLEncoder.encode(base64Signature.replace( "\r\n", ""))
        }
    }

    fun generateCodeChallenge(length: Int): String {
        var text = ""
        val possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~"
        for (i in 0..< length) {
            val random = Random()
            val randomCharsets = random.nextInt(possible.length)
            text += possible.substring(randomCharsets, randomCharsets + 1)
        }
        return text
    }

    fun signatureCodeChallenge(codeVerify: String): String {
        val base64Signature = String(Base64.getEncoder().encode(signatureSHA256(codeVerify).toByteArray(StandardCharsets.UTF_8)), StandardCharsets.UTF_8)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            URLEncoder.encode(base64Signature.replace( "\r\n", ""), StandardCharsets.UTF_8)
        } else {
            URLEncoder.encode(base64Signature.replace( "\r\n", ""))
        }
    }

    fun signatureSHA256(input: String): String {
        try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(input.toByteArray(charset("UTF-8")))
            val hexString = StringBuilder()
            for (b in hash) {
                val hex = Integer.toHexString(0xff and b.toInt())
                if (hex.length == 1) {
                    hexString.append('0')
                }
                hexString.append(hex)
            }
            return hexString.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun encryptMD5(input: String): String {
        try {
            // Create MD5 Hash
            val digest = MessageDigest.getInstance("MD5")
            digest.update(input.toByteArray())
            val messageDigest = digest.digest()


            // Create Hex String
            val hexString = java.lang.StringBuilder()
            for (aMessageDigest in messageDigest) {
                var h = Integer.toHexString(0xFF and aMessageDigest.toInt())
                while (h.length < 2) h = "0$h"
                hexString.append(h)
            }
            return hexString.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }
}