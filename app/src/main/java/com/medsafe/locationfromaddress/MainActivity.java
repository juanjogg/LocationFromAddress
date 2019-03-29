package com.medsafe.locationfromaddress;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private Geocoder geocoder;
    private Button button, uploadBtn;
    private EditText etAddress;
    private TextView textView;
    private static final int REQUEST_CODE = 42;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static boolean permissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        geocoder = new Geocoder(this, Locale.getDefault());

        button = findViewById(R.id.sendButton);
        etAddress = findViewById(R.id.addressText);
        textView = findViewById(R.id.txtViewResult);
        uploadBtn = findViewById(R.id.uploadBtn);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getLocation(etAddress.getText().toString());
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFilePath();
            }
        });
        getStoragePermission();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Uri uri;
            if(data != null){
                uri = data.getData();
                readTextFile(uri);

                //Toast.makeText(this, result,Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getFilePath() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        startActivityForResult(intent, REQUEST_CODE);
    }

    private String getLocation(String address){
        List<Address> addresses;
        String result = "";
        try {
            addresses = geocoder.getFromLocationName(address.concat(" medellin, antioquia, colombia"),1);
            Address address1 = addresses.get(0);
            result = address1.getLatitude() + " " + address1.getLongitude();

        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return result;
    }
    private void readTextFile(Uri uri){
        if(permissionGranted){
            BufferedReader reader = null;
            StringBuilder builder = new StringBuilder();
            //Toast.makeText(this, uri.toString(), Toast.LENGTH_LONG).show();
            File file = new File(uri.getPath());

            try{
                reader = new BufferedReader(new FileReader(file));
                String line = "";

                while ((line = reader.readLine()) != null){
                    line = getLocation(line);
                    builder.append(line);
                    builder.append('\n');
                }

            }
            catch (IOException e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
            finally {
                if(reader != null){
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        else{
            Toast.makeText(this, "No tiene permisos para realizar esta accion", Toast.LENGTH_LONG).show();
        }


    }

    private void getStoragePermission(){

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                permissionGranted = true;
            }
            else {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_CODE);
            }
        }
        else {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_CODE);

        }
    }

    private void writeFile(String data){

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE: {
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            permissionGranted = false;
                            return;
                        }
                    }
                    permissionGranted = true;
                }
            }
        }
    }
}
