package me.drblau.money.ui.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Locale;

import me.drblau.money.R;
import me.drblau.money.db.Expense;
import me.drblau.money.ui.AddDialog;

public class BaseFragment extends Fragment {
    private final static String ARG_INDICATOR = "tab_title";
    private final static String ARG_POS = "tab_position";
    private int pos = 0;
    private ExpenseViewModel sharedModel;

    public static BaseFragment newInstance(String title, int pos) {
        Bundle args = new Bundle();
        args.putString(ARG_INDICATOR, title);
        args.putInt(ARG_POS, pos);
        BaseFragment fragment = new BaseFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            pos = getArguments().getInt(ARG_POS);
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(pos == 3) {
            Button btn = view.findViewById(R.id.export);
            btn.setOnClickListener(button -> {
                    int CREATE_FILE = 1;
                    Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType(MimeTypeMap.getSingleton().getMimeTypeFromExtension("json"));
                    intent.putExtra(Intent.EXTRA_TITLE, "money-data.json");

                    startActivityForResult(intent, CREATE_FILE);
            });

            Button imp = view.findViewById(R.id.import_button);
            imp.setOnClickListener(button -> {
                int SELECT_FILE = 2;
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType(MimeTypeMap.getSingleton().getMimeTypeFromExtension("json"));
                startActivityForResult(intent, SELECT_FILE);
            });
        }
        else {
            sharedModel = new ViewModelProvider(requireActivity()).get(ExpenseViewModel.class);
            sharedModel.select(pos);
            //List
            RecyclerView recyclerView = view.findViewById(R.id.item_recycler);
            final ExpenseListAdapter adapter = new ExpenseListAdapter(new ExpenseListAdapter.ExpenseDiff(), sharedModel);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            DividerItemDecoration deco = new DividerItemDecoration(this.getContext(), DividerItemDecoration.VERTICAL);
            deco.setDrawable(getActivity().getDrawable(R.drawable.divider));
            recyclerView.addItemDecoration(deco);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(adapter, getContext()));
            itemTouchHelper.attachToRecyclerView(recyclerView);

            TextView amountView = view.findViewById(R.id.amount_indicator);
            sharedModel.getSelected().observe(getViewLifecycleOwner(), expensesList-> {
                adapter.submitList(expensesList);
                double allExpenses = expensesList
                        .stream()
                        .mapToDouble(expense -> expense.amount)
                        .sum();
                amountView.setText(String.format(Locale.getDefault(), "%.2f", allExpenses));
                //Prettify numbers
                if(allExpenses >= 0) amountView.setTextColor(Color.GREEN);
                else amountView.setTextColor(Color.RED);
                ((TextView) view.findViewById(R.id.euro_sign)).setTextColor(amountView.getCurrentTextColor());

                //Daily Average
                TextView averageView = view.findViewById(R.id.average_indicator);
                int daysOfMonth = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
                double avg = allExpenses / daysOfMonth;
                String average = (avg < 0 ? "" : "+") + String.format(Locale.getDefault(), "%.2f", avg);
                averageView.setText(average);
                //Prettify numbers
                if(avg >= 0) averageView.setTextColor(Color.GREEN);
                else averageView.setTextColor(Color.RED);
                ((TextView) view.findViewById(R.id.euro_sign_avg)).setTextColor(averageView.getCurrentTextColor());
            });

            //FAB
            FloatingActionButton fab = view.findViewById(R.id.action_add);
            fab.setOnClickListener(fabItem -> new AddDialog().showDialog(view, BaseFragment.this, sharedModel));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        sharedModel = new ViewModelProvider(requireActivity()).get(ExpenseViewModel.class);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if(data != null) {
                sharedModel.select(2);
                sharedModel.getSelected().observe(getViewLifecycleOwner(), expenses -> {
                    JSONArray json = new JSONArray();
                    for (int i = 0; i < expenses.size(); i++) {
                        JSONObject curr = new JSONObject();
                        try {
                            curr.put("reason", expenses.get(i).reason);
                            curr.put("amount", expenses.get(i).amount);
                            curr.put("date", String.format("%d.%d.%d", expenses.get(i).day, expenses.get(i).month, expenses.get(i).year));
                            curr.put("description", expenses.get(i).description);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        json.put(curr);
                    }
                    try {
                        FileOutputStream fos = (FileOutputStream) getActivity().getContentResolver().openOutputStream(data.getData());
                        fos.write(json.toString().getBytes(StandardCharsets.UTF_8));
                        fos.flush();
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
        else if(requestCode == 2 && resultCode == Activity.RESULT_OK) {
            if(data != null) {
                System.out.println(data.getData());
                FileInputStream fis;
                StringBuilder builder = new StringBuilder("");
                try {
                    fis = (FileInputStream) getActivity().getContentResolver().openInputStream(data.getData());
                    byte[] buffer = new byte[1024];
                    int n;
                    while((n = fis.read(buffer)) != -1) {
                        builder.append(new String(buffer, 0, n));
                    }
                    JSONArray arr = new JSONArray(builder.toString());
                    for(int i = 0; i < arr.length(); i++) {
                        JSONObject obj = (JSONObject) arr.get(i);
                        String[] date = obj.getString("date").split("\\.");
                        Expense exp = new Expense(
                                obj.getString("reason"),
                                obj.getString("description"),
                                obj.getDouble("amount"),
                                Integer.parseInt(date[0]),
                                Integer.parseInt(date[1]),
                                Integer.parseInt(date[2])
                        );
                        sharedModel.insert(exp);
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(pos == 3) {
            return inflater.inflate(R.layout.fragment_extra, container, false);
        }
        else {
            return inflater.inflate(R.layout.fragment_basic, container, false);
        }
    }

}
