package th.co.svi.shopfloor.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import th.co.svi.shopfloor.manager.Contextor;
import th.co.svi.shopfloor.view.UserListItem;

/**
 * Created by MIS_Student5 on 8/3/2559.
 */
public class UserListAdapter extends BaseAdapter {
    Context context = Contextor.getInstance().getContext();
    List<HashMap<String, String>> Data = new ArrayList<>();

    public UserListAdapter(List<HashMap<String, String>> Data) {
        this.Data = Data;
    }

    @Override
    public int getCount() {
//        return (Data == null) ? 0 : Data.size();
        return 5;
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
        UserListItem item;
        if (view != null) {
            item = (UserListItem) view;
        } else {
            item = new UserListItem(viewGroup.getContext());

        }

        item.setData("fdsfsdfsdfsdfsdfsd" + i);
//        item.setData(Data.get(i).get("name"));
        return item;
    }
}
