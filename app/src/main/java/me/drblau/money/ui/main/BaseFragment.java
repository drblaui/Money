package me.drblau.money.ui.main;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.Calendar;
import java.util.Locale;

import me.drblau.money.R;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_basic, container, false);
    }

}
