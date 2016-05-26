package th.co.svi.shopfloor.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.otto.Subscribe;

import pl.aprilapps.switcher.Switcher;
import th.co.svi.shopfloor.R;
import th.co.svi.shopfloor.activity.CaptureActivity;
import th.co.svi.shopfloor.bus.ResultBus;
import th.co.svi.shopfloor.event.ActivityResultEvent;
import th.co.svi.shopfloor.manager.SelectDB;


/**
 * Created by nuuneoi on 11/16/2014.
 */
public class ContrainerSearchFragment extends Fragment {
    private EditText txtId;
    private ImageButton btnQrcode, btnSearch;
    private Switcher switcher;
    private DetailTask loadDetailAsync;
    private SelectDB select;
    private String workorder;

    public ContrainerSearchFragment() {
        super();
    }

    public static ContrainerSearchFragment newInstance() {
        ContrainerSearchFragment fragment = new ContrainerSearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);
        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contrainer_search, container, false);
        initInstances(rootView, savedInstanceState);
        btnQrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new IntentIntegrator(getActivity()).setCaptureActivity(CaptureActivity.class).initiateScan();
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDetail();
            }
        });
        return rootView;
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
        txtId = (EditText) rootView.findViewById(R.id.txtID);
        btnQrcode = (ImageButton) rootView.findViewById(R.id.btnQrcode);
        btnSearch = (ImageButton) rootView.findViewById(R.id.btnSearch);
        switcher = new Switcher.Builder(getContext())
                .addContentView(rootView.findViewById(R.id.cardContent)) //content member
                .addErrorView(rootView.findViewById(R.id.error_view)) //error view member
                .addProgressView(rootView.findViewById(R.id.progress_view)) //progress view member
                .setErrorLabel((TextView) rootView.findViewById(R.id.error_label)) // TextView within your error member group that you want to change
                .setProgressLabel((TextView) rootView.findViewById(R.id.progress_label)) // TextView within your progress member group that you want to change
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
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result.getContents() != null) {
            txtId.setText(result.getContents());
            loadDetail();
        }

    }

    private void loadDetail() {
        workorder = txtId.getText().toString();
        if (workorder.equals("")) {
            switcher.showErrorView("Please, input or scan QR Code");
        } else {
            loadDetailAsync = new DetailTask();
            loadDetailAsync.execute((Void) null);
        }

    }

    /**********************
     * inner Class Zone
     **********************/
    private class DetailTask extends AsyncTask<Void, Void, Boolean> {
        String err = null;
        boolean connect = true;

        @Override
        protected void onPreExecute() {
            err = null;
            connect = true;
            switcher.showProgressView("");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            select = new SelectDB();
           /* dataMasterResult = select.data_master(workorder);
            if (dataMasterResult == null) {
                connect = false;
                return false;

            } else {
                if (dataMasterResult.size() > 0) {
                    err = "Sorry! Work Order : " + workorder +
                            " Start job complete.";
                    return false;
                }
                dataOperationResult = select.data_operation(workorder);
                if (dataOperationResult != null) {
                    if (dataOperationResult.size() == 0) {
                        err = "Not found in database (Operation Data) !!!\nPlease, contact administrator (MIS)";
                        return false;
                    }
                    workcenter = dataOperationResult.get("workcenter");
                    route_operation = dataOperationResult.get("route_operation");
                    if (!member.getUserRoute().equals(workcenter)) {
                        err = "Sorry! Work Order : " + workorder +
                                " don\'t pass operation : " + member.getUserRoute();
                        return false;

                    } else {
                        dataOrderResult = select.data_order(workorder);
                        if (dataOrderResult != null) {
                            if (dataOperationResult.size() == 0) {
                                err = "Not found in database (Order Data) !!! \n" +
                                        "Please, contact administrator (MIS)";
                                return false;
                            }
                            status_insert = 1;
                        } else {
                            connect = false;
                            return false;
                        }
                        plant = dataOrderResult.get("plant");
                        projectno = dataOrderResult.get("projectno");
                        orderqty = dataOrderResult.get("orderqty");
                        return true;

                    }
                } else {
                    connect = false;
                    return false;

                }
            }*/
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
           /*     txt_workcenter.setText(route_operation + " - " + workcenter);
                txt_workorder.setText(workorder);
                txt_plant.setText(plant);
                txt_projectno.setText(projectno);
                txt_orderqty.setText(orderqty);
                txt_inputqty.setText(orderqty);*/
                switcher.showContentView();
            } else {
                if (!connect) {
                    switcher.showErrorView("Can't connect to Server");
                } else {
                    switcher.showErrorView(err);
                }
            }
        }

        @Override
        protected void onCancelled() {
            loadDetailAsync = null;
            super.onCancelled();
        }
    }
}
