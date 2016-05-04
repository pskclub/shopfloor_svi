package th.co.svi.shopfloor.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import th.co.svi.shopfloor.manager.Contextor;
import th.co.svi.shopfloor.view.JobPlanListItem;

/**
 * Created by MIS_Student5 on 8/3/2559.
 */
public class JobPlanListAdapter extends BaseAdapter {
    Context context = Contextor.getInstance().getContext();
    List<HashMap<String, String>> Data = new ArrayList<>();

    public JobPlanListAdapter(List<HashMap<String, String>> Data) {
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
        JobPlanListItem item;
        if (view != null) {
            item = (JobPlanListItem) view;
        } else {
            item = new JobPlanListItem(viewGroup.getContext());

        }

        item.setData(
                Data.get(i).get("Work_Order"),
                Data.get(i).get("Plant"),
                Data.get(i).get("Qty_Order"),
                Data.get(i).get("Status"));
        return item;
    }
}
