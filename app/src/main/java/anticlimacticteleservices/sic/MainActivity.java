package anticlimacticteleservices.sic;

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.arch.persistence.room.Room;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.text.HtmlCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.text.Spanned;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static android.app.PendingIntent.getActivity;


public class MainActivity extends AppCompatActivity implements fragment_exoplayer.OnFragmentInteractionListener,
        fragment_videoplayer.OnFragmentInteractionListener, fragment_webviewplayer.OnFragmentInteractionListener,fragment_channel_properties.OnFragmentInteractionListener {
    FragmentManager manager;
    Fragment fragment;
    FragmentTransaction transaction;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;
    public static UserData masterData;
    private BottomNavigationView navView;
    //needed for permissions
    private static final int PERMISSION_REQUEST_CODE = 1;
    final SimpleDateFormat bdf = new SimpleDateFormat("MMM dd, yyyy");
    final SimpleDateFormat ydf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    public static SharedPreferences preferences;
    {
        mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        //setTitle("Video Feed");
                        Log.v("Main-Navigation-Home","starting home navigation");
                        getSupportActionBar().hide();
                        //TODO remove masterdata and use direct DAO
                        masterData.setVideos(masterData.getVideoDao().getVideos());
                        Log.v("Main-Navigation-Home","size of video database:"+masterData.getVideos().size());
                        fragment = new VideoFragment();
                        VideoFragment vfragment = new VideoFragment();
                        vfragment.setVideos(masterData.getVideoDao().getVideos());
                        transaction = masterData.getFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment, vfragment);
                        transaction.addToBackStack(null);
                        Log.v("Main-Navigation-home","commiting video list fragment from navigation"+masterData.getVideos().size());
                        transaction.commitAllowingStateLoss();

                        return true;
                    case R.id.navigation_history:
                        getSupportActionBar().show();
                        setTitle("Not implemented yet");
           return true;
                    case R.id.navigation_channels:
                        getSupportActionBar().hide();
                        masterData.getChannels();
                        Log.v("Main-Navigation-Channel",masterData.getChannels().size()+"  "+ masterData.getChannels().size());
                        ChannelFragment cfragment = new ChannelFragment();
                        ((ChannelFragment) cfragment).setChannels(masterData.getChannels());
                        transaction = masterData.getFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment, cfragment);
                        transaction.addToBackStack(null);
                        transaction.commitAllowingStateLoss();
                        Log.v("Main-Navigation-Channel","creating channel fragment"+masterData.getChannels().size());
                        ((ChannelFragment) cfragment).setChannels(masterData.getChannels());
                        return true;
                    case R.id.navigation_discover:
                        getSupportActionBar().hide();
                        setTitle("under construction");
                        SearchFragment sfragment = new SearchFragment();
                        MainActivity.masterData.fragment=sfragment;
                        transaction = masterData.getFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment, sfragment);
                        transaction.addToBackStack(null);
                        transaction.commitAllowingStateLoss();
                        return true;
                    case R.id.navigation_settings:
                        getSupportActionBar().hide();
                        SettingsFragment settingsfragment = new SettingsFragment();
                        transaction = masterData.getFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment, settingsfragment);
                        transaction.addToBackStack(null);
                        transaction.commitAllowingStateLoss();
                        return true;
                }
                return false;
            }
        };
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v("Main-OC","started oncreate");
        super.onCreate(savedInstanceState);
        if (masterData == null) {
            Log.v("Main-OC","masterData is null");
            preferences = getSharedPreferences( getPackageName() + "_preferences", MODE_PRIVATE);
            masterData = new UserData(getApplicationContext());
            fragment = new VideoFragment();
            Log.v("Main-OC","Should be first video fragment wtih "+masterData.getVideos().size());
            ((VideoFragment) fragment).setVideos(masterData.getVideos());
            manager = getSupportFragmentManager();
            masterData.setFragmentManager(manager);
            transaction = manager.beginTransaction();
            transaction.replace(R.id.fragment, fragment);
            transaction.addToBackStack(null);
            Log.v("Main-OC","commiting video fragment "+masterData.getVideos().size());
            transaction.commitAllowingStateLoss();
            new ChannelUpdate().execute();
        }
        else{
            Log.v("Main-OC","performing soft restart, probably a rotation or off pause");
            if (null != masterData.getPlayer()) {
                Log.v("Main-OC","Exo player exists for "+masterData.getPlayerVideoID());
                fragment_exoplayer efragment = fragment_exoplayer.newInstance("", masterData.getVideoDao().getvideoById(masterData.getPlayerVideoID()));
                transaction = MainActivity.masterData.getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment, efragment);
                transaction.addToBackStack(null);
                Log.v("Main-OC", "committing exo fragment");
                transaction.commitAllowingStateLoss();

            }
            else {
                Log.v("Main-OC","No existing player detected ");
            }
        }
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.v("Main-OC", "high enough version to need the notification channel created");
            CharSequence name = "sic";
            String description = "video site manager";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("anticlimacticteleservices.sic", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        setContentView(R.layout.activity_main);
        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        if (null != masterData.getPlayer()){
            Log.v("Main-OC", "Player still exists for "+masterData.getPlayerVideoID());
        }
        else {
            Log.v("Main-OC", "No player exists");
            if (masterData.getVideos().isEmpty()) {
                Log.w("Main-OC","no videos found");
                if (masterData.getChannels().isEmpty()) {
                    Log.w("Main-OC","No channels found");
                    if (!masterData.getFeedLinks().isEmpty()) {
                        Log.v("Main-OC", "Rebuilding empty channel database from source link backup");
                        for (String feed : masterData.getFeedLinks()) {
                            new ChannelInit().execute(feed);
                        }
                    }
                    else {
                        Log.w("Main-OC","No back up links to regenerate channels from");
                    }
                }
                if (masterData.getVideos().isEmpty()) {
                    Log.v("Main-OC", "No videos reported from channels"+masterData.getChannels().size());
                    final Dialog dialog = new Dialog(this);
                    dialog.setContentView(R.layout.videoprop);
                    dialog.setTitle("new user");

                    TextView message = dialog.findViewById(R.id.channelDetails);

                    message.setText("Looks like this is your first time\n You can use the search feature to find channels,\n or import channels from the settings page");
                    //  message.loadData(,"html","utf-8");
                    ImageView image = dialog.findViewById(R.id.thumbNailView);
                    image.setImageResource(R.mipmap.sic_round);
                    Button dialogButton = dialog.findViewById(R.id.closeButton);
                    dialogButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //TODO check again for empty videos in case of slow background update initially.
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                    getSupportActionBar().show();
                    setTitle("settings");
                    fragment = new SettingsFragment();
                    transaction = manager.beginTransaction();
                    transaction.replace(R.id.fragment, fragment);
                    transaction.addToBackStack(null);
                    Log.v("Main-OC","commiting settings fragment");

                    transaction.commitAllowingStateLoss();
                }
                else {
                    //TODO put in inital scrape to make sure top videos are playable.
                    Log.v("Main-OC", "launcing initial background update");
                    getSupportActionBar().hide();
                    fragment = new VideoFragment();
                    masterData.setVideos(masterData.getVideoDao().getVideos());
                    Log.v("Main-OC","viddeo fragment creation " + masterData.getVideos().size());
                    ((VideoFragment) fragment).setVideos(masterData.getVideos());
                    transaction = MainActivity.masterData.getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment, fragment);
                    transaction.addToBackStack(null);
                    Log.v("Main-OC","committing video fragment");
                    transaction.commitAllowingStateLoss();

                }
            }
        }
        getSupportActionBar().hide();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_REQUEST_CODE)        {
            int grantResultsLength = grantResults.length;
            if(grantResultsLength > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                System.out.println("You did it finally");
            }else {
                System.out.println("You denied write external storage permission.");
            }
        }
    }
    @Override
    public void onPause() {
        Log.v("Main-op","on pause started");
        super.onPause();
// not sure if this is the best
        for (Video v : MainActivity.masterData.getVideos()){
            if (v.getMp4().isEmpty() && v.getUpCount().isEmpty()){
                new VideoScrape().execute(v);
            }
        }
    }

    @Override
    protected void onDestroy() {
        Log.v("Main-op","on destroy started");
        super.onDestroy();
      /*  masterData.sicDatabase.close();
        masterData.channelDatabase.close();
        masterData.commentDatabase.close();
    */
    }

    public void setMainTitle(String t){
        getSupportActionBar().show();
        setTitle(t);
    }
    public void hideMainTitle(){
        getSupportActionBar().hide();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.v("Main-OFI","on FragmentInteraction started");
    }

    @Override
    protected void onStart() {
        Log.v("Main-OS","on start started");
        masterData.setVideos(masterData.getVideoDao().getVideos());
        masterData.setFragmentManager(getSupportFragmentManager());
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.v("Main-OS","on stop started");
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        Log.v("Main-OBP","on back press started");
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        Log.v("Main-OR","on resume started");
        super.onResume();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}
