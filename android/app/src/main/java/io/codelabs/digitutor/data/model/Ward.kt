package io.codelabs.digitutor.data.model

import io.codelabs.digitutor.data.BaseUser
import kotlinx.android.parcel.Parcelize


/**
 * [Ward] data model class
 */
@Parcelize
data class Ward(override var email: String?,
                override var name: String?,
                override var avatar: String?,
                override val key: String,
                override val type: String = BaseUser.Type.WARD) : BaseUser {
    constructor() : this("", "", "", "")
}