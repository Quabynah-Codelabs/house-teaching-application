package io.codelabs.digitutor.data.model

import io.codelabs.digitutor.data.BaseUser
import kotlinx.android.parcel.Parcelize

/**
 * [Parent] data model class
 */
@Parcelize
data class Parent(override var email: String?,
                  override var name: String?,
                  override var avatar: String?,
                  override val key: String,
                  var wards: MutableList<String> = mutableListOf(),
                  override val type: String = BaseUser.Type.PARENT) : BaseUser {
    constructor() : this("", "", "", "")
}