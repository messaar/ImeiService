package sensorimei.com.app.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


/**
 *
 *  TABLE: usuarios_posicion_claves
 *  CAMPOS: id_usuario , clave
 */
public class LoginActivity extends Activity  {
    private static final String TAG = "Login";
    String str="new";
    static ResultSet rs;
    static PreparedStatement st;
    static Connection con;
    String msg;
    TextView txtUsuario,txtClave;
    ProgressDialog pd = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getSharedPreferences("preferencias",Context.MODE_PRIVATE);
        String clave = prefs.getString("clave", "");

        if(clave.length() > 0){
            Intent i = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(i);
            finish();
        }

        setContentView(R.layout.activity_login);

        txtUsuario = (TextView)findViewById(R.id.txtUsuario);
        txtClave = (TextView)findViewById(R.id.txtClave);

        Button btn_enviar = (Button)findViewById(R.id.btn_enviar);
        btn_enviar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new ConnectTask().execute();
            }
        });
    }

    private class ConnectTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(LoginActivity.this);
            pd.setTitle("Login");
            pd.setMessage("Autenticando..");
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try
            {
                Class.forName("com.mysql.jdbc.Driver");
                con = DriverManager.getConnection("jdbc:mysql://"+Constantes.ip_server+":"+Constantes.port_server+"/"+Constantes.db_server,Constantes.user_server, Constantes.password_server);
                st=con.prepareStatement("select clave from usuarios_posicion_claves where id_usuario = '"+txtUsuario.getText().toString()+"'");
                rs=st.executeQuery();
                while(rs.next())
                {
                    str = rs.getString(1);
                }
            }
            catch(Exception e)
            {
                Log.v(TAG, "Error:: " + e.getMessage().toString());
            }
            return "ok";
        }

        @Override
        protected void onPostExecute(String result) {

            Log.v(TAG, "MENSAJE:: "+str);
            if(txtClave.getText().toString().equals(str)) {
                Log.v(TAG, "Login correcto:: ");
                SharedPreferences prefs = getSharedPreferences("preferencias", Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("usuario", txtUsuario.getText().toString());
                editor.putString("clave", txtClave.getText().toString());
                editor.commit();

                Intent i = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(i);
                finish();
            }else{
                Toast.makeText(LoginActivity.this,"Inicio de session no fue correcto.",Toast.LENGTH_LONG).show();
            }
            pd.dismiss();
        }



        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}

