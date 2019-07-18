package anticlimacticteleservices.clienttest26;

public class UserData {
    List<Channel> channels = new ArrayList<>();
    List<String> feedLinks = new ArrayList<>;
    SharedPreferences prefs
    SharedPreferences.Editor editor
        
    public List<String> getFeedLinks(){
        prefs = this.getSharedPreferences( "com.mycompany.client", Context.MODE_PRIVATE);
        editor = prefs.edit();
        Set<String> feeds = prefs.getStringSet("channelUrls",null);
        if (feeds.isEmpty())
            feeds.add("https://www.youtube.com/feeds/videos.xml?channel_id=UC-lHJZR3Gqxm24_Vd_AJ5Yw")
            feeds.add("https://bitchute.com/channel/Styxhexenham/");
        }
        feedLinks=feeds;
      return "done";
    }
    public void removeFeedLink(String deadLink){
      //TODO figure out best way to remove feedlink from extent list
    }
    public void addFeedLink(String newLink){
      //TODO duplicate and viability parsing.
      feedLinks.add(newLink);
      prefs = this.getSharedPreferences( "com.mycompany.client", Context.MODE_PRIVATE);
      editor = prefs.edit();
      editor.putStringSet("channelUrls", feedLinks);
      editor.commit();  
    }
}
   
