package me.drblau.money.ui

import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import me.drblau.money.R
import me.drblau.money.ui.main.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.Arrays
import java.util.Date
import java.util.Locale

class AddDialog(val reason: String, val amount: String, val date: String, private val description: String) {
    private lateinit var reasonParent : TextInputLayout
    private lateinit var dateParent : TextInputLayout
    private lateinit var amountParent : TextInputLayout
    private lateinit var reasonEdit : TextInputEditText
    private lateinit var dateEdit : TextInputEditText
    private lateinit var amountEdit : TextInputEditText
    private lateinit var descriptionEdit : TextInputEditText
    private var errors : HashMap<String, String> = HashMap()
    private var prepop : Array<String> = arrayOf(reason, amount, date, description)

    constructor() : this("", "", "", "")

    fun showDialog(parent: View, initiator: Fragment, model: ExpenseViewModel) {
        val builder = AlertDialog.Builder(initiator.requireContext())
        val viewGroup : ViewGroup = parent.findViewById(android.R.id.content)
        val dialogView = LayoutInflater.from(parent.context).inflate(R.layout.dialog_add, viewGroup, false)

        reasonParent = dialogView.findViewById(R.id.reason)
        reasonEdit = dialogView.findViewById(R.id.reason_input)
        dateParent = dialogView.findViewById(R.id.date)
        dateEdit = dialogView.findViewById(R.id.date_input)
        amountParent = dialogView.findViewById(R.id.amount)
        amountEdit = dialogView.findViewById(R.id.amount_input)
        descriptionEdit = dialogView.findViewById(R.id.description_input)

        if(hasPrepop()) {
            for(i in 0..3) {
                val data = prepop[i]

                if(data.isNotEmpty()) {
                    when (i) {
                        0 -> reasonEdit.setText(data)
                        1 -> amountEdit.setText(data)
                        2 -> dateEdit.setText(data)
                        3 -> descriptionEdit.setText(data)
                    }
                }
            }
        }

        if(errors.isNotEmpty()) {
            //Descriptions can't have errors
            errors.forEach { (field, error) ->
                when (field) {
                    "reason" -> reasonParent.setError(error)
                    "date" -> dateParent.setError(error)
                    "amount" -> amountParent.setError(error)
                }
            }
        }

        //Amount Validation
        amountEdit.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if(p0!!.isNotEmpty() && amountParent.error != null) {
                    amountParent.error = null
                }
            }
        })

        //Date Picker
        //Disable user input on date
        dateEdit.inputType = InputType.TYPE_NULL
        dateEdit.keyListener = null

        dateEdit.setOnFocusChangeListener { _, focus ->
            if(focus) {
                val datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText(R.string.select_date)
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()

                datePicker.addOnPositiveButtonClickListener { selection ->
                    val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                    val dateFormat = formatter.format(Date(selection.toString().toLong()))

                    dateEdit.setText(dateFormat)

                    if(dateParent.error != null) dateParent.error = null
                }

                datePicker.show(initiator.parentFragmentManager, "datepicker")
            }
        }

        //Buttons
        builder.setPositiveButton("Add") { _, _ ->
            val amountStr = if (amountEdit.text == null) "" else amountEdit.text.toString()
            val dateStr = if (dateEdit.text == null) "" else dateEdit.text.toString()
            val reasonStr = if (reasonEdit.text == null) "" else reasonEdit.text.toString()
            val descStr = if (descriptionEdit.text == null) "" else descriptionEdit.text.toString()

            //Error Handling
            val hasError = reasonParent.error != null || amountStr.isEmpty() || dateStr.isEmpty()

            if(hasError) {
                val valErrors = arrayOfNulls<String>(3)

                if(reasonStr.isEmpty()) {
                    valErrors[0] = initiator.getString(R.string.error_empty_reason)
                }
                if(amountStr.isEmpty()) {
                    valErrors[1] = initiator.getString(R.string.error_empty_amount)
                }
                if(dateStr.isEmpty()) {
                    valErrors[2] = initiator.getString(R.string.error_empty_date)
                }

                val newOne = AddDialog(reasonStr, amountStr, dateStr, descStr)

                for(j in 0..valErrors.size) {
                    if(valErrors[j] != null) {
                        when(j) {
                            0 -> newOne.setError("reason", valErrors[j]!!)
                            1 -> newOne.setError("amount", valErrors[j]!!)
                            2 -> newOne.setError("date", valErrors[j]!!)
                        }
                    }
                }

                newOne.showDialog(parent, initiator, model)
                return@setPositiveButton
            }
            val amountRes = amountStr.toDouble()
            val dateParts = dateStr.split("\\.")

            val expense = Expense(reasonStr, descStr, amountRes, dateParts[0].toInt(), dateParts[1].toInt(), dateParts[2].toInt())
            model.insert(expense)
        }

        builder.setNegativeButton(R.string.dialog_cancel) { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        builder.setView(dialogView)
        val dialog = builder.create()
        dialog.show()
    }

    fun setError(field: String, error: String) {
        errors[field] = error
    }

    private fun hasPrepop(): Boolean {
        return !Arrays.stream(prepop).allMatch { el -> el.equals("") }
    }
}