package com.kira.kmpbase.core.common.storage

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.MemScope
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.value
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.CoreFoundation.CFDictionaryCreate
import platform.CoreFoundation.CFDictionaryRef
import platform.CoreFoundation.CFStringRef
import platform.CoreFoundation.CFTypeRef
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFAllocatorDefault
import platform.CoreFoundation.kCFBooleanTrue
import platform.Foundation.CFBridgingRelease
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.errSecDuplicateItem
import platform.Security.errSecItemNotFound
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData
import platform.darwin.OSStatus

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
class IosSecureStorage : SecureStorage {

    private val serviceRef: CFTypeRef? = CFBridgingRetain(SERVICE_NAME)

    override suspend fun getString(key: String): String? = withContext(Dispatchers.Default) {
        cfRetain(key) { cfKey ->
            val result = alloc<CFTypeRefVar>()
            val status = keychainOperation(
                kSecAttrAccount to cfKey,
                kSecReturnData to kCFBooleanTrue,
                kSecMatchLimit to kSecMatchLimitOne,
            ) { SecItemCopyMatching(it, result.ptr) }
            if (status != errSecSuccess) return@withContext null

            val data = CFBridgingRelease(result.value) as? NSData ?: return@withContext null
            data.toKString()
        }
    }

    override suspend fun putString(key: String, value: String) = withContext(Dispatchers.Default) {
        remove(key)
        cfRetain(key, value.toNSData()) { cfKey, cfValue ->
            val status = keychainOperation(
                kSecAttrAccount to cfKey,
                kSecValueData to cfValue,
            ) { SecItemAdd(it, null) }
            status.checkError(errSecDuplicateItem)
        }
        Unit
    }

    override suspend fun remove(key: String) = withContext(Dispatchers.Default) {
        cfRetain(key) { cfKey ->
            val status = keychainOperation(
                kSecAttrAccount to cfKey,
            ) { SecItemDelete(it) }
            status.checkError(errSecItemNotFound)
        }
        Unit
    }

    private inline fun MemScope.keychainOperation(
        vararg input: Pair<CFStringRef?, CFTypeRef?>,
        operation: (query: CFDictionaryRef?) -> OSStatus,
    ): OSStatus {
        val query = cfDictionaryOf(
            mapOf(
                kSecClass to kSecClassGenericPassword,
                kSecAttrService to serviceRef,
            ) + mapOf(*input),
        )
        val output = operation(query)
        CFBridgingRelease(query)
        return output
    }

    private fun MemScope.cfDictionaryOf(map: Map<CFStringRef?, CFTypeRef?>): CFDictionaryRef? {
        val keys = allocArrayOf(*map.keys.toTypedArray())
        val values = allocArrayOf(*map.values.toTypedArray())
        return CFDictionaryCreate(
            kCFAllocatorDefault,
            keys.reinterpret(),
            values.reinterpret(),
            map.size.convert(),
            null,
            null,
        )
    }

    private inline fun <T> cfRetain(value: Any?, block: MemScope.(CFTypeRef?) -> T): T = memScoped {
        val cfValue = CFBridgingRetain(value)
        try {
            block(cfValue)
        } finally {
            CFBridgingRelease(cfValue)
        }
    }

    private inline fun <T> cfRetain(value1: Any?, value2: Any?, block: MemScope.(CFTypeRef?, CFTypeRef?) -> T): T =
        memScoped {
            val cfValue1 = CFBridgingRetain(value1)
            val cfValue2 = CFBridgingRetain(value2)
            try {
                block(cfValue1, cfValue2)
            } finally {
                CFBridgingRelease(cfValue1)
                CFBridgingRelease(cfValue2)
            }
        }

    private fun OSStatus.checkError(vararg expectedErrors: OSStatus) {
        if (this != 0 && this !in expectedErrors) {
            error("Keychain error $this")
        }
    }

    @Suppress("CAST_NEVER_SUCCEEDS")
    private fun NSData.toKString(): String? =
        NSString.create(this, NSUTF8StringEncoding) as String?

    @Suppress("CAST_NEVER_SUCCEEDS")
    private fun String.toNSData(): NSData =
        (this as NSString).dataUsingEncoding(NSUTF8StringEncoding)
            ?: error("Failed to encode secure storage value")

    private companion object {
        const val SERVICE_NAME = "com.kira.kmpbase.secure"
    }
}
