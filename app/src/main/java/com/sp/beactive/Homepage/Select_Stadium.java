package com.sp.beactive.Homepage;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.sp.beactive.Helpers.JSONParser;
import com.sp.beactive.R;



public class Select_Stadium extends AppCompatActivity {
    Button json;
    Button gotomap;
    public static String name="";
    public static double lat;
    public static double lng;
    public static TextView txtname;
    public static TextView txtlat;
    public static TextView txtlng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_stadium_main);
        json=findViewById(R.id.select);
        gotomap=findViewById(R.id.gotomap);
        txtname=findViewById(R.id.json_name);
        txtlat=findViewById(R.id.json_lat);
        txtlng=findViewById(R.id.json_lon);
        json.setOnClickListener(onJson);
        gotomap.setOnClickListener(onGotomap);




    }

   private View.OnClickListener onJson= new View.OnClickListener() {
       @Override
       public void onClick(View v) {

           PopupMenu popup = new PopupMenu(Select_Stadium.this, v);
           popup.getMenuInflater().inflate(R.menu.stadium_select_menu, popup.getMenu());
           popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
               @Override
               public boolean onMenuItemClick(MenuItem item) {
                   Toast.makeText(getApplicationContext(), "Selected Item: " +item.getTitle(), Toast.LENGTH_SHORT).show();
                   switch (item.getItemId()){
                       case R.id.jw:
                           JSONParser jsonParser = new JSONParser();
                           jsonParser.execute();
                           return true;

                       default:
                           return false;
                   }
               }
           });
           popup.show();
       }

   };
    private View.OnClickListener onGotomap = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            name = txtname.getText().toString();
            lat = Double.parseDouble(txtlat.getText().toString());
            lng = Double.parseDouble(txtlng.getText().toString());
            Intent intent = new Intent(Select_Stadium.this, Stadium_Map.class);
            intent.putExtra("name",name);
            intent.putExtra("lat",lat);
            intent.putExtra("lng",lng);
            startActivity(intent);
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
        finish();
    }
}
