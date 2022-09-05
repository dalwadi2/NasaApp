package dev.harshdalwadi.nasaapp.utils

import android.util.Base64
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


/**
 * Created by: Harsh Dalwadi - Senior Software Engineer
 * Created Date: 23-03-2021
 */
object AESEncyption {
    const val secretKey = "tK5UTui+DPh8lIlBxya5XVsmeDCoUl6vHhdIESMB6sQ="
    const val salt = "QWlGNHNhMTJTQWZ2bGhpV3U=" // base64 decode => AiF4sa12SAfvlhiWu
    const val iv = "bVQzNFNhRkQ1Njc4UUFaWA==" // base64 decode => mT34SaFD5678QAZX

    fun encrypt(strToEncrypt: String): String? {
        try {
            val ivParameterSpec = IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))

            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val spec = PBEKeySpec(secretKey.toCharArray(), Base64.decode(salt, Base64.DEFAULT), 10000, 256)
            val tmp = factory.generateSecret(spec)
            val secretKey = SecretKeySpec(tmp.encoded, "AES")

            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)
            return Base64.encodeToString(cipher.doFinal(strToEncrypt.toByteArray(Charsets.UTF_8)), Base64.DEFAULT)
        } catch (e: Exception) {
            println("Error while encrypting: $e")
        }
        return null
    }

    fun decrypt(strToDecrypt: String): String? {
        try {

            val ivParameterSpec = IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))

            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val spec = PBEKeySpec(secretKey.toCharArray(), Base64.decode(salt, Base64.DEFAULT), 10000, 256)
            val tmp = factory.generateSecret(spec);
            val secretKey = SecretKeySpec(tmp.encoded, "AES")

            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            return String(cipher.doFinal(Base64.decode(strToDecrypt, Base64.DEFAULT)))
        } catch (e: Exception) {
            println("Error while decrypting: $e");
        }
        return null
    }


    var PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\n" +
            "MIIBITANBgkqhkiG9w0BAQEFAAOCAQ4AMIIBCQKCAQBtsFcZDYah707TBHVK2SgK\n" +
            "eU9L2FgLzdejXTP4l4YfJS4kcWabeFbK3sqI2MfqoKiFvf0PPky6kpNomfTTF93E\n" +
            "ZNwswRFIuDksgNZXCSY9WrXwkI6r7/JM4KjMYGQA6J1lz0nVAANMrpMk8eTsqmMM\n" +
            "Dt9cFCfn7ikKJh/xI0MtlpmjWtQkMgH2+Xp5wO5ODmYkpVT5X+334sMhFXHvXxWI\n" +
            "aOYl4inapYVZA6OQdFMxJX5PjP5saEAbhGQVMc7pWiaJmvsJ2D3Qyd9h3l0SILlI\n" +
            "/NUaZRZd+NkR3yGj0SnT0axRvr4WGu0uvfCoAljeLflD0vX/EpcVHTkMv47HZ8Nn\n" +
            "AgMBAAE=\n" +
            "-----END PUBLIC KEY-----"

    fun encryptData(txt: String): String? {
        var encoded: String? = ""
        var encrypted: ByteArray? = null
        val publicKy = PUBLIC_KEY.replace("\\r".toRegex(), "")
            .replace("\\n".toRegex(), "")
            .replace(System.lineSeparator().toRegex(), "")
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")

        try {
            val publicBytes = Base64.decode(publicKy, Base64.DEFAULT)
            val keySpec = X509EncodedKeySpec(publicBytes)
            val keyFactory: KeyFactory = KeyFactory.getInstance("RSA")
            val pubKey: PublicKey = keyFactory.generatePublic(keySpec)
            val cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING") //or try with "RSA"
            cipher.init(Cipher.ENCRYPT_MODE, pubKey)
            encrypted = cipher.doFinal(txt.toByteArray())
            encoded = Base64.encodeToString(encrypted, Base64.DEFAULT)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return encoded
    }

    fun decryptData(txt: String): String? {
        var encoded: String? = ""
        var encrypted: ByteArray? = null
        val publicKy = PUBLIC_KEY.replace("\\r".toRegex(), "")
            .replace("\\n".toRegex(), "")
            .replace(System.lineSeparator().toRegex(), "")
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")

        try {
            val publicBytes = Base64.decode(publicKy, Base64.DEFAULT)
            val keySpec = X509EncodedKeySpec(publicBytes)
            val keyFactory: KeyFactory = KeyFactory.getInstance("RSA")
            val pubKey: PublicKey = keyFactory.generatePublic(keySpec)
            val cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING") //or try with "RSA"
            cipher.init(Cipher.DECRYPT_MODE, pubKey)
            encrypted = cipher.doFinal(txt.toByteArray())
            encoded = Base64.encodeToString(encrypted, Base64.DEFAULT)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            encoded = "Error"
        }
        return encoded
    }
}