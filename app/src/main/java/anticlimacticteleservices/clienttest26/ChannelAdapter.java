package anticlimacticteleservices.clienttest26;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.CustomViewHolder> {
    private List<Channel> channels;
    private Context mContext;
    Button dialogButton;
    Button subscribeButton;
    Set<String> subscriptionList;
    String subscriptionArray[];
    Channel chan;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView image;
//        final Context context = this;

        public CustomViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.channelName);
            image = view.findViewById(R.id.channelthumbnail);
         }
    }
    public ChannelAdapter(){}

    public ChannelAdapter(List<Channel> channels, Context context) {
        this.channels = channels;
        this.mContext=context;
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
        final CustomViewHolder holder = hold;

        //holder.name.setText(channel.getTitle());
        if (!channel.getThumbnail().isEmpty()){
            Picasso.get().load(channel.getThumbnail()).into(holder.image);
        }
        holder.name.setText(channel.getTitle());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                chan =channels.get(position);
                System.out.println(chan.toString());
                final Dialog dialog = new Dialog(view.getContext());
                dialog.setContentView(R.layout.channelprop);
                dialog.setTitle(chan.getTitle());

                // set the custom dialog components - text, image and button
                WebView webView = (WebView) dialog.findViewById(R.id.videoDetails);

                webView.loadData(chan.toString(), "text/html", "UTF-8");
                ImageView image = (ImageView) dialog.findViewById(R.id.thumbNailView);
                if (!chan.getThumbnail().isEmpty()){
                    Picasso.get().load(chan.getThumbnail()).into(image);
                }
                dialogButton = (Button) dialog.findViewById(R.id.closeButton);
                subscribeButton =dialog.findViewById(R.id.subscribe_button);


                prefs = view.getContext().getSharedPreferences( "com.mycompany.client", Context.MODE_PRIVATE);
                editor = prefs.edit();
                subscriptionList = prefs.getStringSet("channelUrls",null);
                subscriptionArray = new String[subscriptionList.size()];
                subscriptionArray = subscriptionList.toArray(subscriptionArray);
                System.out.println(subscriptionArray);
                String buttonText="Subscribe";
                for (String d : subscriptionArray){
                    if (d.equals(chan.getUrl())){
                        buttonText="Unsubscribe";
                    }
                }
                subscribeButton.setText(buttonText);

                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                subscribeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println("need to code up the actual subscribe/unsubscribe activity still");
                        Set<String> tempSub = new HashSet<String>();
                        if(subscribeButton.getText().equals("Unsubscribe")){
                            for (String s : subscriptionArray){
                                if (!s.equals(chan.getUrl())){
                                    tempSub.add(s);
                                }
                                else
                                {
                                    System.out.println("removed "+chan.getUrl());
                                    System.out.println("otherwise known as "+chan.getTitle());

                                }
                            }
                            System.out.println(tempSub.size()+"   "+subscriptionArray.length);
                            subscribeButton.setText("Subscribe");
                        }
                        else
                        {
                            for (String s : subscriptionArray){
                                    tempSub.add(s);
                                }
                            tempSub.add(chan.getUrl());
                            {
                                System.out.println("added " + chan.getUrl());
                                System.out.println("otherwise known as " + chan.getTitle());
                                System.out.println(tempSub.size()+"   "+subscriptionArray.length);
                            }
                            subscribeButton.setText("Unsubscribe");
                        }
                        editor.putStringSet("channelUrls", tempSub);
                        editor.commit();
                    }
                });
                dialog.show();
                return false;
            }

        });




        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int adapterPos = holder.getAdapterPosition();

                        Channel chan =channels.get(position);


                        Fragment fragment = new VideoFragment();
                        ((VideoFragment) fragment).setvideos(chan.getVideos());
                        FragmentManager manager = ((AppCompatActivity) mContext).getSupportFragmentManager();
                        FragmentTransaction transaction = manager.beginTransaction();
                        transaction.replace(R.id.fragment, fragment);
                        transaction.commit();

                    }
                } );
                thread.start();
            }
        });
    }

    @Override
    public int getItemCount() {
        return channels.size();
    }

}
