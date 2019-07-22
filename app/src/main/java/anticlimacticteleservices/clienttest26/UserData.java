package anticlimacticteleservices.clienttest26;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.Loader;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static anticlimacticteleservices.clienttest26.MainActivity.masterData;

public class UserData {
    public List<Channel> channels = new ArrayList<>();
    Set<String> feedLinks =new HashSet<String>();
    Boolean useYoutube=true;
    public Context context;
    public SharedPreferences.Editor editor;

    public boolean isUseVlc() {
        return useVlc;
    }

    public void setUseVlc(boolean useVlc) {
        this.useVlc = useVlc;
    }

    public boolean useVlc;
    public boolean useWebview;
    public boolean useDefault;
    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    FragmentManager fragmentManager;

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    Fragment fragment;

    public FragmentTransaction getTransaction() {
        return transaction;
    }

    public void setTransaction(FragmentTransaction transaction) {
        this.transaction = transaction;
    }

    FragmentTransaction transaction;
    public Activity activity;
    public boolean forceRefresh;



    public UserData(Context con){
        this.context=con;
        this.activity=(Activity)con;
        forceRefresh=true;
    }

    public void setForceRefresh(boolean forceRefresh) {
        this.forceRefresh = forceRefresh;
    }
    public boolean getForceRefresh(){
        return forceRefresh;
    }
    public Set<String> getFeedLinks(){
       //ontext context =
       //prefs =  getSharedPreferences( "com.mycompany.client", Context.MODE_PRIVATE);

        editor = MainActivity.preferences.edit();
        Set<String> feeds = MainActivity.preferences.getStringSet("channelUrls",null);
//        commented out to improve speed in testing, need to fix
       if (null == feeds){
  //    if (true){
            feeds=new HashSet<String>();
            feeds.add("https://www.youtube.com/feeds/videos.xml?channel_id=UC-lHJZR3Gqxm24_Vd_AJ5Yw");
            feeds.add("https://bitchute.com/channel/Styxhexenham/");
        }
        for ( String g :feeds){
           feedLinks.add(g)
;        }
        return feedLinks;
    }
    public void removeFeedLink(String deadLink){
        Set<String> tempSub = new HashSet();

        for (String s : feedLinks){
            if (!s.equals(deadLink)){
                    tempSub.add(s);
            }
            else{
                System.out.println("removing feedlink:"+deadLink);
            }
        }
        feedLinks = tempSub;
        editor = MainActivity.preferences.edit();
        editor.putStringSet("channelUrls", (Set<String>) feedLinks);
        editor.commit();
        forceRefresh=true;
    }
    public void addFeedLinks(Set<String> newLinks) {
        for (String newLink : newLinks) {
            boolean unique = true;
            for (String g : feedLinks) {
                if (g.equals(newLink)) {
                    unique = false;
                    System.out.println("rejecting duplicate feed" + g);
                }
            }
            if (unique) {
               feedLinks.add(newLink);
            }
            System.out.println("count of feeds:"+ feedLinks.size());
            editor = MainActivity.preferences.edit();
            editor.putStringSet("channelUrls", (Set<String>) feedLinks);
            editor.commit();
            forceRefresh=true;
        }
    }
    public void addFeedLink(String newLink) {
        boolean unique=true;
        for (String g : feedLinks) {
            if (g.equals(newLink)) {
                unique = false;
            }
        }
        if (unique){
            feedLinks.add(newLink);
        }
        editor = MainActivity.preferences.edit();
        editor.putStringSet("channelUrls", (Set<String>) feedLinks);
        editor.commit();
        forceRefresh=true;
    }
    public void setFeedLinks(Set<String> links){
        feedLinks.clear();
        feedLinks.addAll(links);

        editor = MainActivity.preferences.edit();
        editor.putStringSet("channelUrls", (Set<String>) feedLinks);
        editor.commit();
        forceRefresh=true;
    }

    public Boolean getUseYoutube() {
        return useYoutube;
    }

    public void callImport() {
        System.out.print("creating new import subscription");
        //this was the death of me
       ImportSubscriptions is = new ImportSubscriptions();
       is.execute();
    }

}
   
