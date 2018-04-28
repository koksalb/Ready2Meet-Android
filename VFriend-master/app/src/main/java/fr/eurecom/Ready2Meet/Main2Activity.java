package fr.eurecom.Ready2Meet;

import android.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.eurecom.Ready2Meet.uiExtensions.MainPagerAdapter;
import fr.eurecom.Ready2Meet.uiExtensions.ToolbarActivity;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static fr.eurecom.Ready2Meet.profilepageActivity.getDifferenceDays;

public class Main2Activity extends ToolbarActivity implements  BillingProcessor.IBillingHandler{
    private FirebaseAuth auth;

    public final static String TAG_EVENT_DETAIL_FRAGMENT = "EventDetail";

    BillingProcessor bp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        final String signupEUID = auth.getCurrentUser().getUid();

        setContentView(R.layout.activity_main2);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        setToolbar();

        // For swipe between fragments
        PagerAdapter pagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText("Map View");
        tabLayout.getTabAt(1).setText("Events");
        tabLayout.getTabAt(2).setText("Explore");

        final String token = FirebaseInstanceId.getInstance().getToken();
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();






        FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child
                ("ParticipatingEvents").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String eventId = snapshot.getKey();
                    FirebaseDatabase.getInstance().getReference().child("MessageNotifications")
                            .child(eventId).child("notificationTokens").push().setValue(token);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // TODO: Error handling
            }
        });





        bp = new BillingProcessor(this, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApVqWA6SO/4TO+a5qNbVpY2wttZot3+R5bV0qvRBd4tvslabzJ/39iAztN4eFsp7H3KQQIY43h03AA8nLAhxcr/czLVdugOQJ17n2Vzmf9dV6BV3o12cYlF3Te8aqHpmnIxf+Mof9i6bckseVcGWpgAFI2wWQCCnWgErPyB2Z6uIh0tM7+NkdXCQFv+Frwp8/GKo3tN/03lygbT/TF1c0vcW0Z0CohhLvJ+PFF9SuT7dtZPNOg4z6D99pfPY7O4ijZW9LKi7BN3vPOklyYF+rrg1ebE+KG/cj1IXCo1OKSBcUOm1OvCFNvvZFPDWPVY1IJxSEYimmXPqMNo17KtRgUwIDAQAB", this);






    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 2 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            StorageReference storage = FirebaseStorage.getInstance().getReference().child
                    ("ProfilePictures").child(auth.getCurrentUser().getUid());
            storage.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask
                    .TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    FirebaseDatabase.getInstance().getReference().child("Users").child(auth
                            .getCurrentUser().getUid()).child("ProfilePictureURL").setValue
                            (taskSnapshot.getDownloadUrl().toString());
                    Toast.makeText(getApplication(), "Done", Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplication(), "Couldn't upload image to database", Toast
                            .LENGTH_LONG).show();
                }
            });
        }

        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 0 && getSupportFragmentManager
                ().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1)
                .getName().equals(TAG_EVENT_DETAIL_FRAGMENT)) {
            findViewById(R.id.tabs).setVisibility(View.VISIBLE);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // TODO: Rewrite this method to deal with the new fragment swipe stuff!
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment;
        if(id == R.id.nav_add_event) {

            startActivity(new Intent(Main2Activity.this, AddEventActivity.class));

        } else if(id == R.id.nav_allevents) {
            findViewById(R.id.tabs).setVisibility(View.GONE);
            fragment = new AllEvents();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frame, fragment);
            ft.addToBackStack(TAG_EVENT_DETAIL_FRAGMENT);
            ft.commit();
        } else if(id == R.id.nav_messages) {
            // TODO

            Intent intent = new Intent(getApplication(), MessagesActivity.class);
            startActivity(intent);




        } else if(id == R.id.nav_manage) {
            findViewById(R.id.tabs).setVisibility(View.GONE);
            fragment = new AccountOptions();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frame, fragment);
            ft.addToBackStack(TAG_EVENT_DETAIL_FRAGMENT);
            ft.commit();
        } else if(id == R.id.nav_share) {


            boolean isAvailable = BillingProcessor.isIabServiceAvailable(this);
            if(isAvailable) {

                boolean isOneTimePurchaseSupported = bp.isOneTimePurchaseSupported();
                if(isOneTimePurchaseSupported) {

                    bp.purchase(this,"1monthpremium");



                }

            }




        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    @Override
    public void onBillingInitialized() {
    /*
    * Called when BillingProcessor was initialized and it's ready to purchase
    */
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
    /*
    * Called when requested PRODUCT ID was successfully purchased
    */
    if (productId.equals("1monthpremium"))
    {
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("PremiumTill").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String premiumstring = dataSnapshot.getValue(String.class);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd 'at' hh:mm a", Locale.US);
                long daysleft = 0;
                Calendar cal = Calendar.getInstance();
                try {
                    cal.setTime(format.parse(premiumstring));
                }catch(Exception e){}

                Date premiumtilldate = cal.getTime();

                Date today = Calendar.getInstance().getTime();

                if(today.after(premiumtilldate))
                {
                    daysleft = 0;
                }
                else
                {
                    daysleft = getDifferenceDays(today,premiumtilldate);
                    //premiumdayslefttext.setText(String.valueOf(getDifferenceDays(today,premiumtilldate)));
                }

                daysleft +=30;
                Calendar cal2 = Calendar.getInstance();
                cal2.add(Calendar.DAY_OF_YEAR, (int) (long)daysleft);

                String newstringtoput = format.format(cal2.getTime());
                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("PremiumTill").setValue(newstringtoput);
                Toast.makeText(getApplicationContext(),"Congratz! You have "+ daysleft + " days premium now!",
                        Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });






    }



    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
    /*
    * Called when some error occurred. See Constants class for more details
    *
    * Note - this includes handling the case where the user canceled the buy dialog:
    * errorCode = Constants.BILLING_RESPONSE_RESULT_USER_CANCELED
    */
        Toast.makeText(getApplicationContext(),"Ops. Something went wrong :(",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPurchaseHistoryRestored() {
    /*
    * Called when purchase history was restored and the list of all owned PRODUCT ID's
    * was loaded from Google Play
    */
    }



}
















