package com.zzy.utils

import java.io.ByteArrayOutputStream
import java.security.KeyFactory
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException

/**
 * Created by Jordan on 2022/11/29.
 */
object RSAUtils {

    private const val RSA = "RSA"                                   // 非对称加密密钥算法
    private const val ECB_PKCS1_PADDING = "RSA/ECB/PKCS1Padding"    //加密填充方式
    private const val SIGNATURE_ALGORITHM = "MD5withRSA"    //签名算法
    private const val MAX_ENCRYPT_BLOCK = 117                       //RSA最大加密明文大小
    private const val MAX_DECRYPT_BLOCK = 128                       //RSA最大解密密文大小

    /**
     * 用私钥对信息生成数字签名
     *
     * @param data       已加密数据
     * @param privateKey 私钥(BASE64编码)
     */
    @Throws(Exception::class)
    fun sign(data: ByteArray, privateKey: String): String {
        val keyBytes = Base64Utils.decodeBase64(privateKey)
        val pkcs8KeySpec = PKCS8EncodedKeySpec(keyBytes)
        val privateK = KeyFactory.getInstance(RSA).generatePrivate(pkcs8KeySpec)
        val signature = Signature.getInstance(SIGNATURE_ALGORITHM)
        signature.initSign(privateK)
        signature.update(data)
        return Base64Utils.enCodeString(signature.sign())
    }

    /**
     * 校验数字签名
     *
     * @param data      已加密数据
     * @param publicKey 公钥(BASE64编码)
     * @param sign      数字签名
     */
    @Throws(Exception::class)
    fun verify(data: ByteArray, publicKey: String, sign: String): Boolean {
        val keyBytes = Base64Utils.decodeBase64(publicKey)
        val keySpec = X509EncodedKeySpec(keyBytes)
        val publicK = KeyFactory.getInstance(RSA).generatePublic(keySpec)
        val signature = Signature.getInstance(SIGNATURE_ALGORITHM)
        signature.initVerify(publicK)
        signature.update(data)
        return signature.verify(Base64Utils.decodeBase64(sign))
    }

    /**
     * 公钥加密
     *
     * @param data      源数据
     * @param publicKey 公钥(BASE64编码)
     */
    @Throws(Exception::class)
    fun encryptByPublicKey(data: ByteArray, publicKey: String): ByteArray {
        // 得到公钥
        val keyBytes = Base64Utils.decodeBase64(publicKey)
        val x509KeySpec = X509EncodedKeySpec(keyBytes)
        val publicK = KeyFactory.getInstance(RSA).generatePublic(x509KeySpec)
        // 对数据加密
        val cipher = Cipher.getInstance(ECB_PKCS1_PADDING)
        cipher.init(Cipher.ENCRYPT_MODE, publicK)
        return doFinal(data, cipher, true)
    }

    /**
     * 公钥解密
     *
     * @param encryptedData 已加密数据
     * @param publicKey     公钥(BASE64编码)
     */
    @Throws(Exception::class)
    fun decryptByPublicKey(encryptedData: ByteArray, publicKey: String): ByteArray {
        // 得到公钥
        val keyBytes = Base64Utils.decodeBase64(publicKey)
        val x509KeySpec = X509EncodedKeySpec(keyBytes)
        val publicK = KeyFactory.getInstance(RSA).generatePublic(x509KeySpec)

        // 解密数据
        val cipher = Cipher.getInstance(ECB_PKCS1_PADDING)
        cipher.init(Cipher.DECRYPT_MODE, publicK)
        return doFinal(encryptedData, cipher, false)
    }

    /**
     * 私钥加密
     *
     * @param data       源数据
     * @param privateKey 私钥(BASE64编码)
     */
    @Throws(Exception::class)
    fun encryptByPrivateKey(data: ByteArray, privateKey: String): ByteArray {
        // 得到私钥
        val keyBytes = Base64Utils.decodeBase64(privateKey)
        val keySpec = PKCS8EncodedKeySpec(keyBytes)
        val keyPrivate = KeyFactory.getInstance(RSA).generatePrivate(keySpec)

        //加密数据
        val cipher = Cipher.getInstance(ECB_PKCS1_PADDING)
        cipher.init(Cipher.ENCRYPT_MODE, keyPrivate)
        return doFinal(data, cipher, true)
    }


    /**
     * 私钥解密
     *
     * @param encryptedData 已加密数据
     * @param privateKey    私钥(BASE64编码)
     */
    @Throws(Exception::class)
    fun decryptByPrivateKey(encryptedData: ByteArray, privateKey: String): ByteArray {
        // 得到私钥
        val keyBytes = Base64Utils.decodeBase64(privateKey)
        val keySpec = PKCS8EncodedKeySpec(keyBytes)
        val keyPrivate = KeyFactory.getInstance(RSA).generatePrivate(keySpec)

        // 解密数据
        val cipher = Cipher.getInstance(ECB_PKCS1_PADDING)
        cipher.init(Cipher.DECRYPT_MODE, keyPrivate)
        return doFinal(encryptedData, cipher, false)
    }


    /**
     * 分段加密/解密
     *
     * @param data 已加密数据
     * @param cipher
     * @param isEncryptMode 加密模式/解密模式
     */
    @Throws(
        BadPaddingException::class,
        IllegalBlockSizeException::class,
        java.io.IOException::class
    )
    fun doFinal(data: ByteArray, cipher: Cipher, isEncryptMode: Boolean): ByteArray {
        val inputLen = data.size
        ByteArrayOutputStream().use { out ->
            var offSet = 0
            var cache: ByteArray
            var i = 0
            // 对数据分段解密
            val block = if (isEncryptMode) MAX_ENCRYPT_BLOCK else MAX_DECRYPT_BLOCK
            while (inputLen - offSet > 0) {
                cache = if (inputLen - offSet > block) {
                    cipher.doFinal(data, offSet, block)
                } else {
                    cipher.doFinal(data, offSet, inputLen - offSet)
                }

                out.write(cache, 0, cache.size)
                i++
                offSet = i * block
            }
            return out.toByteArray()
        }
    }
}
