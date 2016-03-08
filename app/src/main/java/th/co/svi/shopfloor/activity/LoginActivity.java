package th.co.svi.shopfloor.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import th.co.svi.shopfloor.R;
import th.co.svi.shopfloor.manager.SelectDB;
import th.co.svi.shopfloor.manager.ShareData;


public class LoginActivity extends AppCompatActivity {

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

    /**********************
     * Method Zone
     **********************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initInstances();
        checkLogin();
        txtPassword.setOnEditorActionListener(textViewEditerListener);
        SignInButton.setOnClickListener(buttonOnClickListener);

    }


    private void checkLogin() {
        if (shareMember.getLogin()) {
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        } else {
            txtUsername.setText(shareMember.getUsername());
            if (!shareMember.getUsername().equals(null)) {
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
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("Authenticating...");
            progressDialog.show();
            mAuthTask = new UserLoginTask(username, password);
            mAuthTask.execute((Void) null);
            progressDialog.setOnKeyListener(dialogonKeyListener);
            progressDialog.setOnCancelListener(dialogCanclelistener);
        }


    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(LoginActivity.this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getString(R.string.confirm));
        builder.setMessage(getString(R.string.want_to_exit));
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //คลิกใช่ ออกจากโปรแกรม
                finish();
                LoginActivity.super.onBackPressed();
            }
        });//second parameter used for onclicklistener
        builder.setNegativeButton(getString(R.string.no), null);
        builder.show();
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

    /**********************
     * innerClass Zone
     **********************/

    private class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
        final String FAIL = "0";
        final String SUCCESS = "1";
        final String ERR = "-1";
        private final String mEmail;
        private final String mPassword;
        private int setERR = 0;
        AlertDialog.Builder builder;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
            setERR = 0;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            SelectDB checkLogin = new SelectDB();
            List<String> result;
            try {
                Thread.sleep(400);
                result = checkLogin.checkLogin(mEmail, mPassword);
                if (result.get(0).equals(SUCCESS)) {
                    shareMember.setMember(true, username, result.get(1).toString(),result.get(2).toString());
                    return true;
                } else if (result.get(0).equals(ERR)) {
                    this.setERR = 1;
                    return false;
                } else {
                    return false;
                }

            } catch (InterruptedException e) {
                return false;
            }

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            progressDialog.dismiss();

            if (success) {
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            } else {
                if (setERR == 1) {
                    builder =
                            new AlertDialog.Builder(LoginActivity.this, R.style.AppCompatAlertDialogStyle);
                    builder.setMessage(R.string.server_fail);
                    builder.setPositiveButton(getString(R.string.ok), null);
                    builder.show();
                    txtUsername.requestFocus();
                } else {
                    builder =
                            new AlertDialog.Builder(LoginActivity.this, R.style.AppCompatAlertDialogStyle);
                    builder.setMessage(R.string.error_incorrect_password);
                    builder.setPositiveButton(R.string.ok, null);
                    builder.show();
                    txtUsername.requestFocus();

                }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            progressDialog.dismiss();
            super.onCancelled();
        }
    }
}

