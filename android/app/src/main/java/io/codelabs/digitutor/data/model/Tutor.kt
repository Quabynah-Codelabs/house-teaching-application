package io.codelabs.digitutor.data.model

import com.google.firebase.iid.FirebaseInstanceId
import io.codelabs.digitutor.data.BaseUser
import kotlinx.android.parcel.Parcelize

/**
 * [Tutor] data model class
 */
@Parcelize
data class Tutor(override var email: String?,
                 override var name: String?,
                 override var avatar: String?,
                 override var key: String,
                 override var token: String?,
                 var blocked: Boolean = false,
                 var rating: Double = 1.0,
                 override var type: String = BaseUser.Type.TUTOR) : BaseUser {
    constructor() : this("", "", "", "", FirebaseInstanceId.getInstance().token)
}