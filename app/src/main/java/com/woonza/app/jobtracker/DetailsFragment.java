package com.woonza.app.jobtracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;
import java.util.Stack;

/**
 * Created by user on 10/30/2016.
 */

public class DetailsFragment extends Fragment {

    private String docPath;
    private String fileExtention;

    // callback methods implemented by MainActivity
    public interface DetailsFragmentListener {
        // called when a job is deleted
        public void onJobDeleted();

        // called to pass Bundle of job's info for editing
        public void onJobEdit(Bundle arguments);
    }

    private DetailsFragmentListener listener;

    private long rowID = -1; // selected job's rowID
    private TextView titleViewText;
    private TextView employerViewText;
    private TextView agencyViewText;
    private TextView agentViewText;
    private TextView phoneViewText;
    private TextView emailViewText;
    private ImageButton jobSpecView;
    private TextView interviewViewText;
    private TextView interviewDateViewText;
    private ImageButton btnPhone;
    private ImageButton btnEmail;

    // set DetailsFragmentListener when fragment attached
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (DetailsFragmentListener) activity;
    }

    // remove DetailsFragmentListener when fragment detached
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    // called when DetailsFragmentListener's view needs to be created
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setRetainInstance(true); // save fragment across config changes

        // if DetailsFragment is being restored, get saved row ID
        if (savedInstanceState != null)
            rowID = savedInstanceState.getLong(MainActivity.ROW_ID);
        else {
            // get Bundle of arguments then extract the job's row ID
            Bundle arguments = getArguments();

            if (arguments != null)
                rowID = arguments.getLong(MainActivity.ROW_ID);
        }

        // inflate DetailsFragment's layout
        View view =
                inflater.inflate(R.layout.fragment_details, container, false);
        setHasOptionsMenu(true); // this fragment has menu items to display

        // get the EditTexts
        titleViewText = (TextView) view.findViewById(R.id.titleViewText);
        employerViewText = (TextView) view.findViewById(R.id.employerViewText);
        agencyViewText = (TextView) view.findViewById(R.id.agencyViewText);
        agentViewText = (TextView) view.findViewById(R.id.agentViewText);
        phoneViewText = (TextView) view.findViewById(R.id.phoneViewText);
        emailViewText = (TextView) view.findViewById(R.id.emailViewText);
        interviewViewText = (TextView) view.findViewById(R.id.interviewViewText);
        interviewDateViewText = (TextView) view.findViewById(R.id.interviewDateViewText);
        jobSpecView = (ImageButton) view.findViewById(R.id.jobSpecView);
        btnPhone = (ImageButton) view.findViewById(R.id.btnNumber);
        btnEmail = (ImageButton) view.findViewById(R.id.btnEmail);
        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String strEmailAddress = emailViewText.getText().toString().trim();
                if(strEmailAddress!=null && strEmailAddress.length()!=0)
                   sendEmail(strEmailAddress,titleViewText.getText().toString());
            }
        });
        btnPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String strPhoneNumber = phoneViewText.getText().toString().trim();
                if(strPhoneNumber!=null && strPhoneNumber.length()!=0)
                    dialContactPhone(strPhoneNumber);
            }
        });
        jobSpecView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!docPath.trim().isEmpty())
                    openDoc();
            }
        });
        return view;
    }

    // called when the DetailsFragment resumes
    @Override
    public void onResume() {
        super.onResume();
        new LoadJobTask().execute(rowID); // load job at rowID
    }

    // save currently displayed job's row ID
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(MainActivity.ROW_ID, rowID);
    }

    // display this fragment's menu items
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_menu_details, menu);
    }

    // handle menu item selections
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                // create Bundle containing job data to edit
                Bundle arguments = new Bundle();
                arguments.putLong(MainActivity.ROW_ID, rowID);
                arguments.putCharSequence("title", titleViewText.getText());
                arguments.putCharSequence("employer", employerViewText.getText());
                arguments.putCharSequence("agency", agencyViewText.getText());
                arguments.putCharSequence("agent", agentViewText.getText());
                arguments.putCharSequence("phone", phoneViewText.getText());
                arguments.putCharSequence("email", emailViewText.getText());
                arguments.putCharSequence("interview", interviewViewText.getText());
                arguments.putCharSequence("interviewdate", interviewDateViewText.getText());
                arguments.putCharSequence("doc", docPath);
                listener.onJobEdit(arguments); // pass Bundle to listener
                return true;
            case R.id.action_delete:
                deleteJob();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void sendEmail(String recipient, String subject){

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:" + recipient));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Dear Sir/Madam!");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send email using..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), "No email clients installed.", Toast.LENGTH_SHORT).show();
        }

    }

    private void dialContactPhone(final String phoneNumber) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
    }

    // performs database query outside GUI thread
    private class LoadJobTask extends AsyncTask<Long, Object, Cursor> {
        DatabaseConnector databaseConnector =
                new DatabaseConnector(getActivity());

        // open database & get Cursor representing specified job's data
        @Override
        protected Cursor doInBackground(Long... params) {
            databaseConnector.open();
            return databaseConnector.getOneJob(params[0]);
        }

        // use the Cursor returned from the doInBackground method
        @Override
        protected void onPostExecute(Cursor result) {
            super.onPostExecute(result);
            result.moveToFirst(); // move to the first item

            // get the column index for each data item
            int titleIndex = result.getColumnIndex("title");
            int employerIndex = result.getColumnIndex("employer");
            int agencyIndex = result.getColumnIndex("agency");
            int agentIndex = result.getColumnIndex("agent");
            int phoneIndex = result.getColumnIndex("phone");
            int emailIndex = result.getColumnIndex("email");
            int interviewIndex = result.getColumnIndex("interview");
            int jobtypeIndex = result.getColumnIndex("jobtype");
            int interviewdateIndex = result.getColumnIndex("interviewdate");
            int jobSpecIndex = result.getColumnIndex("doc");


            // fill TextViews with the retrieved data
            String titleAndjobtype = result.getString(titleIndex) + "(" + result.getString(jobtypeIndex) + ")";
            titleViewText.setText(titleAndjobtype);
           // Log.d("DetailsFragment", "titleIndex = " + titleIndex);
            employerViewText.setText(result.getString(employerIndex));
            agencyViewText.setText(result.getString(agencyIndex));
            agentViewText.setText(result.getString(agentIndex));
            phoneViewText.setText(result.getString(phoneIndex));
            emailViewText.setText(result.getString(emailIndex));
            interviewViewText.setText(result.getString(interviewIndex));
            interviewDateViewText.setText(result.getString(interviewdateIndex));
            //jobSpecViewText.setText(result.getString(jobSpecIndex));
            docPath = result.getString(jobSpecIndex);
            result.close(); // close the result cursor
            databaseConnector.close(); // close database connection
            jobSpecView.setEnabled(false);
            if(docPath!=null) {
                fileExtention = docPath.substring(docPath.lastIndexOf('.') + 1);
                if (fileExtention != null) {
                    if (fileExtention.indexOf('p') != -1) {
                        jobSpecView.setImageResource(R.drawable.pdf_button_icon);
                    } else if (fileExtention.indexOf('o') != -1) {
                        jobSpecView.setImageResource(R.drawable.doc_button_icon);
                    }
                    jobSpecView.setEnabled(true);
                }
            }
            if(phoneViewText.getText().toString().trim().length()==0){
                btnPhone.setEnabled(false);
                btnPhone.setVisibility(View.INVISIBLE);
            }
            if(emailViewText.getText().toString().trim().length()==0){
                btnEmail.setEnabled(false);
                btnEmail.setVisibility(View.INVISIBLE);
            }
        } // end method onPostExecute
    } // end class LoadJobTask

    // Get Result Back
   /* @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case FILE_SELECT_CODE:
                if(resultCode==-1){
                    String FilePath = data.getData().getPath();
                    Log.d("onActivityResult", "FilePath = " + FilePath);
                }
                break;

        }

    }*/
    // delete a job
    private void deleteJob() {
        // use FragmentManager to display the confirmDelete DialogFragment
        //confirmDelete.show(getFragmentManager(), "confirm delete");
        showAlertDialog(getResources().getString(R.string.confirm_title), getResources().getString(R.string.confirm_message));
    }

    private void openDoc() {
        Uri uri = Uri.fromFile(new File(docPath));

        String dataType = "msword";
        if(fileExtention.toLowerCase().indexOf('p')!=-1){
            dataType = "pdf";
        }
        System.out.println("-----------dataType" + dataType);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, "application/" + dataType);
        //intent.setDataAndType(uri, "image/png");
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void onDeleteClick() {
        final DatabaseConnector databaseConnector =
                new DatabaseConnector(getActivity());

        // AsyncTask deletes job and notifies listener
        AsyncTask<Long, Object, Object> deleteTask =
                new AsyncTask<Long, Object, Object>() {
                    @Override
                    protected Object doInBackground(Long... params) {
                        databaseConnector.deleteJob(params[0]);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object result) {
                        listener.onJobDeleted();
                    }
                }; // end new AsyncTask

        // execute the AsyncTask to delete job at rowID
        deleteTask.execute(new Long[]{rowID});
    } // end method onClick

    private void showAlertDialog(String title, String msg) {
        FragmentManager fm = getFragmentManager();
        DeleteDialogFragment alertDialog = DeleteDialogFragment.newInstance(title,msg, new ConfirmOrCancelDialogListener(){
            @Override
            public void onConfirmButtonPressed() {
                onDeleteClick();
            }

            public void onCancelButtonPressed() {
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
            }
        });
        alertDialog.show(fm, "error saving job");
    }

    // DialogFragment to confirm deletion of job
   /* private DialogFragment confirmDelete =
            new DialogFragment() {
                // create an AlertDialog and return it
                @Override
                public Dialog onCreateDialog(Bundle bundle) {
                    // create a new AlertDialog Builder
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(getActivity());

                    builder.setTitle(R.string.confirm_title);
                    builder.setMessage(R.string.confirm_message);

                    // provide an OK button that simply dismisses the dialog
                    builder.setPositiveButton(R.string.button_delete,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(
                                        DialogInterface dialog, int button) {
                                    final DatabaseConnector databaseConnector =
                                            new DatabaseConnector(getActivity());

                                    // AsyncTask deletes job and notifies listener
                                    AsyncTask<Long, Object, Object> deleteTask =
                                            new AsyncTask<Long, Object, Object>() {
                                                @Override
                                                protected Object doInBackground(Long... params) {
                                                    databaseConnector.deleteJob(params[0]);
                                                    return null;
                                                }

                                                @Override
                                                protected void onPostExecute(Object result) {
                                                    listener.onJobDeleted();
                                                }
                                            }; // end new AsyncTask

                                    // execute the AsyncTask to delete job at rowID
                                    deleteTask.execute(new Long[]{rowID});
                                } // end method onClick
                            } // end anonymous inner class
                    ); // end call to method setPositiveButton

                    //builder.setNegativeButton(R.string.button_cancel, null);
                    return builder.create(); // return the AlertDialog
                }
            };*/ // end DialogFragment anonymous inner class
}
