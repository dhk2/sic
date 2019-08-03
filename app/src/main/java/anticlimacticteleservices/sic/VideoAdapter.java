package anticlimacticteleservices.sic;

import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
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

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private ImageView image;
        private TextView author;
        private ImageView youtubeIcon;
        private ImageView bitchuteIcon;
        private ImageView serviceIcon;
        CustomViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.movieName);
            image = view.findViewById(R.id.thumbnail);
            author = view.findViewById(R.id.author);
            serviceIcon = view.findViewById(R.id.videoserviceicon);
        }
    }
    public VideoAdapter(){
    }
    public VideoAdapter(List<Video> videos) {
        this.videos = videos;
    }
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.videolist, parent, false);
        return new CustomViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(CustomViewHolder hold, final int position) {
        Video video = videos.get(position);
        final CustomViewHolder holder = hold;
        holder.name.setText(video.getTitle());

        if (video.isBitchute()) {
            System.out.println("setting video bitchute icon");
            hold.serviceIcon.setImageResource(R.drawable.bitchuteicon2);
        }
        if (video.isYoutube()){
            System.out.println("setting video youtube icon");
           hold.serviceIcon.setImageResource(R.drawable.youtubeicon);
        }
        Picasso.get().load(video.getThumbnail()).into(hold.image);
        Long diff = new Date().getTime()- video.getDate().getTime();
        int minutes = (int) ((diff / (1000*60)) % 60);
        int hours   = (int) ((diff / (1000*60*60)) % 24);
        int days = (int) ((diff / (1000*60*60*24)));
        String timehack="";
        if (minutes ==1) {
             timehack= "1 minute ago";
        }
        if (minutes>1){
             timehack = minutes + " minutes ago";
        }
        if (hours==1){
            timehack="1 hour,"+timehack;
        }
        if (hours>1){
            timehack= hours +" hours,"+timehack;
        }
        if (days==1){
            timehack="1 day,"+timehack;
        }
        if (days>1){
            timehack= days +" days,"+timehack;
        }
        hold.author.setText(video.getAuthor()+ "  "+timehack );

// load Mp4 if it's blank
        if (video.getMp4().isEmpty() && (video.isBitchute())){
  //          System.out.println("missing mp4 in bitchute, running runnable");
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Video vid=null;
                    try {
                        vid = videos.get(position);
//                        System.out.println("no mp4 so setting in onbind" + vid);
                        Document hackDoc = Jsoup.connect(vid.getBitchuteUrl()).get();
                        vid.setMp4(hackDoc.getElementsByTag("Source").first().attr("src"));

                    } catch (MalformedURLException e) {
                        System.out.println("Malformed URL: " + e.getMessage());
                    } catch (IOException e) {
                        System.out.println("I/O Error: " + e.getMessage());
                    }
                    catch (NullPointerException e){
                        System.out.println("null pointer error: " + e.getMessage());
                        System.out.println(vid);
                    }
                }
            });
            thread.start();
        }
        else
        {
  //          System.out.println("either mp4 is populated or this is youtube"+video);
        }
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Video vid = videos.get(position);
                System.out.println(vid.toString());
                final Dialog dialog = new Dialog(view.getContext());
                dialog.setContentView(R.layout.videoprop);
                dialog.setTitle(vid.getTitle());
                TextView textView = dialog.findViewById(R.id.channelDetails);

             //   WebSettings webSettings = webView.getSettings();
             //   webSettings.setJavaScriptEnabled(true);
                Spanned spanned = HtmlCompat.fromHtml(vid.getDescription(), HtmlCompat.FROM_HTML_MODE_COMPACT);
            //    webView.loadData(String.valueOf(spanned), "text/html", "UTF-8");
                //webView.loadData(disqus(""),"text/html","utf-8");
                textView.setText(Html.fromHtml(vid.getDescription()));
                System.out.println();
                /*
                if (vid.isYoutube()) {
                    webView.loadUrl(("https://dissenter.com/discussion/begin?url=" + vid.getYoutubeUrl()));
                }
                if (vid.isBitchute()) {
                    webView.loadUrl(("https://dissenter.com/discussion/begin?url=" + vid.getBitchuteUrl()));
                }
                */
                //webView.getSettings().setUseWideViewPort(true);
                //webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
               // webView.setScrollbarFadingEnabled(false);
                ImageView image = dialog.findViewById(R.id.thumbNailView);
                Picasso.get().load(vid.getThumbnail()).into(image);
                TextView title = dialog.findViewById(R.id.videoproptitle);
                title.setText(vid.getTitle());
                Button dialogButton = dialog.findViewById(R.id.closeButton);
                // if button is clicked, close the custom dialog
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
                switch(switcher){
                    case 4:
                         if (!(MainActivity.masterData.webPlayer == null)) {
                            MainActivity.masterData.webPlayer.destroy();
                         }
                        System.out.println("using webview"+MainActivity.masterData.getBitchutePlayerChoice()+" "+MainActivity.masterData.getYoutubePlayerChoice());

                        final Dialog dialog = new Dialog(v.getContext());
                        dialog.setContentView(R.layout.video_player);
                        dialog.setTitle(vid.getTitle());
                        final WebView webView = dialog.findViewById(R.id.player_window);
                        webView.setWebViewClient(new WebViewClient());
                        WebSettings webSettings = webView.getSettings();
                        webSettings.setJavaScriptEnabled(true);
                        webView.loadUrl(vid.getEmbeddedUrl());
                        MainActivity.masterData.webPlayer=webView;
                        Button dialogButton = dialog.findViewById(R.id.closebutton);
                        dialogButton.setText("close");
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                webView.destroy();
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                        break;
                    case 1:
                        //no break to prevent duplication of case 2 code
                        vlcIntent.setPackage("org.videolan.vlc");
                    case 2:
                        //update for additional sources
                    if (!vid.getMp4().isEmpty()) {
                        path = vid.getMp4();
                    } else {
                        path = vid.getYoutubeUrl();
                    }
                    uri = Uri.parse(path);
                    vlcIntent.setDataAndTypeAndNormalize(uri, "video/*");
                    vlcIntent.putExtra("title", vid.getTitle());
                    v.getContext().startActivity(vlcIntent);
                    break;
                    case 8:
                        fragment_videoplayer fragment = fragment_videoplayer.newInstance("",vid);
                        FragmentManager manager = MainActivity.masterData.getFragmentManager();
                        FragmentTransaction transaction = manager.beginTransaction();
                        transaction.replace(R.id.fragment, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                }
            }
        });

    }
    @Override
    public int getItemCount() {
        return videos.size();
    }
    public String getHtmlComment(String idPost, String shortName) {

        return "<div id='disqus_thread'></div>"
                + "<script type='text/javascript'>"
                + "var disqus_identifier = '"
                + "/video/owmm3OCx38XQ"
                + "';"
                + "var disqus_shortname = '"
                + "www-bitchute-com"
                + "';"
                + " (function() { var dsq = document.createElement('script'); dsq.type = 'text/javascript'; dsq.async = true;"
                + "dsq.src = 'http://' + disqus_shortname + '.disqus.com/embed.js';"
                + "(document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(dsq); })();"
                + "</script>";
    }
}
