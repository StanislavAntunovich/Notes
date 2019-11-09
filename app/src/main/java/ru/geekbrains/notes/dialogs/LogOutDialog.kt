package ru.geekbrains.notes.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import ru.geekbrains.notes.R

class LogOutDialog : DialogFragment() {
    companion object {
        val TAG = LogOutDialog::class.java.name + "TAG"
        fun create() = LogOutDialog()
    }

    interface LogoutListener {
        fun onLogOut()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        context?.let {
            AlertDialog.Builder(context!!)
                .setTitle(R.string.logout_dialog_title)
                .setMessage(R.string.logout_dialog_message)
                .setPositiveButton(R.string.logout_dialog_btn_positive) { _, _ -> (activity as LogoutListener).onLogOut() }
                .setNegativeButton(R.string.logout_dialog_btn_negative) { _, _ -> dismiss() }
                .create()
        } as Dialog
}