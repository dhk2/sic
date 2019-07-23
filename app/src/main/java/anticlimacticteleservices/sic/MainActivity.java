package anticlimacticteleservices.sic;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;


public class MainActivity extends AppCompatActivity {
    public Context dirtyHack = this;
    private TextView mTextMessage;
    List<Video> videos = new ArrayList<>();
    List<Channel> channels = new ArrayList<>();
    FragmentManager manager;
    Fragment fragment;
    FragmentTransaction transaction;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;
    public static UserData masterData;
    public Channel hah;
    public Search huh;
    public Subscription heh;
    private BottomNavigationView navView;
    //hack needed to work with androids hack of a joke of a permission system
    private static final int PERMISSION_REQUEST_CODE = 1;

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
                        getSupportActionBar().hide();
                        if (masterData.getForceRefresh()){
                            System.out.println("forced refresh");
                            //videos.clear();
                            //channels.clear();

                        }
                        if (videos.isEmpty()){
                            System.out.println("no videos loaded, attempting to load from feed");
                            setMainTitle("refreshing video feed");
                            masterData.setForceRefresh(false);
                            Set<String> bob = masterData.getFeedLinks();
                            String doug[] = new String[bob.size()];
                            doug = bob.toArray(doug);
                            new StartUp().execute(doug);
                        }
                        else {
                            fragment = new VideoFragment();
                            ((VideoFragment) fragment).setVideos(videos);
                            manager = getSupportFragmentManager();
                            transaction = manager.beginTransaction();
                            transaction.replace(R.id.fragment, fragment);
                            transaction.commit();
                        }
                        return true;
                    case R.id.navigation_history:
                        getSupportActionBar().show();
                        setTitle("Not implemented yet");
                        return true;
                    case R.id.navigation_channels:
                        getSupportActionBar().hide();
                        System.out.println("setting  channels with  "+channels.size());
                        fragment = new ChannelFragment();
                        ((ChannelFragment) fragment).setChannels(channels);
                        manager = getSupportFragmentManager();
                        masterData.setFragmentManager(manager);
                        transaction = manager.beginTransaction();
                        transaction.replace(R.id.fragment, fragment);
                        transaction.commit();
                        System.out.println("Channel fragment configured with "+channels.size()+"channels");
                        ((ChannelFragment) fragment).setChannels(channels);
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
                        transaction.commit();
                        return true;

                    case R.id.navigation_settings:
                        getSupportActionBar().show();
                        setTitle("settings");
                        fragment = new SettingsFragment();
                        manager = getSupportFragmentManager();
                        transaction = manager.beginTransaction();
                        transaction.replace(R.id.fragment, fragment);
                        transaction.commit();
                        System.out.println("done transacting");
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

        setContentView(R.layout.activity_main);
        System.out.println("set the content view");
        navView = findViewById(R.id.nav_view);

        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        getSupportActionBar().show();
        setTitle("Loading video feed");
        preferences = getSharedPreferences( getPackageName() + "_preferences", MODE_PRIVATE);
        masterData = new UserData(dirtyHack);
        videos=masterData.getVideos();
        channels=masterData.getChannels();
        if (videos.isEmpty()){
            System.out.println("no videos loaded, attempting to load from feed");
            setMainTitle("refreshing video feed");
        }
        else {
            fragment = new VideoFragment();
            ((VideoFragment) fragment).setVideos(videos);
            manager = getSupportFragmentManager();
            transaction = manager.beginTransaction();
            transaction.replace(R.id.fragment, fragment);
            transaction.commit();
        }
        Set<String> bob = masterData.getFeedLinks();
        String doug[] = new String[bob.size()];
        doug = bob.toArray(doug);
        new StartUp().execute(doug);

    }
    private class StartUp extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            Channel chan;
            for (String url : params) {
                chan=new Channel();
                if (url.indexOf("youtube.com") > 0) {
                    try {
                        //url=" https://www.youtube.com/feeds/videos.xml?channel_id="+chan.id;
                        Document doc = Jsoup.connect(url).get();
                        System.out.println(url);
                        System.out.println(doc.id());
                        chan.setTitle(doc.title());
                        chan.setAuthor(doc.getElementsByTag("name").first().text());
                        chan.setUrl(url);

                        Elements entries = doc.getElementsByTag("entry");
                        for (Element entry : entries) {
                            Video nv = new Video(entry.getElementsByTag("link").first().attr("href"));

                            nv.setAuthor(chan.getAuthor());
                            nv.setTitle(entry.getElementsByTag("title").first().html());
                            nv.setThumbnail(entry.getElementsByTag("media:thumbnail").first().attr("url"));
   //                       youtube rss doesn't have channel thumbnail. hack to get a picture in until channel data caching is implemented.
                            if (chan.getThumbnail().isEmpty()){
                                chan.setThumbnail(nv.getThumbnail());
                            }
                            nv.setDescription(entry.getElementsByTag("media:description").first().text());
                            nv.setRating(entry.getElementsByTag("media:starRating").first().attr("average"));
                            nv.setViewCount(entry.getElementsByTag("media:statistics").first().attr("views"));
                            try {
                                Date pd = ydf.parse(entry.getElementsByTag("published").first().text());
                                nv.setDate(pd);
                            } catch (ParseException ex) {
                                Log.v("Exception parsing date", ex.getLocalizedMessage());
                                System.out.println(entry);
                            }
                            boolean unique = true;
                            for (Video match : videoFeed) {
                                if (match.getID().equals(nv.getID())) {
                                    unique = false;
                                }
                            }
                            if (unique) {
                                videoFeed.add(nv);
                            }
                            unique = true;
                            for (Video match : chan.getVideos()) {
                                if (match.getID().equals(nv.getID())) {
                                    unique = false;
                                }
                            }
                            if (unique) {
                                chan.addVideo(nv);
                            }
                       }
                    } catch (MalformedURLException e) {
                        System.out.println("Malformed URL while parsing feed " + e.getMessage());
                        System.out.println(url);
                    } catch (IOException e) {
                        System.out.println("I/O Error while parsing feed " + e.getMessage());
                        System.out.println((url));
                    } catch (NullPointerException e) {
                        System.out.println("null pointer issue" + e.getMessage());

                    }
                }
                if (url.indexOf("bitchute.com") > 0) {
                    try {
                        Document doc = Jsoup.connect(url).get();
                        chan.setTitle(doc.title());
                        chan.setUrl(url);
                        Elements metaElements =doc.getElementsByAttribute("name");

                        System.out.println(metaElements.first().getElementsByClass("channel-videos-text").text());
                        chan.setDescription(doc.getElementsByClass("channel-videos-text").text());
                        chan.setThumbnail(doc.getElementsByClass("image lazyload").attr("data-src"));
                        Elements videoList = doc.getElementsByClass("channel-videos-list");
                        Elements entries = videoList.first().getElementsByClass("row");
                        for (Element entry : entries) {
                            //System.out.println("<<<<entry<<<<"+entry);
                            Video nv = new Video("https://www.bitchute.com" + entry.getElementsByTag("a").first().attr("href"));
                            nv.setDescription(entry.getElementsByClass("channel-videos-text").first().text());
                            nv.setThumbnail(entry.getElementsByTag("img").first().attr("data-src"));
                            nv.setTitle(entry.getElementsByClass("channel-videos-title").first().text());
                            nv.setViewCount(entry.getElementsByClass("video-views").first().text());

                            try {
                                Date pd = bdf.parse(entry.getElementsByClass("channel-videos-details").first().getElementsByTag("span").text());
                                nv.setDate(pd);
                            } catch (ParseException ex) {
                                Log.v("Exception", ex.getLocalizedMessage());
                            }
                            // Document hackDoc = Jsoup.connect(nv.getUrl()).get();
                            //  nv.setMp4(hackDoc.getElementsByTag("Source").first().attr("src"));
                            nv.setAuthor(doc.title());
                            boolean unique=true;
                            for (Video match : videoFeed) {
                                if (match.getID().equals(nv.getID())) {
                                    unique = false;
                                }
                            }
                            if (unique) {
                                videoFeed.add(nv);
                            }
                            unique = true;
                            for (Video match : chan.getVideos()) {
                                if (match.getID().equals(nv.getID())) {
                                    unique = false;
                                }
                            }
                            if (unique) {
                                chan.addVideo(nv);
                            }
                        }
                        System.out.println("finished scraping " + videos.size() + " videos");
                    } catch (MalformedURLException e) {
                        System.out.println("Malformed URL: " + e.getMessage());
                    } catch (IOException e) {
                        System.out.println("I/O Error: " + e.getMessage());
                    } catch (NullPointerException e) {
                        System.out.println("null pointer issue" + e);

                    }



                }
                if (chan.getDescription().isEmpty()) {
                    //need to load more channel info since it wasn't cached
                    if (chan.getUrl().indexOf("youtube") > 1) {
                        try {
                            Document doc = Jsoup.connect("https://www.youtube.com/channel/"+chan.getID()).get();
                            chan.setDescription(doc.getElementsByAttributeValue("name","description").attr("content").toString());
                            chan.setThumbnail(doc.getElementsByAttributeValue("itemprop","thumbnailUrl").attr("href").toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.out.println("Failed to load youtube channel page for " + chan.getTitle()+" at "+"https://www.youtube.com/channel/"+chan.getID());
                        }

                    }
                }
                boolean unique = true;
                for (Channel match : channels) {
                    if (match.getID().equals(chan.getID())) {
                        unique = false;
                    }
                }
                if (unique) {
                    channels.add(chan);
                    System.out.println("adding channel "+chan.getTitle());
                }
                else {
                    System.out.println("dupicate channel rejected "+chan.getTitle());
                }

            }
            System.out.println("channel size"+channels.size());
            System.out.println("video size "+videoFeed.size());
            Collections.sort(videoFeed);
            System.out.println("done sorting video feed");
            return "done";
        }
        @Override
        protected void onPostExecute(String result) {


            videos = videoFeed;
            hideMainTitle();
            //This forces the app to go to main video feed when initial refresh is finished
            //disabled as annoying during testing.
      //      findViewById(R.id.navigation_home).callOnClick();

       }
        @Override
        protected void onPreExecute() {
            System.out.println("pre-executing the feedlist walk and results parse");
            setMainTitle("refreshing videos");
        }

        @Override
        protected void onProgressUpdate(String... values) {
        setTitle(getTitle()+".");

        }

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
        super.onPause();
        System.out.println("saving "+channels.size());
        masterData.saveUserData(channels);
    }
    public void setMainTitle(String t){
        getSupportActionBar().show();
        setTitle(t);
    }
    public void hideMainTitle(){
        getSupportActionBar().hide();
    }
}
