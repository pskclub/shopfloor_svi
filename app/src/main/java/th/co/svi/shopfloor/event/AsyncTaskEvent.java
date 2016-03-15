package th.co.svi.shopfloor.event;

import java.util.HashMap;

/**
 * Created by MIS_Student5 on 15/3/2559.
 */
public class AsyncTaskEvent {
    private final int onPost = 1;
    private final int onCancle = -1;
    private HashMap<String, String> data;
    private boolean success = false;
    private int even;
    private int err = 0;

    public AsyncTaskEvent(HashMap<String, String> data, int event) {
        this.data = data;
        this.even = event;

    }
    public AsyncTaskEvent(boolean success, int event,int err) {
        this.success = success;
        this.even = event;
        this.err = err;
    }
    public AsyncTaskEvent(int event) {
        this.even = event;
    }

    public HashMap<String, String> getData() {
        return data;
    }

    public int getEven() {
        return even;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getErr() {
        return err;
    }
}
