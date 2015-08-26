package sensorimei.com.app.app;

/**
 * Created by Oswaldo Gomez on 27/05/2015.
 */
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class MyService extends Service
{
    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 60000;
    private static final float LOCATION_DISTANCE = 0f;

    String str="new";
    static ResultSet rs;
    static PreparedStatement st;
    static Connection con;
    String msg;

    String imei;
    double latitude;
    double longitude;
    double altitud;
    double velocidad;

    private class LocationListener implements android.location.LocationListener{
        Location mLastLocation;
        public LocationListener(String provider)
        {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }
        @Override
        public void onLocationChanged(Location location)
        {
            //obtiene el IMEI del Telefono
            TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            imei = mngr.getDeviceId();

            latitude = location.getLatitude();
            longitude = location.getLongitude();
            altitud = location.getAltitude();
            velocidad = location.getSpeed();

            Log.e(TAG, "latitude: " + latitude);
            Log.e(TAG, "longitude: " + longitude);
            Log.e(TAG, "altitud: " + altitud);
            Log.e(TAG, "velocidad: " + velocidad);

            new ConnectTask().execute();
            Log.e(TAG, "onLocationChanged: " + location);


            mLastLocation.set(location);
        }
        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }
        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }
    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };
    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }
    @Override
    public void onCreate()
    {
        Log.e(TAG, "onCreate");
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }
    @Override
    public void onDestroy()
    {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }



    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    private class ConnectTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try
            {
                String usuario,clave;
                SharedPreferences prefs = getSharedPreferences("preferencias",Context.MODE_PRIVATE);
                usuario = prefs.getString("usuario", "");
                clave = prefs.getString("clave", "");
                Class.forName("com.mysql.jdbc.Driver");
                con = DriverManager.getConnection("jdbc:mysql://" + Constantes.ip_server + ":" + Constantes.port_server + "/" + Constantes.db_server, Constantes.user_server, Constantes.password_server);
                st=con.prepareStatement("insert into usuarios_posicion(id_cliente,id_usuario,imei,latitud,longuitud,velocidad,asnm,fecha,hora) values('"+clave+"','"+usuario+"'," + imei + "," + latitude + "," + longitude + "," + velocidad + "," + altitud + "," + "CURDATE()" +","+ "CURTIME()" + ")");
                st.executeUpdate();
                msg = "Correcto";
            }
            catch(Exception e)
            {
                msg = e.getMessage().toString();
            }
            return "ok";
        }

        @Override
        protected void onPostExecute(String result) {
            //tv.setText("Mensaje: "+msg);
            Log.v(TAG,"Mensaje:: "+msg);
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}