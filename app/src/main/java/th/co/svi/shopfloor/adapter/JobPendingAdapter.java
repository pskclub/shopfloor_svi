package th.co.svi.shopfloor.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import th.co.svi.shopfloor.manager.Contextor;
import th.co.svi.shopfloor.view.JobPendingListItem;
import th.co.svi.shopfloor.view.JobPlanListItem;

/**
 * Created by MIS_Student5 on 9/3/2559.
 */
public class JobPendingAdapter extends BaseAdapter {
    Context context = Contextor.getInstance().getContext();
    List<HashMap<String, String>> Data = new ArrayList<>();

    public JobPendingAdapter(List<HashMap<String, String>> Data) {
        this.Data = Data;
    }

    @Override
    public int getCount() {
        return (Data == null) ? 0 : Data.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        JobPendingListItem item;
        if (view != null) {
            item = (JobPendingListItem) view;
        } else {
            item = new JobPendingListItem(viewGroup.getContext());

        }
        item.setData(
                Data.get(i).get("QR_CODE"),
                Data.get(i).get("WorkCenter"),
                Data.get(i).get("Qty_WO"));
        return item;
    }
}
