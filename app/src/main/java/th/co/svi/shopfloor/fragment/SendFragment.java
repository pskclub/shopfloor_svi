package th.co.svi.shopfloor.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import th.co.svi.shopfloor.adapter.JobPlanListAdapter;
import th.co.svi.shopfloor.bus.ResultBus;
import th.co.svi.shopfloor.event.ActivityResultEvent;
import th.co.svi.shopfloor.manager.InsertDB;
import th.co.svi.shopfloor.manager.SelectDB;
import th.co.svi.shopfloor.manager.ShareData;
import th.co.svi.shopfloor.manager.UpdateDB;



public class SendFragment extends Fragment {
    TextView txt_workcenter, txt_nextcenter, txt_workorder, txt_plant, txt_project, txt_orderqty,
            txt_inputqty, txt_starttime, txt_contrainer;
    FloatingActionButton fabQrcode;
    ImageButton btnSearch;
    CardView cardContent;
    EditText txtID, edt_outputqty;
    boolean status_do = false, btnsave = false;
    String workcenter = null, operation_act = null, workorder = null, plant = null, project = null, orderqty = null,
            starttime = null, workcenterNext = null, workcenter_true = null, operation_actNext = null, workcenter_trueNext = null, contrainer = null;
    int sumTranIn = 0, sumTranOut = 0, sumTranResult = 0, itemKeyIn = 0, itemKeyOut = 0, outputqty = 0;
    ShareData member;
    SelectDB select;
    UpdateDB update;
    InsertDB insert;
    private SendJobTask loadsendjob;
    private AlertDialog.Builder builder = null;
    HashMap<String, String> orderResult = null;
    private Switcher switcher;
    private ProgressDialog progress;

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
        setHasOptionsMenu(true);
        workorder = getArguments().getString("work_order");
        builder = new AlertDialog.Builder(this.getActivity());
        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_send, container, false);
        initInstances(rootView, savedInstanceState);
        if (workorder != null) {
            txtID.setText(workorder);
            sendJob();
        }

        fabQrcode.setOnClickListener(btnOnClickListener);
        btnSearch.setOnClickListener(btnOnClickListener);
        txtID.setOnEditorActionListener(textViewEditerListener);
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
                sendJob();

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
        cardContent = (CardView) rootView.findViewById(R.id.cardContent);
        txt_workcenter = (TextView) rootView.findViewById(R.id.txt_workcenter_send_job);
        txt_nextcenter = (TextView) rootView.findViewById(R.id.txt_nextworkcenter_send_job);
        txt_workorder = (TextView) rootView.findViewById(R.id.txt_workorder_send_job);
        txt_plant = (TextView) rootView.findViewById(R.id.txt_plant_send_job);
        txt_project = (TextView) rootView.findViewById(R.id.txt_projectno_send_job);
        txt_orderqty = (TextView) rootView.findViewById(R.id.txt_orderqty_send_job);
        txt_inputqty = (TextView) rootView.findViewById(R.id.txt_inputqty_send_job);
        txt_starttime = (TextView) rootView.findViewById(R.id.txt_starttime_send_job);
        txt_contrainer = (TextView) rootView.findViewById(R.id.txt_contrainer);
        fabQrcode = (FloatingActionButton) rootView.findViewById(R.id.fabQrcode);
        btnSearch = (ImageButton) rootView.findViewById(R.id.btnSearch);
        switcher = new Switcher.Builder(getContext())
                .addContentView(rootView.findViewById(R.id.cardContent)) //content member
                .addErrorView(rootView.findViewById(R.id.error_view)) //error view member
                .addProgressView(rootView.findViewById(R.id.progress_view)) //progress view member
                .setErrorLabel((TextView) rootView.findViewById(R.id.error_label)) // TextView within your error member group that you want to change
                .setProgressLabel((TextView) rootView.findViewById(R.id.progress_label)) // TextView within your progress member group that you want to change
                .build();
        cardContent.setVisibility(View.GONE);
        member = new ShareData("MEMBER");
        select = new SelectDB();
        insert = new InsertDB();
        update = new UpdateDB();
    }

    public void sendJob() {
        workorder = txtID.getText().toString();
        operation_act = null;
        status_do = false;
        btnsave = false;
        sumTranIn = 0;
        sumTranOut = 0;
        sumTranResult = 0;
        itemKeyIn = 0;
        itemKeyOut = 0;
        workcenter = null;
        operation_act = null;
        plant = null;
        project = null;
        orderqty = null;
        starttime = null;
        workcenterNext = null;
        workcenter_true = null;
        operation_actNext = null;
        workcenter_trueNext = null;

        if (workorder.equals("") || workorder == null) {
            switcher.showErrorView("Please, input or scan QR Code");
        } else { //ELSEIF CHECK QR CODE
            loadsendjob = new SendJobTask();
            loadsendjob.execute((Void) null);

        }


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            if (btnsave) {
                contrainer = txt_contrainer.getText().toString();
                outputqty = Integer.parseInt(edt_outputqty.getText().toString());
                sumTranIn = 0;
                sumTranOut = 0;
                sumTranResult = 0;
                Date d = new Date();
                final CharSequence date = DateFormat.format("yyyy-MM-dd hh:mm:ss", d.getTime());
                HashMap<String, String> resultMobileMaster = select.data_master(workorder, operation_act, workcenter);
                if (resultMobileMaster.get("status_now").equals("9")) {
                    builder.setMessage("งานนี้ทำเสร็จอยู่แล้ว ไม่สามารถส่งได้");
                    builder.setPositiveButton("OK", null);
                    builder.show();
                } else {
                    List<HashMap<String, String>> tranIn = select.tranIn(workorder, operation_act, workcenter);
                    List<HashMap<String, String>> tranOut = select.tranOut(workorder, operation_act, workcenter);
                    for (HashMap<String, String> resultx : tranIn) {
                        sumTranIn = sumTranIn + Integer.parseInt(resultx.get("qty"));
                    }// END WHILE rs_operation
                    for (HashMap<String, String> result : tranOut) {
                        sumTranOut = sumTranOut + Integer.parseInt(result.get("qty"));

                    }// END WHILE rs_operation
                    sumTranResult = sumTranIn - sumTranOut;
                    if (outputqty > sumTranResult) {

                        builder.setMessage("จำนวนเกิน");
                        builder.setPositiveButton("OK", null);
                        builder.show();
                        return false;
                    } else if (sumTranResult == 0) {
                        builder.setMessage("ส่งครบแล้ว");
                        builder.setPositiveButton("OK", null);
                        builder.show();
                        return false;
                    } else {
                        if (workcenterNext != null) {
                            itemKeyIn = select.countItemKeyIn(workorder, operation_actNext, workcenterNext);
                            insert.data_tranin(workorder, operation_actNext, workcenterNext, String.valueOf(outputqty),
                                    date.toString(), member.getUserID(), contrainer, String.valueOf((itemKeyIn + 1)));
                        /*    itemKeyOut = select.countItemKeyOut(workorder, operation_act, member.getUserRoute());
                            insert.data_tranout(workorder, operation_act, member.getUserRoute(), edt_outputqty.getText().toString(), date.toString(),
                                    member.getUserID(), txt_contrainer.getText().toString(), String.valueOf((itemKeyOut + 1)), "0");*/
                            HashMap<String, String> resultMobileMasternext = select.data_master(workorder, operation_actNext, workcenterNext);
                            if (resultMobileMasternext.size() == 0) {
                                insert.data_master(workorder, operation_actNext, workcenterNext, orderqty, member.getUserID());
                            }
                        }
                     /*   itemKeyIn = select.countItemKeyIn(workorder, operation_act, member.getUserRoute());
                        insert.data_tranin(workorder, operation_actNext, workcenterNext, edt_outputqty.getText().toString(),
                                date.toString(), member.getUserID(), txt_contrainer.getText().toString(), String.valueOf(itemKeyIn + 1));*/
                        itemKeyOut = select.countItemKeyOut(workorder, operation_act, member.getUserRoute());
                        insert.data_tranout(workorder, operation_act, member.getUserRoute(), String.valueOf(outputqty), date.toString(),
                                member.getUserID(), contrainer, String.valueOf((itemKeyOut + 1)), "0");


                        if (resultMobileMaster.get("qty_wo").equals(Integer.toString(sumTranOut + outputqty))) {
                            update.dataMaster(workorder, operation_act, workcenter, "9", date.toString(), member.getUserID());
                        } else {
                            update.dataMaster(workorder, operation_act, workcenter, "0", null, member.getUserID());
                        }


                    }
                    Toast.makeText(getContext(), "Send Success!!", Toast.LENGTH_SHORT).show();
                    getActivity().setResult(1);
                    getActivity().finish();
                }

            } else {
                builder.setMessage("can't save");
                builder.setPositiveButton("OK", null);
                builder.show();
            }

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
                sendJob();
            }

        }
    };

    TextView.OnEditorActionListener textViewEditerListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
            if (id == EditorInfo.IME_ACTION_SEARCH ||
                    id == EditorInfo.IME_ACTION_DONE ||
                    keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                            keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                sendJob();
                return true;
            }
            return false;
        }
    };

    /*******
     * class Zone
     */
    private class SendJobTask extends AsyncTask<Void, Void, Boolean> {
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
            List<HashMap<String, String>> sapResult = select.sap_data_operation(workorder);
            if (sapResult != null) {
                if (sapResult.size() == 0) {
                    err = "Not found in database (Operation Data) !!!\nPlease, contact administrator (MIS)000";
                    return false;
                }
                int index = 0;
                for (HashMap<String, String> result : sapResult) {
                    workcenter = result.get("workcenter");
                    operation_act = result.get("operation_act");
                    workcenter_true = result.get("workcenter_true");
                    if (member.getUserRoute().equals(workcenter)) { //CHECK USER_ROUTE AND WORK CENTER
                        int index2 = index + 1;
                        status_do = true;
                        try {
                            workcenterNext = sapResult.get(index2).get("workcenter");
                            operation_actNext = sapResult.get(index2).get("operation_act");
                            workcenter_trueNext = sapResult.get(index2).get("workcenter_true");
                        } catch (RuntimeException e) {
                            Log.e("null", e.getMessage());
                            workcenterNext = null;
                            operation_actNext = null;
                            workcenter_trueNext = null;
                        }

                        break;
                    } else {
                        workcenter = null;
                        operation_act = null;
                        workcenter_true = null;
                    }// END CHECK USER_ROUTE AND WORK CENTER
                    index = index + 1;
                }// END WHILE rs_operation
                if (status_do) {
                    List<HashMap<String, String>> tranIn = select.tranIn(workorder, operation_act, workcenter);
                    List<HashMap<String, String>> tranOut = select.tranOut(workorder, operation_act, workcenter);
                    HashMap<String, String> resultMobileMaster = select.data_master(workorder, operation_act, workcenter);
                    if (resultMobileMaster == null) {
                        connect = false;
                        return false;
                    } else {
                        if (resultMobileMaster.size() == 0) {
                            err = "Sorry! Work Order : " + workorder + " don\'t pass operation : " + workcenter;
                            return false;
                        }
                        for (HashMap<String, String> result : tranIn) {
                            sumTranIn = sumTranIn + Integer.parseInt(result.get("qty"));
                        }// END WHILE rs_operation
                        for (HashMap<String, String> result : tranOut) {
                            sumTranOut = sumTranOut + Integer.parseInt(result.get("qty"));

                        }// END WHILE rs_operation
                        sumTranResult = sumTranIn - sumTranOut;
                        orderResult = select.data_order(workorder);
                        plant = orderResult.get("plant");
                        project = orderResult.get("projectno");
                        orderqty = orderResult.get("orderqty");
                        starttime = resultMobileMaster.get("starttime");
                        if (orderResult == null) {
                            connect = false;
                            return false;

                        } else {
                            if (orderResult.size() == 0) {
                                err = "Not found in database (Order Data) !!!\nPlease, contact administrator (MIS)";
                                return false;
                            }
                            btnsave = true;
                            return true;

                        }
                    }
                } else {
                    err = "Not found in database (Operation Data) !!!\nPlease, contact administrator (MIS)";
                    return false;
                }


            } else {
                connect = false;
                return false;
            }


        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                switcher.showContentView();
                txt_workcenter.setText(workcenter_true + " - " + operation_act);
                txt_nextcenter.setText(workcenter_trueNext + " - " + operation_actNext);
                txt_workorder.setText(workorder);
                txt_plant.setText(plant);
                txt_project.setText(project);
                txt_orderqty.setText(orderqty);
                txt_inputqty.setText(sumTranIn + "");
                txt_starttime.setText(starttime);
                edt_outputqty.setText(sumTranResult + "");
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
            loadsendjob = null;
            super.onCancelled();
        }
    }


}
