package dev.harshdalwadi.nasaapp.base

import android.content.DialogInterface
import android.view.View

interface RootView {

    fun showSnackBar(message: String)

    fun showErrorSnackBar()

    fun showSnackBar(message: String, showOk: Boolean)

    fun showSnackBar(view: View, message: String, showOk: Boolean)

    fun showLoader()

    fun hideLoader()

    fun showKeyBoard()

    fun hideKeyBoard()

    fun showDialogWithOneAction(
        title: String?, message: String?,
        positiveButton: String?,
        positiveFunction: (DialogInterface, Int) -> Unit,
    )

    fun showDialogWithTwoActions(
        title: String?, message: String?,
        positiveName: String?, negativeName: String?,
        positiveFunction: (DialogInterface, Int) -> Unit,
        negativeFunction: (DialogInterface, Int) -> Unit,
    )

    fun hideDialog()

}