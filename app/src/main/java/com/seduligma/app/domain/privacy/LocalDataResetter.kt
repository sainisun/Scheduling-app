package com.seduligma.app.domain.privacy

/** Clears user-owned encrypted local scheduler content after explicit confirmation. */
interface LocalDataResetter {
    suspend fun eraseLocalData()
}
