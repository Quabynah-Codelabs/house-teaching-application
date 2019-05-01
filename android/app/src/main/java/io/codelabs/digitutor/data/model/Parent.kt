package io.codelabs.digitutor.data.model

import com.google.firebase.iid.FirebaseInstanceId
import io.codelabs.digitutor.data.BaseUser
import kotlinx.android.parcel.Parcelize

/**
 * [Parent] data model class
 */
@Parcelize
data class Parent(override var email: String?,
                  override var name: String?,
                  override var avatar: String?,
                  override var key: String,
                  override var token: String?,
                  var wards: MutableList<String> = mutableListOf(),
                  override var type: String = BaseUser.Type.PARENT) : BaseUser {
    constructor() : this("", "", "", "", FirebaseInstanceId.getInstance().token)
}