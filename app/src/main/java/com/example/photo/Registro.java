package com.example.photo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Registro extends AppCompatActivity {

    EditText code /*, pass*/, apikey;
    TextView msgText;
    Button btnRegistra, btnBorrar;
    String str_code/*, str_pass*/, str_apikey;
    String URL = "http://192.168.15.30/remoterest/PaCheckInOuts/login";
    CardView msgCard;
    private final int TIEMPO = 5000;
    Handler handle = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        msgCard = (CardView) findViewById(R.id.cardMsg);
        msgText = (TextView) findViewById(R.id.lblHeadCard);
        btnRegistra = (Button) findViewById(R.id.btnSingUp);
        btnBorrar = (Button) findViewById(R.id.btnBorrar);
        code = (EditText) findViewById(R.id.txtCode);
//        pass = (EditText) findViewById(R.id.txtPass);
        apikey = (EditText) findViewById(R.id.txtApikey);

        addPreferences();

        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCredentials();
            }
        });

        btnRegistra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(v);
            }
        });

    }

        public void deleteCredentials(){
        SharedPreferences preferences = getSharedPreferences("credentials", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("credenciales");
        editor.remove("user");
        editor.remove("luxandid");
        editor.remove("apikey");
        editor.commit();
        limpiar();

    }

    private void limpiar() {
        code.setText("");
//        pass.setText("");
        apikey.setText("");
    }


    public void saveCredentials(String luxandid){
        SharedPreferences preferences = getSharedPreferences("credentials", Context.MODE_PRIVATE);

        if(code.getText().toString().equals("") || apikey.getText().toString().equals("") ){
            showMessageCard("Debe ingresar las credenciales", "E");
        } else {
            msgCard.setVisibility(View.GONE);
            String usuario = code.getText().toString().trim();
//            String contrasena = pass.getText().toString().trim();
            String llave = apikey.getText().toString().trim();

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("apikey", llave);
            editor.putString("user", usuario);
            editor.putString("luxandid", luxandid);
            editor.commit();

            showMessageCard("Credenciales guardadas", "S");
        }
    }

    // metodo que muestra las credenciales en los campos de la vista previamente
    // guardados en los sharepreferences
    public void addPreferences(){
        SharedPreferences preferences = getSharedPreferences("credentials", Context.MODE_PRIVATE);

        String user = preferences.getString("user", "");
//        String contra = preferences.getString("contra", "");
        String llave = preferences.getString("apikey", "");

        code.setText(user);
//        pass.setText(contra);
        apikey.setText(llave);
    }

    public void showMessageCard(String m, String tipo){
        switch (tipo){
            case "E":
                msjCardError(m);
                break;
            case "S":
                msjCardSuccess(m);
                break;
            case "N":
                msjCard(m);
                break;
        }
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                borramsgCard();
                //handle serviria para ejecutar la funcion cada cierto tiempo
//                handle.postDelayed(this, 0);

            }
        }, TIEMPO);
    }



    public void borramsgCard(){
        msgCard.setVisibility(View.GONE);
    }

    public void msjCardError(String msj){
        msgCard.setVisibility(View.VISIBLE);
        msgCard.setCardBackgroundColor(getColor(R.color.red_tenue));
        msgText.setTextColor(getColor(R.color.red));
        msgText.setText(msj);
    }

    public void msjCardSuccess(String msj){
        msgCard.setVisibility(View.VISIBLE);
        msgCard.setCardBackgroundColor(getColor(R.color.green_tenue));
        msgText.setTextColor(getColor(R.color.green));
        msgText.setText(msj);
    }

    public void msjCard(String msj){
        msgCard.setVisibility(View.VISIBLE);
        msgCard.setCardBackgroundColor(getColor(R.color.orange_tenue));
        msgText.setTextColor(getColor(R.color.orange));
        msgText.setText(msj);
    }

    // comentamos las lineas en las cuales se requeria de contraseña
    // ahora ya no se pide la contraseña
    public void login(View view){
        if(code.getText().toString().equals("")){
            showMessageCard("Ingrese el codigo", "E");
        } /*else if (pass.getText().toString().equals("")){
            showMessageCard("Ingrese la contraseña", "E");
        } */else if (apikey.getText().toString().equals("")) {
            showMessageCard("Ingrese codigo de acceso", "E");
        } else {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Un momento...");
            progressDialog.show();

            str_code = code.getText().toString().trim();
//            str_pass = pass.getText().toString().trim();
            str_apikey = apikey.getText().toString().trim();
            //obtenemos grupo de dispositivos
//            str_campo = campoExtra.getText().toString().trim();

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("apikey", str_apikey);
            hashMap.put("idemployee", str_code);
//            hashMap.put("access", str_pass);

            JsonObjectRequest solicitud = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(hashMap),new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        progressDialog.dismiss();
//                        JSONObject jsondata = new JSONObject(response.getString("viewVars"));

                        String er = response.getString("error");
                        if (er.equalsIgnoreCase("Access denied.")){
                            showMessageCard("Clave de acceso invalida", "E");
                        } else {

                            String result = response.getString("message");
                            if (result.equalsIgnoreCase("Empleado inactivo")){
                                showMessageCard(result, "E");
                            } else if (result.equalsIgnoreCase("Ingreso correctamente")){
                                String luxandid = response.getString("luxandid");
                                Toast.makeText(getApplicationContext(), luxandid, Toast.LENGTH_SHORT).show();
                                if (luxandid.equalsIgnoreCase("0")){
                                    showMessageCard("Empleado aun no enrolado", "N");
                                } else {
                                    saveCredentials(luxandid);
                                    handle.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            borramsgCard();
                                            //handle serviria para ejecutar la funcion cada cierto tiempo
                                            //handle.postDelayed(this, 0);
                                            Intent login = new Intent(getApplicationContext(), Login.class);
                                            startActivity(login);
                                        }
                                    }, TIEMPO);
                                }
                            } else {
                                showMessageCard(result, "E");
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse (VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_LONG).show();
//                    showMessageCard("Clave de acceso incorrecta" , "N");
                }

            });
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(solicitud);
        }


    }



}