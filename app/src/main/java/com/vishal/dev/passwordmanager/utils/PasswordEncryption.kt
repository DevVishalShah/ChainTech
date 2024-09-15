package com.vishal.dev.passwordmanager.utils

import android.content.Context
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object PasswordEncryption {
    lateinit var ivValue: ByteArray

    private fun encrypt(context: Context, strToEncrypt: String): ByteArray {
        val plainText = strToEncrypt.toByteArray(Charsets.UTF_8)
        val key = generateKey("KEY")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val cipherText = cipher.doFinal(plainText)
        ivValue = cipher.iv
        return cipherText
    }

    private fun decrypt(context: Context, dataToDecrypt: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        val key = generateKey("KEY")
        cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(ivValue))
        val cipherText = cipher.doFinal(dataToDecrypt)
        buildString(cipherText, "decrypt")
        return cipherText
    }

    private fun buildString(text: ByteArray, status: String): String {
        val sb = StringBuilder()
        for (char in text) {
            sb.append(char.toInt().toChar())
        }
        return sb.toString()
    }

    private fun generateKey(password: String): SecretKeySpec {
        val digest: MessageDigest = MessageDigest.getInstance("SHA-256")
        val bytes = password.toByteArray()
        digest.update(bytes, 0, bytes.size)
        val key = digest.digest()
        val secretKeySpec = SecretKeySpec(key, "AES")
        return secretKeySpec
    }
}