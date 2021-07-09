package com.smartcity.provider.ui.auth.state

import android.net.Uri
import android.os.Parcelable
import com.smartcity.provider.models.AuthToken
import com.smartcity.provider.models.StoreAddress
import kotlinx.android.parcel.Parcelize

const val AUTH_VIEW_STATE_BUNDLE_KEY = "com.codingwithmitch.openapi.ui.auth.state.AuthViewState"

@Parcelize
data class AuthViewState(
    var registrationFields: RegistrationFields? = RegistrationFields(),

    var loginFields: LoginFields? = LoginFields(),

    var authToken: AuthToken? = null,

    var registrationState: RegistrationState = RegistrationState(),

    var storeFields: StoreFields= StoreFields(),

    var categoryStore: CategoryStore= CategoryStore()

) : Parcelable
@Parcelize
data class RegistrationState(
    var isRegistred: Boolean = false
) : Parcelable

@Parcelize
data class CategoryStore(
    var listCategoryStore: List<String> ?=null
) : Parcelable

@Parcelize
data class StoreFields(
    var store_name: String? = null,
    var store_description: String? = null,
    var store_address: StoreAddress? = null,
    var store_category: List<String>?=null,
    var newImageUri: Uri? = null
) : Parcelable {
    class LoginError {

        companion object{

            fun mustFillAllFields(): String{
                return "You can't create store without an name and description and address."
            }

            fun none():String{
                return "None"
            }

        }
    }
    fun isValidForLogin(): String{

        if(store_name.isNullOrEmpty()
            || store_description.isNullOrEmpty()){

            return LoginError.mustFillAllFields()
        }
        return LoginError.none()
    }

    override fun toString(): String {
        return "LoginState(store_name=$store_name, store_description=$store_description, store_address=$store_address)"
    }
}
@Parcelize
data class RegistrationFields(
    var registration_email: String? = null,
    var registration_username: String? = null,
    var registration_password: String? = null,
    var registration_confirm_password: String? = null
) : Parcelable {

    class RegistrationError {
        companion object{

            fun mustFillAllFields(): String{
                return "All fields are required."
            }

            fun passwordsDoNotMatch(): String{
                return "Passwords must match."
            }

            fun none():String{
                return "None"
            }

        }
    }

    fun isValidForRegistration(): String{
        if(registration_email.isNullOrEmpty()
            || registration_username.isNullOrEmpty()
            || registration_password.isNullOrEmpty()
            || registration_confirm_password.isNullOrEmpty()){
            return RegistrationError.mustFillAllFields()
        }

        if(!registration_password.equals(registration_confirm_password)){
            return RegistrationError.passwordsDoNotMatch()
        }
        return RegistrationError.none()
    }
}

@Parcelize
data class LoginFields(
    var login_email: String? = null,
    var login_password: String? = null
) : Parcelable {
    class LoginError {

        companion object{

            fun mustFillAllFields(): String{
                return "You can't login without an email and password."
            }

            fun none():String{
                return "None"
            }

        }
    }
    fun isValidForLogin(): String{

        if(login_email.isNullOrEmpty()
            || login_password.isNullOrEmpty()){

            return LoginError.mustFillAllFields()
        }
        return LoginError.none()
    }

    override fun toString(): String {
        return "LoginState(email=$login_email, password=$login_password)"
    }
}

