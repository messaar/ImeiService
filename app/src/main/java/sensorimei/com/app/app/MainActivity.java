package sensorimei.com.app.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import android.app.ActivityManager.RunningServiceInfo;

public class MainActivity extends Activity {
    private static final String TAG = "Main";
    String str="new";
    static ResultSet rs;
    static PreparedStatement st;
    static Connection con;

    Intent intent;
    Button btn_on,btn_off,btn_salir;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_on = (Button)findViewById(R.id.btn_on);
        btn_off = (Button)findViewById(R.id.btn_off);
        btn_salir = (Button)findViewById(R.id.btn_salir);
        intent = new Intent(MainActivity.this, MyService.class);

        btn_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Obtiene Localizacion , pero con el GPS
                GPSTracker gps = new GPSTracker(MainActivity.this);
                // check if GPS enabled
                if(!gps.canGetLocation()){
                    gps.showSettingsAlert();
                }else{
                    btn_on.setEnabled(false);
                    btn_off.setEnabled(true);
                    startService(intent);
                }

            }
        });

        btn_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_on.setEnabled(true);
                btn_off.setEnabled(false);
                stopService(intent);
            }
        });

        btn_salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //apaga el servicio de localizacion
                btn_on.setEnabled(true);
                btn_off.setEnabled(false);
                stopService(intent);

                //elimina las credenciales de inisio de sesion
                SharedPreferences settings = MainActivity.this.getSharedPreferences("preferencias", Context.MODE_PRIVATE);
                settings.edit().clear().commit();
                Intent i = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(i);
                finish();

            }
        });





    }



    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("sensorimei.com.app.app.MyService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {

        if(isMyServiceRunning() != false){
            Log.i(TAG, "Service ya fue creado");
            btn_on.setEnabled(false);
            btn_off.setEnabled(true);
        }else{
            Log.i(TAG, "Service no creado");
            btn_on.setEnabled(true);
            btn_off.setEnabled(false);
        }

        super.onResume();
    }
}
