package com.acesdatasystems.quickcash;

/*
GENERAL IMPORTS
 */
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DetailSalesFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "DetailSalesFragment";
    private Button btnDay, btnMonth, btnWeek;
    private FloatingActionButton fabDate;
    private TextView lblDate;
    private String selectedDate;
    private Spinner searchCategorySpinner;
    private EditText etSearchProduct;
    private Date calendar;
    private Map<Integer,String> monthString;

    public DetailSalesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_detail_sales, container, false);
        btnDay = rootView.findViewById(R.id.btnDay);
        btnMonth = rootView.findViewById(R.id.btnMonth);
        btnWeek = rootView.findViewById(R.id.btnWeek);

        fabDate = rootView.findViewById(R.id.fabDate);
        lblDate = rootView.findViewById(R.id.lblDate);
        searchCategorySpinner = rootView.findViewById(R.id.searchCategorySpinner);
        searchCategorySpinner.setOnItemSelectedListener(this);
        etSearchProduct = rootView.findViewById(R.id.etSearchProduct);

        // sets up calendar and retrieves today's date
        calendar = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(calendar);
        lblDate.setText(formattedDate);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.sales_search_category, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        searchCategorySpinner.setAdapter(adapter);


        // selection of date ---> selectSalesDate();
        selectSalesDate();


        setupMonthString();
        return rootView;

    }

    // selects date for sales data
    public void selectSalesDate(){

        fabDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.sales_date_calender,null,false);
                builder.setView(viewInflated);
                Button btnOk = viewInflated.findViewById(R.id.btnOk);
                final CalendarView calendarView = viewInflated.findViewById(R.id.calendarView);

                calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

                        selectedDate = dayOfMonth+ "-"+monthString.get((month + 1))+"-"+year;
                        lblDate.setText(selectedDate);
                    }
                });

                final AlertDialog alertDialog = builder.create();
                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();
                alertDialog.setCancelable(true);
            }
        });
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String selectedCat =  adapterView.getItemAtPosition(i).toString();
        etSearchProduct.setHint("Search by "+ selectedCat);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /*
    method maintain a key value pair of month literals and corresponding digits
     */

    public void setupMonthString(){
        monthString = new HashMap<>();
        monthString.put(1,"Jan");
        monthString.put(2,"Feb");
        monthString.put(3,"March");
        monthString.put(4,"April");
        monthString.put(5,"May");
        monthString.put(6,"June");
        monthString.put(7,"July");
        monthString.put(8,"Aug");
        monthString.put(9,"Sept");
        monthString.put(10,"Oct");
        monthString.put(11,"Nov");
        monthString.put(12,"Dec");
    }
}
