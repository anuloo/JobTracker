package com.woonza.app.jobtracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by user on 10/19/2016.
 */

public class AddEditFragment extends Fragment {

    public static final int FILE_SELECT_CODE = 1;
    private static final int REQUEST_PATH = 1;

    String curFileName;
    private String fileExtention;
    // callback method implemented by MainActivity
    public interface AddEditFragmentListener {
        // called after edit completed so job can be redisplayed
        public void onAddEditCompleted(long rowID);
    }

    private AddEditFragmentListener listener;

    private long rowID; // database row ID of the job
    private Bundle jobInfoBundle; // arguments for editing a job

    // EditTexts for contact information
    private EditText titleEditText;
    private EditText employerEditText;
    private EditText agencyEditText;
    private EditText agentEditText;
    private EditText phoneEditText;
    private EditText emailEditText;
    private TextView interviewDateEditText;
    private Spinner stagesSpinner;
    private Spinner jobTypeSpinner;
    private ImageButton btnBrowse;
    private ImageButton btnInterviewDate;
    private ImageButton btnInterviewTime;


    // set AddEditFragmentListener when Fragment attached
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (AddEditFragmentListener) activity;
    }

    // Get Result Back
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PATH){
            if (resultCode == Activity.RESULT_OK) {
                curFileName = data.getStringExtra("GetPath")+"/"+ data.getStringExtra("GetFileName");
                setDocImageToButton();
            }
        }
        /*switch(requestCode){
            case FILE_SELECT_CODE:
                if(resultCode==-1){
                    String FilePath = data.getData().getPath();
                    Log.d("onActivityResult", "FilePath = " + FilePath);
                    docEditText.setText(FilePath);
                }
                break;

        }*/

    }

    private void setDocImageToButton(){
        if(curFileName!=null) {
            fileExtention = curFileName.substring(curFileName.lastIndexOf('.') + 1);
            if (fileExtention != null) {
                if (fileExtention.indexOf('p') != -1) {
                    btnBrowse.setImageResource(R.drawable.pdf_button_icon);
                } else if (fileExtention.indexOf('o') != -1) {
                    btnBrowse.setImageResource(R.drawable.doc_button_icon);
                }

            }
        }
    }

    // remove AddEditFragmentListener when Fragment detached
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    // called when Fragment's view needs to be created
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setRetainInstance(true); // save fragment across config changes
        setHasOptionsMenu(true); // fragment has menu items to display

        ArrayAdapter<CharSequence> adapter;
        ArrayAdapter<CharSequence> jobTypeadapter;

        // inflate GUI and get references to EditTexts
        View view =
                inflater.inflate(R.layout.fragment_add_edit, container, false);
        titleEditText= (EditText) view.findViewById(R.id.titleEditText);
        employerEditText = (EditText) view.findViewById(R.id.employerEditText);
        agencyEditText = (EditText) view.findViewById(R.id.agencyEditText);
        agentEditText = (EditText) view.findViewById(R.id.agentEditText);
        phoneEditText= (EditText) view.findViewById(R.id.phoneEditText);
        emailEditText = (EditText) view.findViewById(R.id.emailEditText);
        interviewDateEditText = (TextView) view.findViewById(R.id.interviewDateEditText);
        stagesSpinner = (Spinner) view.findViewById(R.id.spnStages);
        adapter = ArrayAdapter.createFromResource(container.getContext(),R.array.interview_types,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stagesSpinner.setAdapter(adapter);

        jobTypeSpinner = (Spinner) view.findViewById(R.id.spnJobType);
        jobTypeadapter = ArrayAdapter.createFromResource(container.getContext(),R.array.job_types,android.R.layout.simple_spinner_item);
        jobTypeadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jobTypeSpinner.setAdapter(jobTypeadapter);

        btnBrowse = (ImageButton) view.findViewById(R.id.btnBrowse);
        btnInterviewDate = (ImageButton) view.findViewById(R.id.btnInterviewDate);
        btnInterviewTime = (ImageButton) view.findViewById(R.id.btnInterviewTime);
        btnInterviewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = (TextView) getActivity().findViewById(R.id.interviewDateEditText);
                String[] trimmedDate = tv.getText().toString().trim().split("\\|");
                String strDate = "";
                if(trimmedDate.length>1) {
                    strDate = trimmedDate[1].toString().trim();
                }
                tv.setText(strDate);
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(),"Set your Interview date");
            }
        });

        btnInterviewTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = (TextView) getActivity().findViewById(R.id.interviewDateEditText);
                String[] trimmedDate = tv.getText().toString().trim().split("\\|");
                tv.setText(trimmedDate[0].toString().trim());
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getFragmentManager(),"Set your Interview time");
            }
        });
        btnBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getfile();
            }
        });

        jobInfoBundle = getArguments(); // null if creating new job

        if (jobInfoBundle != null) {
            rowID = jobInfoBundle.getLong(MainActivity.ROW_ID);
            String data = jobInfoBundle.getString("title");
            String[] trimmedTitle = data.split("\\(");
            //Log.d("AddEditFragment","items length = " + items[0]);
            titleEditText.setText(trimmedTitle[0].toString().trim());
            employerEditText.setText(jobInfoBundle.getString("employer"));
            agencyEditText.setText(jobInfoBundle.getString("agency"));
            agentEditText.setText(jobInfoBundle.getString("agent"));
            phoneEditText.setText(jobInfoBundle.getString("phone"));
            emailEditText.setText(jobInfoBundle.getString("email"));
            interviewDateEditText.setText(jobInfoBundle.getString("interviewdate"));
            curFileName = jobInfoBundle.getString("doc");
            setDocImageToButton();
            selectSpinnerValue(stagesSpinner,jobInfoBundle.getString("interview"));
            selectSpinnerValue(jobTypeSpinner,jobInfoBundle.getString("jobtype"));
        }
        return view;
    }

    private void selectSpinnerValue(Spinner spinner, String myString)
    {
        int index = 0;
        for(int i = 0; i < spinner.getCount(); i++){
            if(spinner.getItemAtPosition(i).toString().equals(myString)){
                spinner.setSelection(i);
                break;
            }
        }
    }

    public void getfile(){
        Intent intent1 = new Intent(getActivity(),FileChooser.class);
        startActivityForResult(intent1,REQUEST_PATH);
    }

    private void selectDoc(){
        boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        Intent intent;
        if (isKitKat) {
            intent = new Intent();
            //intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent,FILE_SELECT_CODE);
            //startActivityForResult(Intent.createChooser(intent, "DEMO"),1001);
        } else {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent,FILE_SELECT_CODE);
        }
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                .matches();
    }

    private void onMenuSave() {
        if (titleEditText.getText().toString().trim().length() != 0) {
            // AsyncTask to save job, then notify listener
            if(employerEditText.getText().toString().trim().length()==0){
                employerEditText.setText("Unkown");
            }
            if(emailEditText.getText().toString().trim().length()!=0){
                if(!isEmailValid(emailEditText.getText().toString().trim())){
                    emailEditText.setTextColor(Color.parseColor("#ff0000"));
                    showAlertDialog("Warning", getResources().getString(R.string.error_email));
                    return;
                }
            }
            AsyncTask<Object, Object, Object> saveContactTask =
                    new AsyncTask<Object, Object, Object>() {
                        @Override
                        protected Object doInBackground(Object... params) {
                            saveContact(); // save job to the database
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Object result) {
                            // hide soft keyboard
                            InputMethodManager imm = (InputMethodManager)
                                    getActivity().getSystemService(
                                            Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(
                                    getView().getWindowToken(), 0);

                            listener.onAddEditCompleted(rowID);
                        }
                    }; // end AsyncTask

            // save the contact to the database using a separate thread
            saveContactTask.execute((Object[]) null);
        } else // required job title is blank, so display error dialog
        {
            showAlertDialog("Warning", getResources().getString(R.string.error_message));
        }
    } // end method onClick


    private void showAlertDialog(String title, String msg) {
        FragmentManager fm = getFragmentManager();
        CustomDialogFragment alertDialog = CustomDialogFragment.newInstance(title,msg);
        alertDialog.show(fm, "error saving job");
    }

    // display this fragment's menu items
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_save_job_menu, menu);
    }

    // handle choice from options menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_save:
                onMenuSave();
                return true;
        }

        return super.onOptionsItemSelected(item); // call super's method
    }

    // saves contact information to the database
    private void saveContact() {
        // get DatabaseConnector to interact with the SQLite database
        //String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("dd-MM-yyy HH:mm");
        date.setTimeZone(TimeZone.getTimeZone("GMT"));
        String localTime = date.format(currentLocalTime);
        System.out.println(localTime);

        DatabaseConnector databaseConnector =
                new DatabaseConnector(getActivity());

        if (jobInfoBundle == null) {
            // insert the contact information into the database
            rowID = databaseConnector.insertJob(
                    titleEditText.getText().toString(),
                    employerEditText.getText().toString(),
                    agencyEditText.getText().toString(),
                    agentEditText.getText().toString(),
                    phoneEditText.getText().toString(),
                    emailEditText.getText().toString(),
                    stagesSpinner.getSelectedItem().toString(),
                    jobTypeSpinner.getSelectedItem().toString(),
                    interviewDateEditText.getText().toString(),
                   curFileName,localTime);
        } else {
            databaseConnector.updateJob(rowID,
                    titleEditText.getText().toString(),
                    employerEditText.getText().toString(),
                    agencyEditText.getText().toString(),
                    agentEditText.getText().toString(),
                    phoneEditText.getText().toString(),
                    emailEditText.getText().toString(),
                    stagesSpinner.getSelectedItem().toString(),
                    jobTypeSpinner.getSelectedItem().toString(),
                    interviewDateEditText.getText().toString(),
                    curFileName);
        }
    } // end method saveContact

}
