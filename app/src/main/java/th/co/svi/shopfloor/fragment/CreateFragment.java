package th.co.svi.shopfloor.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.otto.Subscribe;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import pl.aprilapps.switcher.Switcher;
import th.co.svi.shopfloor.R;
import th.co.svi.shopfloor.activity.QrCodeActivity;
import th.co.svi.shopfloor.bus.ResultBus;
import th.co.svi.shopfloor.event.ActivityResultEvent;
import th.co.svi.shopfloor.manager.InsertDB;
import th.co.svi.shopfloor.manager.SelectDB;
import th.co.svi.shopfloor.manager.ShareData;


/**
 * Created by nuuneoi on 11/16/2014.
 */
public class CreateFragment extends Fragment {
    private EditText txtID;
    private ImageButton btnSearch;
    private FloatingActionButton fabQrcode;
    private CardView cardContent;
    private ShareData member;
    private TextView txt_workcenter, txt_workorder, txt_plant,
            txt_projectno, txt_orderqty, txt_inputqty;
    private String regis_date;
    private int status_insert = 0;
    private AlertDialog.Builder builder = null;
    private String workcenter, route_operation, workorder, plant, projectno, orderqty;
    private SelectDB select;
    private InsertDB insert;
    private HashMap<String, String> dataMasterResult, dataOperationResult, dataOrderResult;
    private Switcher switcher;
    private StartJobTask loadstartjob;

    public CreateFragment() {
        super();
    }

    public static CreateFragment newInstance() {
        CreateFragment fragment = new CreateFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        init(savedInstanceState);
        member = new ShareData("MEMBER");
        builder = new AlertDialog.Builder(this.getActivity());
        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create, container, false);
        initInstances(rootView, savedInstanceState);

        fabQrcode.setOnClickListener(fabOnClickListener);
        btnSearch.setOnClickListener(btnSearchOnClickListener);
        txtID.setOnEditorActionListener(textOnEditorActionListener);
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
        txtID = (EditText) rootView.findViewById(R.id.txtID);
        btnSearch = (ImageButton) rootView.findViewById(R.id.btnSearch);
        fabQrcode = (FloatingActionButton) rootView.findViewById(R.id.fabQrcode);
        fabQrcode = (FloatingActionButton) rootView.findViewById(R.id.fabQrcode);
        cardContent = (CardView) rootView.findViewById(R.id.cardContent);
        txt_workcenter = (TextView) rootView.findViewById(R.id.txt_workcenter_start);
        txt_workorder = (TextView) rootView.findViewById(R.id.txt_workorder_start);
        txt_plant = (TextView) rootView.findViewById(R.id.txt_plant_start);
        txt_projectno = (TextView) rootView.findViewById(R.id.txt_projectno_start);
        txt_orderqty = (TextView) rootView.findViewById(R.id.txt_orderqty_start);
        txt_inputqty = (TextView) rootView.findViewById(R.id.txt_inputqty_start);
        switcher = new Switcher.Builder(getContext())
                .addContentView(rootView.findViewById(R.id.cardContent)) //content member
                .addErrorView(rootView.findViewById(R.id.error_view)) //error view member
                .addProgressView(rootView.findViewById(R.id.progress_view)) //progress view member
                .setErrorLabel((TextView) rootView.findViewById(R.id.error_label)) // TextView within your error member group that you want to change
                .setProgressLabel((TextView) rootView.findViewById(R.id.progress_label)) // TextView within your progress member group that you want to change
                .build();
        cardContent.setVisibility(View.GONE);
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
        if (result != null && result.getContents() != null) {
            txtID.setText(result.getContents());
            txtID.setSelection(txtID.getText().length());
            startJob();

        }

    }

    private void startJob() {
        workorder = txtID.getText().toString();
        Date d = new Date();
        final CharSequence date = DateFormat.format("yyyy-MM-dd hh:mm:ss", d.getTime());
        regis_date = date.toString();
        if (workorder.equals("")) {
            switcher.showErrorView("Please, input or scan QR Code");
        } else {
            loadstartjob = new StartJobTask();
            loadstartjob.execute((Void) null);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            if (status_insert == 1) {
                Date d = new Date();
                final CharSequence date = DateFormat.format("yyyy-MM-dd hh:mm:ss", d.getTime());
                regis_date = date.toString();
                insert = new InsertDB();
                insert.data_master(workorder, route_operation, workcenter,
                        orderqty, member.getUserID());
                insert.data_tranin(workorder, dataOperationResult.get("route_operation"),
                        workcenter, orderqty, regis_date, member.getUserID(), "-1", "1");
                Toast.makeText(getActivity(), "Start job complete", Toast.LENGTH_SHORT).show();
                getActivity().setResult(1);
                getActivity().finish();
            } else {
                switcher.showErrorView("Please, input or scan QR Code");

            }

        }


        return true;
    }


    /*******
     * listenner Zone
     */

    View.OnClickListener fabOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            IntentIntegrator integrator = new IntentIntegrator(getActivity());
            integrator.setCaptureActivity(QrCodeActivity.class);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
            integrator.setPrompt("Scan QR Code");
            integrator.setOrientationLocked(true);
            integrator.setBeepEnabled(true);
            integrator.initiateScan();
        }
    };

    View.OnClickListener btnSearchOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startJob();
        }
    };

    TextView.OnEditorActionListener textOnEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                startJob();
                return true;
            }
            return false;
        }
    };


    /*******
     * class Zone
     */
    private class StartJobTask extends AsyncTask<Void, Void, Boolean> {
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
            dataMasterResult = select.data_master(workorder);
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
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                txt_workcenter.setText(route_operation + " - " + workcenter);
                txt_workorder.setText(workorder);
                txt_plant.setText(plant);
                txt_projectno.setText(projectno);
                txt_orderqty.setText(orderqty);
                txt_inputqty.setText(orderqty);
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
            loadstartjob = null;
            super.onCancelled();
        }
    }
}
