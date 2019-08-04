package com.sp.beactive.Homepage;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.sp.beactive.Helpers.GoalsHelper;
import com.sp.beactive.R;

import java.util.ArrayList;

public class Goals extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private EditText mGoals;
    private Button btnAddGoals;
    private ListView list;

    private ArrayList<String> items;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goals_main);

        mGoals = findViewById(R.id.Goals);
        btnAddGoals = findViewById(R.id.addGoals);
        list = findViewById(R.id.list);

        items = GoalsHelper.readData(this);

        adapter = new ArrayAdapter<String>(this, R.layout.listofgoals,R.id.goals_text, items);
        list.setAdapter(adapter);
        btnAddGoals.setOnClickListener(this);
        list.setOnItemClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addGoals:
                String goalEntered=mGoals.getText().toString();
                adapter.add(goalEntered);
                mGoals.setText("");

                GoalsHelper.writeData(items, this);

                Toast.makeText(this,"Goal added!",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        items.remove(position);
        adapter.notifyDataSetChanged();
        GoalsHelper.writeData(items,this);
        Toast.makeText(this, "Goal Reached!",Toast.LENGTH_SHORT).show();
    }
}
