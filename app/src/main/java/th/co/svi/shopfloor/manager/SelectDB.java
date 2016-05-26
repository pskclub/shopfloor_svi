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


    public HashMap<String, String> checkLogin(String username, String password) {
        ResultSet result = null;
        HashMap<String, String> listData = new HashMap<>();
        listData.put("status", FAIL);
        try {
            Connection con = ConnectionClass.CONN();
            if (con != null) {
                query = "SELECT * FROM MOBILE_USER WHERE USER_ID = '" +
                        username + "' AND USER_PASSWORD = '" +
                        password + "'";
                Statement stmt = con.createStatement();
                result = stmt.executeQuery(query);
            } else {
                listData.put("status", ERR);
                return listData;
            }
        } catch (SQLException e) {
            Log.e("DbSelErr", e.getMessage());
        } catch (NullPointerException e) {
            Log.e("DbSelNull", e.getMessage());
        }
        try {
            if (result != null && result.next()) {
                listData.put("status", SUCCESS);
                listData.put("ID", result.getString("USER_ID"));
                listData.put("route", result.getString("USER_ROUTE"));
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
                        "ON pl.Work_Order = shop.workorder AND shop.WorkCenter LIKE '" + USER_ROUTE + "%' " +
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
            Log.e("DbSelErrPlan", e.getMessage());
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
            Log.e("DbSelErrPlan", e.getMessage());
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
                query = "select data_in.* , isnull(data_out.qty_out,0) as qty_out  " +
                        ", isnull(data_in.qty_in,0)  - isnull(data_out.qty_out,0) as qty_bal from  ( " +
                        " SELECT [workorder]  ,[route_operation] ,[workcenter] " +
                        "      ,sum([qty]) as qty_in " +
                        "  FROM MOBILE_SHOPFLOOR_TRANIN " +
                        "  where workcenter ='" + USER_ROUTE + "' " +
                        "  group by  [workorder] ,[route_operation]  ,[workcenter] ) as data_in " +
                        "  left outer join  " +
                        "  (SELECT [workorder] ,[route_operation]  ,[workcenter] " +
                        "      ,sum([qty]) as qty_out " +
                        "  FROM MOBILE_SHOPFLOOR_TRANOUT " +
                        "  where workcenter = '" + USER_ROUTE + "' " +
                        "  group by [workorder] ,[route_operation] ,[workcenter] ) as data_out " +
                        "  on data_in.[workorder] = data_out.[workorder] and  data_in.[workcenter] = data_out.[workcenter] " +
                        "  where (isnull(data_in.qty_in,0) - isnull(data_out.qty_out,0)) > 0 order by data_in.[workorder] desc  ";

                Statement stmt = con.createStatement();
                result = stmt.executeQuery(query);
            } else {
                return listData;
            }
        } catch (SQLException e) {
            Log.e("DbSelErrPendding", e.getMessage());
        } catch (NullPointerException e) {
            Log.e("DbSelNull", e.getMessage());
        }
        try {
            if (result != null && result.next()) {
                do {
                    HashMap<String, String> planning = new HashMap<>();
                    planning.put("QR_CODE", result.getString("workorder"));
                    planning.put("WorkCenter", result.getString("WorkCenter"));
                    planning.put("Qty_WO", result.getString("Qty_bal"));
                    listData.add(planning);
                } while (result.next());
                return listData;
            }
        } catch (SQLException e) {
            Log.e("DbSelErrPendding", e.getMessage());
        }

        return listData;
    }

    public HashMap<String, String> show_contrainer(String contrainer_id) {
        ResultSet result = null;
        HashMap<String, String> listData = null;
        try {
            Connection con = ConnectionClass.CONN();
            if (con != null) {
                listData = new HashMap<>();
                query = "select * from ( " +
                        "select TOP 2 * from ( " +
                        "SELECT workorder,route_operation,item_key,contrainer_id,workcenter,qty,trans_date ,'IN' as type_case" +
                        "  FROM [SAPWI_DEV_Study].[dbo].[MOBILE_SHOPFLOOR_TRANIN]" +
                        "  where contrainer_id = '" + contrainer_id + "' " +
                        " union all" +
                        "SELECT workorder,route_operation,item_key,contrainer_id,workcenter,qty,trans_date,'OUT' as type_case" +
                        "  FROM [SAPWI_DEV_Study].[dbo].[MOBILE_SHOPFLOOR_TRANOUT]" +
                        "where contrainer_id = '" + contrainer_id + "' " +
                        ") as chk_contrainer" +
                        "order by trans_date desc ,type_case ) as data_OK" +
                        "left outer join " +
                        "(SELECT [WorkOrder] as sap_wo  ,[OrderType]  ,[Plant] ,left([Material] ,3) as cust_code" +
                        "      ,[Material]  ,[Description] ,Ord_QTY_True as qty_order" +
                        "      ,[Order_startdate] as rel_date ,[Order_finishdate]  as commit_date" +
                        "  FROM [SAPWI_DEV_Study].[dbo].[SAP_ORDER_DATA] )  as data_sap" +
                        " on data_OK.WorkOrder = data_sap.sap_wo  ";

                Statement stmt = con.createStatement();
                result = stmt.executeQuery(query);
            } else {
                return listData;
            }
        } catch (SQLException e) {
            Log.e("DbSelErrPendding", e.getMessage());
        } catch (NullPointerException e) {
            Log.e("DbSelNull", e.getMessage());
        }
        try {
            if (result != null && result.next()) {
                do {
                    if (result.getString("type_case").equals("OUT")) {
                        listData.put("workorder", result.getString("workorder"));
                        listData.put("workcenter_out", result.getString("WorkCenter"));
                        listData.put("qty_order", result.getString("qty_order"));
                        listData.put("qty_job", result.getString("Qty"));
                        listData.put("trans_date", result.getString("trans_date"));
                        listData.put("OrderType", result.getString("OrderType"));
                        listData.put("Plant", result.getString("Plant"));
                        listData.put("cust_code", result.getString("cust_code"));
                        listData.put("Material", result.getString("Material"));
                        listData.put("Description", result.getString("Description"));
                        listData.put("rel_date", result.getString("rel_date"));
                        listData.put("commit_date", result.getString("commit_date"));
                    } else if (result.getString("type_case").equals("IN")) {
                        listData.put("workcenter_in", result.getString("WorkCenter"));
                    }

                } while (result.next());
                return listData;
            }
        } catch (SQLException e) {
            Log.e("DbSelErrPendding", e.getMessage());
        }
        return listData;
    }

    public List<HashMap<String, String>> sap_data_operation(String qrcode) {
        ResultSet result = null;
        List<HashMap<String, String>> listData = null;
        try {
            Connection con = ConnectionClass.CONN();
            if (con != null) {
                listData = new ArrayList<>();
                query = "SELECT * FROM SAP_ORDER_OPERATION WHERE WorkOrder = '" + qrcode + "' ORDER BY Opertion_act";
                Statement stmt = con.createStatement();
                result = stmt.executeQuery(query);
            } else {
                return null;
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
                    planning.put("workcenter", result.getString("Work_Center"));
                    planning.put("operation_act", result.getString("Opertion_act"));
                    planning.put("workcenter_true", result.getString("Work_Center_True"));
                    listData.add(planning);
                } while (result.next());
                return listData;
            }
        } catch (SQLException e) {
            Log.e("DbSelErr", e.getMessage());
        }

        return listData;
    }

    public HashMap<String, String> container_detail(String qrcode) {
        ResultSet result = null;
        HashMap<String, String> listData = null;
        try {
            Connection con = ConnectionClass.CONN();
            if (con != null) {
                listData = new HashMap<>();
                query = "select data1.* , data2.* from ( " +
                        "select TOP 2 * from ( " +
                        "SELECT workorder as wo " +
                        ",route_operation,item_key,contrainer_id,workcenter,qty,trans_date " +
                        ",'IN' as type_case " +
                        "  FROM [MOBILE_SHOPFLOOR_TRANIN] " +
                        "  where contrainer_id =  '"+ qrcode +"'  " +
                        " union all " +
                        "SELECT workorder as " +
                        "wo,route_operation,item_key,contrainer_id,workcenter,qty,trans_date " +
                        ",'OUT' as type_case" +
                        "  FROM [MOBILE_SHOPFLOOR_TRANOUT] " +
                        "where contrainer_id = '"+ qrcode +"' " +
                        ") as chk_contrainer " +
                        "order by trans_date desc ,type_case ) as data1 " +
                        "left outer join " +
                        "( Select * ,  CAST(case when Ord_QTY_True is null then Ord_QTY else Ord_QTY_True end as int) as qty_ok " +
                        "FROM [SAP_ORDER_DATA] ) as data2 " +
                        "on data1.wo = data2.WorkOrder";
                Statement stmt = con.createStatement();
                result = stmt.executeQuery(query);
            } else {
                return null;
            }
        } catch (SQLException e) {
            Log.e("DbSelErr", e.getMessage());
        } catch (NullPointerException e) {
            Log.e("DbSelNull", e.getMessage());
        }
        try {
            if (result != null && result.next()) {

                do {
                    listData.put("wo", result.getString("wo"));
                    listData.put("qty", result.getString("qty"));
                    listData.put("trans_date", result.getString("trans_date"));
                    listData.put("OrderType", result.getString("OrderType"));
                    listData.put("Plant", result.getString("Plant"));
                    listData.put("Material", result.getString("Material"));
                    listData.put("Description", result.getString("Description"));
                    listData.put("Ord_QTY_True", result.getString("qty_ok"));
                    if (result.getString("type_case").equals("OUT")) {
                        listData.put("workcenter_out", result.getString("workcenter"));
                    }else {
                        listData.put("workcenter_in", result.getString("workcenter"));
                    }
                } while (result.next());
                return listData;
            }
        } catch (SQLException e) {
            Log.e("DbSelErr", e.getMessage());
        }

        return listData;
    }


    public List<HashMap<String, String>> tranIn(String qrcode, String operation_act, String workcenter) {
        ResultSet result = null;
        List<HashMap<String, String>> listData = null;
        try {
            Connection con = ConnectionClass.CONN();
            if (con != null) {
                listData = new ArrayList<>();
                query = "SELECT * FROM MOBILE_Shopfloor_TranIN WHERE workorder = '" + qrcode + "' AND Route_Operation = '" + operation_act + "' AND WorkCenter = '" + workcenter + "'";
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
                    planning.put("qty", result.getString("Qty"));
                    listData.add(planning);
                } while (result.next());
                return listData;
            }
        } catch (SQLException e) {
            Log.e("DbSelErr", e.getMessage());
        }

        return listData;
    }

    public List<HashMap<String, String>> tranOut(String qrcode, String operation_act, String workcenter) {
        ResultSet result = null;
        List<HashMap<String, String>> listData = null;
        try {
            Connection con = ConnectionClass.CONN();
            if (con != null) {
                listData = new ArrayList<>();
                query = "SELECT * FROM MOBILE_Shopfloor_TranOUT WHERE workorder = '" + qrcode + "' AND Route_Operation = '" + operation_act + "' AND WorkCenter = '" + workcenter + "'";
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
                    planning.put("qty", result.getString("Qty"));
                    planning.put("itemkey", result.getString("Item_Key"));
                    listData.add(planning);
                } while (result.next());
                return listData;
            }
        } catch (SQLException e) {
            Log.e("DbSelErr", e.getMessage());
        }

        return listData;
    }


    public List<HashMap<String, String>> cmsMaster() {
        ResultSet result = null;
        List<HashMap<String, String>> listData = null;
        try {
            Connection con = ConnectionClass.CONN();
            if (con != null) {
                listData = new ArrayList<>();
                query = "SELECT * FROM CMS_Master WHERE CMS_Status = '1' ";

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
                    planning.put("CMS_WorkOrder", result.getString("CMS_WorkOrder"));
                    planning.put("CMS_ProjectNo", result.getString("CMS_ProjectNo"));
                    planning.put("CMS_SideType", result.getString("CMS_SideType"));
                    planning.put("CMS_LineName", result.getString("CMS_LineName"));
                    planning.put("CMS_PlanYear", result.getString("CMS_PlanYear"));
                    planning.put("CMS_PlanWeek", result.getString("CMS_PlanWeek"));
                    planning.put("CMS_Plant", result.getString("CMS_Plant"));
                    planning.put("CMS_OrderQty", result.getString("CMS_OrderQty"));
                    planning.put("CMS_PlanLineNo", result.getString("CMS_PlanLineNo"));
                    planning.put("CMS_Status", result.getString("CMS_Status"));
                    listData.add(planning);
                } while (result.next());
                return listData;
            }
        } catch (SQLException e) {
            Log.e("DbSelErr", e.getMessage());
        }

        return listData;
    }

    public HashMap<String, String> data_master(String qrcode) {
        ResultSet result = null;
        HashMap<String, String> listData = null;
        try {
            Connection con = ConnectionClass.CONN();
            if (con != null) {
                listData = new HashMap<>();
                query = "SELECT * FROM MOBILE_Shopfloor_Master WHERE workorder = '" + qrcode + "' ORDER BY Route_Operation ";
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
                listData.put("workcenter", result.getString("WorkCenter"));
                return listData;
            }
        } catch (SQLException e) {
            Log.e("DbSelErr", e.getMessage());
        }

        return listData;
    }

    public HashMap<String, String> data_master(String qrcode, String operationAct, String workCenter) {
        ResultSet result = null;
        HashMap<String, String> listData = null;
        try {
            Connection con = ConnectionClass.CONN();
            if (con != null) {
                listData = new HashMap<>();
                query = "SELECT * FROM MOBILE_Shopfloor_Master WHERE workorder = '" + qrcode + "' AND Route_Operation = '" + operationAct +
                        "' AND WorkCenter = '" + workCenter + "'";
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
                listData.put("workorder", result.getString("workorder"));
                listData.put("route_operation", result.getString("route_operation"));
                listData.put("qty_wo", result.getString("qty_wo"));
                listData.put("status_now", result.getString("status"));
                listData.put("close_jobdate", result.getString("close_jobdate"));
                listData.put("regis_by", result.getString("regis_by"));
                listData.put("starttime", result.getString("Regis_Date"));
                listData.put("update_by", result.getString("update_by"));
                listData.put("update_date", result.getString("update_date"));
                return listData;
            }
        } catch (SQLException e) {
            Log.e("DbSelErr", e.getMessage());
        }

        return listData;
    }

    public HashMap<String, String> data_operation(String qrcode) {
        ResultSet result = null;
        HashMap<String, String> listData = null;
        try {
            Connection con = ConnectionClass.CONN();
            if (con != null) {
                listData = new HashMap<>();
                query = "SELECT * FROM SAP_ORDER_OPERATION WHERE WorkOrder = '" + qrcode + "' ORDER BY Opertion_act";
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

                listData.put("workcenter", result.getString("Work_Center"));
                listData.put("route_operation", result.getString("Opertion_act"));
                return listData;
            }
        } catch (SQLException e) {
            Log.e("DbSelErr", e.getMessage());
        }

        return listData;
    }

    public HashMap<String, String> data_order(String qrcode) {
        ResultSet result = null;
        HashMap<String, String> listData = null;
        try {
            Connection con = ConnectionClass.CONN();
            if (con != null) {
                listData = new HashMap<>();
                query = "SELECT * FROM SAP_ORDER_DATA WHERE WorkOrder = '" + qrcode + "'";
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
                    listData.put("workorder", result.getString("WorkOrder"));
                    listData.put("plant", result.getString("Plant"));
                    listData.put("projectno", result.getString("Material"));
                    listData.put("orderqty", result.getString("Ord_QTY"));
                } while (result.next());
                return listData;
            }
        } catch (SQLException e) {
            Log.e("DbSelErr", e.getMessage());
        }

        return listData;
    }

    public int countItemKeyIn(String workorder, String route_operation, String workcenter) {
        ResultSet result = null;
        HashMap<String, Integer> listData = new HashMap<>();
        try {
            Connection con = ConnectionClass.CONN();
            if (con != null) {
                query = "SELECT * FROM MOBILE_SHOPFLOOR_TRANIN WHERE workorder = '" +
                        workorder + "' AND route_operation = '" +
                        route_operation + "' AND workcenter = '" + workcenter + "' order by item_key desc  ";
                Statement stmt = con.createStatement();
                result = stmt.executeQuery(query);
            } else {
                return 0;
            }
        } catch (SQLException e) {
            Log.e("DbSelErr", e.getMessage());
        } catch (NullPointerException e) {
            Log.e("DbSelNull", e.getMessage());
        }
        try {
            if (result != null && result.next()) {
                listData.put("count", result.getInt("item_key"));
                return listData.get("count");
            }
        } catch (SQLException e) {
            Log.e("DbSelErr", e.getMessage());
        }
        return 0;
    }

    public int countItemKeyOut(String workorder, String route_operation, String workcenter) {
        ResultSet result = null;
        HashMap<String, Integer> listData = new HashMap<>();
        try {
            Connection con = ConnectionClass.CONN();
            if (con != null) {
                query = "SELECT * FROM MOBILE_SHOPFLOOR_TRANOUT WHERE workorder = '" +
                        workorder + "' AND route_operation = '" +
                        route_operation + "' AND workcenter = '" + workcenter + "' order by item_key desc  ";
                Statement stmt = con.createStatement();
                result = stmt.executeQuery(query);
            } else {
                return 0;
            }
        } catch (SQLException e) {
            Log.e("DbSelErr", e.getMessage());
        } catch (NullPointerException e) {
            Log.e("DbSelNull", e.getMessage());
        }
        try {
            if (result != null && result.next()) {
                listData.put("count", result.getInt("item_key"));
                return listData.get("count");
            }
        } catch (SQLException e) {
            Log.e("DbSelErr", e.getMessage());
        }
        return 0;
    }

}
