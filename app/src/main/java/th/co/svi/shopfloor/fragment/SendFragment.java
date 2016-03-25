package th.co.svi.shopfloor.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.List;

import th.co.svi.shopfloor.R;
import th.co.svi.shopfloor.activity.QrCodeActivity;
import th.co.svi.shopfloor.bus.ResultBus;
import th.co.svi.shopfloor.event.ActivityResultEvent;
import th.co.svi.shopfloor.manager.SelectDB;
import th.co.svi.shopfloor.manager.ShareData;


/**
 * Created by nuuneoi on 11/16/2014.
 */
public class SendFragment extends Fragment {
    EditText txtID, edt_outputqty;
    TextView txt_workcenter, txt_nextcenter, txt_workorder, txt_plant, txt_project, txt_orderqty,
            txt_inputqty, txt_starttime, txt_finishtime;
    FloatingActionButton fabQrcode;
    ImageButton btnSearch;
    String status_now;
    String status_next;
    boolean status_do = false;
    String status;
    String status_save;
    String qrcode, workcenter, workcenter_check, nextcenter, nextoperation_act, checkcenter, checkoperation_act, operation_act, workorder, plant, project, orderqty, inputqty, outputqty = "0", starttime, finishtime, error;
    String data_operation, data_master, data_operation1, data_tranout, data_tranin, data_tranin_next, data_order, data_output, data_input, data_update_master, data_insert_master, data_check_master;
    float x, qty_output, output, qty_output_total, qty_order, qty_total, qty_check_output;
    int keyin, keyout, qty_input;
    String workcenter_true, nextworkcenter_true;
    String workcenterNext, operation_actNext, workcenter_trueNext;
    int sumTranIn = 0, sumTranOut = 0, sumTranResult = 0;
    ShareData member;
    private AlertDialog.Builder builder = null;

    public SendFragment() {
        super();
    }

    public static SendFragment newInstance(String work_order) {
        SendFragment fragment = new SendFragment();
        Bundle args = new Bundle();
        args.putString("work_order", work_order);
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
        View rootView = inflater.inflate(R.layout.fragment_send, container, false);
        initInstances(rootView, savedInstanceState);
        if (getArguments().getString("work_order") != null) {
            txtID.setText(getArguments().getString("work_order"));
            sendJob(txtID.getText().toString());
        }

        fabQrcode.setOnClickListener(btnOnClickListener);
        btnSearch.setOnClickListener(btnOnClickListener);
        return rootView;
    }

    @SuppressWarnings("UnusedParameters")
    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
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
        if (result != null) {
            if (!(result.getContents() == null)) {
                txtID.setText(result.getContents());
                txtID.setSelection(txtID.getText().length());
                sendJob(txtID.getText().toString());

            }
        }
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        // Note: State of variable initialized here could not be saved
        //       in onSavedInstanceState
        txtID = (EditText) rootView.findViewById(R.id.txtID);
        edt_outputqty = (EditText) rootView.findViewById(R.id.txt_outputqty_send_job);
        txt_workcenter = (TextView) rootView.findViewById(R.id.txt_workcenter_send_job);
        txt_nextcenter = (TextView) rootView.findViewById(R.id.txt_nextworkcenter_send_job);
        txt_workorder = (TextView) rootView.findViewById(R.id.txt_workorder_send_job);
        txt_plant = (TextView) rootView.findViewById(R.id.txt_plant_send_job);
        txt_project = (TextView) rootView.findViewById(R.id.txt_projectno_send_job);
        txt_orderqty = (TextView) rootView.findViewById(R.id.txt_orderqty_send_job);
        txt_inputqty = (TextView) rootView.findViewById(R.id.txt_inputqty_send_job);
        txt_starttime = (TextView) rootView.findViewById(R.id.txt_starttime_send_job);
        txt_finishtime = (TextView) rootView.findViewById(R.id.txt_finishtime_send_job);
        fabQrcode = (FloatingActionButton) rootView.findViewById(R.id.fabQrcode);
        btnSearch = (ImageButton) rootView.findViewById(R.id.btnSearch);
    }

    public void sendJob(final String qrcode) {
        if (qrcode.equals("")) {
            builder.setMessage("Please, input or scan QR Code");
            builder.setPositiveButton("OK", null);
            builder.show();
        }

        qty_total = 0;
        qty_input = 0;
        qty_output = 0;
        qty_order = 0;
        x = 0;
        output = 0;
        outputqty = "0";
        qty_check_output = 0;
        qty_output_total = 0;
        status_next = "0";
        operation_act = "null";
        nextcenter = "null";
        status = "0";
        status_save = "0";

        if (qrcode.equals("")) {
            builder.setMessage("Please, input or scan QR Code");
            builder.setPositiveButton("OK", null);
            builder.show();
        } else { //ELSEIF CHECK QR CODE
            SelectDB select = new SelectDB();
            List<HashMap<String, String>> sapResult = select.sap_data_operation(qrcode);
            if (sapResult != null) {
                int index = 0;
                for (HashMap<String, String> result : sapResult) {
                    workcenter = result.get("workcenter");
                    operation_act = result.get("operation_act");
                    workcenter_true = result.get("workcenter_true");
                    if (member.getUserRoute().equals(workcenter)) { //CHECK USER_ROUTE AND WORK CENTER
                        status_do = true;
                        workcenterNext = sapResult.get(index++ + 1).get("workcenter");
                        operation_actNext = sapResult.get(index++ + 1).get("operation_act");
                        workcenter_trueNext = sapResult.get(index++ + 1).get("workcenter_true");
                        break;
                    } else {
                        workcenter = null;
                        operation_act = null;
                    }// END CHECK USER_ROUTE AND WORK CENTER
                }// END WHILE rs_operation
                List<HashMap<String, String>> tranIn = select.tranIn(qrcode, operation_act, workcenter);
                List<HashMap<String, String>> tranOut = select.tranOut(qrcode, operation_act, workcenter);
                for (HashMap<String, String> result : tranIn) {
                    sumTranIn = sumTranIn + Integer.parseInt(result.get("qty"));
                }// END WHILE rs_operation
                for (HashMap<String, String> result : tranOut) {
                    sumTranOut = sumTranOut + Integer.parseInt(result.get("qty"));

                }// END WHILE rs_operation
                sumTranResult = sumTranIn - sumTranOut;
                HashMap<String, String> orderResult = select.data_order(qrcode);
                if (orderResult == null) {
                    //txt_error.setText("Find not found work order : "+qrcode+" in database (Order Data) !!!  Pls., contact administrator (MIS)");
                    builder.setMessage("Find not found work order : " + qrcode + " in database (Order Data) !!!\nPlease, contact administrator (MIS)");
                    builder.setPositiveButton("OK", null);
                    builder.show();
                }

             
            } else {
                //txt_error.setText("Find not found work order : "+qrcode+" in database (Operation Data) !!!  Pls., contact administrator (MIS)");
                builder.setMessage("Find not found work order : " + qrcode + " in database (Operation Data) !!!\nPlease, contact administrator (MIS)");
                builder.setPositiveButton("OK", null);
                builder.show();
            }
          /*  HashMap<String, String> resultMobileMaster = select.data_master(qrcode, operation_act, workcenter);
            if (resultMobileMaster != null) {
                status_now = resultMobileMaster.get("status_now");
                starttime = resultMobileMaster.get("starttime");
            } else {
                //txt_error.setText("Sorry! Work Order : "+qrcode+" don\'t pass operation : "+workcenter);
                builder.setMessage("Sorry! Work Order : " + qrcode + " don\'t pass operation : " + workcenter);
                builder.setPositiveButton("OK", null);
                builder.show();
            }*/


            //txt_error.setText(workcenter+"  "+operation_act+"\n"+nextcenter+"  "+nextoperation_act);

        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {

        }

        return true;
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

    /*******
     * listenner Zone
     */

    View.OnClickListener btnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.fabQrcode) {
                IntentIntegrator integrator = new IntentIntegrator(getActivity());
                integrator.setCaptureActivity(QrCodeActivity.class);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan QR Code");
                integrator.setOrientationLocked(true);
                integrator.setBeepEnabled(true);
                integrator.initiateScan();
            }
            if (view.getId() == R.id.btnSearch) {
                sendJob(txtID.getText().toString());
            }

        }
    };
}
