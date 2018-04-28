package fr.eurecom.Ready2Meet;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import fr.eurecom.Ready2Meet.database.Event;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Fragment for the landing page. Displays events in a map view.
 */
public class DashboardFragment extends Fragment implements OnMapReadyCallback, SharedPreferences
        .OnSharedPreferenceChangeListener {

    private GoogleMap googleMap;

    private double maxPeople = 1;

    private LocationRequest mLocationRequest;


    public DashboardFragment() {
        // Required empty public constructor
    }

    private List<Event> eventlist = new ArrayList<>();

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(AllEvents.FILTER_KEY)) {
            Set<String> filterStrings = sharedPreferences.getStringSet(key, null);
            showEventsInMap(filterStrings);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        final SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        // Get all events (except default event) and store them in the list.
        FirebaseDatabase.getInstance().getReference().child("Events")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventlist.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    /*
                    if(snapshot.getKey().equals("-L0AEWfuhQx3DjXz7H6Q")) {
                        continue;
                    }
                    */
                    Event eventread = snapshot.getValue(Event.class);
                    eventread.id = snapshot.getKey();
                    eventlist.add(eventread);
                    if(eventread.current > maxPeople) {
                        maxPeople = eventread.current;
                    }
                }
                mapFragment.getMapAsync(DashboardFragment.this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getPreferences(Context.MODE_PRIVATE)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().getPreferences(Context.MODE_PRIVATE)
                .unregisterOnSharedPreferenceChangeListener(this);
    }





    /**
     * Iterate over the list of all events and display the events in the map.
     * <p>
     * The icon to show the position of an event is scaled depending on the number of
     * participants of events in relation to the biggest event. Hence, more popular events have a
     * larger icon.
     * <p>
     * By clicking on an event title, the complete description of the event is displayed.
     *
     * @param googleMap - The map to display
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Get required permissions to show own position in the map
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission
                (getActivity().getApplicationContext(), android.Manifest.permission
                        .ACCESS_FINE_LOCATION) != PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(getActivity().getApplicationContext(), android.Manifest
                        .permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            Log.w("DashboardFragment", "No permission to get location");
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 123);
            return;
        } else {
            googleMap.setMyLocationEnabled(true);

            FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(this.getActivity());

            locationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // GPS location can be null if GPS is switched off
                            if (location != null) {


                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                        .target(new LatLng(location.getLatitude(),location.getLongitude()))
                                        .zoom(10)
                                        .build();
                                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                                FirebaseAuth auth = FirebaseAuth.getInstance();
                                FirebaseUser user = auth.getCurrentUser();
                                final String signupEUID = user.getUid();

                                FirebaseDatabase.getInstance().getReference().child("Users")
                                        .child(signupEUID).child("LastKnownLatitude")
                                        .setValue(location.getLatitude());
                                FirebaseDatabase.getInstance().getReference().child("Users")
                                        .child(signupEUID).child("LastKnownLongitude")
                                        .setValue(location.getLongitude());

                                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());;
                                List<Address> addresses;
                                try {
                                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    String postalCode = addresses.get(0).getPostalCode();
                                    String city = addresses.get(0).getLocality();
                                    String address = addresses.get(0).getAddressLine(0);
                                    String state = addresses.get(0).getAdminArea();
                                    String country = addresses.get(0).getCountryName();
                                    String knownName = addresses.get(0).getFeatureName();

                                    FirebaseDatabase.getInstance().getReference().child("Users")
                                            .child(signupEUID).child("LastKnownCity")
                                            .setValue(city);

                                    FirebaseDatabase.getInstance().getReference().child("Users")
                                            .child(signupEUID).child("LastKnownCountry")
                                            .setValue(country);






                                }catch(Exception e)
                                {

                                }


                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("MapDemoActivity", "Error trying to get last GPS location");
                            e.printStackTrace();
                        }
                    });


        }

        // Get strings of categories to filter events.
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        Set<String> filterStrings = sharedPref.getStringSet(AllEvents.FILTER_KEY, null);
        sharedPref.registerOnSharedPreferenceChangeListener(this);

        showEventsInMap(filterStrings);

    }

    private void showEventsInMap(Set<String> filterStrings) {
        googleMap.clear();

        // Iterate over list of events to set the positions in the map
        for(Event event : eventlist) {
            boolean categoryMatches = false;
            if(filterStrings != null && ! filterStrings.isEmpty()) {
                for(String filter : filterStrings) {
                    categoryMatches |= event.categories.containsKey(filter) && event.categories
                            .get(filter);

                }
            } else {
                categoryMatches = true;
            }

            // Only show events with matching category
            if(categoryMatches && event.latitude != null && event.longitude != null) {
                LatLng location = new LatLng(event.latitude, event.longitude);
                MarkerOptions marker = new MarkerOptions().position(location);
                marker.title(event.title);

                // Get icon and scale its size depending on the number of current people in the
                // event
                Drawable drawableicon;
                Bitmap icon;
                if(event.categories.containsKey("Outdoor") && event.categories.get("Outdoor")) {
                    drawableicon = getResources().getDrawable(R.drawable.ic_location_green);
                } else if(event.categories.containsKey("Party") && event.categories.get("Party")) {
                    drawableicon = getResources().getDrawable(R.drawable.ic_location_blue);
                }
                else if(event.categories.containsKey("Sport") && event.categories.get("Sport")) {
                    drawableicon = getResources().getDrawable(R.drawable.ic_location_red);
                }
                else {
                    drawableicon = getResources().getDrawable(R.drawable.ic_location_on_black);
                }
                if(drawableicon instanceof BitmapDrawable) {
                    icon = ((BitmapDrawable) drawableicon).getBitmap();
                } else {
                    icon = Bitmap.createBitmap(drawableicon.getIntrinsicWidth(), drawableicon
                            .getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(icon);
                    drawableicon.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                    drawableicon.draw(canvas);
                }
                marker.icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(icon,
                        (int) Math.ceil(icon.getWidth() * 1.5 * event.current / maxPeople), (int)
                                Math.ceil(icon.getHeight() * 1.5 * event.current / maxPeople),
                        false)));

                // Set tag to get the id of the event from the marker in the map. This allows us
                // to show the event details later.
                googleMap.addMarker(marker).setTag(event.id);
            }
        }

        // Click on the info to display the full event descriptions
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                getActivity().findViewById(R.id.tabs).setVisibility(View.GONE);
                EventDetailFragment fragment = new EventDetailFragment();
                fragment.setEvent(marker.getTag().toString());
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.frame, fragment);
                ft.addToBackStack(Main2Activity.TAG_EVENT_DETAIL_FRAGMENT);
                ft.commit();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch(requestCode) {
            case 123: {
                // If request is cancelled, the result arrays are empty.
                if(grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat
                            .checkSelfPermission(getContext(), Manifest.permission
                                    .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(getContext(), Manifest
                            .permission.ACCESS_COARSE_LOCATION) != PackageManager
                            .PERMISSION_GRANTED) {
                        // This block can never occur.
                        return;
                    }
                    googleMap.setMyLocationEnabled(true);
                }
            }
        }
    }





}
