package th.co.svi.shopfloor.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.otto.Subscribe;

import java.util.Date;
import java.util.HashMap;

import th.co.svi.shopfloor.R;
import th.co.svi.shopfloor.activity.QrCodeActivity;
import th.co.svi.shopfloor.bus.ActivityResultBus;
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
    private TextView txt_error, txt_workcenter, txt_workorder, txt_plant,
            txt_projectno, txt_orderqty, txt_inputqty, txt_starttime;
    private String status_save, regis_date;
    private int status_cmc_insert = 0;
    private AlertDialog.Builder builder = null;
    String workcenter, workcenter_check, route_operation;
    SelectDB select;
    InsertDB insert;
    HashMap<String, String> dataMasterResult, dataOperationResult, dataOrderResult;

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
        btnSearch.setOnClickListener(btnSearchonClickListener);
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
        txt_starttime = (TextView) rootView.findViewById(R.id.txt_starttime_start);
        txt_error = (TextView) rootView.findViewById(R.id.txt_error_start);
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
        ActivityResultBus.getInstance().register(mActivityResultSubscriber);
    }

    @Override
    public void onStop() {
        super.onStop();
        ActivityResultBus.getInstance().unregister(mActivityResultSubscriber);
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
                Toast.makeText(getActivity(), result.getContents(), Toast.LENGTH_SHORT).show();
                txtID.setText(result.getContents());
                txtID.setSelection(txtID.getText().length());
                startJob();

            }
        }
    }

    private void startJob() {
        Date d = new Date();
        final CharSequence date = DateFormat.format("yyyy-MM-dd hh:mm:ss", d.getTime());
        regis_date = date.toString();
        if (txtID.getText().toString().equals("")) {
            Toast.makeText(getActivity(), "Please, input or scan QR Code", Toast.LENGTH_SHORT).show();
        } else {
            select = new SelectDB();
            dataMasterResult = select.data_master(txtID.getText().toString());
            if (dataMasterResult != null) {
                workcenter = dataMasterResult.get("workcenter");
                if (member.getUserRoute().equals("BE")) {
                    workcenter_check = workcenter.substring(0, 2);
                } else if (member.getUserRoute().equals("BI")) {
                    workcenter_check = workcenter.substring(0, 2);
                } else if (member.getUserRoute().equals("QA")) {
                    workcenter_check = workcenter.substring(0, 2);
                } else {
                    workcenter_check = workcenter.substring(0, 4);
                }

                builder.setMessage("Sorry! Work Order : " + txtID.getText().toString() +
                        " Start job complete.\nPlease, input new Work Order");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        txtID.setText("");
                    }
                });
                builder.show();
            } else {
                dataOperationResult = select.data_operation(txtID.getText().toString());
                if (dataOperationResult != null) {
                    workcenter = dataOperationResult.get("Work_Center");
                    route_operation = dataOperationResult.get("Opertion_act");
                    if (member.getUserRoute().equals("BE")) {
                        //workcenter_check = workcenter.substring(0,2);
                        //txt_error.setText("Sorry! Work Order : "+qrcode+" don\'t pass operation : "+ USER_ROUTE);
                        Toast.makeText(getActivity(), "Sorry! Work Order : " + txtID.getText().toString() +
                                " don\'t pass operation : " + member.getUserRoute(), Toast.LENGTH_SHORT).show();

                    } else if (member.getUserRoute().equals("BI")) {
                        //workcenter_check = workcenter.substring(0,2);
                        //txt_error.setText("Sorry! Work Order : "+qrcode+" don\'t pass operation : "+ USER_ROUTE);
                        Toast.makeText(getActivity(), "Sorry! Work Order : " + txtID.getText().toString() +
                                " don\'t pass operation : " + member.getUserRoute(), Toast.LENGTH_SHORT).show();
                    } else if (member.getUserRoute().equals("QA")) {
                        //workcenter_check = workcenter.substring(0,2);
                        //txt_error.setText("Sorry! Work Order : "+qrcode+" don\'t pass operation : "+ USER_ROUTE);
                        Toast.makeText(getActivity(), "Sorry! Work Order : " + txtID.getText().toString() +
                                " don\'t pass operation : " + member.getUserRoute(), Toast.LENGTH_SHORT).show();

                    } else {
                        workcenter_check = workcenter.substring(0, 4);
                        if (workcenter_check.equals(member.getUserRoute()) && member.getUserRoute().equals("CMC1")) {
                            dataOrderResult = select.data_order(txtID.getText().toString());
                            if (dataOrderResult != null) {
                                status_cmc_insert = 1;
                                status_save = "1";
                            } else {
                                //txt_error.setText("Find not found work order : "+qrcode+" in database (Order Data) !!!  Pls., contact administrator (MIS)");
                                Toast.makeText(getActivity(), "Find not found work order : \" + txtID.getText().toString() +\n" +
                                        " \" in database (Order Data) !!! \n" +
                                        "Please, contact administrator (MIS)", Toast.LENGTH_SHORT).show();

                            }

                            txt_workcenter.setText(route_operation + " - " + workcenter);
                            txt_workorder.setText(dataOrderResult.get("workorder"));
                            txt_plant.setText(dataOrderResult.get("plant"));
                            txt_projectno.setText(dataOrderResult.get("projectno"));
                            txt_orderqty.setText(dataOrderResult.get("orderqty"));
                            txt_inputqty.setText(dataOrderResult.get("orderqty"));


                        }
                    }
                } else {
                    //txt_error.setText("Find not found work order : "+qrcode+" in database (Operation Data) !!!  Pls., contact administrator (MIS)");
                    builder.setMessage("Find not found work order : " + txtID.getText().toString() + " in database (Operation Data) !!!\nPlease, contact administrator (MIS)");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            txtID.setText("");
                        }
                    });
                    builder.show();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            if (status_cmc_insert == 1) {
                Date d = new Date();
                final CharSequence date = DateFormat.format("yyyy-MM-dd hh:mm:ss", d.getTime());
                regis_date = date.toString();
                insert = new InsertDB();
                insert.data_master(txtID.getText().toString(), route_operation, workcenter, dataOrderResult.get("workorder"),
                        dataOrderResult.get("orderqty"), member.getUserID());
                insert.data_tranin(txtID.getText().toString(),
                        dataOperationResult.get("route_operation"),
                        dataOperationResult.get("workcenter"),
                        dataOrderResult.get("orderqty"),
                        regis_date,
                        member.getUserID());
                Toast.makeText(getActivity(), "Start job complete", Toast.LENGTH_SHORT).show();
                getActivity().finish();
                Toast.makeText(getActivity(), "save", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Please, input or scan QR Code", Toast.LENGTH_SHORT).show();

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

    View.OnClickListener btnSearchonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startJob();
        }
    };
}
