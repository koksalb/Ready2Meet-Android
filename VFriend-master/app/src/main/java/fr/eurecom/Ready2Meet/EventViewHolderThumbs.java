package fr.eurecom.Ready2Meet;

import android.app.Activity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;

import net.igenius.customcheckbox.CustomCheckBox;

import fr.eurecom.Ready2Meet.database.Event;

public class EventViewHolderThumbs extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView txtTitle, txtDate;
    ImageView eventpicture;
    FrameLayout ownercrown;
    com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar prgProgressbar;

    View layout;
    private Event event;

    public EventViewHolderThumbs(View itemView) {
        super(itemView);
        layout = itemView;

        txtTitle = (TextView) itemView.findViewById(R.id.txteventname);

        txtDate = (TextView) itemView.findViewById(R.id.txtdate);

        eventpicture = (ImageView) itemView.findViewById(R.id.eventpicture);

        prgProgressbar = (RoundCornerProgressBar) itemView.findViewById(R.id.eventprogress);

        ownercrown = (FrameLayout) itemView.findViewById(R.id.ownercrown) ;
        itemView.setOnClickListener(this);
    }

    public void setEvent(Event event) {
        this.event = event;
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
