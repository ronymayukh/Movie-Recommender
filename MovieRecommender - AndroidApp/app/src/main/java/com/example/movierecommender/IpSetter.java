package com.example.movierecommender;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class IpSetter extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip_setter);

        EditText etIp = findViewById(R.id.etIp);
        Button btnSetIp = findViewById(R.id.btnSetIp);

        btnSetIp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentIp = etIp.getText().toString();
                USER_ID.setLocalIP(currentIp);

                Intent i = new Intent(getApplicationContext(),UserLogin.class);
                startActivity(i);

            }
        });

    }
}