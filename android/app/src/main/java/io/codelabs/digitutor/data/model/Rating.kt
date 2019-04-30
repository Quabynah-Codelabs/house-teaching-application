package io.codelabs.digitutor.data.model

import io.codelabs.digitutor.data.BaseDataModel
import kotlinx.android.parcel.Parcelize

/**
 * [Rating] data model class
 */
@Parcelize
data class Rating(
        override val key: String,
        val parent: String,
        val tutor: String,
        val subject: String,
        var rating: Double
) : BaseDataModel {
    constructor() : this("", "", "", "", 0.0)
}