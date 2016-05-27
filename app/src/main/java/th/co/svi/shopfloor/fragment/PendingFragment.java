package th.co.svi.shopfloor.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.List;

import pl.aprilapps.switcher.Switcher;
import th.co.svi.shopfloor.R;
import th.co.svi.shopfloor.activity.SendActivity;
import th.co.svi.shopfloor.adapter.JobPendingAdapter;
import th.co.svi.shopfloor.bus.ResultBus;
import th.co.svi.shopfloor.event.ActivityResultEvent;
import th.co.svi.shopfloor.manager.SelectDB;
import th.co.svi.shopfloor.manager.ShareData;


/**
 * Created by nuuneoi on 11/16/2014.
 */
public class PendingFragment extends Fragment {
    private ShareData member;
    private GridView gvPlan;
    private SwipeRefreshLayout layoutRefresh;
    private PenddingTask loadPlanAsync;
    private JobPendingAdapter pendingAdapter = null;
    private List<HashMap<String, String>> pendingList = null;
    private Switcher switcher;
    private boolean first = true;

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
        gvPlan.setOnItemClickListener(gridClickListener);
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

    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance (Fragment level's variables) State here
    }

    @Override
    public void onStart() {
        super.onStart();
        ResultBus.getInstance().register(mActivityResultSubscriber);
    }

    @Override
    public void onStop() {
        super.onStop();
        ResultBus.getInstance().unregister(mActivityResultSubscriber);
    }

    private Object mActivityResultSubscriber = new Object() {
        @Subscribe
        public void onActivityResultReceived(ActivityResultEvent event) {
            int requestCode = event.getRequestCode();
            int resultCode = event.getResultCode();
            Intent data = event.getData();
            onActivityResult(requestCode, resultCode, data);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == 1) {
            loadPlan();
        }


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

    AdapterView.OnItemClickListener gridClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent send = new Intent(getActivity(), SendActivity.class);
            send.putExtra("work_order", pendingList.get(position).get("QR_CODE"));
            startActivity(send);
        }
    };

    /**********************
     * inner Class Zone
     **********************/
    private class PenddingTask extends AsyncTask<Void, Void, Boolean> {

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
            pendingList = plan.pending(member.getUserRoute());
            return pendingList != null;

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            loadPlanAsync = null;
            layoutRefresh.setRefreshing(false);
            layoutRefresh.setEnabled(true);
            if (success) {
                if (pendingList.size() == 0) {
                    gvPlan.setAdapter(null);
                    switcher.showEmptyView();
                } else {
                    pendingAdapter = new JobPendingAdapter(pendingList);
                    gvPlan.setAdapter(pendingAdapter);
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
