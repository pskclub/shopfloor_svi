package th.co.svi.shopfloor.fragment;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;

import com.google.zxing.Result;
import com.welcu.android.zxingfragmentlib.BarCodeScannerFragment;

import th.co.svi.shopfloor.R;

public class QrCodeFragment extends BarCodeScannerFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setmCallBack(new IResultCallback() {
            @Override
            public void result(Result lastResult) {
                Vibrator v = (Vibrator) getActivity().getSystemService(getActivity().VIBRATOR_SERVICE);
                v.vibrate(200);
                MediaPlayer mp = MediaPlayer.create(getActivity(), R.raw.beep);
                mp.start();
                Intent intent = new Intent();
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