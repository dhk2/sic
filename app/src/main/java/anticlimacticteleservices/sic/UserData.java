package anticlimacticteleservices.sic;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class UserData {
   //Database data
    public SicDatabase sicDatabase;
    public ChannelDatabase channelDatabase;
    public CommentDatabase commentDatabase;
    private CommentDao commentDao;
    private VideoDao videoDao;
    private ChannelDao channelDao;
    public CommentDao getCommentDao() {
        System.out.println("getting comment dao");
        return commentDao;
    }
    public void setCommentDao(CommentDao value) {
        System.out.println("setting comment dao in masterdata"+value.toString());
        this.commentDao = value;
    }
    public void setVideoDao(VideoDao value) {
        System.out.println("setting video dao in masterdata"+value.toString());
        this.videoDao = value;
    }
    public VideoDao getVideoDao(){
        return videoDao;
    }
    public ChannelDao getChannelDao() {
        System.out.println("getting Channel dao");
        return channelDao;
    }
    public void setChannelDao(ChannelDao value) {
        System.out.println("setting video dao in masterdata"+value.toString());
        this.channelDao = value;
    }

    //fragment manager management hooks.
    FragmentManager fragmentManager;
    Fragment fragment;
    FragmentTransaction transaction;
    public Fragment getFragment() {
        return fragment;
    }
    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }
    public FragmentTransaction getTransaction() {
        return transaction;
    }
    public void setTransaction(FragmentTransaction transaction) {
        this.transaction = transaction;
    }
    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }
    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public Activity activity;
    public Context context;

    //gui management data

    //handle to webview so it can be shut down while working in background
    public WebView webPlayer;
    public WebViewClient webViewClient;
    public fragment_webviewplayer wvf_handle;
    public fragment_videoplayer vvf_handle;
    public Video webPlayerVideo;
    public SimpleExoPlayer player;
    public Long playerVideoID;

    public Long getPlayerVideoID() {
        return playerVideoID;
    }

    public void setPlayerVideoID(Long playerVideoID) {
        this.playerVideoID = playerVideoID;
    }

    public SimpleExoPlayer getPlayer() {
        return player;
    }

    public void setPlayer(SimpleExoPlayer player) {
        this.player = player;
    }

    private List<Channel> channels = new ArrayList<Channel>();
    public List<Channel> getChannels() {
        return channels;
    }
    public void addChannel(Channel value){
        boolean unique = true;
        for (Channel test : channels) {
            if (test.matches(value.getSourceID())){
                unique=false;
            }
        }
        if (unique==true) {
            channels.add(value);
            feedLinks.add(value.getUrl());
            getChannelDao().insert(value);
        }
    }
    public void removeChannel(String ID){

        for (int i=0;i<channels.size();i++){
            if (channels.get(i).matches(ID)){
                channels.remove(i);
                break;
            }
        }
    }

    private List<Video>videos = new ArrayList<Video>();
    public void sortVideos(){
        Collections.sort(videos);
        System.out.println("sorted videos");
    }
    public List<Video> getVideos() {
        return videos;
    }
    public void setVideos(List<Video> value) {this.videos = value;}
    public void addVideo(Video value) {

        boolean unique=true;
        for (Video v : videos) {
            if (v.getSourceID().equals(value.getSourceID())){
                System.out.println("attempted to add duplicate video");
                unique=false;
                break;
            }
        }
        if (unique){
            System.out.println(videos.size()+"trying to add"+value);
            videos.add(value);
            videoDao.insert(value);

        }
    }

    private List<Video>sVideos = new ArrayList<Video>();
    public VideoAdapter searchVideoAdapter= new VideoAdapter(sVideos);
    public void sortsVideos(){
        Collections.sort(sVideos);
        System.out.println("sorted search videos");
    }
    public List<Video> getsVideos() {
        return sVideos;
    }
    public void setsVideos(List<Video> value) {
        this.sVideos = value;
    }
    public void addsVideos(Video value){
        this.sVideos.add(value);
    }


    private List<Channel> sChannels = new ArrayList<Channel>();
    public void addsChannel(Channel value){
        boolean unique = true;
        for (Channel test : sChannels) {
            if (test.matches(value.getSourceID())){
                unique=false;
            }
        }
        if (unique==true) {
            sChannels.add(value);
        }
    }


    public List<Channel> getsChannels() {
        return sChannels;
    }
    public void setsChannels(ArrayList<Channel> value) {
        this.sChannels = value;
    }


    //Preference data
    private SharedPreferences.Editor editor;

    //user data

    private int youtubePlayerChoice;
    private int bitchutePlayerChoice;
    public boolean youtubeUseExoView() {
        return youtubePlayerChoice == 8;
    }
    public boolean youtubeUseWebView() {
        return youtubePlayerChoice == 4;
    }
    public boolean youtubeUseVlc() {
        return youtubePlayerChoice == 1;
    }
    public boolean youtubeUseDefault() {return youtubePlayerChoice ==2; }
    public boolean bitchuteUseWebView() {
        return bitchutePlayerChoice == 4;
    }
    public boolean bitchuteUseVlc() {
        return bitchutePlayerChoice == 1;
    }
    public boolean bitchuteUseDefault() {return bitchutePlayerChoice ==2; }
    public boolean bitchuteUseNative() {return bitchutePlayerChoice ==8; }
    public boolean bitchuteUseExo() {return bitchutePlayerChoice ==16; }
    public int getYoutubePlayerChoice() {
        return youtubePlayerChoice;
    }
    public int getBitchutePlayerChoice() {
        return bitchutePlayerChoice;
    }
    public void setYoutubePlayerChoice(int playerChoice) {
        this.youtubePlayerChoice = playerChoice;
    }
    public void setBitchutePlayerChoice(int playerChoice) {
        this.bitchutePlayerChoice = playerChoice;
    }

    public long feedAge;
    public long getFeedAge() {
        return feedAge;
    }
    public void setFeedAge(long feedAge) {
        this.feedAge = feedAge;
    }

    private Set<String> feedLinks =new HashSet<String>();
    public void removeFeedLink(String deadLink){
        HashSet tempSub = new HashSet();

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
        editor.putStringSet("channelUrls", feedLinks);
        editor.commit();
    }
    public void addFeedLinks(Set<String> newLinks) {
        for (String newLink : newLinks) {
            try {
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
                System.out.println("count of feeds:" + feedLinks.size());
                editor = MainActivity.preferences.edit();
                editor.putStringSet("channelUrls", feedLinks);
                editor.commit();
            }
            catch(NullPointerException e) {
                System.out.println("null pointer error adding feed links");
                e.printStackTrace();
            }
        }
    }
    public void addFeedLink(String newLink) {
        boolean unique=true;
        if ((null != newLink ) && (!"".equals(newLink))) {
            for (String g : feedLinks) {
                if (g.equals(newLink)) {
                    unique = false;
                }
            }
            if (unique) {
                feedLinks.add(newLink);
                editor = MainActivity.preferences.edit();
                editor.putStringSet("channelUrls", feedLinks);
                editor.commit();
            }
        }
    }
    public void setFeedLinks(Set<String> links){
        feedLinks.clear();
        feedLinks.addAll(links);

        editor = MainActivity.preferences.edit();
        editor.putStringSet("channelUrls", feedLinks);
        editor.commit();
    }
    public Set<String> getFeedLinks(){
        //      editor = MainActivity.preferences.edit();
        //      Set<String> feeds = MainActivity.preferences.getStringSet("channelUrls",null);
        return feedLinks;
    }

    //Other stuff
    final SimpleDateFormat bdf = new SimpleDateFormat("EEE','  dd MMM yyyy HH:mm:SSZZZZ");
    final SimpleDateFormat ydf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");





    public UserData(Context con) {
        context=con;
        //TODO rationalize the preferences betwixt userdata and mainactivity.
        editor = MainActivity.preferences.edit();
        youtubePlayerChoice = MainActivity.preferences.getInt("youtubePlayerChoice", 4);
        bitchutePlayerChoice = MainActivity.preferences.getInt("bitchutePlayerChoice", 8);
        feedAge = MainActivity.preferences.getLong("feedAge",7);
        feedLinks = MainActivity.preferences.getStringSet("feedlinks",feedLinks);
        System.out.println("loaded/reloaded preferences:"+feedAge+" "+youtubePlayerChoice+" "+bitchutePlayerChoice);
        //shouldn't be needed
        if (youtubePlayerChoice==0)
            youtubePlayerChoice=4;
        if (bitchutePlayerChoice==0)
            bitchutePlayerChoice=8;

        channelDatabase = Room.databaseBuilder(context , ChannelDatabase.class, "channel")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        channelDao = channelDatabase.ChannelDao();
        if (null != channelDao){
            channels = ((ArrayList<Channel>) channelDao.getChannels());
        }
        else{
            System.out.println("ChannelDAO failed to load channels");
        }
        sicDatabase = Room.databaseBuilder(con, SicDatabase.class, "mydb")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        videoDao = sicDatabase.videoDao();
        if (null != videoDao){
            setVideos(videoDao.getVideos());
        }
        else{
            System.out.println("VideoDAO failure, this can't end well");
        }
        commentDatabase = Room.databaseBuilder(context , CommentDatabase.class, "comment")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        commentDao = commentDatabase.CommentDao();
    }
        public boolean saveUserData(){
        Context context = MainActivity.masterData.context;
        editor = MainActivity.preferences.edit();
        editor.putInt("youtubePlayerChoice", youtubePlayerChoice);
        editor.putInt("bitchutePlayerChoice", bitchutePlayerChoice);
        editor.putLong("feedAge",feedAge);
        editor.putStringSet("feedlinks",getFeedLinks());
        editor.commit();
        return true;
    }
}
   
