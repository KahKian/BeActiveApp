package com.sp.beactive.Homepage;

import android.app.Dialog;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sp.beactive.Helpers.RemindersHelper;
import com.sp.beactive.OneTimeAlertDialog;
import com.sp.beactive.R;

import java.util.ArrayList;
import java.util.Objects;

public class Reminders extends AppCompatActivity implements View.OnClickListener{
    private EditText mGoals;
    private Button btnAddGoals;
    private ListView list;

    private ArrayList<String> items;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminders_main);

        mGoals = findViewById(R.id.Goals);
        btnAddGoals = findViewById(R.id.addGoals);
        list = findViewById(R.id.list);
        items = RemindersHelper.readData(this);
        registerForContextMenu(list);
        adapter = new ArrayAdapter<>(this, R.layout.reminders_list, R.id.goals_text, items);
        list.setAdapter(adapter);
        btnAddGoals.setOnClickListener(this);


        Intent intent = getIntent();
        if(intent.hasExtra("meetup")){
            mGoals.setText(Objects.requireNonNull(intent.getExtras()).getString("meetup"));
        }
        new OneTimeAlertDialog.Builder(this, "reminders_dialog")
                .setTitle("Hey there")
                .setMessage("This is Reminders, a place to keep your notes and meet-ups. Long click on an item to delete or add information.")
                .show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Pick an Action");
        getMenuInflater().inflate(R.menu.remindersmenu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int pos = info.position;

        switch (item.getItemId()){
            case R.id.add_info:
                showInputBox(items.get(pos),pos);
                return true;
            case R.id.delete:
                items.remove(pos);
                adapter.notifyDataSetChanged();
                RemindersHelper.writeData(items,this);
                Toast.makeText(this, "Reminder deleted!",Toast.LENGTH_SHORT).show();
                return true;
                default:
                    return super.onContextItemSelected(item);
        }

    }
    public void showInputBox(String oldItem, final int index){
        final Dialog dialog=new Dialog(Reminders.this);
        dialog.setTitle("Input Box");
        dialog.setContentView(R.layout.edit_list_item);
        TextView txtHeader=dialog.findViewById(R.id.txtheader);
        txtHeader.setText("Update Reminder/Note");
        txtHeader.setTextColor(Color.parseColor("#000000"));
        final EditText editText=dialog.findViewById(R.id.txtinput);
        editText.setText(oldItem);
        Button btn=dialog.findViewById(R.id.btndone);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                items.set(index,editText.getText().toString());
                adapter.notifyDataSetChanged();
                RemindersHelper.writeData(items,Reminders.this);
                dialog.dismiss();
                Toast.makeText(Reminders.this, "Reminder updated!",Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addGoals:
                String goalEntered=mGoals.getText().toString();
                adapter.add(goalEntered);
                mGoals.setText("");

                RemindersHelper.writeData(items, this);

                Toast.makeText(this,"Reminder added!",Toast.LENGTH_SHORT).show();
                break;
        }
    }

}
