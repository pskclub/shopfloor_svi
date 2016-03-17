package th.co.svi.shopfloor.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

import th.co.svi.shopfloor.R;
import th.co.svi.shopfloor.adapter.JobPendingAdapter;
import th.co.svi.shopfloor.manager.SelectDB;
import th.co.svi.shopfloor.manager.ShareData;


/**
 * Created by nuuneoi on 11/16/2014.
 */
public class PendingFragment extends Fragment {
    private ShareData member;
    private GridView gvPlan;
    private TextView tvErr;
    private SwipeRefreshLayout layoutRefresh;
    private PenddingTask loadPlanAsync;
    private JobPendingAdapter pendingAdapter = null;
    private List<HashMap<String, String>> pendingList = null;

    public PendingFragment() {
        super();
    }

    public static PendingFragment newInstance() {
        PendingFragment fragment = new PendingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);
        member = new ShareData("MEMBER");
        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pending, container, false);
        initInstances(rootView, savedInstanceState);
        loadPlan();
        layoutRefresh.setOnRefreshListener(onRefreshListener);
        return rootView;
    }

    private void loadPlan() {
        loadPlanAsync = new PenddingTask();
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
        tvErr = (TextView) rootView.findViewById(R.id.tvErr);
        layoutRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        layoutRefresh.setColorSchemeResources(R.color.colorPrimary);
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
    SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            loadPlan();
        }
    };

    /**********************
     * inner Class Zone
     **********************/
    private class PenddingTask extends AsyncTask<Void, Void, Boolean> {
        boolean ERR = false;

        @Override
        protected Boolean doInBackground(Void... params) {
            SelectDB plan = new SelectDB();
            pendingList = plan.pending(member.getUserRoute());
            if (pendingList != null) {
                ERR = true;
                return true;
            } else {
                return false;
            }

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            loadPlanAsync = null;
            layoutRefresh.setRefreshing(false);
            if (success) {
                if (pendingList.size() == 0) {
                    tvErr.setText("NO DATA PENDING");
                    tvErr.setVisibility(View.VISIBLE);
                    pendingAdapter = new JobPendingAdapter(pendingList);
                    gvPlan.setAdapter(pendingAdapter);
                } else {
                    tvErr.setVisibility(View.GONE);
                    pendingAdapter = new JobPendingAdapter(pendingList);
                    gvPlan.setAdapter(pendingAdapter);
                }
            } else {
                tvErr.setText(R.string.server_fail);
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
