package anticlimacticteleservices.sic;

//import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class Subscription {
    private String title;
    private String author;
    private String description;
    private ArrayList<Channel> channels;


    public Subscription(String title) {
        this.title = title;
        channels = new ArrayList<Channel>();

    }
    public void addChannel(Channel chan){
        channels.add(chan);
    }

}
