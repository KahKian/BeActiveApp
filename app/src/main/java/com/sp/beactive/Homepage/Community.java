package com.sp.beactive.Homepage;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sp.beactive.Helpers.ChatMessage;
import com.sp.beactive.Helpers.PhotoUpload;
import com.sp.beactive.Helpers.UserDetails;
import com.sp.beactive.R;

import org.w3c.dom.Text;

import java.io.File;
import java.util.Objects;

public class Community extends AppCompatActivity {

    private static final String TAG = "Community";
    private DatabaseReference ref;
    private DatabaseReference databaseReference;
    private FirebaseListAdapter<ChatMessage> adapter;
    private FirebaseAuth mAuth;
    ListView listofMessages;
    private ValueEventListener nicknamelistener;
    private ValueEventListener mPhotoListener;
    String age="";
    String nickname="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community_main);
        ref = FirebaseDatabase.getInstance().getReference("chatroom/");
        listofMessages = findViewById(R.id.list_of_messages);
        mAuth=FirebaseAuth.getInstance();
        final String uid= Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        final String name=mAuth.getCurrentUser().getDisplayName();
        databaseReference = FirebaseDatabase.getInstance().getReference("users/"+ uid+"/profile");

        //GET CHAT USERNAME FROM FIREBASE
        nicknamelistener=databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserDetails userDetails = dataSnapshot.getValue(UserDetails.class);
                assert userDetails!=null;
                nickname = " ("+userDetails.username+")";
                age = userDetails.age;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                Toast.makeText(Community.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
            }
        });
        databaseReference.addValueEventListener(nicknamelistener);

        displayChatMessages();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = findViewById(R.id.input);
                ref.push().setValue(new ChatMessage(input.getText().toString(),
                        name+nickname+" Age: "+age));
                input.setText("");

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
    }

        @Override
        public void onBackPressed(){
            super.onBackPressed();
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
            finish();
        }

    @Override
    protected void onStop() {
        super.onStop();
        databaseReference.removeEventListener(nicknamelistener);
    }
}

