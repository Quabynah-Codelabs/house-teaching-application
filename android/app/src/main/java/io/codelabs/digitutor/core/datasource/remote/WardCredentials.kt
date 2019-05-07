package io.codelabs.digitutor.core.datasource.remote

import androidx.annotation.Nullable

/**
 * [Ward] credentials class
 * Helper class in creating a new ward
 */
data class WardCredentials(var id: String, var name: String,
                           @Nullable var avatar: String? = null)