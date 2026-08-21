package com.seduligma.app.data.local

import android.content.Context
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabasePassphraseProvider @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun getOrCreate(): ByteArray {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        val preferences = EncryptedSharedPreferences.create(
            context,
            "seduligma_secure_preferences",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
        val stored = preferences.getString(PASSPHRASE_KEY, null)
        if (stored != null) return Base64.decode(stored, Base64.NO_WRAP)

        val generated = ByteArray(PASSPHRASE_BYTES).also(SecureRandom()::nextBytes)
        preferences.edit()
            .putString(PASSPHRASE_KEY, Base64.encodeToString(generated, Base64.NO_WRAP))
            .commit()
        return generated
    }

    private companion object {
        const val PASSPHRASE_KEY = "room_sqlcipher_passphrase"
        const val PASSPHRASE_BYTES = 32
    }
}
