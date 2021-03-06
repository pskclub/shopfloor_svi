package th.co.svi.shopfloor.manager;

import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by MIS_Student5 on 4/3/2559.
 */
public class ShareData {
    SharedPreferences member;
    SharedPreferences.Editor member_editer;

    public ShareData(String name) {
        member = Contextor.getInstance().getContext().getSharedPreferences(name,
                Contextor.getInstance().getContext().MODE_PRIVATE);
        member_editer = member.edit();
    }

    public void setMember(boolean status, String username, String user_id, String user_route) {
        member_editer.putBoolean("login", status);
        member_editer.putString("username", username);
        member_editer.putString("user_id", user_id);
        member_editer.putString("user_route", user_route);
        member_editer.commit();
    }

    public void logOut() {
        member_editer.putBoolean("login", false);
        member_editer.putString("user_id", "");
        member_editer.putString("user_route", "");
        member_editer.putInt("user_size", 0);
        member_editer.commit();
    }

    public void addMember(String username, String user_id, String user_route) {
        int n = this.getUserSize() + 1;
        member_editer.putString("username[" + n + "]", username);
        member_editer.putString("user_id[" + n + "]", user_id);
        member_editer.putString("user_route[" + n + "]", user_route);
        member_editer.putInt("user_size", n);
        member_editer.commit();
    }

    public HashMap<String, String> getMember(int n) {
        HashMap<String, String> userMap = new HashMap<>();
        userMap.put("username", member.getString("username[" + n + "]", ""));
        userMap.put("user_id", member.getString("user_id[" + n + "]", ""));
        userMap.put("user_route", member.getString("user_route[" + n + "]", ""));
        return userMap;
    }

    public boolean getLogin() {
        return member.getBoolean("login", false);
    }

    public String getUsername() {
        return member.getString("username", "");
    }

    public String getUserID() {
        return member.getString("user_id", "");
    }

    public String getUserRoute() {
        return member.getString("user_route", "");
    }

    public int getUserSize() {
        return member.getInt("user_size", 0);
    }
}
