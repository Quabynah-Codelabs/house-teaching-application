package io.codelabs.digitutor.data.model

import io.codelabs.digitutor.data.BaseDataModel
import kotlinx.android.parcel.Parcelize

/**
 * [Subject] data model class
 */
@Parcelize
data class Subject(
        override val key: String,
        val name: String,
        val description: String
) : BaseDataModel {
    constructor() : this("", "", "")
}