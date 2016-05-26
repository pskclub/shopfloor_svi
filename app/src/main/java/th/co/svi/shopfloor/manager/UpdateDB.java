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

    public boolean dataMaster(String workorder, String route_operation, String workcenter,
                              String status, String close_jobdate, String update_by) {
        try {
            Connection con = ConnectionClass.CONN();
            if (con != null) {
                query = "UPDATE MOBILE_SHOPFLOOR_MASTER SET status = '" + status + "' " +
                        ", close_jobdate = GETDATE() " +
                        ", update_by = '" + update_by + "' , update_date = getdate()  " +
                        " where workorder = '" + workorder + "' and route_operation = '" + route_operation + "'" +
                        "and workcenter = '" + workcenter + "'  ";
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
