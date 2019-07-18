package anticlimacticteleservices.clienttest26;

public class UserData {
    List<Channel> channels = new ArrayList<>();
    List<String> feedLinks = new ArrayList<>;
    
    public List<String> getFeedLinks(){
      return feedLinks;
    }
    public void removeFeedLink(String deadLink){
      // figure out best way to remove feedlink from extent list
    }
    public void addFeedLink(String newLink){
      //TODO duplicate and viability parsing.
      feedLinks.add(newLink);
    }
    
}
   
