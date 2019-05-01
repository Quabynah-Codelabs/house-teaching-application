package io.codelabs.digitutor.data.model

import io.codelabs.digitutor.data.BaseDataModel
import kotlinx.android.parcel.Parcelize

/**
 * [Parent] requests for [Tutor] data model class
 * todo: add additional fields
 */
@Parcelize
data class Request(
        override var key: String
) : BaseDataModel {
    constructor() : this("")
}