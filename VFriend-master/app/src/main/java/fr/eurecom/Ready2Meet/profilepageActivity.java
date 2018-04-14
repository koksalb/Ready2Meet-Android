package fr.eurecom.Ready2Meet;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.vision.Frame;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.eurecom.Ready2Meet.database.User;



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


        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Users/" + useridtoshow);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
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
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid());
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
