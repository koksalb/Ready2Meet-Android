package fr.eurecom.Ready2Meet;

import android.content.Context;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import fr.eurecom.Ready2Meet.database.User;


/**
 * Created by koksa on 15.04.2018.
 */

public class userthumbsAdapter extends RecyclerView.Adapter<UserViewHolder>{

    private Context context;
    private ArrayList<User> users;
    private String owner;

    public userthumbsAdapter(Context context, ArrayList<User> users) {
        this.context=context;
        this.users=users;
        this.owner = owner;
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
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.row_userthumbs, parent, false);
        UserViewHolder vh = new UserViewHolder(v);
        return vh;
    }



    @Override
    public void onBindViewHolder(final UserViewHolder holder, int position) {
        final User info = users.get(position);

        holder.setUser(info);
        holder.senderdisplayname.setText(info.DisplayName);

        Picasso.with(context).load(info.ProfilePictureURL).into(holder.senderpicture);



    }









}
