package com.hcorp.tuition.userHandler

data class firebasePost(
    var userName: String?, var className: String?, var userGender: String?,
    var userContact: String?, var profilePicture: String
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "userName" to userName,
            "clasName" to className,
            "userGender" to userGender,
            "userContact" to userContact,
            "profilePicture" to profilePicture
        )
    }
}
