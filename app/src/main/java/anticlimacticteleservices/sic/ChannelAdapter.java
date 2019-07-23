package anticlimacticteleservices.sic;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.text.HtmlCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


import org.w3c.dom.Text;

import java.util.List;
import java.util.Set;


public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.CustomViewHolder> {
    private List<Channel> channels;

    Button dialogButton;
    Button subscribeButton;
    Set<String> subscriptionList;
    String subscriptionArray[];
 //   Channel chan;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    String status;
    private FragmentActivity myContext;
    public class CustomViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private ImageView image;
        private Button subscribed;
        private TextView description;
//        final Context context = this;

        public CustomViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.channelName);
            image = view.findViewById(R.id.channelthumbnail);
            subscribed = view.findViewById(R.id.button);
            description= view.findViewById(R.id.channel_description);
         }
    }
    public ChannelAdapter(){}

    public ChannelAdapter(List<Channel> channels) {
        this.channels = channels;
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.channellist, parent, false);

        return new CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder hold, final int position) {
        final Channel channel = channels.get(position);
 //       final CustomViewHolder holder = hold;

        //holder.name.setText(channel.getTitle());
        if (!channel.getThumbnail().isEmpty()){
            Picasso.get().load(channel.getThumbnail()).into(hold.image);
        }

   //   System.out.println(channel.toString());
        hold.name.setText(channel.getTitle());
        hold.description.setText(channel.getDescription());
        status="Subscribe";
        for (String g : MainActivity.masterData.getFeedLinks()){
            if (g.equals(channel.getUrl())){
                status="Unsubscribe";
            }
        }
        hold.subscribed.setText(status);
        final Button sub = hold.subscribed;
        hold.subscribed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(status.equals("Unsubscribe")){
                    MainActivity.masterData.removeFeedLink(channel.getUrl());
                    status="Subscribe";
                    sub.setText(status );
                }
                else {
                    MainActivity.masterData.addFeedLink(channel.getUrl());

                    status = "Unsubscribe";
                    sub.setText(status);
                }
            }
        });

        hold.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Channel chan =channels.get(position);

                final Dialog dialog = new Dialog(view.getContext());
                dialog.setContentView(R.layout.channelprop);
                dialog.setTitle(chan.getTitle());

                // set the custom dialog components - text, image and button
 /*               WebView webView = (WebView) dialog.findViewById(R.id.channelDetails);
                webView.loadData(chan.getDescription(), "text/html", "UTF-8");
 */
                TextView description =(TextView)dialog.findViewById(R.id.channel_description);
                Spanned spanned = HtmlCompat.fromHtml(chan.getDescription(), HtmlCompat.FROM_HTML_MODE_COMPACT);
                description.setText(spanned);
                ImageView image = (ImageView) dialog.findViewById(R.id.thumbNailView);
                if (!chan.getThumbnail().isEmpty()){
                    Picasso.get().load(chan.getThumbnail()).into(image);
                }
                dialogButton = (Button) dialog.findViewById(R.id.closeButton);
                subscribeButton =dialog.findViewById(R.id.subscribe_button);
                TextView name = dialog.findViewById(R.id.channel_name);
                name.setText(chan.getTitle());
                status="Subscribe";
                for (String g : MainActivity.masterData.getFeedLinks()){
                    if (g.equals(chan.getUrl())){
                        status="Unsubscribe";
                    }
                }
                subscribeButton.setText(status);
 //               System.out.println(chan);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                subscribeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(status.equals("Unsubscribe")){
                            MainActivity.masterData.removeFeedLink(channel.getUrl());
                            status = "Subscribe" ;
                            sub.setText(status);
                        }
                        else {
                            MainActivity.masterData.addFeedLink(channel.getUrl());
                            status="Unsubscribe";
                            sub.setText(status);
                         }
                    }
                });
                dialog.show();
            }
        });
        hold.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                System.out.println("need to make a new view that works properly with context ");

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Channel chan =channels.get(position);
                        if (!chan.getVideos().isEmpty()) {
                            Fragment fragment = new VideoFragment();
                            ((VideoFragment) fragment).setVideos(chan.getVideos());
                            FragmentTransaction transaction = MainActivity.masterData.fragmentManager.beginTransaction();
                            transaction.replace(R.id.fragment, fragment);
                            transaction.addToBackStack(null);
                            transaction.commit();
                        }
                    }
                } );
                thread.start();
            return true;
            }
        });
    }
    @Override
    public int getItemCount() {
        return channels.size();
    }
}
