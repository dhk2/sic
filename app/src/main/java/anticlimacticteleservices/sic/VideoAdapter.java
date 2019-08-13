package anticlimacticteleservices.sic;

import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.text.HtmlCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static android.app.PendingIntent.getActivity;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.CustomViewHolder> {
   private List<Video> videos = new ArrayList<>();
   private List<Comment> comments = new ArrayList<>();
   private int pos;
    public class CustomViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private ImageView image;
        private TextView author;
        private ImageView youtubeIcon;
        private ImageView bitchuteIcon;
        private ImageView serviceIcon;
        private FloatingActionButton floatingActionButton;
        CustomViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.movieName2);
            image = view.findViewById(R.id.thumbnail2);
            author = view.findViewById(R.id.author2);
            serviceIcon = view.findViewById(R.id.videoserviceicon2);
        //    floatingActionButton = view.findViewById(R.id.floatingActionButton);
        }
    }
    public VideoAdapter(){
    }
    public VideoAdapter(List<Video> videos) {
        this.videos = videos;
    }
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //System.out.println("this is the view type dude"+ viewType);
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.smallvideolist, parent, false);
        return new CustomViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(CustomViewHolder hold, final int position) {
       // System.out.println("building video card for "+position+" video of "+videos.size());
        Video video = videos.get(position);
        final CustomViewHolder holder = hold;
        if (video.getMp4().isEmpty() && video.getUpCount().isEmpty()){
            System.out.println("getting extra info for video"+video.getTitle());
            new VideoScrape().execute(video);
        }
        comments = MainActivity.masterData.getCommentDao().getCommentsByFeedId(video.getID());
        if (comments.size()>0){
            holder.name.setText(video.getTitle()+" ("+comments.size()+")");
        }
        else {
            holder.name.setText(video.getTitle());
        }


        if (video.isBitchute()) {
            System.out.println("setting video bitchute icon");
            hold.serviceIcon.setImageResource(R.drawable.bitchuteicon2);
        }
        if (video.isYoutube()){
            System.out.println("setting video youtube icon");
           hold.serviceIcon.setImageResource(R.drawable.youtubeicon);
        }
        Picasso.get().load(video.getThumbnail()).fit().into(hold.image);
        hold.author.setText(video.getAuthor()+ " \n "+Util.getHowLongAgo(video.getDate()) );

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Video vid = videos.get(position);
               // System.out.println(vid.toDebugString());
                new VideoScrape().execute(vid);
                //System.out.println(vid.toString());
                final Dialog dialog = new Dialog(view.getContext());
                dialog.setContentView(R.layout.videoprop);
                dialog.setTitle(vid.getTitle());
                TextView textView = dialog.findViewById(R.id.channelDetails);
                Spanned spanned = HtmlCompat.fromHtml(vid.getDescription(), HtmlCompat.FROM_HTML_MODE_COMPACT);
                String description=vid.getDescription()+"<p>";
                for (Comment c : comments) {
                    description = description + c.toHtml();
                }
                textView.setText(Html.fromHtml(description));
                ImageView image = dialog.findViewById(R.id.thumbNailView);
                Picasso.get().load(vid.getThumbnail()).into(image);
                TextView title = dialog.findViewById(R.id.videoproptitle);
                title.setText(vid.getTitle());
                Button dialogButton = dialog.findViewById(R.id.closeButton);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                return false;
            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
   //             System.out.println("attempting to play video"+videos.get(position).getUrl());
                int adapterPos = holder.getAdapterPosition();
                Video vid = videos.get(position);
                Uri uri;
                int vlcRequestCode = 42;
                String path;
                Intent vlcIntent = new Intent(Intent.ACTION_VIEW);
                int switcher = 0;
                if (vid.isYoutube()){
                    switcher = MainActivity.masterData.getYoutubePlayerChoice();
                }
                if (vid.isBitchute()){
                    switcher = MainActivity.masterData.getBitchutePlayerChoice();
                }
                // 1=vlc, 2=system default, 4=webview, 8=internal player
            //    if( vid.isBitchute())switcher=8;
                FragmentManager manager = MainActivity.masterData.getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                switch(switcher){
                    case 1:
                        //no break to prevent duplication of case 2 code
                        vlcIntent.setPackage("org.videolan.vlc");
                    case 2:
                            //update for additional sources
                        if (vid.isBitchute()) {
                            path = vid.getMp4();
                        } else {
                            path = vid.getYoutubeUrl();
                        }
                        uri = Uri.parse(path);
                        vlcIntent.setDataAndTypeAndNormalize(uri, "video/*");
                        vlcIntent.putExtra("title", vid.getTitle());
                        v.getContext().startActivity(vlcIntent);
                        break;
                    case 4:
                        fragment_webviewplayer wfragment = fragment_webviewplayer.newInstance("",vid);
                        manager = MainActivity.masterData.getFragmentManager();
                        transaction = manager.beginTransaction();
                        transaction.replace(R.id.fragment, wfragment);
                        transaction.addToBackStack(null);
                        transaction.commitAllowingStateLoss();
                        break;
                    case 8:
                        fragment_videoplayer vfragment = fragment_videoplayer.newInstance("",vid);
                        manager = MainActivity.masterData.getFragmentManager();
                        transaction = manager.beginTransaction();
                        transaction.replace(R.id.fragment, vfragment);
                        transaction.addToBackStack(null);
                        transaction.commitAllowingStateLoss();
                        break;
                    case 16:
                        fragment_exoplayer efragment = fragment_exoplayer.newInstance("",vid);
                        manager = MainActivity.masterData.getFragmentManager();
                        transaction = manager.beginTransaction();
                        transaction.replace(R.id.fragment, efragment);
                        transaction.addToBackStack(null);
                        transaction.commitAllowingStateLoss();
                        break;
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return videos.size();
    }

    @Override
    public int getItemViewType(int position) {
        if ( videos.get(position).isYoutube()) {
         //   return super.getItemViewType(position);
            return 1;
        }
        else{
            return 2;
        }
    }
}
