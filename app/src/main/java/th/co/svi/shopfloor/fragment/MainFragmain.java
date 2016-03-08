package th.co.svi.shopfloor.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import th.co.svi.shopfloor.R;
import th.co.svi.shopfloor.activity.MainActivity;
import th.co.svi.shopfloor.adapter.JobPlanListAdapter;
import th.co.svi.shopfloor.manager.SelectDB;
import th.co.svi.shopfloor.manager.ShareData;
import th.co.svi.shopfloor.view.JobPlanListItem;


/**
 * Created by nuuneoi on 11/16/2014.
 */
public class MainFragmain extends Fragment {
    private GridView gvPlan;
    String txtDate = null;
    String txtDateTo = null;
    private JobPlanListAdapter planAdapter = null;
    private List<HashMap<String, String>> planList = null;
    private ShareData member;
    private PlanTask loadPlanAsync;

    public MainFragmain() {
        super();
    }

    public static MainFragmain newInstance() {
        MainFragmain fragment = new MainFragmain();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Calendar calObject = Calendar.getInstance();
        calObject.add(Calendar.DAY_OF_YEAR, 0);
        txtDate = new SimpleDateFormat("yyyy-MM-dd").format(calObject.getTime());
        calObject.add(Calendar.DAY_OF_YEAR, 1);
        txtDateTo = new SimpleDateFormat("yyyy-MM-dd").format(calObject.getTime());
        init(savedInstanceState);
        member = new ShareData("MEMBER");
        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        initInstances(rootView, savedInstanceState);
        loadPlan();
        return rootView;
    }

    private void loadPlan() {
        loadPlanAsync = new PlanTask();
        loadPlanAsync.execute((Void) null);

    }

    @SuppressWarnings("UnusedParameters")
    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        // Note: State of variable initialized here could not be saved
        //       in onSavedInstanceState
        gvPlan = (GridView) rootView.findViewById(R.id.gvPlan);
      }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance (Fragment level's variables) State here
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 3) {
            DialogFragment newFragment = new SelectDateFragment();
            newFragment.show(getFragmentManager(), "DatePicker");
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance (Fragment level's variables) State here
    }

    /**********************
     * innerClass Zone
     **********************/
    public class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int yy = calendar.get(Calendar.YEAR);
            int mm = calendar.get(Calendar.MONTH);
            int dd = calendar.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, yy, mm, dd);
        }

        public void onDateSet(DatePicker view, int yy, int mm, int dd) {
            populateSetDate(yy, mm + 1, dd);
        }

        public void populateSetDate(int year, int month, int day) {
            txtDate = (year + "-" + month + "-" + day);
            txtDateTo = (year + "-" + month + "-" + (day+1));
            loadPlan();
        }

    }

    private class PlanTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            SelectDB plan = new SelectDB();
            planList = plan.plan(member.getUserRoute(), txtDate, txtDateTo);
            if (planList != null) {
                return true;
            } else {
                return false;
            }

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            loadPlanAsync = null;

            if (success) {
                planAdapter = new JobPlanListAdapter(planList);
                gvPlan.setAdapter(planAdapter);
            } else {
                Toast.makeText(getContext(), R.string.server_fail, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            loadPlanAsync = null;
            super.onCancelled();
        }
    }
}
