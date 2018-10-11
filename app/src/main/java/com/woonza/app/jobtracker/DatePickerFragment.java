package com.woonza.app.jobtracker;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by user on 11/29/2016.
 */

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        //Use the current date as the default date in the date picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        //Create a new DatePickerDialog instance and return it
        /*
            DatePickerDialog Public Constructors - Here we uses first one
            public DatePickerDialog (Context context, DatePickerDialog.OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth)
            public DatePickerDialog (Context context, int theme, DatePickerDialog.OnDateSetListener listener, int year, int monthOfYear, int dayOfMonth)
         */
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }
    public void onDateSet(DatePicker view, int year, int month, int day) {
        //Do something with the date chosen by the user
        TextView tv = (TextView) getActivity().findViewById(R.id.interviewDateEditText);
        String stringOfDate = day + "/" + month + "/" + year;
        tv.setText(stringOfDate);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        TextView tv = (TextView) getActivity().findViewById(R.id.interviewDateEditText);
        String[] trimmedDate = tv.getText().toString().trim().split("\\|");
        Log.d("DatePickerFragment","trimmedDate = " +trimmedDate[0]);
        tv.setText("");
    }
}
