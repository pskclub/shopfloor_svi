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
public class JobPlanListItem extends BaseCustomViewGroup {
    private TextView txtWork;
    private TextView txtPlan;
    private TextView txtQuantity;
    private TextView txtStatus;
    private CardView card_view;

    public JobPlanListItem(Context context) {
        super(context);
        initInflate();
        initInstances();
    }

    public JobPlanListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initInflate();
        initInstances();
        initWithAttrs(attrs, 0, 0);
    }

    public JobPlanListItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initInflate();
        initInstances();
        initWithAttrs(attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public JobPlanListItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initInflate();
        initInstances();
        initWithAttrs(attrs, defStyleAttr, defStyleRes);
    }

    private void initInflate() {
        inflate(getContext(), R.layout.listplan, this);
    }

    private void initInstances() {
        // findViewById here
        txtPlan = (TextView) findViewById(R.id.txt_plan);
        txtQuantity = (TextView) findViewById(R.id.txtQuantity);
        txtStatus = (TextView) findViewById(R.id.txtStatus);
        txtWork = (TextView) findViewById(R.id.txt_work);
        card_view = (CardView) findViewById(R.id.card_view);
        card_view.setClickable(true);
    }

    public void setData(String txtWork, String txtPlan, String txtQuantity, String txtStatus) {
        this.txtWork.setText(txtWork);
        this.txtPlan.setText("Plant : "+txtPlan);
        this.txtQuantity.setText(txtQuantity);
        if (txtStatus.equals("0")) {
            this.txtStatus.setText("Progress");
        } else if (txtStatus.equals("9")) {
            this.txtStatus.setText("Complete");
        } else {
            this.txtStatus.setText("Wait");
        }
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
