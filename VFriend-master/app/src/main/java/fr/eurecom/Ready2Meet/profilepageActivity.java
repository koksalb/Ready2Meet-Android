package fr.eurecom.Ready2Meet;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Line;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.eurecom.Ready2Meet.database.Event;
import fr.eurecom.Ready2Meet.database.User;
import fr.eurecom.Ready2Meet.uiExtensions.ImageArrayAdapter;


public class profilepageActivity extends AppCompatActivity {

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd 'at' hh:mm a", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilepage);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Serializable incomingid = getIntent().getSerializableExtra("userid");
        final String useridtoshow = incomingid.toString();

        final TextView displaynameText = (TextView) findViewById(R.id.displaynametext);
        final TextView locationText = (TextView) findViewById(R.id.locationtext);
        final CircleImageView profileimgView = (CircleImageView) findViewById(R.id.profilePictureCircleImageView);
        final TextView descriptionText = (TextView) findViewById(R.id.descriptionText);
        final EditText descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);


        final TextView followerstext = (TextView) findViewById(R.id.followerstext);
        final TextView followingtext = (TextView) findViewById(R.id.followingtext);
        final TextView categoriestext = (TextView) findViewById(R.id.categoriestext);

        final FrameLayout premiumframe = (FrameLayout) findViewById(R.id.premiumFrame);
        final TextView premiumdayslefttext = (TextView) findViewById(R.id.premiumdayslefttext);

        final Button followbutton = (Button) findViewById(R.id.followbutton);
        final Button blockbutton = (Button) findViewById(R.id.blockbutton);

        final LinearLayout participatingsLayout =(LinearLayout) findViewById(R.id.participatingsLayout);

        final ArrayList<Event> eventlist = new ArrayList<Event>();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Users/" + useridtoshow);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                final User user = snapshot.getValue(User.class);
                displaynameText.setText(user.DisplayName);
                descriptionText.setText(user.Description);
                descriptionEditText.setText(user.Description);
                locationText.setText("("+user.LastKnownCity+ " / " + user.LastKnownCountry +")");

                Picasso.with(getApplicationContext()).load(user.ProfilePictureURL).fit().into(profileimgView);

            if(user.Followers!=null) {
                    followerstext.setText("Followers (" + user.Followers.size() + ")");
            }
            else
            {
                followerstext.setText("Followers (0)");
            }

                if(user.Following!=null) {
                    followingtext.setText("Followers (" + user.Following.size() + ")");
                }
                else
                {
                    followingtext.setText("Followers (0)");
                }

                if(user.InterestedCategories!=null) {
                    categoriestext.setText("Categories (" + user.InterestedCategories.size() + ")");
                }
                else
                {
                    categoriestext.setText("Categories (0)");
                }

                Calendar cal = Calendar.getInstance();
            try {
                cal.setTime(format.parse(user.PremiumTill));
            }catch(Exception e){}

                Date premiumtilldate = cal.getTime();

                Date today = Calendar.getInstance().getTime();

                if(today.after(premiumtilldate))
                {
                    premiumframe.setVisibility(View.GONE);
                }
                else
                {
                    premiumframe.setVisibility(View.VISIBLE);
                    premiumdayslefttext.setText(String.valueOf(getDifferenceDays(today,premiumtilldate)));
                }



                if(useridtoshow.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                {
                    followbutton.setEnabled(false);
                    blockbutton.setEnabled(false);
                    descriptionText.setVisibility(View.GONE);
                    descriptionEditText.setVisibility(View.VISIBLE);

                }
                else
                {
                    followbutton.setEnabled(true);
                    blockbutton.setEnabled(true);
                    descriptionText.setVisibility(View.VISIBLE);
                    descriptionEditText.setVisibility(View.GONE);
                }




                followerstext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupMenu menu = new PopupMenu(getApplicationContext(), view);
                        if(user.Followers==null){return;}
                        for(String key : user.Followers.keySet()) {
                                menu.getMenu().add(key);
                        }

                        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {


                                    default:
                                        Intent intent = new Intent(getApplication(), profilepageActivity.class);
                                        intent.putExtra("userid", item.toString());
                                        startActivity(intent);
                                        return true;
                                             }
                            }
                        });
                        menu.show();
/*
                        final Spinner spinner = (Spinner) findViewById(R.id.followers_spinner);
                        final List<String> participants = new ArrayList<>();
                        for(Map.Entry<String, Boolean> p : user.Followers.entrySet()) {
                            participants.add(p.getKey());
                        }
                        SpinnerAdapter adapter = new ImageArrayAdapter(getApplicationContext(), R.layout
                                .participant_spinner_row, participants);
                        spinner.setAdapter(adapter);
                        spinner.setVisibility(View.VISIBLE);
*/

                    }
                });

                followingtext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupMenu menu = new PopupMenu(getApplicationContext(), view);
                        if(user.Following==null){return;}
                        for(String key : user.Following.keySet()) {
                            menu.getMenu().add(key);
                        }

                        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {


                                    default:
                                        Intent intent = new Intent(getApplication(), profilepageActivity.class);
                                        intent.putExtra("userid", item.toString());
                                        startActivity(intent);
                                        return true;
                                }
                            }
                        });
                        menu.show();
/*
                        final Spinner spinner = (Spinner) findViewById(R.id.followers_spinner);
                        final List<String> participants = new ArrayList<>();
                        for(Map.Entry<String, Boolean> p : user.Followers.entrySet()) {
                            participants.add(p.getKey());
                        }
                        SpinnerAdapter adapter = new ImageArrayAdapter(getApplicationContext(), R.layout
                                .participant_spinner_row, participants);
                        spinner.setAdapter(adapter);
                        spinner.setVisibility(View.VISIBLE);
*/

                    }
                });




                categoriestext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupMenu menu = new PopupMenu(getApplicationContext(), view);

                        if (user.InterestedCategories != null) {
                            for (String key : user.InterestedCategories.keySet()) {
                                menu.getMenu().add(key);
                            }

                            menu.show();

                        }
                    }
                });






                if(user.ParticipatingEvents==null){return;}
                for(final String key : user.ParticipatingEvents.keySet()) {
                    FirebaseDatabase.getInstance().getReference().child("Events").child(key).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // This method is called once with the initial value and again
                            // whenever data at this location is updated.
                            Event value = dataSnapshot.getValue(Event.class);
                            Log.d("test", "Value is: " + value.title);
                            if(!eventlist.contains(value)){
                                eventlist.add(value);

                                //add owner first!
                                StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl
                                        ("gs://ready2meet-e0286.appspot.com/EventPhotos/" + key +"/startPhoto");


                                LinearLayout eventlayout = new LinearLayout(participatingsLayout.getContext());
                                eventlayout.setOrientation(LinearLayout.VERTICAL);
                                LinearLayout.LayoutParams test2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.MATCH_PARENT);
                                eventlayout.setLayoutParams(test2);
                                eventlayout.setBackgroundColor(Color.RED);
                                ImageView ii = new ImageView(eventlayout.getContext());
                                LinearLayout.LayoutParams test = new LinearLayout.LayoutParams(600, 600);
                                ii.setLayoutParams(test);

                                eventlayout.addView(ii);

                                TextView valueTV = new TextView(eventlayout.getContext());
                                valueTV.setText(value.title);
                                valueTV.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                                eventlayout.addView(valueTV);


                                participatingsLayout.addView(eventlayout);
                                Glide.with(getApplicationContext()).using(new FirebaseImageLoader()).load
                                        (storageRef).into(ii);




                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            Log.w("test", "Failed to read value.", error.toException());
                        }
                    });
                }

//end
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // TODO: Error handling
            }
        });


        followbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Following");
                mDatabase.child(useridtoshow).setValue(true);

                mDatabase = FirebaseDatabase.getInstance().getReference("Users/" + useridtoshow + "/Followers");
                mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);


            }
        });



        blockbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/BlockedUsers");
                mDatabase.child(useridtoshow).setValue(true);
            }
        });

        descriptionEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

                String newdescription = s.toString();
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Users/" + useridtoshow);
                mDatabase.child("Description").setValue(newdescription);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {


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

    public static long getDifferenceDays(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }








}
