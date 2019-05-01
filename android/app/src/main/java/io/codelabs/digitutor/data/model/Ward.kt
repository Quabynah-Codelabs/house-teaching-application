package io.codelabs.digitutor.data.model

import com.google.firebase.iid.FirebaseInstanceId
import io.codelabs.digitutor.data.BaseUser
import kotlinx.android.parcel.Parcelize


/**
 * [Ward] data model class
 */
@Parcelize
data class Ward(override var email: String?,
                override var name: String?,
                override var avatar: String?,
                override var key: String,
                override var token: String?,
                override var type: String = BaseUser.Type.WARD) : BaseUser {
    constructor() : this("", "", "", "", FirebaseInstanceId.getInstance().token)
}