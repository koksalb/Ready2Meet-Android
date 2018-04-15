package fr.eurecom.Ready2Meet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView;
import com.takusemba.multisnaprecyclerview.OnSnapListener;

import java.util.ArrayList;

import fr.eurecom.Ready2Meet.database.Event;
import fr.eurecom.Ready2Meet.database.User;

public class MessagesActivity extends AppCompatActivity {






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final MultiSnapRecyclerView multiSnapRecyclerView = (MultiSnapRecyclerView) findViewById(R.id.multisnaprecylertest);

        final ArrayList<User> messagesenders = new ArrayList<User>();
        final ArrayList<String> messagesendersid = new ArrayList<String>();


        final userthumbsAdapter adapter = new userthumbsAdapter(getApplicationContext(), messagesenders,messagesendersid);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        multiSnapRecyclerView.setLayoutManager(layoutManager);
        multiSnapRecyclerView.setAdapter(adapter);
        multiSnapRecyclerView.setOnSnapListener(new OnSnapListener() {
            @Override
            public void snapped(int position) {
                // do something with the position of the snapped view
            }
        });

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("UserMessages");
        mDatabase.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String channelid = snapshot.getKey();
                    if(channelid.contains(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                    {
                        String senderid = channelid.replace(FirebaseAuth.getInstance().getCurrentUser().getUid(),"");
                        if(!messagesendersid.contains(senderid)){
                            messagesendersid.add(senderid);
                            adapter.notifyDataSetChanged();
                        }
                    }


                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("Users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            User userread = snapshot.getValue(User.class);
                            if(messagesendersid.contains(snapshot.getKey()))
                            {
                                messagesenders.add(userread);
                                adapter.notifyDataSetChanged();
                            }
                        }

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });



    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
