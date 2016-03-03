package th.co.svi.shopfloor.manager;

import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by MIS_Student5 on 17/2/2559.
 */
public class SelectDB {
    ConnectionDB ConnectionClass;
    String query;

    public SelectDB() {
        ConnectionClass = new ConnectionDB();
    }

    public boolean checkLogin(String username, String password) {
        ResultSet result = null;
        try {
            Connection con = ConnectionClass.CONN();
            query = "SELECT * FROM MOBILE_USER WHERE USER_ID = '" +
                    username + "' AND USER_PASSWORD = '" +
                    password + "'";
            Statement stmt = con.createStatement();
            result = stmt.executeQuery(query);
        } catch (SQLException e) {
            Log.e("DbErr", e.getMessage());
        } catch (NullPointerException e) {
            Log.e("DbErr", e.getMessage());
        }
        try {
            if (result != null && result.next()) {
                return true;
            }
        } catch (SQLException e) {
            Log.e("DbErr", e.getMessage());
        }
        return false;
    }

}
