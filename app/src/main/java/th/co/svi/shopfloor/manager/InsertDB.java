package th.co.svi.shopfloor.manager;

import android.util.Log;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by MIS_Student5 on 17/2/2559.
 */
public class InsertDB {
    ConnectionDB ConnectionClass;
    String query;

    public InsertDB() {
        ConnectionClass = new ConnectionDB();

    }

    public boolean data_master(String workorder, String route_operation, String workcenter,
                                String orderqty, String USER_ID) {
        try {
            Connection con = ConnectionClass.CONN();
            if (con != null) {
                query = "INSERT INTO MOBILE_Shopfloor_Master(workorder,Route_Operation," +
                        "WorkCenter,Qty_WO,Status,Close_JobDate,Regis_by,Regis_Date,Update_by,Update_Date)" +
                        " VALUES ('" + workorder + "','" + route_operation + "','" + workcenter + "','" +
                        orderqty + "','0',NULL,'" + USER_ID + "',GETDATE(),NULL,NULL)";
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

    public boolean data_tranin(String workorder, String route_operation, String workcenter, String orderqty,
                               String regis_date, String USER_ID, String contrainer_id) {
        try {
            Connection con = ConnectionClass.CONN();
            if (con != null) {
                query = "INSERT INTO MOBILE_Shopfloor_TranIN(workorder,Route_Operation,Item_Key,WorkCenter,Qty,Trans_Date,Regis_by," +
                        "Regis_Date,Update_by,Update_Date,contrainer_id)" +
                        " VALUES ('" + workorder + "','" + route_operation + "','1','" + workcenter + "','" +
                        orderqty + "','" + regis_date + "','" + USER_ID + "',GETDATE(),NULL,NULL,'" + contrainer_id + "')";
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
