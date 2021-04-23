package com.felipedeveloper.shareplacesapp.utilities

import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.widget.Toast
import com.felipedeveloper.shareplacesapp.R
import java.util.regex.Matcher
import java.util.regex.Pattern

fun isValidEmail(email: String): Boolean {
    val pattern: Pattern
    val EMAIL_PATTERN =
        "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
    pattern = Pattern.compile(EMAIL_PATTERN)
    val matcher: Matcher = pattern.matcher(email)
    return matcher.matches()
}

fun isValidPassword(password: String): Boolean {
    // Necesita Contener -->    1 Num / 1 Minuscula / 1 Mayuscula / 1 Special / Min Caracteres 4
    //val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$"
    val passwordPattern = "^(?=\\S+$).{6,}$"
    val pattern = Pattern.compile(passwordPattern)
    return pattern.matcher(password).matches()
}

inline fun <reified T : Activity> Activity.goToActivity(noinline init: Intent.() -> Unit = {}) {
    val intent = Intent(this, T::class.java)
    intent.init()
    startActivity(intent)
}


fun Activity.toast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, message, duration).show()

fun Activity.toast(resourceId: Int, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, resourceId, duration).show()

var progressDialog: ProgressDialog? = null

fun Activity.showProgressDialog(message: String) {
    progressDialog = ProgressDialog(this, R.style.AppTheme_Dark_Dialog)
    progressDialog?.isIndeterminate = true
    progressDialog?.setCancelable(false)
    progressDialog?.setMessage(message)
    progressDialog?.show()
}

fun hideProgressDialog() {
    if (progressDialog != null && progressDialog!!.isShowing) {
        progressDialog?.dismiss()
    }
}