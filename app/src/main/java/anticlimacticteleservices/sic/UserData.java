package anticlimacticteleservices.sic;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.webkit.WebView;

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
    final SimpleDateFormat bdf = new SimpleDateFormat("EEE','  dd MMM yyyy HH:mm:SSZZZZ");
    final SimpleDateFormat ydf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    public List<Channel> getChannels() {
        return channels;
    }
    public void setChannels(ArrayList<Channel> value) {
        this.channels = value;
    }
    private List<Channel> sChannels = new ArrayList<Channel>();
    private List<Channel> channels = new ArrayList<Channel>();
    public Context context;
    private List<Video>videos = new ArrayList<Video>();
    public List<Video> getVideos() {
        return videos;
    }
    public void setVideos(List<Video> value) {
        this.videos = value;
    }
    private List<Video>sVideos = new ArrayList<Video>();
    public VideoAdapter searchVideoAdapter= new VideoAdapter(sVideos);
    private Set<String> feedLinks =new HashSet<String>();
    private Boolean useYoutube=true;
    private SharedPreferences.Editor editor;
    // 1=vlc, 2=system default, 4=webview
    private int youtubePlayerChoice;
    private int bitchutePlayerChoice;
    FragmentManager fragmentManager;
    Fragment fragment;
    private FragmentTransaction transaction;
    public Activity activity;
    private boolean forceRefresh;
    public WebView webPlayer;



    public int dirtyData =0;
    public List<Channel> getsChannels() {
        return sChannels;
    }
    public void setsChannels(ArrayList<Channel> value) {
        this.sChannels = value;
    }
    public void addsChannel(Channel value){
        boolean unique = true;
        for (Channel test : sChannels) {
            if (test.matches(value.getID())){
                unique=false;
            }
        }
        if (unique==true) {
            sChannels.add(value);
        }
    }
    public void addChannel(Channel value){
        this.channels.add(value);
        this.dirtyData++;
    }
    public void removeChannel(String ID){

        for (int i=0;i<channels.size();i++){
            if (channels.get(i).matches(ID)){
                channels.remove(i);
                dirtyData++;
                break;
            }
        }
    }
    public void sortVideos(){
        Collections.sort(videos);
        System.out.println("sorted videos");
    }
    public void sortsVideos(){
        Collections.sort(sVideos);
        System.out.println("sorted search videos");
    }
    public void addVideo(Video value) {
        videos.add(value);
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
    public boolean youtubeUseWebView() {
        return youtubePlayerChoice == 4;
    }
    public boolean youtubeUseVlc() {
        return youtubePlayerChoice == 1;
    }
    public boolean youtubeUseDefault() {return youtubePlayerChoice ==2; }
    public boolean bitchuteUseWebView() {
        return youtubePlayerChoice == 4;
    }
    public boolean bitchuteUseVlc() {
        return youtubePlayerChoice == 1;
    }
    public boolean bitchuteUseDefault() {return youtubePlayerChoice ==2; }
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
    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }
    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }
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
    public UserData(Context con) {
        editor = MainActivity.preferences.edit();
        youtubePlayerChoice = MainActivity.preferences.getInt("youtubePlayerChoice", 1);
        bitchutePlayerChoice = MainActivity.preferences.getInt("bitchutePlayerChoice", 1);
        this.context=con;
        //shouldn't be needed
        if (youtubePlayerChoice==0)
            youtubePlayerChoice=1;
        if (bitchutePlayerChoice==0)
            bitchutePlayerChoice=1;

        try {
            FileInputStream fileIn = new FileInputStream(this.context.getFilesDir() + "channels.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            channels = (ArrayList<Channel>) in.readObject();
            System.out.println("Saved channels read "+channels.size());
            in.close();
            fileIn.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            System.out.println("null pointer issue:");
            e.printStackTrace();
            //Toast.makeText(activity,"error reading in subsciption file, subscriptions reset",Toast.LENGTH_SHORT).show();
        }
        System.out.println("read in " + channels.size());
        if(channels.isEmpty()){
            try {
                FileInputStream fileIn = new FileInputStream(this.context.getFilesDir() + "channels.lkg");
                ObjectInputStream in = new ObjectInputStream(fileIn);
                channels = (ArrayList<Channel>) in.readObject();
                System.out.println("last known good channels read "+channels.size());
                in.close();
                fileIn.close();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e){
                System.out.println("null pointer issue:");
                e.printStackTrace();
                //Toast.makeText(activity,"error reading in subsciption file, subscriptions reset",Toast.LENGTH_SHORT).show();
            }
        }
        else {
            try (FileInputStream in = new FileInputStream("channels.src")) {
                try (FileOutputStream out = new FileOutputStream("channels.lkg")) {
                    // Transfer bytes from in to out
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (Channel c : channels) {
            for (Video v : c.getVideos()) {
                videos.add(v);
            }

        }
        Collections.sort(videos);
    }
    public boolean saveUserData(){
        Context context = MainActivity.masterData.context;
        if (dirtyData>0) {
            dirtyData=0;
            try {
                FileOutputStream fileOut = new FileOutputStream(context.getFilesDir() + "channels.ser");
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(channels);
                out.close();
                fileOut.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();

            }
        }

        editor = MainActivity.preferences.edit();
        editor.putInt("youtubePlayerChoice", youtubePlayerChoice);
        editor.putInt("bitchutePlayerChoice", bitchutePlayerChoice);

        editor.commit();
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
        forceRefresh=true;
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
                forceRefresh = true;
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
                forceRefresh = true;
            }
        }
    }
    public void setFeedLinks(Set<String> links){
        feedLinks.clear();
        feedLinks.addAll(links);

        editor = MainActivity.preferences.edit();
        editor.putStringSet("channelUrls", feedLinks);
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
    public int getDirtydata() {
        return dirtyData;
    }
    public void setDirtydata(int dirtydata) {
        dirtyData = dirtydata;
    }
}
   
