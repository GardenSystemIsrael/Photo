package com.example.photo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.SpannableString;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;


public class Login extends AppCompatActivity {

    CheckBox chkCredenciales, chkCampoExtra;
    FloatingActionButton btnFloat, btnFloatAyuda;
    EditText code, pass, campoExtra;
    com.google.android.material.textfield.TextInputLayout materialCampo;
    TextView msgText;
    Button btnRegistra, btnGps;
    String str_code, str_pass;
    String URL = "http://192.168.15.30/remoteapp/login.php";
    ImageView photo;
    CardView msgCard, cardConf;
    public static final int REQUEST_CODE_PHOTO = 1;
    private final String UPLOAD_URL = "http://192.168.15.30/remoteapp/evento.php";
    private Bitmap bitmap;
    private final String KEY_CODE = "code";
    private final String KEY_FECHA = "datetime";
    private final String KEY_HORA = "time";
    private final String KEY_IMAGEN = "photo";
    private final String KEY_UBI = "lat_long";
    LocationManager locationManager;
    String latitud, longitud;
    private final int TIEMPO = 5000;
    Handler handle = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        materialCampo = (com.google.android.material.textfield.TextInputLayout) findViewById(R.id.materialCampo);
        chkCredenciales = (CheckBox) findViewById(R.id.chkCredenciales);
        chkCampoExtra = (CheckBox) findViewById(R.id.chkCampoExtra);
        msgText = (TextView) findViewById(R.id.lblHeadCard);
        code = (EditText) findViewById(R.id.txtCode);
        campoExtra = (EditText) findViewById(R.id.txtCampoExtra);
        pass = (EditText) findViewById(R.id.txtPass);
        btnRegistra = (Button) findViewById(R.id.btnRegistrar);
        photo = (ImageView) findViewById(R.id.img);
        msgCard = (CardView) findViewById(R.id.cardMsg);
        cardConf = (CardView) findViewById(R.id.cardConf);
        btnFloat = (FloatingActionButton) findViewById(R.id.btnFloat);
        btnFloatAyuda = (FloatingActionButton) findViewById(R.id.btnFloatAyuda);

        addPreferences();

        chkCampoExtra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCampo();
                addCampoExtra();
            }
        });

        chkCredenciales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("credentials", Context.MODE_PRIVATE);
                String user = preferences.getString("user", "");

                if(user.toString().equals("")){
                    saveCredentials();
                    addPreferences();
                } else {
                    deleteCredentials();
                }
            }
        });

        btnFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cardConf.getVisibility() == View.GONE){
                    cardConf.setVisibility(View.VISIBLE);
                } else if (cardConf.getVisibility() == View.VISIBLE) {
                    cardConf.setVisibility(View.GONE);
                }

            }
        });

        btnRegistra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(v);
            }
        });

        btnFloatAyuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent help = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(help);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
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




    public void addCampoExtra(){
        if (chkCampoExtra.isChecked()){
            materialCampo.setVisibility(View.VISIBLE);
        } else if (!chkCampoExtra.isChecked()) {
            materialCampo.setVisibility(View.GONE);
        }
    }

    public void deleteCredentials(){
        SharedPreferences preferences = getSharedPreferences("credentials", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("credenciales");
        editor.remove("user");
        editor.remove("contra");
        editor.commit();
        limpiar();

    }

    public void deleteCampo(){
        SharedPreferences preferences = getSharedPreferences("credentials", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("campo");
        editor.commit();
    }

    public void addPreferences(){
        SharedPreferences preferences = getSharedPreferences("credentials", Context.MODE_PRIVATE);

        String user = preferences.getString("user", "");
        String contra = preferences.getString("contra", "");
        Boolean campoBool = preferences.getBoolean("campo", false);
        Boolean credentialBool = preferences.getBoolean("credenciales", false);

        code.setText(user);
        pass.setText(contra);
        chkCampoExtra.setChecked(campoBool);
        chkCredenciales.setChecked(credentialBool);
        addCampoExtra();
    }

    public void saveCredentials(){
        SharedPreferences preferences = getSharedPreferences("credentials", Context.MODE_PRIVATE);

        if(code.getText().toString().equals("") || pass.getText().toString().equals("")){
            showMessageCard("Debe ingresar las credenciales", "E");
        } else {
            msgCard.setVisibility(View.GONE);
            String usuario = code.getText().toString().trim();
            String contrasena = pass.getText().toString().trim();
            Boolean checkCredential = chkCredenciales.isChecked();

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("user", usuario);
            editor.putString("contra", contrasena);
            editor.putBoolean("credenciales", checkCredential);
            editor.commit();
        }

    }

    public void saveCampo(){
        SharedPreferences preferences = getSharedPreferences("credentials", Context.MODE_PRIVATE);
        Boolean checkCampo = chkCampoExtra.isChecked();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("campo", checkCampo);
        editor.commit();
    }


    public String localizacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.INTERNET
            }, 1000);
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(locationManager!=null){
            latitud = String.valueOf(loc.getLatitude());
            longitud = String.valueOf(loc.getLongitude());
        }
            return latitud + " , " + longitud;
    }

    public void ejecutaComandos(){
        uploadData();
        limpiar();
        showMessageCard("Se registro su asistencia a las: " + getHora(), "S");
    }

    private String getFecha() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private String getHora(){
        DateFormat dt = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        return dt.format(date);
    }

    public void takePhoto(){
        Intent picture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(picture, REQUEST_CODE_PHOTO);
        showMessageCard("Captura una selfie para asistencia", "N");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PHOTO && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap img = (Bitmap) extras.get("data");
            photo.setImageBitmap(img);
            ejecutaComandos();
        }
    }

    public String getStringImagen(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public void borramsgCard(){
        msgCard.setVisibility(View.GONE);
    }

    public void limpiar(){
//        photo.setImageDrawable(getDrawable(R.drawable.logopre));
//        SharedPreferences preferences = getSharedPreferences("credentials", Context.MODE_PRIVATE);
//        String user = preferences.getString("user", "");
//        String contra = preferences.getString("contra", "");
        code.setText("");
        pass.setText("");
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

    public void login(View view){
        if(code.getText().toString().equals("")){
            showMessageCard("Ingrese el codigo", "E");
        } else if (pass.getText().toString().equals("")){
            showMessageCard("Ingrese la contraseña", "E");
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Un momento...");
            progressDialog.show();

            str_code = code.getText().toString().trim();
            str_pass = pass.getText().toString().trim();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            //Descartar el diálogo de progreso
                            progressDialog.dismiss();
                            if(s.equalsIgnoreCase("ingreso correctamente")){

                                takePhoto();

                            } else {
//                                Toast.makeText(Login.this, s, Toast.LENGTH_SHORT).show();
                                showMessageCard("Credenciales incorrectas", "E");
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError err) {
                            //Descartar el diálogo de progreso
                            progressDialog.dismiss();
                            //Showing toast
                            showMessageCard("Error, compruebe su red", "E");
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new Hashtable<String, String>();

                    //Agregando de parámetros
                    params.put("code", str_code);
                    params.put("pass", str_pass);

                    //Parámetros de retorno
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(Login.this);
            requestQueue.add(stringRequest);

        }

    }


    private void uploadData(){
        //Mostrar el diálogo de progreso
        final ProgressDialog loading = ProgressDialog.show(this,"Registrando...","por favor espere...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Descartar el diálogo de progreso
                        loading.dismiss();
                        //Mostrando el mensaje de la respuesta
//                        msjCardSuccess(s.toString());

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError err) {
                        //Descartar el diálogo de progreso
                        loading.dismiss();

                        //Showing toast
//                        Toast.makeText(Login.this, "Error conection", Toast.LENGTH_LONG).show();
                        showMessageCard("Error al registrar asistencia", "E");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Convertir bits a cadena
                bitmap = ((BitmapDrawable) photo.getDrawable()).getBitmap();
                String imagen = getStringImagen(bitmap);

                //Obtener el nombre de la imagen
                String nombre = "prueba";

                //obtenemos la fecha
                String fecha = getFecha();

                //obtenemos la hora
                String hora = getHora();

                //obtenemos localizacion
                String lat_long = localizacion();

                //Creación de parámetros
                Map<String,String> params = new Hashtable<String, String>();

                //Agregando de parámetros
                params.put(KEY_CODE, str_code.toUpperCase());
                params.put(KEY_FECHA, fecha);
                params.put(KEY_HORA, hora);
                params.put(KEY_IMAGEN, imagen);
                params.put(KEY_UBI, lat_long);

                //Parámetros de retorno
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


}