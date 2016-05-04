package th.co.svi.shopfloor.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.format.DateFormat;
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
    private String workcenter, route_operation;
    private SelectDB select;
    private InsertDB insert;
    private HashMap<String, String> dataMasterResult, dataOperationResult, dataOrderResult;
    private Switcher switcher;

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
        Date d = new Date();
        switcher.showProgressView("");
        final CharSequence date = DateFormat.format("yyyy-MM-dd hh:mm:ss", d.getTime());
        regis_date = date.toString();
        if (txtID.getText().toString().equals("")) {
            switcher.showErrorView("Please, input or scan QR Code");
        } else {
            select = new SelectDB();
            dataMasterResult = select.data_master(txtID.getText().toString());
            if (dataMasterResult != null) {
                switcher.showErrorView("Sorry! Work Order : " + txtID.getText().toString() +
                        " Start job complete.\nPlease, input new Work Order");
            } else {
                dataOperationResult = select.data_operation(txtID.getText().toString());
                if (dataOperationResult != null) {
                    workcenter = dataOperationResult.get("workcenter");
                    route_operation = dataOperationResult.get("route_operation");
                    if (!member.getUserRoute().equals(workcenter)) {
                        switcher.showErrorView("Sorry! Work Order : " + txtID.getText().toString() +
                                " don\'t pass operation : " + member.getUserRoute());

                    } else {
                        dataOrderResult = select.data_order(txtID.getText().toString());
                        if (dataOrderResult != null) {
                            status_insert = 1;
                        } else {
                            switcher.showErrorView("Not found work order : \" + txtID.getText().toString() +\n" +
                                    " \" in database (Order Data) !!! \n" +
                                    "Please, contact administrator (MIS)");

                        }
                        switcher.showContentView();
                        txt_workcenter.setText(route_operation + " - " + workcenter);
                        txt_workorder.setText(dataOrderResult.get("workorder"));
                        txt_plant.setText(dataOrderResult.get("plant"));
                        txt_projectno.setText(dataOrderResult.get("projectno"));
                        txt_orderqty.setText(dataOrderResult.get("orderqty"));
                        txt_inputqty.setText(dataOrderResult.get("orderqty"));

                    }
                } else {
                    switcher.showErrorView("Not found work order : " + txtID.getText().toString() +
                            " in database (Operation Data) !!!\nPlease, contact administrator (MIS)");
                }
            }
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
                insert.data_master(txtID.getText().toString(), route_operation, workcenter,
                        dataOrderResult.get("orderqty"), member.getUserID());
                insert.data_tranin(txtID.getText().toString(), dataOperationResult.get("route_operation"),
                        dataOperationResult.get("workcenter"), dataOrderResult.get("orderqty"), regis_date, member.getUserID(), "-1", "1");
                Toast.makeText(getActivity(), "Start job complete", Toast.LENGTH_SHORT).show();
                getActivity().setResult(1);
                getActivity().finish();
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
}
