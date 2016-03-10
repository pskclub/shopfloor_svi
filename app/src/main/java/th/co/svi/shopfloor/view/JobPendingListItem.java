package th.co.svi.shopfloor.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.TextView;

import th.co.svi.shopfloor.R;
import th.co.svi.shopfloor.view.state.BundleSavedState;

/**
 * Created by nuuneoi on 11/16/2014.
 */
public class JobPendingListItem extends BaseCustomViewGroup {
    private TextView txtWork;
    private TextView txtQr;
    private TextView txtQuantity;
    private CardView card_view;

    public JobPendingListItem(Context context) {
        super(context);
        initInflate();
        initInstances();
    }

    public JobPendingListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initInflate();
        initInstances();
        initWithAttrs(attrs, 0, 0);
    }

    public JobPendingListItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initInflate();
        initInstances();
        initWithAttrs(attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public JobPendingListItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initInflate();
        initInstances();
        initWithAttrs(attrs, defStyleAttr, defStyleRes);
    }

    private void initInflate() {
        inflate(getContext(), R.layout.listpending, this);
    }

    private void initInstances() {
        // findViewById here
        txtQuantity = (TextView) findViewById(R.id.txtQuantity);
        txtQr = (TextView) findViewById(R.id.txtQr);
        txtWork = (TextView) findViewById(R.id.txtWork);
        card_view = (CardView) findViewById(R.id.card_view);
        card_view.setClickable(true);
    }

    public void setData(String txtQr, String txtWork, String txtQuantity) {
        this.txtQr.setText(txtQr);
        this.txtWork.setText(txtWork);
        this.txtQuantity.setText(txtQuantity);
    }

    private void initWithAttrs(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        /*
        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.StyleableName,
                defStyleAttr, defStyleRes);

        try {

        } finally {
            a.recycle();
        }
        */
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        BundleSavedState savedState = new BundleSavedState(superState);
        // Save Instance State(s) here to the 'savedState.getBundle()'
        // for example,
        // savedState.getBundle().putString("key", value);

        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        BundleSavedState ss = (BundleSavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        Bundle bundle = ss.getBundle();
        // Restore State from bundle here
    }

}
