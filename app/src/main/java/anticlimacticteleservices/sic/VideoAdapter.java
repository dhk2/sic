package anticlimacticteleservices.sic;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import static android.app.PendingIntent.getActivity;
//import static android.support.v4.content.ContextCompat.startActivity;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.CustomViewHolder> {
   private List<Video> videos = new ArrayList<>();

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private ImageView image;
        private TextView author;
        private ImageView youtubeIcon;
        private ImageView bitchuteIcon;

        public CustomViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.movieName);
            image = view.findViewById(R.id.thumbnail);
            author = view.findViewById(R.id.author);
            youtubeIcon = view.findViewById(R.id.yahooIcon);
            bitchuteIcon = view.findViewById(R.id.bitchuteIcon);
        }
    }
    public VideoAdapter(){
        //used to avoid null error

        //videos.add(new Video("https://www.youtube.com/watch?v=2ips2mM7Zqw"));

    }
    public VideoAdapter(List<Video> videos) {
        this.videos = videos;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.videolist, null, false);

        return new CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder hold, final int position) {
        Video video = videos.get(position);
        final CustomViewHolder holder = hold;
        holder.name.setText(video.getTitle());
        holder.author.setText(video.getAuthor());
        if (video.getUrl().indexOf("bitchute.com") > 0) {
            holder.bitchuteIcon.setVisibility(View.VISIBLE);
            holder.youtubeIcon.setVisibility(View.INVISIBLE);
        } else {
            holder.youtubeIcon.setVisibility(View.VISIBLE);
            holder.bitchuteIcon.setVisibility(View.INVISIBLE);
        }
        Picasso.get().load(video.getThumbnail()).into(holder.image);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Video vid = videos.get(position);
                System.out.println(vid.toString());
                final Dialog dialog = new Dialog(view.getContext());
                dialog.setContentView(R.layout.videoprop);
                dialog.setTitle(vid.getTitle());

                // set the custom dialog components - text, image and button
                WebView webView = (WebView) dialog.findViewById(R.id.channelDetails);

                webView.loadData(vid.toString(), "text/html", "UTF-8");
                ImageView image = (ImageView) dialog.findViewById(R.id.thumbNailView);
                Picasso.get().load(vid.getThumbnail()).into(image);
                Button dialogButton = (Button) dialog.findViewById(R.id.closeButton);
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
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int adapterPos = holder.getAdapterPosition();

                        Video vid = videos.get(position);
                        Uri uri = null;
                        int vlcRequestCode = 42;
                        if (vid.getUrl().indexOf("bitchute")>0) {
                            if (vid.getMp4().isEmpty()) {
                                try {
                                    Document hackDoc = Jsoup.connect(vid.getUrl()).get();
                                    vid.setMp4(hackDoc.getElementsByTag("Source").first().attr("src"));
                                    uri = Uri.parse(vid.getMp4());
                                } catch (MalformedURLException e) {
                                    System.out.println("Malformed URL: " + e.getMessage());
                                } catch (IOException e) {
                                    System.out.println("I/O Error: " + e.getMessage());
                                }
                            }
                            else {
                                uri = Uri.parse(vid.getMp4());
                            }
                        }
                        else
                        {
                            uri = Uri.parse(vid.getUrl());
                        }

                        Intent vlcIntent = new Intent(Intent.ACTION_VIEW);
                        if (MainActivity.masterData.isUseVlc()) {
                            vlcIntent.setPackage("org.videolan.vlc");
                        }
                        vlcIntent.setDataAndTypeAndNormalize(uri, "video/*");
                        vlcIntent.putExtra("title", vid.getTitle());
                        v.getContext().startActivity(vlcIntent);
                        //System.out.println(vlcIntent.toString());

                    }
                } );
                thread.start();
            }
        });
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

}
