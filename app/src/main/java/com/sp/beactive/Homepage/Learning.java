package com.sp.beactive.Homepage;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.sp.beactive.Helpers.JSONParser;
import com.sp.beactive.R;

import java.util.ArrayList;

import javax.xml.transform.Templates;

public class Learning extends AppCompatActivity {
    Button button;
    public static TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.learning_main);
        button=findViewById(R.id.test_json);
        textView=findViewById(R.id.json_list);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONParser jsonParser = new JSONParser();
                jsonParser.execute();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
        finish();
    }
}
