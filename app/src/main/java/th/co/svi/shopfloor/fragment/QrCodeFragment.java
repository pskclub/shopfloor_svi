package th.co.svi.shopfloor.fragment;

import android.content.Intent;
import android.os.Bundle;

import com.google.zxing.Result;
import com.welcu.android.zxingfragmentlib.BarCodeScannerFragment;

public class QrCodeFragment extends BarCodeScannerFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setmCallBack(new IResultCallback() {
            @Override
            public void result(Result lastResult) {
                Intent intent = new Intent ( );
                intent.putExtra("data", lastResult.toString());
                getActivity().setResult(1, intent);
                getActivity().finish();
            }
        });
    }

    @Override
    public int getRequestedCameraId() {
        return -1; // set to 1 to use the front camera (won't work if the device doesn't have one, it is up to you to handle this method ;)
    }
}