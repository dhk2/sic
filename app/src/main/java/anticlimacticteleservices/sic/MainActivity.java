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
    public Context dirtyHack = this;

    FragmentManager manager;
    Fragment fragment;
    FragmentTransaction transaction;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;
    public static UserData masterData;

    private BottomNavigationView navView;
    //hack needed to work with androids hack of a joke of a permission system
    private static final int PERMISSION_REQUEST_CODE = 1;
    int feedLinkCount=0;

    public List<Video> videoFeed = new ArrayList<>();
    final SimpleDateFormat bdf = new SimpleDateFormat("MMM dd, yyyy");
    final SimpleDateFormat ydf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    public static SharedPreferences preferences;
 //   Fragment vfragment = new VideoFragment();
 //   Fragment cfragment = new ChannelFragment();
 //   Fragment sfragment = new SearchFragment();

    {
        mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
  //              FragmentManager fragrentManager;
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        //setTitle("Video Feed");
                        Log.v("Main-Navigation-Home","starting home navigation");
                        getSupportActionBar().hide();
                        //TODO remove masterdata and use direct DAO
                        masterData.setVideos(masterData.getVideoDao().getVideos());

                        Log.v("Main-Navigation-Home","size of video database:"+masterData.getVideos().size());
                        fragment = new VideoFragment();
                        ((VideoFragment) fragment).setVideos(masterData.getVideoDao().getVideos());
                        transaction = masterData.getFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment, fragment);
                        transaction.addToBackStack(null);
                        Log.v("Main-Navigation-home","creating video list fragment from navigation"+masterData.getVideos().size());
                        transaction.commitAllowingStateLoss();

                        return true;
                    case R.id.navigation_history:
                        getSupportActionBar().show();
                        setTitle("Not implemented yet");


/*
                       // new ChannelUpdate().execute();
                        Uri uri;
                        int vlcRequestCode = 42;
                        String path;
                        String subtitles="";
                        Intent vlcIntent = new Intent(Intent.ACTION_VIEW);
                        Video foo=new Video();
                        for (Video v: masterData.getVideos()){
                            if (MainActivity.masterData.getCommentDao().getCommentsByFeedId(v.getID()).size()>5 && v.isBitchute()){
                                subtitles = Util.writeSubtitles(MainActivity.masterData.context,v);
                                foo=v;
                                break;
                            }
                        }
                        vlcIntent.setPackage("org.videolan.vlc");

                        path = foo.getMp4();
                        new Util.DownloadVideo().execute(path);
                        uri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/video.mp4");
                        vlcIntent.setDataAndTypeAndNormalize(uri, "video/*");
                        vlcIntent.putExtra("subtitles_location"	, subtitles);
                        vlcIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        System.out.println("video:"+path+ "   subtitles"+subtitles);
                        System.out.println("trying to play vlc "+vlcIntent.toString());
                        getApplication().startActivity(vlcIntent);


                        Video vid=masterData.getVideos().get(1);
                        if (!vid.getMagnet().isEmpty()){
                            String header="<!doctype html>\n" +
                                    "<html>\n" +
                                    "  <body>\n" +
                                    "    <h1>Download files using the WebTorrent protocol (BitTorrent over WebRTC).</h1>\n" +
                                    "\n" +
                                    "    <form>\n" +
                                    "      <label for=\"torrentId\">Download from a magnet link: </label>\n" +
                                    "      <input name=\"torrentId\", placeholder=\"magnet:\" value=\"";
                            String footer="\">\n" +
                                    "      <button type=\"submit\">Download</button>\n" +
                                    "    </form>\n" +
                                    "\n" +
                                    "    <h2>Log</h2>\n" +
                                    "    <div class=\"log\"></div>\n" +
                                    "\n" +
                                    "    <!-- Include the latest version of WebTorrent -->\n" +
                                    "    <script src=\"https://cdn.jsdelivr.net/webtorrent/latest/webtorrent.min.js\"></script>\n" +
                                    "\n" +
                                    "    <script>\n" +
                                    "      var client = new WebTorrent()\n" +
                                    "\n" +
                                    "      client.on('error', function (err) {\n" +
                                    "        console.error('ERROR: ' + err.message)\n" +
                                    "      })\n" +
                                    "\n" +
                                    "      document.querySelector('form').addEventListener('submit', function (e) {\n" +
                                    "        e.preventDefault() // Prevent page refresh\n" +
                                    "\n" +
                                    "        var torrentId = document.querySelector('form input[name=torrentId]').value\n" +
                                    "        log('Adding ' + torrentId)\n" +
                                    "        client.add(torrentId, onTorrent)\n" +
                                    "      })\n" +
                                    "\n" +
                                    "      function onTorrent (torrent) {\n" +
                                    "        log('Got torrent metadata!')\n" +
                                    "        log(\n" +
                                    "          'Torrent info hash: ' + torrent.infoHash + ' ' +\n" +
                                    "          '<a href=\"' + torrent.magnetURI + '\" target=\"_blank\">[Magnet URI]</a> ' +\n" +
                                    "          '<a href=\"' + torrent.torrentFileBlobURL + '\" target=\"_blank\" download=\"' + torrent.name + '.torrent\">[Download .torrent]</a>'\n" +
                                    "        )\n" +
                                    "\n" +
                                    "        // Print out progress every 5 seconds\n" +
                                    "        var interval = setInterval(function () {\n" +
                                    "          log('Progress: ' + (torrent.progress * 100).toFixed(1) + '%')\n" +
                                    "        }, 5000)\n" +
                                    "\n" +
                                    "        torrent.on('done', function () {\n" +
                                    "          log('Progress: 100%')\n" +
                                    "          clearInterval(interval)\n" +
                                    "        })\n" +
                                    "\n" +
                                    "        // Render all files into to the page\n" +
                                    "        torrent.files.forEach(function (file) {\n" +
                                    "          file.appendTo('.log')\n" +
                                    "          log('(Blob URLs only work if the file is loaded from a server. \"http//localhost\" works. \"file://\" does not.)')\n" +
                                    "          file.getBlobURL(function (err, url) {\n" +
                                    "            if (err) return log(err.message)\n" +
                                    "            log('File done.')\n" +
                                    "            log('<a href=\"' + url + '\">Download full file: ' + file.name + '</a>')\n" +
                                    "          })\n" +
                                    "        })\n" +
                                    "      }\n" +
                                    "\n" +
                                    "      function log (str) {\n" +
                                    "        var p = document.createElement('p')\n" +
                                    "        p.innerHTML = str\n" +
                                    "        document.querySelector('.log').appendChild(p)\n" +
                                    "      }\n" +
                                    "    </script>\n" +
                                    "  </body>\n" +
                                    "</html>";
                            String foo=header+vid.getMagnet()+footer;

                            Intent i = new Intent();
                            //i.setComponent(new ComponentName("com.brave.browser","com.brave.browser"));
                            i.setAction(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(Util.writeHtml(foo)));
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(i);

                            Dialog dialog = new Dialog(MainActivity.this);
                            dialog.setContentView(R.layout.importdialog);
                            WebView webView = dialog.findViewById(R.id.idplayer_window);
                            WebSettings webSettings = webView.getSettings();
                            webSettings.setJavaScriptEnabled(true);
                            webSettings.setAllowUniversalAccessFromFileURLs(true);
                            webSettings.setAllowContentAccess(true);
                            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

                            //webView.loadUrl("https://www.youtube.com/subscription_manager");
                            Button closeButton = dialog.findViewById(R.id.idclosebutton);
                            closeButton.setText("close");
                            closeButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    webView.destroy();
                                    dialog.dismiss();
                                }
                            });


                            dialog.show();
                        }
                        */
                        return true;
                    case R.id.navigation_channels:
                        getSupportActionBar().hide();
                        masterData.getChannels();
                        Log.v("Main-Navigation-Channel",masterData.getChannels().size()+"  "+ masterData.getChannels().size());
                        fragment = new ChannelFragment();
                        ((ChannelFragment) fragment).setChannels(masterData.getChannels());
                        manager = getSupportFragmentManager();
                        transaction = manager.beginTransaction();
                        transaction.replace(R.id.fragment, fragment);
                        transaction.addToBackStack(null);
                        transaction.commitAllowingStateLoss();
                        ((ChannelFragment) fragment).setChannels(masterData.getChannels());
                        return true;


                    case R.id.navigation_discover:
                        getSupportActionBar().hide();
                        setTitle("under construction");

                        fragment = new SearchFragment();
//                        ((SearchFragment) fragment).setChannels(channels);
                        MainActivity.masterData.fragment=fragment;
                        manager = getSupportFragmentManager();
                        transaction = manager.beginTransaction();
                        transaction.replace(R.id.fragment, fragment);
                        transaction.addToBackStack(null);
                        transaction.commitAllowingStateLoss();
                        return true;

                    case R.id.navigation_settings:
                        getSupportActionBar().hide();
                        //setTitle("settings");
                        fragment = new SettingsFragment();
                        manager = getSupportFragmentManager();
                        transaction = manager.beginTransaction();
                        transaction.replace(R.id.fragment, fragment);
                        transaction.addToBackStack(null);
                        transaction.commitAllowingStateLoss();
                      //  System.out.println("done transacting");
                        return true;
                }
                return false;
            }
        };
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("started oncreate");
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
        }
        else{
            Log.v("Main-OC","performing soft restart, probably a rotation or off pause");
            if (null != masterData.getPlayer()) {
                Log.v("Main-OC","Exo player exists for "+masterData.getPlayerVideoID());
                fragment_exoplayer efragment = fragment_exoplayer.newInstance("", masterData.getVideoDao().getvideoById(masterData.getPlayerVideoID()));
                manager = MainActivity.masterData.getFragmentManager();
                transaction = manager.beginTransaction();
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
                    new ChannelUpdate().execute();
                    getSupportActionBar().hide();
                    fragment = new VideoFragment();
                    masterData.setVideos(masterData.getVideoDao().getVideos());
                    Log.v("Main-OC","viddeo fragment creation " + masterData.getVideos().size());
                    ((VideoFragment) fragment).setVideos(masterData.getVideos());
                    manager = getSupportFragmentManager();
                    transaction = manager.beginTransaction();
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
        //need to add a dirty data switch to skip saving if unneeded
        super.onPause();

        for (Video v : MainActivity.masterData.getVideos()){
            if (v.getMp4().isEmpty() && v.getUpCount().isEmpty()){
                new VideoScrape().execute(v);
            }
        }
    }

    @Override
    protected void onDestroy() {
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

    }


}
