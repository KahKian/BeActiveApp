package com.sp.beactive.Homepage;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sp.beactive.Helpers.ChatMessage;
import com.sp.beactive.R;

import org.w3c.dom.Text;

import java.util.Objects;

public class Community extends AppCompatActivity {
    private DatabaseReference ref;
    private FirebaseListAdapter<ChatMessage> adapter;
    ListView listofMessages;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community_main);
        //final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference("chatroom/");
        listofMessages = findViewById(R.id.list_of_messages);

        displayChatMessages();


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = findViewById(R.id.input);
                ref.push().setValue(new ChatMessage(input.getText().toString(),
                        Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName()));
                input.setText("");
                //scrollBottom();

            }
        });
    }

    private void displayChatMessages() {

        listofMessages = findViewById(R.id.list_of_messages);

        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
                R.layout.message, ref) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                TextView messageText = v.findViewById(R.id.message_text);
                TextView messageUser = v.findViewById(R.id.message_user);
                TextView messageTime = v.findViewById(R.id.message_time);

                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());

                messageTime.setText(DateFormat.format("dd-MM(HH:mm)", model.getMessageTime()));
            }
        };
        listofMessages.setAdapter(adapter);
        //scrollBottom();
    }

    private void scrollBottom(){
        listofMessages.smoothScrollToPosition(listofMessages.getCount()-1);
    }

        @Override
        public void onBackPressed(){
            super.onBackPressed();
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
            finish();
        }
    }

