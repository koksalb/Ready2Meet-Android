package fr.eurecom.Ready2Meet;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import net.igenius.customcheckbox.CustomCheckBox;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.eurecom.Ready2Meet.R;
import fr.eurecom.Ready2Meet.database.Event;

/**
 * Created by koksa on 15.04.2018.
 */

public class eventthumbsAdapter extends RecyclerView.Adapter<EventViewHolderThumbs> {

    private Context context;
    private ArrayList<Event> events;
    private String owner;
    public eventthumbsAdapter(Context context, ArrayList<Event> events,String owner) {
        this.context=context;
        this.events=events;
        this.owner = owner;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return events != null ? events.size() : 0;
    }


    @Override
    public EventViewHolderThumbs onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.row_eventsthumbs, parent, false);
        EventViewHolderThumbs vh = new EventViewHolderThumbs(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final EventViewHolderThumbs holder, int position) {
        final Event info = events.get(position);
        holder.setEvent(info);

        holder.txtTitle.setText(info.title);


        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd 'at' hh:mm a", Locale.US);
        SimpleDateFormat formatTime = new SimpleDateFormat("hh:mm a", Locale.US);
        SimpleDateFormat formatDate = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
        try {
            Date start = format.parse(info.startTime);
            holder.txtDate.setText(formatDate.format(start));
        } catch(ParseException e) {
            e.printStackTrace();
        }

        holder.prgProgressbar.setProgress(Float.parseFloat(String.valueOf(Long.toString(info
                .current))));
        holder.prgProgressbar.setMax(Float.parseFloat(String.valueOf(Long.toString(info.capacity)
        )));

        Picasso.with(context).load(info.picture).into(holder.eventpicture);


        if(!owner.equals(info.owner))
        {
            holder.ownercrown.setVisibility(View.GONE);
        }

    }






}
