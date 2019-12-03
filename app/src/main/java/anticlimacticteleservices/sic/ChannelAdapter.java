package anticlimacticteleservices.sic;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.text.HtmlCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spanned;
import android.util.Log;
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
    private String status;
    public class CustomViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private ImageView image;
        private Button subscribed;
        private TextView description;
        private ImageView serviceIcon;
        CustomViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.channelName);
            image = view.findViewById(R.id.channelthumbnail);
            subscribed = view.findViewById(R.id.button);
            description= view.findViewById(R.id.channel_property_description);
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
        Channel channel = channels.get(position);
        //Log.v("Channel-Adaptor","attempting to display a channel"+channel.toString() );
        if (!channel.getThumbnail().isEmpty()){
            Picasso.get().load(channel.getThumbnail()).resize(160,120).centerInside().into(hold.image);
        }
        else {
            Log.w("ChannelAdaptor","no thumbnail set for channel");
        }
        if (channel.isBitchute()) {
            hold.serviceIcon.setImageResource(R.drawable.bitchuteicon2);
        }
        if (channel.isYoutube()) {
            hold.serviceIcon.setImageResource(R.drawable.youtubeicon);
        }
        if (channel.isYoutube() && channel.isBitchute()) {
            hold.serviceIcon.setImageResource(R.drawable.dualsource);
        }
        hold.name.setText(channel.getTitle());
        hold.description.setText(channel.getDescription());
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
                    MainActivity.masterData.removeChannel(channel);
                    sub.setText(status );
                }
                else {
                    Log.v("Channel Adapter","Subscribing to"+channel.getUrl());
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
                fragment_channel_properties cpfragment = fragment_channel_properties.newInstance(chan,"");
                FragmentTransaction transaction = MainActivity.masterData.getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment, cpfragment);
                transaction.addToBackStack(null);
                transaction.commitAllowingStateLoss();
            }
        });
        hold.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                Channel chan = channels.get(position);
                //System.out.println(chan);
                ArrayList<Video> channelVideos = new ArrayList<Video>();
                channelVideos = (ArrayList<Video>) MainActivity.masterData.getVideoDao().getVideosByAuthorId(chan.getID());
                if (!channelVideos.isEmpty()) {
                    Fragment fragment = new VideoFragment();
                    ((VideoFragment) fragment).setVideos(channelVideos);
                    FragmentTransaction transaction = MainActivity.masterData.fragmentManager.beginTransaction();
                    transaction.replace(R.id.fragment, fragment);
                    transaction.addToBackStack(null);
                    transaction.commitAllowingStateLoss();
                } else {
                    Toast.makeText(MainActivity.masterData.context,  "no videos for channel in feed currently", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
    }


    public int getItemCount() {
        return channels.size();
    }
}
