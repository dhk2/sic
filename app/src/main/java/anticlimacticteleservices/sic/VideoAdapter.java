package anticlimacticteleservices.sic;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        private TextView videoViewCount;
        private FloatingActionButton floatingActionButton;

        private TextView viewCount;
        CustomViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.movieName2);
            image = view.findViewById(R.id.thumbnail2);
            author = view.findViewById(R.id.author2);
            serviceIcon = view.findViewById(R.id.videoserviceicon2);
            videoViewCount = view.findViewById(R.id.videoviewcount);
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
        String iconString ="";
        if (!"".equals(video.getViewCount())) {
            iconString= "\uD83D\uDC41" + video.getViewCount();
        }
        if (!"".equals(video.getUpCount())){
            iconString=iconString+"\uD83D\uDC4D"+video.getUpCount();
        }
        if (!"".equals(video.getDownCount())){
            iconString=iconString+"\uD83D\uDC4E"+video.getDownCount();
        }
        holder.videoViewCount.setText(iconString);
        if (video.isBitchute()) {
            hold.serviceIcon.setImageResource(R.drawable.bitchuteicon2);
        }
        if (video.isYoutube()){
           hold.serviceIcon.setImageResource(R.drawable.youtubeicon);
        }
        if (video.isBitchute() && video.isYoutube()){
            hold.serviceIcon.setImageResource(R.drawable.ic_home_black_24dp);
        }
        if (video.getThumbnailurl().isEmpty()){
            hold.image.setImageResource(R.drawable.bitchuteicon2 );
        }
        else{
            Picasso.get().load(video.getThumbnail()).fit().into(hold.image);
        }
        if (null == video.getAuthor()){
            video.setAuthor(video.getTitle());
            if (null == video.getAuthor()){
                video.setAuthor(("Author Unknown"));
            }
        }
        String howLongAgo="Forever";
        if (video.getHackDateString().isEmpty()) {
            howLongAgo = Util.getHowLongAgo(video.getDate());
        }
        else {
            howLongAgo=video.getHackDateString();
        }
            hold.author.setText(video.getAuthor()+ " \n "+howLongAgo);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Video video  =videos.get(position);
                    fragment_video_properties vpfragment = fragment_video_properties.newInstance(video,"");
                    FragmentTransaction transaction = MainActivity.masterData.getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment, vpfragment);
                    transaction.addToBackStack(null);
                    transaction.commitAllowingStateLoss();
                    return false;
                }
            });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Video vid = videos.get(position);
                Log.v("Videoadapter","Attempting to play video at "+vid.getUrl());

                //Clear stored settings and save current position for actively playing EXO video
                if ((null != MainActivity.masterData.getPlayer() ) && (vid.getID() != MainActivity.masterData.getPlayerVideoID())) {
                    MainActivity.masterData.getPlayer().stop();
                    Long spot = MainActivity.masterData.getPlayer().getCurrentPosition();
                    Video tempVideo = MainActivity.masterData.getVideoDao().getvideoById(MainActivity.masterData.getPlayerVideoID());
                    if (null != tempVideo){
                        tempVideo.setCurrentPosition(spot);
                        MainActivity.masterData.getVideoDao().update(tempVideo);
                    }
                    MainActivity.masterData.getPlayer().release();
                    MainActivity.masterData.setPlayer(null);
                    MainActivity.masterData.setPlayerVideoID(0l);
                }
                vid.setWatched(true);
                MainActivity.masterData.getVideoDao().update(vid);
                int adapterPos = holder.getAdapterPosition();
                Uri uri;
                int vlcRequestCode = 42;
                String path ="";
                Intent playerIntent = new Intent(Intent.ACTION_VIEW);
                int switcher = 0;
                if (vid.isYoutube()){
                    switcher = MainActivity.masterData.getYoutubePlayerChoice();
                }
                if (vid.isBitchute()){
                    switcher = MainActivity.masterData.getBitchutePlayerChoice();
                }
                Log.v("videoadapter","switcher set to "+switcher);
                // 1=vlc, 2=system default, 4=webview, 8=internal player
            //    if( vid.isBitchute())switcher=8;
                FragmentTransaction transaction; //= MainActivity.masterData.getFragmentManager().beginTransaction();

                switch(switcher){
                    case 1:
                        playerIntent.setPackage("org.videolan.vlc");
                        if (vid.isBitchute()) {
                            path = vid.getMp4();
                        }
                        if (vid.isYoutube()){
                            path = vid.getYoutubeUrl();
                        }
                        uri = Uri.parse(path);
                        playerIntent.setDataAndTypeAndNormalize(uri, "video/*");
                        playerIntent.putExtra("title", vid.getTitle());
                        v.getContext().startActivity(playerIntent);
                        break;
                    case 2:
                        if (vid.isBitchute()) {
                            path = vid.getMp4();
                        } else {
                            path = vid.getYoutubeUrl();
                        }
                        uri = Uri.parse(path);
                        playerIntent.setData(uri);
                        playerIntent.putExtra("title", vid.getTitle());
                        v.getContext().startActivity(playerIntent);
                        break;
                    case 4:
                        MainActivity.masterData.setWebViewOption(0);
                        fragment_webviewplayer wfragment = fragment_webviewplayer.newInstance("",vid);
                        transaction = MainActivity.masterData.getFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment, wfragment);
                        transaction.addToBackStack(null);

                        transaction.commitAllowingStateLoss();
                        break;
                    case 8:
                        fragment_videoplayer vfragment = fragment_videoplayer.newInstance("",vid);
                        transaction = MainActivity.masterData.getFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment, vfragment);
                        transaction.addToBackStack(null);
                        transaction.commitAllowingStateLoss();
                        break;
                    case 16:
                        fragment_exoplayer efragment = fragment_exoplayer.newInstance("",vid);
                        transaction = MainActivity.masterData.getFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment, efragment);
                        transaction.addToBackStack(null);
                        transaction.commitAllowingStateLoss();
                        break;
                    case 32:
                        playerIntent.setPackage("org.schabi.newpipe");
                        if (vid.isBitchute()) {
                            path = vid.getMp4();
                        }
                        if (vid.isYoutube()){
                            path = vid.getYoutubeUrl();
                        }
                        uri = Uri.parse(path);
                        //playerIntent.setDataAndTypeAndNormalize(uri, "video/*");
                        playerIntent.setData(uri);
                       // playerIntent.putExtra("title", vid.getTitle());
                        v.getContext().startActivity(playerIntent);
                        break;
                    case 64:
                        MainActivity.masterData.setWebViewOption(1);
                        fragment_webviewplayer wwfragment = fragment_webviewplayer.newInstance("",vid);
                        transaction = MainActivity.masterData.getFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment, wwfragment);
                        transaction.addToBackStack(null);
                        transaction.commitAllowingStateLoss();
                        break;
                    case 128:
                        // actual kodi doesn't seem to launchable with intent, will see about integrating library with feed later.
                        playerIntent.setPackage( "org.xbmc.kodi" );
                        if (vid.isBitchute()) {
                            path = vid.getMp4();
                        }
                        if (vid.isYoutube()){
                            path ="http"+vid.getYoutubeUrl().substring((vid.getYoutubeUrl()).indexOf(":"));
                        }
                        uri = Uri.parse(path);
                        Log.v("Video-Update","Path for intent:"+path);
                        playerIntent.setData(uri);

                        v.getContext().startActivity(playerIntent);
                        break;
                    case 256:
                        playerIntent.setPackage( "com.google.android.youtube" );
                        if (vid.isYoutube()){
                            path = vid.getYoutubeUrl();
                        }
                        uri = Uri.parse(path);
                        playerIntent.setData(uri);
                        v.getContext().startActivity(playerIntent);
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
