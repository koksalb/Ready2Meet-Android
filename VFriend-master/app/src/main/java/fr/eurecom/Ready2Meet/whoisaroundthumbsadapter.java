package fr.eurecom.Ready2Meet;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import fr.eurecom.Ready2Meet.database.Event;
import fr.eurecom.Ready2Meet.database.User;


public class whoisaroundthumbsadapter extends RecyclerView.Adapter<WhoisaroundViewHolderThumbs> {

    private Context context;
    private ArrayList<User> users;
    private ArrayList<String> userids;
    private ArrayList<String> locationlist;

    public whoisaroundthumbsadapter(Context context, ArrayList<User> users, ArrayList<String> userids, ArrayList<String> locationlist) {
        this.context=context;
        this.users=users;
        this.userids = userids;
        this.locationlist = locationlist;
        this.context = context;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return users != null ? users.size() : 0;
    }


    @Override
    public WhoisaroundViewHolderThumbs onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.row_whoisaroundthumbs, parent, false);
        WhoisaroundViewHolderThumbs vh = new WhoisaroundViewHolderThumbs(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final WhoisaroundViewHolderThumbs holder, int position) {
        final User info = users.get(position);
        final String infoid = userids.get(position);
        final String infodistance = locationlist.get(position);
        holder.setUser(info);
        holder.setUserid(infoid);
        holder.setDistance(infodistance);

        holder.username.setText(info.DisplayName);
        holder.userdistance.setText(infodistance);


        Picasso.with(context).load(info.ProfilePictureURL).into(holder.userpicture);

    holder.messagebutton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        String otheruser = infoid;
        String thisuser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String channelid = "";
        if(otheruser.compareTo(thisuser)<0)
        {
            channelid = otheruser + thisuser;
        }
        else
        {
            channelid = thisuser + otheruser;
        }


        Intent intent = new Intent(context, ChatActivity2.class);
        intent.putExtra("EventId", channelid);
        intent.putExtra("EventTitle", info.DisplayName.toString());
        context.startActivity(intent);
    }
    });


        FirebaseDatabase.getInstance().getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Following").child(infoid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null)
                {
                    holder.followbutton.setText("Unfollow");
                }
                else
                {
                    holder.followbutton.setText("Follow");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


holder.userpicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, profilepageActivity.class);
                intent.putExtra("userid", infoid);
                context.startActivity(intent);
            }
        });




    holder.followbutton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {


            final String follower = FirebaseAuth.getInstance().getCurrentUser().getUid();
            final String followed = infoid;

            final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Users/" + follower + "/Following");
            mDatabase.child(followed).addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue()!=null)
                    {
                        mDatabase.child(followed).removeValue();
                        DatabaseReference mDatabase2 = FirebaseDatabase.getInstance().getReference("Users/" + followed + "/Followers");
                        mDatabase2.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                    }
                    else
                    {
                        mDatabase.child(followed).setValue(true);
                        DatabaseReference mDatabase2 = FirebaseDatabase.getInstance().getReference("Users/" + followed + "/Followers");
                        mDatabase2.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);


                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });







        }
    });



        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd 'at' hh:mm a", Locale.US);
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(format.parse(info.PremiumTill));
        }catch(Exception e){}

        Date premiumtilldate = cal.getTime();

        Date today = Calendar.getInstance().getTime();

        if(today.after(premiumtilldate))
        {
            holder.premiumstar.setVisibility(View.GONE);
        }
        else
        {
            holder.premiumstar.setVisibility(View.VISIBLE);

        }









    }






}
