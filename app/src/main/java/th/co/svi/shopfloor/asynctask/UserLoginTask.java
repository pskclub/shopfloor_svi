package th.co.svi.shopfloor.asynctask;

import android.os.AsyncTask;

import java.util.HashMap;

import th.co.svi.shopfloor.bus.ResultBus;
import th.co.svi.shopfloor.event.AsyncTaskEvent;
import th.co.svi.shopfloor.manager.InsertDB;
import th.co.svi.shopfloor.manager.SelectDB;
import th.co.svi.shopfloor.manager.ShareData;

/**
 * Created by MIS_Student5 on 15/3/2559.
 */
public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
    final String FAIL = "0";
    final String SUCCESS = "1";
    final String ERR = "-1";
    private final String mUsername;
    private final String mPassword;
    private int setERR = 0;
    ShareData shareMember;

    public UserLoginTask(String username, String password) {
        mUsername = username;
        mPassword = password;
        shareMember = new ShareData("MEMBER");
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        SelectDB checkLogin = new SelectDB();
        HashMap<String, String> result;
        try {
            Thread.sleep(400);
            result = checkLogin.checkLogin(mUsername, mPassword);
            if (result.get("status").equals(SUCCESS)) {
                shareMember.setMember(true, mUsername, result.get("ID"), result.get("route"));
                shareMember.addMember(mUsername, result.get("ID"), result.get("route"));
                InsertDB insert = new InsertDB();
                insert.log_user(result.get("ID"),"Authentication","Login");
                return true;
            } else if (result.get("status").equals(ERR)) {
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
    protected void onPostExecute(Boolean success) {
        ResultBus.getInstance().post(
                new AsyncTaskEvent(success, 1, setERR));
    }

    @Override
    protected void onCancelled() {
        ResultBus.getInstance().post(
                new AsyncTaskEvent(-1));
    }
}
