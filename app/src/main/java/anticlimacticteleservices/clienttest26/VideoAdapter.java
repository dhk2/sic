package anticlimacticteleservices.clienttest26;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static android.app.PendingIntent.getActivity;
import static android.provider.AlarmClock.EXTRA_MESSAGE;
import static android.support.v4.content.ContextCompat.startActivity;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.CustomViewHolder> {
    private List<Video> videos;

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView image;
        public TextView author;
//        final Context context = this;

        public CustomViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.movieName);
            image = view.findViewById(R.id.thumbnail);
            author = view.findViewById(R.id.author);
        }
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
        holder.author.setText(video.getAuthor());
//        System.out.println(position+ " what the hell:"+video.getThumbnail());
//       System.out.println(video);

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
                TextView text = (TextView) dialog.findViewById(R.id.videoDetails);
                text.setText(vid.toString());
                ImageView image = (ImageView) dialog.findViewById(R.id.thumbNailView);
                Picasso.get().load(vid.getThumbnail()).into(image);
                Button dialogButton = (Button) dialog.findViewById(R.id.closebutton);
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
                        Uri uri;
                        System.out.println("starting to do my shit");
//                        System.out.println(vid);
                        int vlcRequestCode = 42;
                        if (vid.getMp4().isEmpty()) {
                            uri = Uri.parse(vid.getUrl());
                        }
                        else {
                            uri = Uri.parse(vid.getMp4());
                        }
//                        System.out.println(uri);
                        Intent vlcIntent = new Intent(Intent.ACTION_VIEW);
                            vlcIntent.setPackage("org.videolan.vlc");
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
