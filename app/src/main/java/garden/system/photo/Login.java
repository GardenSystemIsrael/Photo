package garden.system.photo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;


public class Login extends AppCompatActivity {
    //comentamos los check que se requierian antes para guardar las credenciales
    //tambien se comento las opciones de contrsaeña ya que ya se se pide

//    CheckBox chkCredenciales, chkCampoExtra;
    FloatingActionButton btnFloat, btnFloatAyuda;
    EditText code, pass, campoExtra;
//    com.google.android.material.textfield.TextInputLayout matCampo, matCode, matPass;
    TextView msgText, lblPoliticas;
    Button btnRegistra, btnAsistencia, btnCerrar, btnSignUp;
    String str_code, str_pass, str_apikey, str_luxandid;
//    String URL = "https://www.preasystweb.com/remoteApp/login.php"; URL de app pruebas
    String URL = "https://www.preasystweb.com/remoterest/PaCheckInOuts/login";
    ImageView photo;
    CardView msgCard, cardConf;
    public static final int REQUEST_CODE_PHOTO = 1;
//    private final String UPLOAD_URL = "https://www.preasystweb.com/remoteApp/evento.php"; URL de app pruebas
    private final String UPLOAD_URL = "https://www.preasystweb.com/remoterest/PaCheckInOuts/add";
    private final String VERIFY_URL = "https://www.preasystweb.com/remoterest/PaCheckInOuts/verifyPerson";
    private Bitmap bitmap;
    private final String KEY_CODE = "code";
    private final String KEY_FECHA = "datetime";
    private final String KEY_HORA = "time";
    private final String KEY_IMAGEN = "photo";
    private final String KEY_LAT = "lat";
    private final String KEY_LNG = "long";
    private final String KEY_DESC = "gpo_dispositivos";
    LocationManager locationManager;
    String latitud, longitud;
    private final int TIEMPO = 5000;
    Handler handle = new Handler();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//
//        matCampo = (com.google.android.material.textfield.TextInputLayout) findViewById(R.id.matCampo);
//        matCode = (com.google.android.material.textfield.TextInputLayout) findViewById(R.id.matCode);
//        matPass = (com.google.android.material.textfield.TextInputLayout) findViewById(R.id.matPass);
//        chkCredenciales = (CheckBox) findViewById(R.id.chkCredenciales);
//        chkCampoExtra = (CheckBox) findViewById(R.id.chkCampoExtra);
        msgText = (TextView) findViewById(R.id.lblHeadCard);
        code = (EditText) findViewById(R.id.txtCode);
//        campoExtra = (EditText) findViewById(R.id.txtCampoExtra);
//        pass = (EditText) findViewById(R.id.txtPass);
//        btnRegistra = (Button) findViewById(R.id.btnRegistrar);
        photo = (ImageView) findViewById(R.id.img);
        msgCard = (CardView) findViewById(R.id.cardMsg);
        cardConf = (CardView) findViewById(R.id.cardConf);
        btnFloat = (FloatingActionButton) findViewById(R.id.btnFloat);
        btnFloatAyuda = (FloatingActionButton) findViewById(R.id.btnFloatAyuda);
        btnAsistencia = (Button)findViewById(R.id.btnAsistencia);
        btnCerrar = (Button)findViewById(R.id.btnCerrar);
        lblPoliticas = (TextView)findViewById(R.id.lbPoliticas);

        btnSignUp = (Button) findViewById(R.id.btnSingUp);


        localizacionAlternativo();




//            code.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                @Override
//                public void onFocusChange(View v, boolean hasFocus) {
//                    if(hasFocus && !code.getText().equals("")){
//                        limpiar();
//                    }
//                }
//            });


        lblPoliticas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link = "https://www.tuprenomina.com/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(link));
                startActivity(i);
            }
        });

//        chkCampoExtra.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                saveCampo();
////                addCampoExtra();
//            }
//        });

//        chkCredenciales.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SharedPreferences preferences = getSharedPreferences("credentials", Context.MODE_PRIVATE);
//                String user = preferences.getString("user", "");
//
//                if(user.toString().equals("")){
//                    saveCredentials();
//                    addPreferences();
//                } else {
//                    deleteCredentials();
//                }
//            }
//        });

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

//        btnRegistra.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                login(v);
//            }
//        });

        btnFloatAyuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent help = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(help);
            }
        });


        btnCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarApp();
            }
        });

        btnAsistencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardConf.setVisibility(View.GONE);
                Intent signup = new Intent(getApplicationContext(), Registro.class);
                startActivity(signup);
            }
        });
    }



    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences preferences = getSharedPreferences("credentials", Context.MODE_PRIVATE);
        str_code = preferences.getString("user", "");
        str_apikey = preferences.getString("apikey", "");
        str_luxandid = preferences.getString("luxandid", "");

        if (str_apikey.toString().equals("")){
            Intent signup = new Intent(getApplicationContext(), Registro.class);
            startActivity(signup);
        } else {
//            Toast.makeText(this, "No esta vacio" + str_apikey.toString().trim(), Toast.LENGTH_SHORT).show();

        }
    }

    public void muestraItemsAsistencia(){
//        matCode.setVisibility(View.VISIBLE);
//        matPass.setVisibility(View.VISIBLE);
        btnAsistencia.setVisibility(View.GONE);
//        btnCerrar.setVisibility(View.GONE);
//        btnRegistra.setVisibility(View.VISIBLE);
        btnFloat.setVisibility(View.VISIBLE);
//        addCampoExtra();
    }

    private void alertaNoIA(){

       AlertDialog alerta = new AlertDialog.Builder(this).create();
       alerta.setTitle("Mensaje de PreAsyst");
       alerta.setMessage("por el momento no funcionan los servidores, ¿Decea tomar asistencia tradicional?");
        alerta.setButton2("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showMessageCard("No a registrado asistencia", "N");
            }
        });
        alerta.setButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                uploadData();
            }
        });
        alerta.show();
    }

    public void cerrarApp() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.camera_24)
                .setTitle("¿Desea cerrar la app?")
                .setCancelable(false)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //esto realizara funcion como forzar detencion
                        //android.os.Process.killProcess(android.os.Process.myPid());
                        //se usaria finish() para mandar app a segundo plano
                        //finish();
                        //para cerrar app
//                        finishAndRemoveTask();
                         finishAffinity();
                    }
                }).show();
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
                photo.setImageDrawable(getDrawable(R.drawable.logopre));

            }
        }, TIEMPO);
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

    public String localizacionAlternativo() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.INTERNET
            }, 1000);
        }
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = locationManager.getBestProvider(criteria, true);
        if (provider != null){
            LocationListener locationListenerBest = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    latitud = String.valueOf(location.getLatitude());
                    longitud = String.valueOf(location.getLongitude());

                }
            };
            locationManager.requestLocationUpdates(provider, 2 * 20 * 1000, 10, locationListenerBest);

        }
        return latitud + " , " + longitud;
    }

    public void ejecutaComandos(){
//        uploadData();
        verifyPerson();
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

//    public void limpiar(){
//        photo.setImageDrawable(getDrawable(R.drawable.logopre));
//        SharedPreferences preferences = getSharedPreferences("credentials", Context.MODE_PRIVATE);
//        String user = preferences.getString("user", "");
//        String contra = preferences.getString("contra", "");
//        code.setText("");
//        pass.setText("");
//        campoExtra.setText("");
//    }

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

    private void verifyPerson() {
        final ProgressDialog loading = ProgressDialog.show(this, "Verificando...", "por favor espere...", false, false);

//        Convertir bits a cadena
        bitmap = ((BitmapDrawable) photo.getDrawable()).getBitmap();
        String imagen = getStringImagen(bitmap);

        HashMap<String,String> map = new HashMap<>();
        map.put("apikey", str_apikey);
        map.put("luxandid", str_luxandid);
        map.put("img", imagen);

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, VERIFY_URL, new JSONObject(map), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject jsondata = new JSONObject(response.getString("viewVars"));
                    String msj = jsondata.getString("message");
//                    Toast.makeText(getApplicationContext(), msj, Toast.LENGTH_LONG).show();

                    if (msj.equalsIgnoreCase("no autorizado para usar ia")){
                        uploadData();
                    } else {
                        JSONObject jsonResult = new JSONObject(jsondata.getString("response"));
                        String result = jsonResult.getString("status");

                        if(result.equalsIgnoreCase("failure")){
                            showMessageCard("Persona incorrecta", "N");
                        } else {
                            uploadData();
                        }
                    }

                    loading.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
//                showMessageCard("Error: Verifique su conexion a internet", "E");
//                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                alertaNoIA();
            }

        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jor);

    }

    private void uploadData() {
        //Mostrar el diálogo de progreso
        final ProgressDialog loading = ProgressDialog.show(this, "Registrando...", "por favor espere...", false, false);

//        Convertir bits a cadena
        bitmap = ((BitmapDrawable) photo.getDrawable()).getBitmap();
        String imagen = getStringImagen(bitmap);

        //obtenemos localizacion
        String la = latitud;
        String lg = longitud;
        String lat = latitud;
        String lng = longitud;
        if(la.equals("") || la == null){
            localizacion();
            lat = latitud;
            lng = longitud;
        } else {
            lat = la;
            lng = lg;
        }

        if(lat.equalsIgnoreCase("") || lat == null){
            showMessageCard("No se pudo obtener la ubicacion", "E");
            loading.dismiss();
        } else if (str_apikey.toString().equals("")) {
            showMessageCard("Debe registrarse", "E");
            loading.dismiss();
        }  else {

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("apikey", str_apikey);
            hashMap.put("enroll_id", str_code);
            hashMap.put("lat", lat);
            hashMap.put("lng", lng);
            hashMap.put("img", imagen);

            JsonObjectRequest solicitud = new JsonObjectRequest(Request.Method.POST, UPLOAD_URL, new JSONObject(hashMap), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        JSONObject jsondata = new JSONObject(response.getString("viewVars"));
                        String result = jsondata.getString("message");

                        if(result.equalsIgnoreCase("saved")){
                            JSONObject checkInOut = new JSONObject(jsondata.getString("paCheckInOut"));
                            String hora = checkInOut.getString("check_dt");
                            String[] fecha = hora.split("T");
                            String[] dato = fecha[1].split("\\-");
                            String timedefault = fecha[0] + " a las " + dato[0] ;
                            showMessageCard("Se Registro el " + timedefault, "S");

                        } else {
                            showMessageCard(result, "E");
                        }

                        loading.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loading.dismiss();
                    showMessageCard("Error: " + error, "E");
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(solicitud);

        }

    }



}