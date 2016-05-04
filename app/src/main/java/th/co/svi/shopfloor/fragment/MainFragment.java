package th.co.svi.shopfloor.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import java.util.HashMap;
import java.util.List;

import pl.aprilapps.switcher.Switcher;
import th.co.svi.shopfloor.R;
import th.co.svi.shopfloor.activity.MainActivity;
import th.co.svi.shopfloor.adapter.JobPlanListAdapter;
import th.co.svi.shopfloor.manager.SelectDB;
import th.co.svi.shopfloor.manager.ShareData;

import static android.support.v4.app.ActivityCompat.invalidateOptionsMenu;


/**
 * Created by nuuneoi on 11/16/2014.
 */
public class MainFragment extends Fragment {
    private GridView gvPlan;
    String txtDate = null;
    String txtDateTo = null;
    private int mYear = 0, mMonth = 0, mDay = 0;
    private JobPlanListAdapter planAdapter = null;
    private List<HashMap<String, String>> planList = null;
    private ShareData member;
    private PlanTask loadPlanAsync;
    private Switcher switcher;
    private boolean first = true;
    private SwipeRefreshLayout layoutRefresh;

    public MainFragment() {
        super();
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
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
        layoutRefresh.setOnRefreshListener(onRefreshListener);
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
        layoutRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        layoutRefresh.setColorSchemeResources(R.color.colorPrimary);
        switcher = new Switcher.Builder(getContext())
                .addContentView(rootView.findViewById(R.id.hide)) //content member
                .addErrorView(rootView.findViewById(R.id.error_view)) //error view member
                .addProgressView(rootView.findViewById(R.id.progress_view)) //progress view member
                .setErrorLabel((TextView) rootView.findViewById(R.id.error_label)) // TextView within your error member group that you want to change
                .setProgressLabel((TextView) rootView.findViewById(R.id.progress_label)) // TextView within your progress member group that you want to change
                .addEmptyView(rootView.findViewById(R.id.empty_view)) //empty placeholder member
                .build();
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
     * Listener Zone
     **********************/
    SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            loadPlan();
        }
    };

    public class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            if (mYear == 0) {
                return new DatePickerDialog(getActivity(), this, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            } else {
                return new DatePickerDialog(getActivity(), this, mYear,
                        mMonth, mDay);
            }

        }

        public void onDateSet(DatePicker view, int yy, int mm, int dd) {
            populateSetDate(yy, mm + 1, dd);
        }

        public void populateSetDate(int year, int month, int day) {
            String m = (month < 10 ? "0" : "") + month;
            String d = (day < 10 ? "0" : "") + day;
            txtDate = (year + "-" + month + "-" + day);
            txtDateTo = (year + "-" + month + "-" + (day + 1));
            txtDate = (year + "-" + m + "-" + d);
            MainActivity.txtDate = txtDate;
            mDay = day;
            mMonth = month - 1;
            mYear = year;
            invalidateOptionsMenu(getActivity());
            loadPlan();
        }

    }

    private class PlanTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            if (first) {
                layoutRefresh.setEnabled(false);
                switcher.showProgressView("Loading data. Please wait.");
                first = false;
            }

        }

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
        protected void onPostExecute(Boolean success) {
            loadPlanAsync = null;
            layoutRefresh.setRefreshing(false);
            layoutRefresh.setEnabled(true);
            if (success) {
                if (planList.size() == 0) {
                    gvPlan.setAdapter(null);
                    switcher.showEmptyView();
                } else {
                    planAdapter = new JobPlanListAdapter(planList);
                    gvPlan.setAdapter(planAdapter);
                    switcher.showContentView();
                }
            } else {
                gvPlan.setAdapter(null);
                switcher.showErrorView("Can not connect to Server");
            }
        }

        @Override
        protected void onCancelled() {
            loadPlanAsync = null;
            super.onCancelled();
        }
    }
}
