package anticlimacticteleservices.sic;

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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static anticlimacticteleservices.sic.MainActivity.masterData;

public class UserData {
    public List<Channel> sChannels = new ArrayList<Channel>();
    public List<Channel> getsChannels() {
        return sChannels;
    }
    public void setsChannels(ArrayList<Channel> value) {
        this.sChannels = value;
    }
    public void addsChannel(Channel value){
        sChannels.add(value);
    }
    public List<Channel> channels = new ArrayList<Channel>();
    public List<Channel> getChannels() {
        return channels;
    }
    public void setChannels(ArrayList<Channel> value) {
        this.channels = value;
    }
    public void addChannel(Channel value){
        this.channels.add(value);
    }
    public void removeChannel(String ID){
        for (int i=0;i<channels.size();i++){
            if (channels.get(i).getID().equals(ID)){
                channels.remove(i);
                break;
            }
        }
    }
    public List<Video>videos = new ArrayList<Video>();
    public List<Video> getVideos() {
        return videos;
    }
    public void setVideos(List<Video> value) {
        this.videos = value;
    }
    public void sortVideos(){
        Collections.sort(videos);
        System.out.println("sorted videos");
    }
    public void addVideo(Video value) {
        videos.add(value);
    }
    public List<Video>sVideos = new ArrayList<Video>();
    public List<Video> getsVideos() {
        return sVideos;
    }
    public void setsVideos(List<Video> value) {
        this.sVideos = value;
    }
    public void addsVideos(Video value){
        this.sVideos.add(value);
    }

    public VideoAdapter searchVideoAdapter= new VideoAdapter(sVideos);

    Set<String> feedLinks =new HashSet<String>();
    Boolean useYoutube=true;
    public Context context;
    public SharedPreferences.Editor editor;

    public boolean isUseVlc() {
        if (playerChoice ==1)
            return true;
        else
            return false;
    }
    public void setUseVlc(boolean useVlc){
        this.playerChoice = 1;
    }

    public int getPlayerChoice() {
        return playerChoice;
    }

    public void setPlayerChoice(int playerChoice) {
        this.playerChoice = playerChoice;
    }

    // 1=vlc, 2=system default, 4=webview
    public int playerChoice;
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



    public UserData(Context con) {
        editor = MainActivity.preferences.edit();
        playerChoice = MainActivity.preferences.getInt("playerChoice", 1);
        this.context = con;
        this.activity = (Activity) con;
        //forceRefresh=true;
        try {
            FileInputStream fileIn = new FileInputStream(this.context.getFilesDir() + "channels.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            channels = (ArrayList<Channel>) in.readObject();
            System.out.println("dir.exists()");
            in.close();
            fileIn.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("read in " + channels.size());
        for (Channel c : channels) {
            for (Video v : c.getVideos()) {
                videos.add(v);
            }

        }
    }
    public boolean saveUserData(List<Channel>channels){
        editor = MainActivity.preferences.edit();
        editor.putInt("playerChoice", playerChoice);
        editor.commit();

        try {
            FileOutputStream fileOut = new FileOutputStream(this.context.getFilesDir()+"channels.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(channels);
            out.close();
            fileOut.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        }





        return true;
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
   
