package th.co.svi.shopfloor.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import th.co.svi.shopfloor.R;
import th.co.svi.shopfloor.asynctask.UserLoginTask;
import th.co.svi.shopfloor.bus.ResultBus;
import th.co.svi.shopfloor.event.AsyncTaskEvent;
import th.co.svi.shopfloor.manager.ShareData;


public class AddLoginActivity extends AppCompatActivity {

    /**********************
     * variable Zone
     **********************/

    private UserLoginTask mAuthTask = null;
    private EditText txtUsername;
    private EditText txtPassword;
    private ProgressDialog progressDialog;
    private Button SignInButton;
    String username;
    String password;
    View focusView;
    ShareData shareMember;
    AlertDialog.Builder builder;

    /**********************
     * Method Zone
     **********************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add User");
        getSupportActionBar().setHomeButtonEnabled(true);
        initInstances();
        txtPassword.setOnEditorActionListener(textViewEditerListener);
        SignInButton.setOnClickListener(buttonOnClickListener);

    }


    private void checkLogin() {
        if (shareMember.getLogin()) {
            Intent i = new Intent(AddLoginActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        } else {
            txtUsername.setText(shareMember.getUsername());
            if (!(shareMember.getUsername() == null)) {
                focusView = txtPassword;
                focusView.requestFocus();
            }
        }

    }

    private void initInstances() {
        txtUsername = (EditText) findViewById(R.id.txtusername);
        txtPassword = (EditText) findViewById(R.id.password);
        SignInButton = (Button) findViewById(R.id.btn_login);
        shareMember = new ShareData("MEMBER");
    }

    private void attemptLogin() {
        // Store values at the time of the login attempt.
        username = txtUsername.getText().toString().trim();
        password = txtPassword.getText().toString().trim();
        boolean cancel = false;
        focusView = null;

        if (mAuthTask != null) {
            return;
        }
        // Reset errors.
        txtUsername.setError(null);
        txtPassword.setError(null);

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            txtPassword.setError(getString(R.string.error_field_required));
            focusView = txtPassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            txtUsername.setError(getString(R.string.error_field_required));
            focusView = txtUsername;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            progressDialog = new ProgressDialog(AddLoginActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("Authenticating...");
            progressDialog.show();
            progressDialog.setOnKeyListener(dialogonKeyListener);
            progressDialog.setOnCancelListener(dialogCanclelistener);
            mAuthTask = new UserLoginTask(username, password);
            mAuthTask.execute();

        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

   
    @Override
    protected void onResume() {
        super.onResume();
        ResultBus.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ResultBus.getInstance().unregister(this);
    }

    @Subscribe
    public void loginResult(AsyncTaskEvent event) {
        if (event.getEven() == 1) {
            progressDialog.dismiss();
            mAuthTask = null;
            if (event.isSuccess()) {
                Intent i = new Intent(AddLoginActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            } else {
                if (event.getErr() == 1) {
                    builder = new AlertDialog.Builder(AddLoginActivity.this, R.style.AppCompatAlertDialogStyle);
                    builder.setMessage(R.string.server_fail);
                    builder.setPositiveButton(getString(R.string.ok), null);
                    builder.show();
                    txtUsername.requestFocus();
                } else {
                    builder = new AlertDialog.Builder(AddLoginActivity.this, R.style.AppCompatAlertDialogStyle);
                    builder.setMessage(R.string.error_incorrect_password);
                    builder.setPositiveButton(R.string.ok, null);
                    builder.show();
                    txtUsername.requestFocus();

                }
            }

        } else if (event.getEven() == -1) {
            mAuthTask = null;
            progressDialog.dismiss();
        }

    }


    /**********************
     * Listenner Zone
     **********************/

    TextView.OnEditorActionListener textViewEditerListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
            if (id == EditorInfo.IME_ACTION_SEARCH ||
                    id == EditorInfo.IME_ACTION_DONE ||
                    keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                            keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                attemptLogin();
                return true;
            }
            return false;
        }
    };

    OnClickListener buttonOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == SignInButton)
                attemptLogin();
        }
    };

    DialogInterface.OnCancelListener dialogCanclelistener = new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialogInterface) {
            mAuthTask.cancel(false);
            mAuthTask = null;
        }
    };

    DialogInterface.OnKeyListener dialogonKeyListener = new DialogInterface.OnKeyListener() {
        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && !event.isCanceled()) {
                if (progressDialog.isShowing()) {
                    mAuthTask.cancel(false);
                    mAuthTask = null;
                    progressDialog.dismiss();
                }
                return true;
            }
            return false;
        }
    };

}

