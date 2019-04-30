package io.codelabs.digitutor.data.model

import io.codelabs.digitutor.data.BaseUser
import kotlinx.android.parcel.Parcelize

/**
 * [Tutor] data model class
 */
@Parcelize
data class Tutor(override var email: String?,
                 override var name: String?,
                 override var avatar: String?,
                 override val key: String,
                 var blocked: Boolean = false,
                 var rating: Double = 1.0,
                 override val type: String = BaseUser.Type.TUTOR) : BaseUser {
    constructor() : this("", "", "", "")
}