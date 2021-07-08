package com.example.photo;

import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
    TextView msg;
    Button btnRegistra;
    String str_code, str_pass;
    String URL = "http://192.168.15.30/remoteapp/php/login.php";
    ImageView photo;
    public static final  int REQUEST_CODE_PHOTO = 1;
    private String UPLOAD_URL ="http://192.168.15.30/remoteapp/php/evento.php";
    private Bitmap bitmap;
    private String KEY_CODE = "code";
    private String KEY_FECHA = "datetime";
    private String KEY_HORA = "time";
    private String KEY_IMAGEN = "photo";
    private String KEY_UBI = "lat_long";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        msg = (TextView)findViewById(R.id.lblHeadCard);
        code = (EditText)findViewById(R.id.txtCode);
        pass = (EditText)findViewById(R.id.txtPass);
        btnRegistra = (Button)findViewById(R.id.btnRegistrar);
        photo = (ImageView)findViewById(R.id.img);

        btnRegistra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(v);
            }
        });

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

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PHOTO && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap img = (Bitmap) extras.get("data");
            photo.setImageBitmap(img);
        }
        uploadData();
        limpiar();
    }

    public String getStringImagen(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public void limpiar(){
        photo.setImageDrawable(getDrawable(R.drawable.logopre));
        msg.setText("Se Registro su asistencia");
        msg.setTextColor(getColor(R.color.green));
        code.setText("");
        pass.setText("");
    }

    public void login(View view){
        if(code.getText().toString().equals("")){
            Toast.makeText(this, "Ingrese el codigo", Toast.LENGTH_SHORT).show();
        } else if (pass.getText().toString().equals("")){
            Toast.makeText(this, "Ingrese la contraseña", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(Login.this, s.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError err) {
                            //Descartar el diálogo de progreso
                            progressDialog.dismiss();
                            //Showing toast
                            Toast.makeText(Login.this, "Error conection", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(Login.this, s.toString() , Toast.LENGTH_LONG).show();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError err) {
                        //Descartar el diálogo de progreso
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(Login.this, "Error conection", Toast.LENGTH_LONG).show();
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

                //Creación de parámetros
                Map<String,String> params = new Hashtable<String, String>();

                //Agregando de parámetros
                params.put(KEY_CODE, str_code);
                params.put(KEY_FECHA, fecha);
                params.put(KEY_HORA, hora);
                params.put(KEY_IMAGEN, imagen);
                //params.put(KEY_UBI, lat_long);

                //Parámetros de retorno
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


}