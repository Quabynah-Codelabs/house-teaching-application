package io.codelabs.digitutor.data

/**
 * Base class for all user data models
 */
interface BaseUser : BaseDataModel {
    var email: String?
    var name: String?
    var avatar: String?
    var type: String
    var token: String?

    /**
     * Stores the user's type
     */
    object Type {
        const val PARENT = "parent"
        const val TUTOR = "tutor"
        const val WARD = "ward"
    }
}