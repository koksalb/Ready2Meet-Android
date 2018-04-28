package fr.eurecom.Ready2Meet;

import android.*;
import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
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

import static android.content.pm.PackageManager.PERMISSION_GRANTED;


public class ExploreFragment extends Fragment {

    private ArrayList<User> users;
    private ArrayList<String> userids;
    private ArrayList<String> distancelist;
    private Location checkerlocation;
    public ExploreFragment() {
        // Required empty public constructor
        users = new ArrayList<User>();
        userids = new ArrayList<String>();
        distancelist= new ArrayList<String>();
        users.clear();
        userids.clear();
        distancelist.clear();
        checkerlocation=new Location("dummyprovider");
        checkerlocation.setLatitude(0);
        checkerlocation.setLongitude(0);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_explore, container, false);

        final MultiSnapRecyclerView multiSnapRecyclerView = (MultiSnapRecyclerView) view.findViewById(R.id.multisnaprecylerwhoisaround);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        multiSnapRecyclerView.setLayoutManager(layoutManager);

        final whoisaroundthumbsadapter adapter = new whoisaroundthumbsadapter(getContext(), users,userids,distancelist);

        multiSnapRecyclerView.setAdapter(adapter);
        multiSnapRecyclerView.setOnSnapListener(new OnSnapListener() {
            @Override
            public void snapped(int position) {
                // do something with the position of the snapped view
            }
        });




        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission
                (getActivity().getApplicationContext(), android.Manifest.permission
                        .ACCESS_FINE_LOCATION) != PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(getActivity().getApplicationContext(), android.Manifest
                        .permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            Log.w("ExploreFragment", "No permission to get location");
            requestPermissions(new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION}, 123);

        }
        FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(this.getActivity());

        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                                          @Override
                                          public void onSuccess(Location location) {
                                              checkerlocation = location;
                                          }
                                      });


                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    User user = snapshot.getValue(User.class);

                    if(user!=null) {
                        if (!snapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            final Location checkerlocation2 = checkerlocation;
                            Location userlocation = new Location("doesntmatter");
                            userlocation.setLatitude(user.LastKnownLatitude);
                            userlocation.setLongitude(user.LastKnownLongitude);

                            float distance = checkerlocation2.distanceTo(userlocation);

                            if (distance > 50000) {

                            } else {
                                String s="";
                                /*
                                if(distance > 1000) {
                                    distance /= 1000;
                                    s = Float.toString(distance).substring(0, Math.min(Float.toString(distance).length(), 4)) + " km";
                                }
                                else{
                                    s = Float.toString(distance).substring(0, Math.min(Float.toString(distance).length(), 4)) + " m";
                                }
                                */
                                distance /= 1000;
                                s = Float.toString(distance).substring(0, Math.min(Float.toString(distance).length(), 5)) + " km";
                                users.add(user);
                                userids.add(snapshot.getKey());
                                distancelist.add(s);
                                adapter.notifyDataSetChanged();

                            }

                        }
                    }



                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return view;
    }

}
