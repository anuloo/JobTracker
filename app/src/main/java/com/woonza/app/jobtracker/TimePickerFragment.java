package com.woonza.app.jobtracker;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by user on 11/29/2016.
 */

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        //Use the current date as the default date in the date picker
        final Calendar c = Calendar.getInstance();
        int hourOfDay = c.get(c.HOUR_OF_DAY); //Current Hour
        int minute = c.get(c.MINUTE); //Current Minute

        //Create a new DatePickerDialog instance and return it
        /*
            DatePickerDialog Public Constructors - Here we uses first one
            public DatePickerDialog (Context context, DatePickerDialog.OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth)
            public DatePickerDialog (Context context, int theme, DatePickerDialog.OnDateSetListener listener, int year, int monthOfYear, int dayOfMonth)
         */
        return new TimePickerDialog(getActivity(), this, hourOfDay, minute, true);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        //Do something with the date chosen by the user
        String strHour = String.valueOf(hourOfDay);
        String strMin = String.valueOf(minute);
        if(strMin.length()==1){
           strMin = '0' + strMin;
        }
        if(strHour.length()==1){
            strHour = '0' + strHour;
        }

        TextView tv = (TextView) getActivity().findViewById(R.id.interviewDateEditText);
        String stringOfTime = strHour + ":" + strMin;
        Log.d("TimerPickerFragment","stringOfTime = " + stringOfTime);
        if(tv.getText().length()!=0)
            tv.setText(tv.getText() + " | " + stringOfTime);
    }
}
