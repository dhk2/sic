package anticlimacticteleservices.clienttest26;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserData {
    private List<Channel> channels = new ArrayList<>();
    Set<String> feedLinks =new HashSet<String>();

 //   SharedPreferences prefs;
    SharedPreferences.Editor editor;
        
    public Set<String> getFeedLinks(){
       //ontext context =
       //prefs =  getSharedPreferences( "com.mycompany.client", Context.MODE_PRIVATE);

        editor = MainActivity.preferences.edit();
        Set<String> feeds = MainActivity.preferences.getStringSet("channelUrls",null);
        if (null == feeds){
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
      //TODO figure out best way to remove feedlink from extent list
    }
    public void addFeedLink(String newLink){
      //TODO duplicate and viability parsing.
      feedLinks.add(newLink);
      Context context = null;
      //MainActivity.preferences = .getSharedPreferences( "com.mycompany.client", Context.MODE_PRIVATE);
      editor = MainActivity.preferences.edit();
      editor.putStringSet("channelUrls", (Set<String>) feedLinks);
      editor.commit();  
    }
    public void setFeedLinks(Set<String> links){
        feedLinks.clear();
        feedLinks.addAll(links);
    }
}
   
