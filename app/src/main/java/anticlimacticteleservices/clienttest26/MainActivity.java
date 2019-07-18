package anticlimacticteleservices.clienttest26;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.util.HashSet;
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
                        setTitle("Video Feed");
                        System.out.println("setting feed with "+videos.size());
                       // new StartUp().execute(new FeedList().getPages());
                        fragment = new VideoFragment();
                        ((VideoFragment) fragment).setvideos(videoFeed);
                         manager = getSupportFragmentManager();
                        transaction = manager.beginTransaction();
                        transaction.replace(R.id.fragment, fragment);
                        transaction.commit();
                        return true;
                    case R.id.navigation_dashboard:
                        setTitle("Channels");

                        System.out.println("setting  channels with  "+channels.size());
                        fragment = new ChannelFragment();
                        manager = getSupportFragmentManager();
                        transaction = manager.beginTransaction();
                        transaction.replace(R.id.fragment, fragment);
                        transaction.commit();
                        System.out.println("done transacting");
                        ((ChannelFragment) fragment).setChannels(channels);
                        return true;

                    case R.id.navigation_notifications:
                        setTitle("Search");

                        fragment = new SearchFragment();
//                        ((SearchFragment) fragment).setChannels(channels);
                        manager = getSupportFragmentManager();
                        transaction = manager.beginTransaction();
                        transaction.replace(R.id.fragment, fragment);
                        transaction.commit();
                        System.out.println("done transacting");

                        return true;



                       // mTextMessage.setText(R.string.title_notifications);
                        /*videos.clear();
                        videos.addAll(hah.getVideos());
                        vAdapter.notifyDataSetChanged();
                        System.out.println(hah.getVideos().size()+ "vadapter:"+vAdapter.getItemCount()+"channel videos:");
                        System.out.println(hah.toString());
                        return true;
                */
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
        BottomNavigationView navView = findViewById(R.id.nav_view);
        //mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        setTitle("Loading video feed");
        Subscription favorites = new Subscription  ("favorites");
        //FeedList dave = new FeedList();

        preferences = getSharedPreferences( getPackageName() + "_preferences", MODE_PRIVATE);


        masterData = new UserData();
        //masterData.getFeedLinks();
       // Set<String>  mySet = masterData.getFeedLinks();

   /*     SharedPreferences prefs = this.getSharedPreferences( "com.mycompany.client", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet("channelUrls", mySet);
        editor.commit();

        prefs = this.getSharedPreferences( "com.mycompany.client", Context.MODE_PRIVATE);
        editor = prefs.edit();
     */
        Set<String> bob = masterData.getFeedLinks();
        String doug[] = new String[bob.size()];
        doug = bob.toArray(doug);
        new StartUp().execute(doug);
    }
    private class StartUp extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
 //           String url = params[0];
            int oneTenth;
            int feedCounter=1;
            String progress="Loading video feed";
            List<Video> aVideos=new ArrayList<Video>();
            System.out.println("starting video Feed filler"+params);
            Channel chan = new Channel();
            for (String url : params) {
                chan = new Channel();
                if (url.indexOf("youtube.com") > 0) {
                    try {
                        Document doc = Jsoup.connect(url).get();
                        chan.setTitle(doc.title());
                        chan.setAuthor(doc.getElementsByTag("Author").first().getElementsByTag("name").text());
                        chan.setUrl(url);
                        Elements entries = doc.getElementsByTag("entry");
                        for (Element entry : entries) {
                            //  System.out.println("(((" + entry + ")))");
                            // System.out.println("((" + entry.getElementsByTag("published").first().text());
                            Video nv = new Video(entry.getElementsByTag("link").first().attr("href"));
                            nv.setTitle(entry.getElementsByTag("title").first().html());
                            Elements media = entry.getElementsByTag("media:group");
                            nv.setThumbnail(media.first().getElementsByTag("media:thumbnail").first().attr("url"));
                            //nv.setThumbnail(entry.getElementsByTag("media:group").first().getElementsByTag("media:thumbnail").first().attr("url"));
                            nv.setDescription(media.first().getElementsByTag("media:description").first().text());
                            //                                nv.setAuthor(tempAuthor);
                            try {
                                Date pd = ydf.parse(entry.getElementsByTag("published").first().text());
                                nv.setDate(pd);
                            } catch (ParseException ex) {
                                Log.v("Exception", ex.getLocalizedMessage());
                                System.out.println(entry);
                            }
                            nv.setAuthor(doc.getElementsByTag("title").first().text());
                            chan.addVideo((nv));
                            videoFeed.add(nv);
                            // System.out.println(doc.title());
                            //System.out.println("title:"+entry.getElementsByTag("title").first());
                        }    //                       System.out.println(nv);
                    } catch (MalformedURLException e) {
                        System.out.println("Malformed URL: " + e.getMessage());
                    } catch (IOException e) {
                        System.out.println("I/O Error: " + e.getMessage());
                    }
                }
                if (url.indexOf("bitchute.com") > 0) {
                    try {
                        Document doc = Jsoup.connect(url).get();
                        chan.setTitle(doc.title());
//not all bitchute sites have an author
//                        chan.setAuthor(doc.getElementsByTag("Author").first().getElementsByTag("name").text());
                        chan.setUrl(url);
                        chan.setThumbnail(doc.getElementsByClass("image lazyload").attr("data-src"));

                        Elements videoList = doc.getElementsByClass("channel-videos-list");
                        Elements entries = videoList.first().getElementsByClass("row");
                        for (Element entry : entries) {
                            Video nv = new Video("https://www.bitchute.com" + entry.getElementsByTag("a").first().attr("href"));
                            nv.setDescription(entry.getElementsByClass("channel-videos-text").first().text());
                            nv.setThumbnail(entry.getElementsByTag("img").first().attr("data-src"));
                            nv.setTitle(entry.getElementsByClass("channel-videos-title").first().text());

                            try {
                                Date pd = bdf.parse(entry.getElementsByClass("channel-videos-details").first().getElementsByTag("span").text());
                                nv.setDate(pd);
                            } catch (ParseException ex) {
                                Log.v("Exception", ex.getLocalizedMessage());
                            }
                            // Document hackDoc = Jsoup.connect(nv.getUrl()).get();
                            //  nv.setMp4(hackDoc.getElementsByTag("Source").first().attr("src"));
                            nv.setAuthor(doc.title());
                            videoFeed.add(nv);
                            chan.addVideo((nv));
                            //System.out.println(doc.title());

                        }
                        System.out.println("finished scraping " + videos.size() + " videos");
                    } catch (MalformedURLException e) {
                        System.out.println("Malformed URL: " + e.getMessage());
                    } catch (IOException e) {
                        System.out.println("I/O Error: " + e.getMessage());
                    }
                }
                System.out.println(chan);
                channels.add(chan);
                feedCounter++;
            }
            System.out.println("channel size"+channels.size());
            return "done";
        }
        @Override
        protected void onPostExecute(String result) {

            Collections.sort(videoFeed);

            VideoFragment frag = new VideoFragment();
            frag.setvideos(videoFeed);


            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = manager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment,frag);
            fragmentTransaction.commit();
            setTitle("video feed");
       }
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(String... values) {
        setTitle(getTitle()+".");

        }

    }

}
