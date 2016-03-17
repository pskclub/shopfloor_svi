package th.co.svi.shopfloor.manager;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDB {
    String ip = "12.1.2.18";
    String classs = "net.sourceforge.jtds.jdbc.Driver";
    String db = "SAPWI_DEV";
    String username = "sa";
    String password = "svi2014DB";
    Connection connect = null;
    String ConnURL;

    @SuppressLint("NewApi")
    public Connection CONN() {
        if (isInternetAvailable()) {
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
                Log.e("DbConErr", se.getMessage());
            } catch (ClassNotFoundException e) {
                Log.e("DbConErr", e.getMessage());
            } catch (Exception e) {
                Log.e("DbConErr", e.getMessage());
            }
            return connect;
        } else {
            return null;
        }
    }

    private boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName(ip); //You can replace it with your name

            if (ipAddr.equals("")) {
                return false;
            } else {
                return true;
            }

        } catch (Exception e) {
            return false;
        }

    }

}

