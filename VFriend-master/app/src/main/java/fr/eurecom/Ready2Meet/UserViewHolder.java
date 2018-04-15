package fr.eurecom.Ready2Meet;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;

import net.igenius.customcheckbox.CustomCheckBox;

import fr.eurecom.Ready2Meet.R;
import fr.eurecom.Ready2Meet.database.Event;
import fr.eurecom.Ready2Meet.database.User;


/**
 * Created by koksa on 15.04.2018.
 */

public class UserViewHolder extends RecyclerView.ViewHolder{

    ImageView senderpicture;
    TextView senderdisplayname;
    View layout;
    private User user;


    public UserViewHolder(View itemView) {
        super(itemView);
        layout = itemView;
        senderpicture = (ImageView) itemView.findViewById(R.id.senderpicture);
        senderdisplayname = (TextView) itemView.findViewById(R.id.senderdisplayname);
    }

    public void setUser(User user) {
        this.user = user;
    }



}
