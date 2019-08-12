package anticlimacticteleservices.sic;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;


public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.CustomViewHolder> {
    private List<Channel> channels;

    private Button dialogButton;
    private Button subscribeButton;
    Set<String> subscriptionList;
    String[] subscriptionArray;
 //   Channel chan;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    private String status;
    private FragmentActivity myContext;
    public class CustomViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private ImageView image;
        private Button subscribed;
        private TextView description;
        private ImageView serviceIcon;

//        final Context context = this;

        CustomViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.channelName);
            image = view.findViewById(R.id.channelthumbnail);
            subscribed = view.findViewById(R.id.button);
            description= view.findViewById(R.id.channel_description);
            serviceIcon= view.findViewById(R.id.channelserviceicon);
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
        else {
            System.out.println("no thumbnail set for channel");
        }

        if (channel.isBitchute()) {
            hold.serviceIcon.setImageResource(R.drawable.bitchuteicon2);
        } else {
            hold.serviceIcon.setImageResource(R.drawable.youtubeicon);
        }


        hold.name.setText(channel.getTitle());
        hold.description.setText(channel.getDescription());
        //System.out.println(channel);
        status="Subscribe";
        for (Channel c : MainActivity.masterData.getChannels()){
            if (c.matches(channel.getSourceID())){
                status="Unsubscribe";
            }
        }
        hold.subscribed.setText(status);
        final Button sub = hold.subscribed;
        hold.subscribed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status=(String)sub.getText();
                if(status.equals("Unsubscribe")){
                    status="Subscribe";
                    MainActivity.masterData.removeChannel(channel.getSourceID());
                    sub.setText(status );
                }
                else {
                    System.out.println("trying to add channel"+channel.getUrl());
                    new ChannelInit().execute(channel.getUrl());
                    status="Unsubscribe";
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
                TextView description = dialog.findViewById(R.id.channel_description);
                Spanned spanned = HtmlCompat.fromHtml(chan.getDescription(), HtmlCompat.FROM_HTML_MODE_COMPACT);
                description.setText(spanned);
                description.append(System.getProperty("line.separator"));
                description.append("Subscribers:"+chan.getSubscribers()+System.getProperty("line.separator"));
                description.append("last synch:"+new Date(chan.getLastsync())+System.getProperty("line.separator"));
                description.append("source url:"+chan.getUrl()+System.getProperty("line.separator"));
                description.append("Started:"+new Date(chan.getJoined())+System.getProperty("line.separator"));
                //description.append("youtube:"+chan.getSubscribers()+System.getProperty("line.separator"));

                ImageView image = dialog.findViewById(R.id.thumbNailView);
                if (!chan.getThumbnail().isEmpty()){
                    Picasso.get().load(chan.getThumbnail()).into(image);
                }
                dialogButton = dialog.findViewById(R.id.closeButton);
                subscribeButton =dialog.findViewById(R.id.subscribe_button);
                TextView name = dialog.findViewById(R.id.channel_name);
                name.setText(chan.getTitle());
                CheckBox archive =dialog.findViewById(R.id.channel_archive);
                archive.setChecked(chan.isArchive());
                CheckBox notify = dialog.findViewById(R.id.channel_notify);
                notify.setChecked(chan.isNotify());
                status="Subscribe";
                for (Channel c : MainActivity.masterData.getChannels()){
                    if (c.matches(channel.getSourceID())){
                        status="Unsubscribe";
                    }
                }
                subscribeButton.setText(status);
 //               System.out.println(chan);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chan.setNotify(notify.isChecked());
                        chan.setArchive(archive.isChecked());
                        MainActivity.masterData.getChannelDao().update(chan);
                        dialog.dismiss();
                    }
                });
                subscribeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(status.equals("Unsubscribe")){
                            MainActivity.masterData.removeChannel(channel.getSourceID());
                            status = "Subscribe" ;
                            sub.setText(status);
                        }
                        else {
                          //  MainActivity.masterData.addFeedLink(channel.getUrl());
                            System.out.println("trying to add channel"+channel.getUrl());
                            new ChannelInit().execute(channel.getUrl());
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
                int videoCount=0;
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        Channel chan =channels.get(position);
                        //System.out.println(chan);
                        ArrayList <Video> channelVideos=new ArrayList<Video>();
                        channelVideos = (ArrayList<Video>) MainActivity.masterData.getVideoDao().getVideosByAuthorId(chan.getID());
                        if (!channelVideos.isEmpty()) {
                            Fragment fragment = new VideoFragment();
                            ((VideoFragment) fragment).setVideos(channelVideos);
                            FragmentTransaction transaction = MainActivity.masterData.fragmentManager.beginTransaction();
                            transaction.replace(R.id.fragment, fragment);
                            transaction.addToBackStack(null);
                            transaction.commitAllowingStateLoss();
                        }
                        else {
                         //   Toast.makeText(MainActivity.masterData.context,  "no videos for channel in feed currently", Toast.LENGTH_SHORT).show();
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
