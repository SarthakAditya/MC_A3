package com.example.mc_a3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private WifiManager wifiManager;
    private List<ScanResult> results;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    DatabaseHelper databaseHelper;
    List<DataModel> databaselist;
    String pathsave="",filename = "";
    Sensor accelerometer;

    Timestamp timestamp;

    TextView X, Y, Z , Lat, Long,WifiAp,WifiStrength,AudioPath,TimeStamp;
    Button btnshow,btnstart,btnstop,savedata;
    String sX="",sY="",sZ="",sLat="",sLong="",date="";
    String Wnames="AP Names : \n";
    String Wstrengths="Signal Srengths : \n";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        X = (TextView) findViewById(R.id.Xval);
        Y = (TextView) findViewById(R.id.Yval);
        Z = (TextView) findViewById(R.id.Zval);
        Lat = (TextView) findViewById(R.id.Latitude);
        Long = (TextView) findViewById(R.id.Longitude);
        WifiAp = (TextView) findViewById(R.id.APname);
        WifiStrength = (TextView) findViewById(R.id.Strength);
        btnshow = (Button) findViewById(R.id.Show);
        btnstart = (Button) findViewById(R.id.Start);
        btnstop = (Button) findViewById(R.id.Stop);
        AudioPath = (TextView) findViewById(R.id.Alocation);
        TimeStamp = (TextView) findViewById(R.id.datetime);

        btnstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkaudiopermission())
                {
                    pathsave = getExternalFilesDir(null)+"/"+ UUID.randomUUID().toString()+"_audio_record.3gp";
                    Log.d("Main Activity", "Location : " + pathsave);
                    setupmediarecorder();
                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                        Toast.makeText(MainActivity.this,"Recording audio",Toast.LENGTH_LONG).show();
                    }catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    btnstart.setVisibility(View.INVISIBLE);
                    btnstop.setVisibility(View.VISIBLE);

                }
                else
                    requestPermissions();
            }
        });

        btnstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();

                btnstop.setVisibility(View.INVISIBLE);
                btnstart.setVisibility(View.VISIBLE);
            }
        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled())
        {
            Toast.makeText(this,"Enable wifi",Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }

        final long period = 5000;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                scanWifi();
            }
        }, 0, period);

        databaseHelper = new DatabaseHelper(MainActivity.this);

        btnshow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                databaselist = databaseHelper.getAllText();

                    DataModel model = databaselist.get(databaselist.size()-1);

                    X.setText("X axis : "+model.getX()+" ");
                    Y.setText("Y axis : "+model.getY()+" ");
                    Z.setText("Z axis : "+model.getZ()+" ");
                    Lat.setText("Latitude :"+ model.getLat()+" ");
                    Long.setText("Longitude :"+ model.getLong()+" ");
                    WifiAp.setText(model.getAPnam());
                    WifiStrength.setText(model.getAPstrength());
                    AudioPath.setText(model.getLocation());
                    TimeStamp.setText(model.getTime());

                    if (!model.getLocation().equals(""))
                    {
                        mediaPlayer = new MediaPlayer();
                        try {
                            mediaPlayer.setDataSource(model.getLocation());
                            mediaPlayer.prepare();
                        }catch (IOException e)
                        {
                            e.printStackTrace();
                        }

                        mediaPlayer.start();
                    }

                FileOutputStream fos = null;
                try {

                    String baseFolder;
                    if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        baseFolder = getExternalFilesDir(null).getAbsolutePath();
                    }
                    else {
                        baseFolder = getFilesDir().getAbsolutePath();
                    }

                    filename = baseFolder+ UUID.randomUUID().toString()+"_sensor_data.txt";;
                    File file = new File(filename);
                    fos = new FileOutputStream(file);
                    String write = X.getText()+"" + Y.getText()+ Z.getText()+"\n"+Lat.getText()+Long.getText()+"\n"+WifiAp.getText()+WifiStrength.getText()+"\n"+AudioPath.getText()+"\n"+TimeStamp.getText();
                    fos.write(write.getBytes());
                    Toast.makeText(MainActivity.this,"File Saved at"+ filename,Toast.LENGTH_LONG).show();
                }catch (IOException e)
                {
                    e.printStackTrace();
                }
                finally {
                    if (fos!=null)
                    {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
//                Lat.setText("Latitude :"+location.getLatitude());
//                Long.setText("Longitude :"+location.getLongitude());
                //Log.d("Main Activity", "Location data => Lattitude : " + location.getLatitude() + " Longitude : " + location.getLongitude());
                sLat = location.getLatitude()+" ";
                sLong = location.getLongitude()+" ";
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 25);
                return;
            }
        }
        else
        {
//            Log.d("Main Activity", "Location Manager : " + locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
            locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(MainActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onSensorChanged(final SensorEvent sensorEvent) {

        sX = sensorEvent.values[0]+" ";
        sY = sensorEvent.values[1]+" ";
        sZ = sensorEvent.values[2]+" ";

       //Log.d("Main Activity","Sensor data => X : "+sensorEvent.values[0] + " Y : "+sensorEvent.values[1] + " Z : "+sensorEvent.values[2]);

    }


    public void scanWifi()
    {
        registerReceiver(wifiReciver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
    }

    BroadcastReceiver wifiReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

             Wnames="AP Names : \n";
             Wstrengths="Signal Srengths : \n";
             results = wifiManager.getScanResults();
             unregisterReceiver(this);

            for (ScanResult scanResult : results)
            {
                Wnames += scanResult.SSID + "\n" ;
                Wstrengths += scanResult.level + "\n" ;
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
            date = simpleDateFormat.format(new Date());

            boolean test = databaseHelper.addText(sX,sY,sZ,sLat,sLong,Wnames,Wstrengths,pathsave,date);

            if (test) {
                Toast.makeText(MainActivity.this,"Data successfully sent",Toast.LENGTH_LONG).show();
            }
            else
                Toast.makeText(MainActivity.this,"Code phat gya",Toast.LENGTH_LONG).show();


        }
    };
    public boolean checkaudiopermission(){

        int write_external_storage = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio = ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO);
        return  write_external_storage == PackageManager.PERMISSION_DENIED && record_audio == PackageManager.PERMISSION_GRANTED;

    }

    public void requestPermissions()
    {
        ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        },10);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case 10:
                if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(MainActivity.this,"Permission Granted",Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(MainActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();

             break;
        }
    }

    public void setupmediarecorder(){
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(pathsave);

    }
}
