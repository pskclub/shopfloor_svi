package th.co.svi.shopfloor.manager;

import android.util.Log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by MIS_Student5 on 17/2/2559.
 */
public class SelectDB {
    final String FAIL = "0";
    final String SUCCESS = "1";
    final String ERR = "-1";
    ConnectionDB ConnectionClass;
    String query;

    public SelectDB() {
        ConnectionClass = new ConnectionDB();

    }

    public List<String> checkLogin(String username, String password) {
        ResultSet result = null;
        List<String> listData = new ArrayList<>();
        listData.add(0, FAIL);
        try {
            Connection con = ConnectionClass.CONN();
            if (con != null) {
                query = "SELECT * FROM MOBILE_USER WHERE USER_NAME = '" +
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
                listData.add(1, result.getString("USER_ID"));
                listData.add(2, result.getString("USER_ROUTE"));
                return listData;
            }
        } catch (SQLException e) {
            Log.e("DbSelErr", e.getMessage());
        }
        return listData;
    }

    public List<HashMap<String, String>> plan(String USER_ROUTE, String DatePlan, String DateTo) {
        ResultSet result = null;
        List<HashMap<String, String>> listData = null;
        try {
            Connection con = ConnectionClass.CONN();
            if (con != null) {
                listData = new ArrayList<>();
                query = "SELECT DISTINCT pl.Work_Order,pl.Plant,pl.Qty_Order,ISNULL(shop.Status,1) AS Status " +
                        "FROM Planning_Master AS pl LEFT OUTER JOIN MOBILE_Shopfloor_Master AS shop " +
                        "ON pl.Work_Order = shop.QR_CODE AND shop.WorkCenter LIKE '" + USER_ROUTE + "%' " +
                        "WHERE pl.Date_Start_Plan >= '" + DatePlan + "'  " +
                        "AND pl.Date_Start_Plan < '" + DateTo + "'  " +
                        "AND pl.Plan_Week = DATEPART(wk,pl.Date_Start_Plan) " +
                        "AND pl.Work_Order IN (SELECT DISTINCT WorkOrder FROM SAP_ORDER_OPERATION WHERE Work_Center LIKE '" + USER_ROUTE + "%') ";


                Statement stmt = con.createStatement();
                result = stmt.executeQuery(query);
            } else {
                return listData;
            }
        } catch (SQLException e) {
            Log.e("DbSelErr", e.getMessage());
        } catch (NullPointerException e) {
            Log.e("DbSelNull", e.getMessage());
        }
        try {
            if (result != null && result.next()) {
                do {
                    HashMap<String, String> planning = new HashMap<>();
                    planning.put("Work_Order", result.getString("Work_Order"));
                    planning.put("Qty_Order", result.getString("Qty_Order"));
                    planning.put("Plant", result.getString("Plant"));
                    planning.put("Status", result.getString("Status"));
                    listData.add(planning);
                } while (result.next());
                return listData;
            }
        } catch (SQLException e) {
            Log.e("DbSelErr", e.getMessage());
        }

        return listData;
    }

    public List<HashMap<String, String>> pending(String USER_ROUTE) {
        ResultSet result = null;
        List<HashMap<String, String>> listData = null;
        try {
            Connection con = ConnectionClass.CONN();
            if (con != null) {
                listData = new ArrayList<>();
                query = "SELECT QR_CODE,Route_Operation,WorkCenter,WorkOrder,CAST(Qty_WO AS int) " +
                        "AS Qty_WO,Status,Close_JobDate,Regis_by,Regis_Date,Update_by,Update_Date " +
                        "FROM MOBILE_Shopfloor_Master  WHERE Status = '0'  AND WorkCenter LIKE '" + USER_ROUTE +
                        "%' ORDER BY QR_CODE";

                Statement stmt = con.createStatement();
                result = stmt.executeQuery(query);
            } else {
                return listData;
            }
        } catch (SQLException e) {
            Log.e("DbSelErr", e.getMessage());
        } catch (NullPointerException e) {
            Log.e("DbSelNull", e.getMessage());
        }
        try {
            if (result != null && result.next()) {
                do {
                    HashMap<String, String> planning = new HashMap<>();
                    planning.put("QR_CODE", result.getString("QR_CODE"));
                    planning.put("WorkCenter", result.getString("WorkCenter"));
                    planning.put("Qty_WO", result.getString("Qty_WO"));
                    listData.add(planning);
                } while (result.next());
                return listData;
            }
        } catch (SQLException e) {
            Log.e("DbSelErr", e.getMessage());
        }

        return listData;
    }

}
