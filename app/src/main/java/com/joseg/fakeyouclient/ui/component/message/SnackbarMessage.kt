package com.joseg.fakeyouclient.ui.component.message

import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.joseg.fakeyouclient.R

object SnackbarMessage {
    fun displayErrorMessage(view: View, message: CharSequence) {
        Snackbar.make(
            view,
            message,
            Snackbar.LENGTH_SHORT
        )
            .setBackgroundTint(ContextCompat.getColor(view.context, R.color.red_2))
            .setTextColor(ContextCompat.getColor(view.context, R.color.white))
            .setAnchorView(view)
            .show()
    }

    fun displaySuccessMessage(view: View, message: CharSequence) {
        Snackbar.make(
            view,
            message,
            Snackbar.LENGTH_SHORT
        )
            .setBackgroundTint(ContextCompat.getColor(view.context, R.color.green))
            .setTextColor(ContextCompat.getColor(view.context, R.color.white))
            .setAnchorView(view)
            .show()
    }
}