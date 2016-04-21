package th.co.svi.shopfloor.manager;

import android.util.Log;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class UpdateDB {
    ConnectionDB ConnectionClass;
    String query;

    public UpdateDB() {
        ConnectionClass = new ConnectionDB();

    }

    public boolean xxxx() {
        try {
            Connection con = ConnectionClass.CONN();
            if (con != null) {
                query = "";
                Statement stmt = con.createStatement();
                stmt.executeQuery(query);
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            Log.e("DbSelErr", e.getMessage());
        } catch (NullPointerException e) {
            Log.e("DbSelNull", e.getMessage());
        }
        return false;
    }


}
