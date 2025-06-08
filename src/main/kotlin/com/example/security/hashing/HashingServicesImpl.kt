package com.example.security.hashing

import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import java.security.SecureRandom

class HashingServicesImpl : HashingServices {
    override fun createSaltedHash(value: String, saltLength: Int): SaltedHash {
        val salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLength)
        val saltHex = Hex.encodeHexString(salt)
        val hash = DigestUtils.sha256Hex("$saltHex$value$saltHex")
        return SaltedHash(
            hash = hash,
            salt = saltHex
        )
    }

    override fun verifySaltedHash(value: String, saltedHash: SaltedHash): Boolean {
        return DigestUtils.sha256Hex("${saltedHash.salt}$value${saltedHash.salt}") == saltedHash.hash
    }
}