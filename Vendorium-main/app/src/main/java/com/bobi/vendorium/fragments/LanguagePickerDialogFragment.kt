package com.bobi.vendorium.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import com.bobi.vendorium.R
import java.util.Locale


class LanguagePickerDialogFragment : DialogFragment() {

    internal lateinit var listener: NoticeDialogListener

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    interface NoticeDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = context as NoticeDialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException((context.toString() +
                    " must implement NoticeDialogListener"))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.languagePickerTitle)
                .setItems(R.array.languagesArray,
                    DialogInterface.OnClickListener { dialog, which ->
                        Log.d("DEBUG", "Selected item: $which")
                        // The 'which' argument contains the index position
                        // of the selected item
                        changeLanguage(which)
                    })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    fun changeLanguage(languageIndex : Int){
        val res = context!!.resources
        val dm = res.displayMetrics
        val conf = res.configuration

        var languageCode = "en"
        languageCode = when (languageIndex) {
            0 -> "sl"
            1 -> "en"
            else -> "en"
        }

        conf.setLocale(Locale(languageCode.lowercase(Locale.ROOT)))
        res.updateConfiguration(conf, dm)
        listener.onDialogPositiveClick(this)
    }
}