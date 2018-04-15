package fr.eurecom.Ready2Meet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.google.firebase.auth.FirebaseAuth;

import net.igenius.customcheckbox.CustomCheckBox;

import fr.eurecom.Ready2Meet.R;
import fr.eurecom.Ready2Meet.database.Event;
import fr.eurecom.Ready2Meet.database.User;


/**
 * Created by koksa on 15.04.2018.
 */

public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    ImageView senderpicture;
    TextView senderdisplayname;
    View layout;
    private User user;
    private String userid;

    public UserViewHolder(View itemView) {
        super(itemView);
        layout = itemView;
        senderpicture = (ImageView) itemView.findViewById(R.id.senderpicture);
        senderdisplayname = (TextView) itemView.findViewById(R.id.senderdisplayname);
        itemView.setOnClickListener(this);

    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    @Override
    public void onClick(View view) {


        String otheruser = this.userid;
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

        Intent intent = new Intent(layout.getContext(), ChatActivity2.class);
        intent.putExtra("EventId", channelid);
        intent.putExtra("EventTitle", this.user.DisplayName);
        layout.getContext().startActivity(intent);



    }


}
