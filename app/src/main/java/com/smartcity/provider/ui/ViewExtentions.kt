package com.smartcity.provider.ui

import android.app.Activity
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.snackbar.Snackbar
import com.smartcity.provider.R
import kotlinx.android.synthetic.main.fragment_create_store.*


fun Activity.displayToast(@StringRes message:Int){
    Toast.makeText(this, message,Toast.LENGTH_LONG).show()
}

fun Activity.displayToast(message:String){
    Toast.makeText(this,message,Toast.LENGTH_LONG).show()
}

fun Activity.displaySnackBar(message:String){
    val snckBar=Snackbar.make(this.window.decorView.findViewById(android.R.id.content),message,Snackbar.LENGTH_SHORT)
    snckBar.setAnchorView(this.findViewById(R.id.bottom_navigation_view) as View)
    snckBar.show()
}

fun Activity.displaySuccessDialog(message: String?){
    MaterialDialog(this)
        .show{
            title(R.string.text_success)
            message(text = message)
            positiveButton(R.string.text_ok)
        }
}

fun Activity.displayErrorDialog(errorMessage: String?){
    MaterialDialog(this)
        .show{
            title(R.string.text_error)
            message(text = errorMessage)
            positiveButton(R.string.text_ok)
        }
}

fun Activity.displayInfoDialog(message: String?){
    MaterialDialog(this)
        .show{
            title(R.string.text_info)
            message(text = message)
            positiveButton(R.string.text_ok)
        }
}

fun Activity.areYouSureDialog(message: String, callback: AreYouSureCallback){
    MaterialDialog(this)
        .show{
            title(R.string.are_you_sure)
            message(text = message)
            negativeButton(R.string.text_cancel){
                callback.cancel()
            }
            positiveButton(R.string.text_yes){
                callback.proceed()
            }
        }
}


interface AreYouSureCallback {

    fun proceed()

    fun cancel()
}









