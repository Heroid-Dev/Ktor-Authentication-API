package com.example.security.hashing

interface HashingServices {
    fun createSaltedHash(value:String,saltLength:Int=8):SaltedHash
    fun verifySaltedHash(value:String,saltedHash: SaltedHash):Boolean
}