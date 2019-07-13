package anticlimacticteleservices.clienttest26;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
public class MainActivity extends AppCompatActivity {
    public Context dirtyHack = this;
    private TextView mTextMessage;
  List<Video> videos = new ArrayList<>();
    private RecyclerView recyclerView;
    private VideoAdapter vAdapter;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;
    List<Channel> channels = new ArrayList<>();
    public Channel hah;
    public Search huh;
    public Subscription heh;
    public List<Video> videoFeed = new ArrayList<>();
    final SimpleDateFormat bdf = new SimpleDateFormat("MMM dd, yyyy");
    final SimpleDateFormat ydf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    {
        mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        mTextMessage.setText(R.string.title_home);
                        videos.clear();
                        videos.addAll(videoFeed);
                        vAdapter.notifyDataSetChanged();
                        System.out.println("setting feed with "+videos.size());
                        return true;
                    case R.id.navigation_dashboard:
                        mTextMessage.setText(R.string.title_dashboard);
                        videos.clear();
                        videos.addAll(huh.getVideos());
                        vAdapter.notifyDataSetChanged();
                        return true;
                    case R.id.navigation_notifications:
                        mTextMessage.setText(R.string.title_notifications);
                        videos.clear();
                        videos.addAll(hah.getVideos());
                        vAdapter.notifyDataSetChanged();
                        System.out.println(hah.getVideos().size()+ "vadapter:"+vAdapter.getItemCount()+"channel videos:");
                        return true;
                }
                return false;
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        recyclerView = findViewById(R.id.recView);
        vAdapter = new VideoAdapter(videos);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(vAdapter);
        Subscription favorites = new Subscription  ("favorites");
        FeedList dave = new FeedList();
        System.out.println(dave.getPages());
        new StartUp().execute(new FeedList().getPages());

        hah = new Channel("https://www.bitchute.com/channel/soph/");
  //      favorites.addChannel(hah);
        huh = new Search("trump");
 //       heh = new Subscription("favorites");

//        vAdapter = new VideoAdapter(videos);
    }
    private class StartUp extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
 //           String url = params[0];
            List<Video> aVideos=new ArrayList<Video>();
            System.out.println("starting video Feed filler"+params);
            for (String url : params) {
                if (url.indexOf("youtube.com") > 0) {
                    try {
                        Document doc = Jsoup.connect(url).get();
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
                            videoFeed.add(nv);
                            System.out.println(doc.title());
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
                            Document hackDoc = Jsoup.connect(nv.getUrl()).get();
                            nv.setMp4(hackDoc.getElementsByTag("Source").first().attr("src"));
                            nv.setAuthor(doc.title());
                            videoFeed.add(nv);
                            System.out.println(doc.title());

                        }
                        System.out.println("finished scraping " + videos.size() + " videos");
                    } catch (MalformedURLException e) {
                        System.out.println("Malformed URL: " + e.getMessage());
                    } catch (IOException e) {
                        System.out.println("I/O Error: " + e.getMessage());
                    }
                }
            }
            return "done";
        }
        @Override
        protected void onPostExecute(String result) {

            Collections.sort(videoFeed);
            videos.addAll(videoFeed);
            vAdapter.notifyDataSetChanged();
       }
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    public void propertyDialog(Video vid){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.videoprop);
        dialog.setTitle(vid.getTitle());

        // set the custom dialog components - text, image and button
        TextView text = (TextView) dialog.findViewById(R.id.videoDetails);
        text.setText(vid.toString());
        ImageView image = (ImageView) dialog.findViewById(R.id.thumbNailView);
        Picasso.get().load(vid.getThumbnail()).into(image);
        Button dialogButton = (Button) dialog.findViewById(R.id.closebutton);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }



}
