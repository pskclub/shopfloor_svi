package th.co.svi.shopfloor.manager;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDB {
    String ip = "12.1.2.18";
    String classs = "net.sourceforge.jtds.jdbc.Driver";
    String db = "TESTDB";
    String username = "sa";
    String password = "svi2014DB";
    Connection connect = null;
    String ConnURL;

    @SuppressLint("NewApi")
    public Connection CONN() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            Class.forName(classs);
            ConnURL = "jdbc:jtds:sqlserver://" + ip + ";"
                    + "databaseName=" + db + ";user=" + username + ";password="
                    + password + ";";
            connect = DriverManager.getConnection(ConnURL);
        } catch (SQLException se) {
            Log.e("DbErr", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("DbErr", e.getMessage());
        } catch (Exception e) {
            Log.e("DbErr", e.getMessage());
        }
        return connect;
    }

}

