package me.drblau.money.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import me.drblau.money.R;
import me.drblau.money.db.Expense;
import me.drblau.money.ui.main.ExpenseViewModel;

public class AddDialog {
    private TextInputLayout reason_parent;
    private TextInputLayout date_parent;
    private TextInputLayout amount_parent;
    private TextInputEditText reason;
    private TextInputEditText date;
    private TextInputEditText amount;
    private TextInputEditText description;
    private final HashMap<String, String> errors;
    private final String[] prepop = new String[4];
    public AddDialog() {
        this("", "", "", "");
    }

    //Allows prepopulation
    public AddDialog(String reason, String amount, String date, String description) {
        prepop[0] = reason;
        prepop[1] = amount;
        prepop[2] = date;
        prepop[3] = description;
        errors = new HashMap<>();
    }
    @SuppressLint("ClickableViewAccessibility")
    public void showDialog(View parent, Fragment initiator, ExpenseViewModel model) {
        AlertDialog.Builder builder = new AlertDialog.Builder(initiator.getActivity());
        ViewGroup viewGroup = parent.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_add, viewGroup, false);
        //Access section
        reason_parent = dialogView.findViewById(R.id.reason);
        reason = dialogView.findViewById(R.id.reason_input);
        date_parent = dialogView.findViewById(R.id.date);
        date = dialogView.findViewById(R.id.date_input);
        amount_parent = dialogView.findViewById(R.id.amount);
        amount = dialogView.findViewById(R.id.amount_input);
        description = dialogView.findViewById(R.id.description_input);
        if(hasPrepop()) {
            for(int i = 0; i < prepop.length; i++) {
                String data = prepop[i];
                if(!data.isEmpty()) {
                    switch (i) {
                        case 0:
                            reason.setText(data);
                            break;
                        case 1:
                            amount.setText(data);
                            break;
                        case 2:
                            date.setText(data);
                            break;
                        case 3:
                            description.setText(data);
                            break;
                    }
                }
            }
        }
        if(!errors.isEmpty()) {
            //Descriptions can't have errors
            errors.forEach((field, error) -> {
                switch (field) {
                    case "reason":
                        reason_parent.setError(error);
                        break;
                    case "date":
                        date_parent.setError(error);
                        break;
                    case "amount":
                        amount_parent.setError(error);
                        break;
                }
            });
        }

        //Reason Validation
        reason.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length() > reason_parent.getCounterMaxLength()) {
                    reason_parent.setError(initiator.getString(R.string.error_too_many_chars));
                }
                if(editable.length() <= reason_parent.getCounterMaxLength() && reason_parent.getError() != null) {
                    reason_parent.setError(null);
                }
            }
        });

        //Amount validation
        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length() != 0 && amount_parent.getError() != null) {
                    amount_parent.setError(null);
                }
            }
        });

        //Date Picker
        //Disable user input on date
        date.setInputType(InputType.TYPE_NULL);
        date.setKeyListener(null);

        date.setOnFocusChangeListener((view, focus) -> {
            if(focus) {
                MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText(R.string.select_date)
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();
                datePicker.addOnPositiveButtonClickListener(selection -> {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                    String dateFormat = formatter.format(new Date(Long.parseLong(selection.toString())));
                    date.setText(dateFormat);
                    //Date validation
                    if(date_parent.getError() != null) date_parent.setError(null);
                });
                datePicker.show(initiator.getParentFragmentManager(), "datepicker");
            }
        });

        //Buttons
        builder.setPositiveButton(R.string.dialog_add, (dialogInterface, i) -> {
            //Error handling
            boolean hasError = reason_parent.getError() != null ||
                    amount.getText().toString().equals("") ||
                    date.getText().toString().equals("");
            if(hasError) {
                String[] valErrors = {null, null, null};
                if(reason.getText().toString().isEmpty()) {
                    valErrors[0] = initiator.getString(R.string.error_empty_reason);
                }
                if(amount.getText().toString().isEmpty()) {
                    valErrors[1] = initiator.getString(R.string.error_empty_amount);
                }
                if(date.getText().toString().isEmpty()) {
                    valErrors[2] = initiator.getString(R.string.error_empty_date);
                }
                AddDialog newOne = new AddDialog(reason.getText().toString(), amount.getText().toString(), date.getText().toString(), description.getText().toString());
                for(int j = 0; j < valErrors.length; j++) {
                    if(valErrors[j] != null) {
                        switch (j) {
                            case 0:
                                newOne.setError("reason", valErrors[j]);
                                break;
                            case 1:
                                newOne.setError("amount", valErrors[j]);
                                break;
                            case 2:
                                newOne.setError("date", valErrors[j]);
                                break;
                        }
                    }
                }
                newOne.showDialog(parent, initiator, model);
                return;
            }
            double amountRes = Double.parseDouble(amount.getText().toString());
            String[] dateParts = date.getText().toString().split("\\.");
            Expense expense = new Expense(reason.getText().toString(),
                    description.getText().toString(),
                    amountRes,
                    Integer.parseInt(dateParts[0]), Integer.parseInt(dateParts[1]), Integer.parseInt(dateParts[2]));
            model.insert(expense);
        });

        builder.setNegativeButton(R.string.dialog_cancel, (dialogInterface, i) -> dialogInterface.dismiss());
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void setError(String field, String error) {
        errors.put(field, error);
    }
    private boolean hasPrepop() {
        return !Arrays.stream(prepop).allMatch(el -> el.equals(""));
    }
}
