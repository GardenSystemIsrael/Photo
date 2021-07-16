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
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.SpannableString;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

    EditText code, pass;
    TextView msgText;
    Button btnRegistra, btnGps;
    String str_code, str_pass;
    String URL = "http://192.168.15.30/remoteapp/php/login.php";
    ImageView photo;
    CardView msgCard;
    public static final int REQUEST_CODE_PHOTO = 1;
    private final String UPLOAD_URL = "http://192.168.15.30/remoteapp/php/evento.php";
    private Bitmap bitmap;
    private final String KEY_CODE = "code";
    private final String KEY_FECHA = "datetime";
    private final String KEY_HORA = "time";
    private final String KEY_IMAGEN = "photo";
    private final String KEY_UBI = "lat_long";
    LocationManager locationManager;
    String latitud, longitud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        msgText = (TextView) findViewById(R.id.lblHeadCard);
        code = (EditText) findViewById(R.id.txtCode);
        pass = (EditText) findViewById(R.id.txtPass);
        btnRegistra = (Button) findViewById(R.id.btnRegistrar);
        photo = (ImageView) findViewById(R.id.img);
        msgCard = (CardView) findViewById(R.id.cardMsg);
//        btnGps = (Button) findViewById(R.id.btnGps);

//        btnGps.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                localizacion();
//            }
//        });

        btnRegistra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(v);
            }
        });

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
        msjCardSuccess("Se registro su asistencia a las: " + getHora());
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
        msjCard("Captura una selfie para asistencia");
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

    public void limpiar(){
//        photo.setImageDrawable(getDrawable(R.drawable.logopre));
        code.setText("");
        pass.setText("");
    }

    public void msjCardError(String msj){
        msgCard.setCardBackgroundColor(getColor(R.color.red_tenue));
        msgText.setTextColor(getColor(R.color.red));
        msgText.setText(msj);
    }

    public void msjCardSuccess(String msj){
        msgCard.setCardBackgroundColor(getColor(R.color.green_tenue));
        msgText.setTextColor(getColor(R.color.green));
        msgText.setText(msj);
    }

    public void msjCard(String msj){
        msgCard.setCardBackgroundColor(getColor(R.color.orange_tenue));
        msgText.setTextColor(getColor(R.color.orange));
        msgText.setText(msj);
    }

    public void login(View view){
        if(code.getText().toString().equals("")){
            msjCard("Ingrese el codigo");
        } else if (pass.getText().toString().equals("")){
            msjCard("Ingrese la contraseña");
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
                                msjCardError("Credenciales incorrectas");
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError err) {
                            //Descartar el diálogo de progreso
                            progressDialog.dismiss();
                            //Showing toast
                            msjCardError("Error de conexion");
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
        final ProgressDialog loading = ProgressDialog.show(this,"Uploanding...","Wait please...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Descartar el diálogo de progreso
                        loading.dismiss();
                        //Mostrando el mensaje de la respuesta
                        Toast.makeText(Login.this, s, Toast.LENGTH_LONG).show();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError err) {
                        //Descartar el diálogo de progreso
                        loading.dismiss();

                        //Showing toast
//                        Toast.makeText(Login.this, "Error conection", Toast.LENGTH_LONG).show();
                        msjCardError("Error de conexion");
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
                params.put(KEY_CODE, str_code);
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