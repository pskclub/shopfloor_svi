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
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import pl.aprilapps.switcher.Switcher;
import th.co.svi.shopfloor.R;
import th.co.svi.shopfloor.adapter.JobPlanListAdapter;
import th.co.svi.shopfloor.manager.SelectDB;
import th.co.svi.shopfloor.manager.ShareData;

import static android.support.v4.app.ActivityCompat.invalidateOptionsMenu;


/**
 * Created by nuuneoi on 11/16/2014.
 */
public class PlanFragment extends Fragment {
    private GridView gvPlan;
    private String txtDate = null;
    private String txtDateTo = null;
    private int mYear = 0, mMonth = 0, mDay = 0;
    private JobPlanListAdapter planAdapter = null;
    private List<HashMap<String, String>> planList = null;
    private ShareData member;
    private PlanTask loadPlanAsync;
    private Switcher switcher;
    private boolean first = true;
    private SwipeRefreshLayout layoutRefresh;
    private TextView tvDate;
    private ImageButton dateImg;

    public PlanFragment() {
        super();
    }

    public static PlanFragment newInstance() {
        PlanFragment fragment = new PlanFragment();
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

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_plan, container, false);
        initInstances(rootView, savedInstanceState);
        tvDate.setText(txtDate);
        loadPlan();
        layoutRefresh.setOnRefreshListener(onRefreshListener);
        dateImg.setOnClickListener(dateClick);
        tvDate.setOnClickListener(dateClick);
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
        tvDate = (TextView) rootView.findViewById(R.id.tvDate);
        layoutRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        layoutRefresh.setColorSchemeResources(R.color.colorPrimary);
        dateImg = (ImageButton) rootView.findViewById(R.id.dateImg);
        switcher = new Switcher.Builder(getContext())
                .addContentView(rootView.findViewById(R.id.hide)) //content member
                .addErrorView(rootView.findViewById(R.id.error_view)) //error view member
                .addProgressView(rootView.findViewById(R.id.progress_view)) //progress view member
                .setErrorLabel((TextView) rootView.findViewById(R.id.error_label)) // TextView within your error member group that you want to change
                .setProgressLabel((TextView) rootView.findViewById(R.id.progress_label)) // TextView within your progress member group that you want to change
                .addEmptyView(rootView.findViewById(R.id.empty_view)) //empty placeholder member
                .build();
        member = new ShareData("MEMBER");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance (Fragment level's variables) State here
    }


    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance (Fragment level's variables) State here
    }

    /**********************
     * Listener Zone
     **********************/
    View.OnClickListener dateClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DialogFragment newFragment = new SelectDateFragment();
            newFragment.show(getFragmentManager(), "DatePicker");
        }
    };

    SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            loadPlan();
        }
    };

    private class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

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
            txtDate = (year + "-" + month + "-" + day);
            txtDateTo = (year + "-" + month + "-" + (day + 1));
            mDay = day;
            mMonth = month - 1;
            mYear = year;
            invalidateOptionsMenu(getActivity());
            tvDate.setText(txtDate);
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
