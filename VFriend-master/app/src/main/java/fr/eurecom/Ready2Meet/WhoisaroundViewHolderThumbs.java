package fr.eurecom.Ready2Meet;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;

import fr.eurecom.Ready2Meet.database.Event;
import fr.eurecom.Ready2Meet.database.User;


public class WhoisaroundViewHolderThumbs extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView username, userdistance;
    ImageView userpicture, premiumstar;
    Button followbutton, messagebutton;

    View layout;
    private User user;
    private String userid;
    private String distance;

    public WhoisaroundViewHolderThumbs(View itemView) {
        super(itemView);
        layout = itemView;

        username = (TextView) itemView.findViewById(R.id.username);

        userdistance = (TextView) itemView.findViewById(R.id.userdistance);

        userpicture = (ImageView) itemView.findViewById(R.id.userpicture);

        premiumstar = (ImageView) itemView.findViewById(R.id.premiumstar);

        followbutton = (Button) itemView.findViewById(R.id.followbutton);

        messagebutton = (Button) itemView.findViewById(R.id.messagebutton);

        itemView.setOnClickListener(this);
    }

    public void setUser(User user) {
        this.user = user;
    }
    public void setUserid(String userid) {
        this.userid = userid;
    }
    public void setDistance(String distance) {
        this.distance = distance;
    }
    @Override
    public void onClick(View view) {
        /*
        ((Activity) view.getContext()).findViewById(R.id.tabs).setVisibility(View.GONE);
        EventDetailFragment fragment = new EventDetailFragment();
        fragment.setEvent(event.id);
        FragmentTransaction ft = ((Main2Activity) view.getContext()).getSupportFragmentManager()
                .beginTransaction();
        ft.replace(R.id.frame_all_events, fragment);
        ft.addToBackStack(Main2Activity.TAG_EVENT_DETAIL_FRAGMENT);
        ft.commit();
        */
    }
}
