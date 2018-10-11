package com.woonza.app.jobtracker;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class MainActivity extends Activity implements JobListFragment.JobListFragmentListener,
        DetailsFragment.DetailsFragmentListener,
        AddEditFragment.AddEditFragmentListener {
    JobListFragment jobListFragment; // displays job list
    // keys for storing row ID in Bundle passed to a fragment
    public static final String ROW_ID = "row_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // return if Activity is being restored, no need to recreate GUI
        if (savedInstanceState != null)
            return;

        // check whether layout contains fragmentContainer (phone layout);
        // JobListFragment is always displayed
        if (findViewById(R.id.fragmentContainer) != null) {
            // create ContactListFragment
            jobListFragment = new JobListFragment();

            // add the fragment to the FrameLayout
            FragmentTransaction transaction =
                    getFragmentManager().beginTransaction();
            transaction.add(R.id.fragmentContainer, jobListFragment);
            transaction.commit(); // causes ContactListFragment to display
        }
    }

    // called when MainActivity resumes
    @Override
    protected void onResume() {
        super.onResume();

        // if contactListFragment is null, activity running on tablet,
        // so get reference from FragmentManager
        if (jobListFragment == null) {
            jobListFragment =
                    (JobListFragment) getFragmentManager().findFragmentById(
                            R.id.jobListFragment);
        }
    }

    @Override
    public void onJobSelected(long rowID) {

        if (findViewById(R.id.fragmentContainer) != null) // phone
            displayJob(rowID, R.id.fragmentContainer);
        else // tablet
        {
            getFragmentManager().popBackStack(); // removes top of back stack
            displayJob(rowID, R.id.rightPaneContainer);
        }
    }

    @Override
    public void onAddJob() {
        if (findViewById(R.id.fragmentContainer) != null)
            displayAddEditFragment(R.id.fragmentContainer, null);
        else
            displayAddEditFragment(R.id.rightPaneContainer, null);
    }

    // display a contact
    private void displayJob(long rowID, int viewID)
    {
        DetailsFragment detailsFragment = new DetailsFragment();

        // specify rowID as an argument to the DetailsFragment
        Bundle arguments = new Bundle();
        arguments.putLong(ROW_ID, rowID);
        detailsFragment.setArguments(arguments);

        // use a FragmentTransaction to display the DetailsFragment
        FragmentTransaction transaction =
                getFragmentManager().beginTransaction();
        transaction.replace(viewID, detailsFragment);
        transaction.addToBackStack(null);
        transaction.commit(); // causes DetailsFragment to display
    }

    // display fragment for adding a new or editing an existing job
    private void displayAddEditFragment(int viewID, Bundle arguments) {
        AddEditFragment addEditFragment = new AddEditFragment();

        if (arguments != null) // editing existing job
            addEditFragment.setArguments(arguments);

        // use a FragmentTransaction to display the AddEditFragment
        FragmentTransaction transaction =
                getFragmentManager().beginTransaction();
        transaction.replace(viewID, addEditFragment);
        transaction.addToBackStack(null);
        transaction.commit(); // causes AddEditFragment to display
    }

    // update GUI after new contact or updated contact saved
    @Override
    public void onAddEditCompleted(long rowID) {
        getFragmentManager().popBackStack(); // removes top of back stack

        if (findViewById(R.id.fragmentContainer) == null) // tablet
        {
            getFragmentManager().popBackStack(); // removes top of back stack
            jobListFragment.updateJobList(); // refresh jobs

            // on tablet, display contact that was just added or edited
            //displayContact(rowID, R.id.rightPaneContainer);
        }
    }

    @Override
    public void onJobDeleted() {

        getFragmentManager().popBackStack(); // removes top of back stack

        if (findViewById(R.id.fragmentContainer) == null) // tablet
            jobListFragment.updateJobList();
    }

    @Override
    public void onJobEdit(Bundle arguments) {

        if (findViewById(R.id.fragmentContainer) != null) // phone
            displayAddEditFragment(R.id.fragmentContainer, arguments);
        else // tablet
            displayAddEditFragment(R.id.rightPaneContainer, arguments);
    }
}
