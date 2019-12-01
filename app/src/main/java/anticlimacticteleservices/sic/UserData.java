package anticlimacticteleservices.sic;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.util.Log;
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
    public long downloadID=0;
    public long downloadVideoID=0;
    public String downloadSourceID;
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
                Log.v("User-Data","attempted to add pre-existing channel "+value.toCompactString());
            }
        }
        if (unique) {
            Log.v("User-Data","adding channel "+value.toCompactString());
            channels.add(value);
            feedLinks.add(value.getUrl());
            getChannelDao().insert(value);
        }
    }
    public void removeChannel(Channel target){
        channelDao.delete(target);
        for (int i=0;i<channels.size();i++){
            if (channels.get(i).equals(target)){
                channels.remove(i);
                break;
            }
        }
    }

    public void refreshChannels(){
        channels = channelDao.getChannels();
    }

    public void refreshVideos(){
        videos = videoDao.getVideos();
    }

    public void removeVideo(Video target){
        videoDao.delete(target);
        for (int i=0;i<videos.size();i++){
            if (videos.get(i).equals(target)){
                videos.remove(i);
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
    public void updateVideo(Video value){
        for (Video v : videos) {
            if (v.getID() == (value.getID())){
                v = value;
                videoDao.update(value);
                break;

            }
        }
    }

    public void updateChannel(Channel value){
        Log.v("User-Data","updating channel "+value.toCompactString());
        channelDao.update(value);
        channels = channelDao.getChannels();
        Log.e("User-Data","updated channel "+value.toCompactString());
    }

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
            System.out.println(videos.size()+"trying to add"+value.toCompactString());
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
        boolean unique=true;
        for (Video v : sVideos) {
            if (v.getSourceID().equals(value.getSourceID())){
                System.out.println("attempted to add duplicate video");
                unique=false;
                break;
            }
        }
        if (unique){
            System.out.println(videos.size()+"trying to add"+value);
           sVideos.add(value);
        }
    }


    private List<Channel> sChannels = new ArrayList<Channel>();
    public void addsChannel(Channel value){
        boolean unique = true;
        for (Channel test : sChannels) {
            if (test.matches(value.getSourceID())){
                unique=false;
            }
        }
        if (unique) {
            sChannels.add(value);
        }
    }


    public List<Channel> getsChannels() {
        return sChannels;
    }
    public void setsChannels(List<Channel> value) {
        this.sChannels = value;
    }


    //Preference data
    private SharedPreferences.Editor editor;

    //user data

    private int youtubePlayerChoice;
    private int bitchutePlayerChoice;
    public boolean newpipeInstalled,chromeInstalled,youtubeInstalled,vlcInstalled;
    public boolean youtubeUseNewpipe() {return youtubePlayerChoice == 32;}
    public boolean youtubeUseExoView() {
        return youtubePlayerChoice == 16;
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
    //went a different way
    public boolean bitchuteUseDefault() {return bitchutePlayerChoice ==2; }
    public boolean bitchuteUseNative() {return bitchutePlayerChoice ==8; }
    public boolean bitchuteUseExo() {return bitchutePlayerChoice ==16; }
    public boolean bitchuteUseNewpipe() {return bitchutePlayerChoice ==32;}
    public boolean bitchuteUseWebtorrentWebview() {return bitchutePlayerChoice ==64;}
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

    public boolean useComments;
    public boolean dissenterComments;
    public boolean kittenComments;
    public boolean backgroundSync;
    public boolean wifionly;

    public boolean isUseComments() {
        return useComments;
    }

    public void setUseComments(boolean useComments) {
        this.useComments = useComments;
    }

    public boolean isDissenterComments() {
        return dissenterComments;
    }

    public void setDissenterComments(boolean dissenterComments) {
        this.dissenterComments = dissenterComments;
    }

    public boolean isKittenComments() {
        return kittenComments;
    }

    public void setKittenComments(boolean kittenComments) {
        this.kittenComments = kittenComments;
    }

    public boolean isBackgroundSync() {
        return backgroundSync;
    }

    public void setBackgroundSync(boolean backgroundSync) {
        this.backgroundSync = backgroundSync;
    }

    public boolean isWifionly() {
        return wifionly;
    }

    public void setWifionly(boolean wifionly) {
        this.wifionly = wifionly;
    }
    public Long scrapeInterval;
    public Long backgroundUpdateInterval;
    public Long activeUpdateInterval;
    public Long channelUpdateInterval;

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
        fragmentID="home";
        //TODO rationalize the preferences betwixt userdata and mainactivity.
        editor = MainActivity.preferences.edit();
        youtubePlayerChoice = MainActivity.preferences.getInt("youtubePlayerChoice", 1024);
        bitchutePlayerChoice = MainActivity.preferences.getInt("bitchutePlayerChoice", 1024);

        feedLinks = MainActivity.preferences.getStringSet("feedlinks",feedLinks);
        feedAge = MainActivity.preferences.getLong("feedAge",7);
        useComments = MainActivity.preferences.getBoolean("useComments",true);
        dissenterComments = MainActivity.preferences.getBoolean("dissenterComments",false);
        kittenComments = MainActivity.preferences.getBoolean("kittenComments",true);
        backgroundSync = MainActivity.preferences.getBoolean("backgroundSync",true);
        wifionly = MainActivity.preferences.getBoolean("wifiOnly",false);
        muteErrors = MainActivity.preferences.getBoolean("muteErrors",true);
        hideWatched = MainActivity.preferences.getBoolean("hideWatched",true);
        scrapeInterval = MainActivity.preferences.getLong("scrapeInterval",720);
        backgroundUpdateInterval = MainActivity.preferences.getLong("backgroundUpdateInterval",60);
        activeUpdateInterval = MainActivity.preferences.getLong("activeUpdateInterval",60);
        channelUpdateInterval = MainActivity.preferences.getLong("channelUpdateInterval", 60);
        bitchuteSearchBitchute = MainActivity.preferences.getBoolean("bitchuteSearchBitchute",true);
        bitchuteSearchGoogle = MainActivity.preferences.getBoolean("bitchuteSearchGoogle",true);
  //      bitchuteSearchDuck = MainActivity.preferences.getBoolean("bitchuteSearchDuck",false);
        //shouldn't be needed
        if (youtubePlayerChoice==0)
            youtubePlayerChoice=1024;
        if (bitchutePlayerChoice==0)
            bitchutePlayerChoice=1024;
        youtubeInstalled=false;
        newpipeInstalled=false;
        chromeInstalled=false;
        vlcInstalled=false;

        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> list = pm.getInstalledApplications(0);
        for (int i = 0; i < list.size(); i++) {

            if (list.get(i).packageName.equals("org.videolan.vlc")){
                System.out.println(list.get(i).packageName);
                vlcInstalled=true;
            }
            if (list.get(i).packageName.equals("org.schabi.newpipe")){
                System.out.println(list.get(i).packageName);
                newpipeInstalled=true;
            }
            if (list.get(i).packageName.equals("com.google.android.youtube")){
                System.out.println(list.get(i).packageName);
                youtubeInstalled=true;
            }
            if (list.get(i).packageName.equals("com.android.chrome")){
                System.out.println(list.get(i).packageName);
                chromeInstalled=true;
            }
        }

        channelDatabase = Room.databaseBuilder(context , ChannelDatabase.class, "channel")
             //TODO get rid of main thread queries
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        channelDao = channelDatabase.ChannelDao();
        if (null != channelDao){
            channels = ((ArrayList<Channel>) channelDao.getChannels());
        }
        else{
            Log.e("User-Data","ChannelDAO failed to load channels");
        }
        sicDatabase = Room.databaseBuilder(con, SicDatabase.class, "mydb")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        videoDao = sicDatabase.videoDao();
        if (null != videoDao) {
            if (hideWatched) {
                videos = videoDao.getUnWatchedVideos();
            } else {
                videos = videoDao.getVideos();
            }
        }
        else{
            Log.e("User-Data","VideoDAO failure, this can't end well");
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
        editor.putBoolean("useComments",useComments);
        editor.putBoolean("dissenterComments",dissenterComments);
        editor.putBoolean("kittenComments",kittenComments);
        editor.putBoolean("backgroundSync",backgroundSync);
        editor.putBoolean("wifiOnly",wifionly);
        editor.putBoolean("muteErrors",muteErrors);
        editor.putBoolean("hideWatched",hideWatched);
        editor.putBoolean("bitchuteSearchBitchute",bitchuteSearchBitchute);
        editor.putBoolean("bitchuteSearchGoogle",bitchuteSearchGoogle);
        editor.putBoolean("bitchuteSearchDuck",bitchuteSearchDuck);
        editor.putBoolean("bitchuteSearchDuck",bitchuteSearchDuck);
        editor.putBoolean("bitchuteSearchDuck",bitchuteSearchDuck);
        editor.putBoolean("bitchuteSearchDuck",bitchuteSearchDuck);
        editor.putLong("scrapeInterval",scrapeInterval);
        editor.putLong("backgroundUpdateInterval",backgroundUpdateInterval);
        editor.putLong("channelUpdateInterval",channelUpdateInterval);
        editor.putLong("activeUpdateInterval",activeUpdateInterval);
        editor.commit();
        return true;
    }
    private SwipeRefreshLayout swipeRefreshLayout;

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }

    public void setSwipeRefreshLayout(SwipeRefreshLayout swipeRefreshLayout) {
        this.swipeRefreshLayout = swipeRefreshLayout;
    }
    private String fragmentID;

    public String getFragmentID() {
        return fragmentID;
    }

    public void setFragmentID(String fragmentID) {
        this.fragmentID = fragmentID;
    }
    private boolean forceRefresh;

    public boolean isForceRefresh() {
        return forceRefresh;
    }

    public void setForceRefresh(boolean forceRefresh) {
        this.forceRefresh = forceRefresh;
    }
    private boolean muteErrors;

    public boolean isMuteErrors() {
        return muteErrors;
    }

    public void setMuteErrors(boolean muteErrors) {
        this.muteErrors = muteErrors;
    }
    private boolean bitchuteSearchGoogle,bitchuteSearchDuck,bitchuteSearchBitchute;

    public boolean hideWatched;
    public boolean isHideWatched(){return hideWatched;}
    public void setHideWatched(boolean hideWatched){this.hideWatched = hideWatched;}

    public boolean isBitchuteSearchGoogle() {
        return bitchuteSearchGoogle;
    }

    public void setBitchuteSearchGoogle(boolean bitchuteSearchGoogle) {
        this.bitchuteSearchGoogle = bitchuteSearchGoogle;
    }

    public boolean isBitchuteSearchDuck() {
        return bitchuteSearchDuck;
    }

    public void setBitchuteSearchDuck(boolean bitchuteSearchDuck) {
        this.bitchuteSearchDuck = bitchuteSearchDuck;
    }

    public boolean isBitchuteSearchBitchute() {
        return bitchuteSearchBitchute;
    }

    public void setBitchuteSearchBitchute(boolean bitchuteSearchBitchute) {
        this.bitchuteSearchBitchute = bitchuteSearchBitchute;
    }
    private ActionBar mainActionBar;

    public ActionBar getMainActionBar() {
        return mainActionBar;
    }

    public void setMainActionBar(ActionBar mainActionBar) {
        this.mainActionBar = mainActionBar;
    }
    private int webViewOption;

    public int getWebViewOption() {
        return webViewOption;
    }

    public void setWebViewOption(int webViewOption) {
        this.webViewOption = webViewOption;
    }
}
   
