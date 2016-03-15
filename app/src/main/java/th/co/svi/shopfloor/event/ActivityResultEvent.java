package th.co.svi.shopfloor.event;

import android.content.Intent;

/**
 * Created by MIS_Student5 on 15/3/2559.
 */
public class ActivityResultEvent {
    private int requestCode;
    private int resultCode;
    private Intent data;

    public ActivityResultEvent(int requestCode, int resultCode, Intent data) {
        this.requestCode = requestCode;
        this.resultCode = resultCode;
        this.data = data;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public Intent getData() {
        return data;
    }

    public void setData(Intent data) {
        this.data = data;
    }
}
