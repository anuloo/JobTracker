package com.woonza.app.jobtracker;

import android.app.Activity;
import android.app.ListFragment;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * Created by user on 10/18/2016.
 */

public class JobListFragment extends ListFragment {

    // callback methods implemented by MainActivity
    public interface JobListFragmentListener
    {
        // called when user selects a job
        public void onJobSelected(long rowID);

        // called when user decides to add a job
        public void onAddJob();
    }

    private JobListFragmentListener listener;

    private ListView jobListView; // the ListActivity's ListView
    private CursorAdapter jobAdapter; // adapter for ListView

    // set JobListFragmentListener when fragment attached
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        listener = (JobListFragmentListener) activity;
    }

    // remove JobListFragmentListener when Fragment detached
    @Override
    public void onDetach()
    {
        super.onDetach();
        listener = null;
    }

    // called after View is created
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setRetainInstance(true); // save fragment across config changes
        setHasOptionsMenu(true); // this fragment has menu items to display

        // set text to display when there are no jobs
        setEmptyText(getResources().getString(R.string.no_jobs));

        // get ListView reference and configure ListView
        jobListView = getListView();
        jobListView.setOnItemClickListener(viewJobListener);
        jobListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // map each job's title to a TextView in the ListView layout

        String[] from = new String[] { "title", "employer", "date" };
        int[] to = new int[] { R.id.text_jobtitle, R.id.text_employer, R.id.text_date };
        jobAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.list_item, null, from, to, 0);
        setListAdapter(jobAdapter); // set adapter that supplies data
    }

    // responds to the user touching a job's title in the ListView
    AdapterView.OnItemClickListener viewJobListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view,
                                int position, long id)
        {
            listener.onJobSelected(id); // pass selection to MainActivity
        }
    }; // end viewContactListener

    // when fragment resumes, use a GetContactsTask to load contacts
    @Override
    public void onResume()
    {
        super.onResume();
        new GetJobsTask().execute((Object[]) null);
    }

    // performs database query outside GUI thread
    private class GetJobsTask extends AsyncTask<Object, Object, Cursor>
    {
        DatabaseConnector databaseConnector =
                new DatabaseConnector(getActivity());

        // open database and return Cursor for all jobs
        @Override
        protected Cursor doInBackground(Object... params)
        {
            databaseConnector.open();
            return databaseConnector.getAllJobs();
        }

        // use the Cursor returned from the doInBackground method
        @Override
        protected void onPostExecute(Cursor result)
        {
            jobAdapter.changeCursor(result); // set the adapter's Cursor
            databaseConnector.close();
        }
    } // end class GetJobsTask

    // when fragment stops, close Cursor and remove from jobAdapter
    @Override
    public void onStop()
    {
        Cursor cursor = jobAdapter.getCursor(); // get current Cursor
        jobAdapter.changeCursor(null); // adapter now has no Cursor

        if (cursor != null)
            cursor.close(); // release the Cursor's resources

        super.onStop();
    }

    // display this fragment's menu items
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.list_menu_fragment, menu);
    }

    // handle choice from options menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_add:
                listener.onAddJob();
                return true;
        }

        return super.onOptionsItemSelected(item); // call super's method
    }

    // update data set
    public void updateJobList()
    {
        new GetJobsTask().execute((Object[]) null);
    }
}
