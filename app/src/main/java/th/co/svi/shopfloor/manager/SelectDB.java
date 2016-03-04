package th.co.svi.shopfloor.manager;

import android.util.Log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MIS_Student5 on 17/2/2559.
 */
public class SelectDB {
    List<Integer> listData;
    final int FAIL = 0;
    final int SUCCESS = 1;
    final int ERR = -1;
    ConnectionDB ConnectionClass;
    String query;

    public SelectDB() {
        ConnectionClass = new ConnectionDB();
        listData = new ArrayList<>();
    }

    public List<Integer> checkLogin(String username, String password) {
        ResultSet result = null;
        listData.add(0, FAIL);
        try {
            Connection con = ConnectionClass.CONN();
            if (con != null) {
                query = "SELECT * FROM MOBILE_USER WHERE USER_ID = '" +
                        username + "' AND USER_PASSWORD = '" +
                        password + "'";
                Statement stmt = con.createStatement();
                result = stmt.executeQuery(query);
            } else {
                listData.add(0, ERR);
                return listData;
            }
        } catch (SQLException e) {
            Log.e("DbSelErr", e.getMessage());
        } catch (NullPointerException e) {
            Log.e("DbSelNull", e.getMessage());
        }
        try {
            if (result != null && result.next()) {
                listData.add(0, SUCCESS);
                listData.add(1, result.getInt("id_employee"));
                return listData;
            }
        } catch (SQLException e) {
            Log.e("DbSelErr", e.getMessage());
        }
        return listData;
    }

}
