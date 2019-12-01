package anticlimacticteleservices.sic;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;


public class fragment_exoplayer extends Fragment {

    private static final String PassedUrl = "URL";
    private static final String PassedVideo = "VIDEO";
    private static Uri uri;
    private String url;
    private Video video;
    private ArrayList<Comment> allComments;
    private OnFragmentInteractionListener mListener;
    SimpleExoPlayer player;
    public fragment_exoplayer() { }

    public static fragment_exoplayer newInstance(String param1, Video param2) {
        fragment_exoplayer fragment = new fragment_exoplayer();
        Bundle args = new Bundle();
        args.putString(PassedUrl, param1);
        args.putSerializable(PassedVideo, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            url = getArguments().getString(PassedUrl);
            video = (Video) getArguments().getSerializable(PassedVideo);
            if (null == video) {
                video = new Video(url);
            }
            if ((null == url) || (url.isEmpty())) {
                url = video.getEmbeddedUrl();
            }

        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("starting to create view");
        // figure out if landscape or portait to determine how to arrange video
        int orientation = getResources().getConfiguration().orientation;
        View v=null;
        PlayerView playerView;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            v = inflater.inflate(R.layout.fragment_exo_full_screen, container, false);
            playerView = v.findViewById(R.id.exofullscreenplayer);
        } else {
            v = inflater.inflate(R.layout.fragment_exoplayer, container, false);
            WebView comments=v.findViewById(R.id.exoviewcomments);
            String description=video.getDescription();
            playerView = v.findViewById(R.id.videoFullScreenPlayer);
            if (MainActivity.masterData.isUseComments()) {
                allComments = (ArrayList<Comment>) MainActivity.masterData.getCommentDao().getCommentsByFeedId(video.getID());
                if (!(allComments.isEmpty())) {
                    description = description + "<p><p><h2>comments</h2><p>";
                    for (Comment c : allComments) {
                        if (MainActivity.masterData.isDissenterComments()) {
                            description = description + c.toHtml();
                        }
                        if (MainActivity.masterData.isKittenComments()){
                            description = description + "<img src=\"https://cataas.com/cat?"+ ThreadLocalRandom.current().nextInt(1, 1001) +"\" width=\"240\" ><p>";

                        }
                    }
                }
            }
            comments.loadData(description,"text/html","utf-8");
        }


        if (null == video.getLocalPath()) {
            uri = uri.parse(video.getMp4());
        }
        else {
            uri = uri.parse(video.getLocalPath());
        }
        System.out.println(uri.getPath());
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getContext(),"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100 Safari/537.36");
        MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
        if (null != MainActivity.masterData.getPlayer()){
            System.out.println("existing player instance for video "+MainActivity.masterData.getPlayerVideoID());
            if (MainActivity.masterData.getPlayerVideoID()==video.getID()) {
                player = MainActivity.masterData.getPlayer();
            }
            else {
                MainActivity.masterData.getPlayer().stop();
                Long spot = MainActivity.masterData.getPlayer().getCurrentPosition();
                Video tempVideo=MainActivity.masterData.getVideoDao().getvideoById(MainActivity.masterData.getPlayerVideoID());
                tempVideo.setCurrentPosition(spot);
                MainActivity.masterData.getVideoDao().update(tempVideo);
                MainActivity.masterData.getPlayer().release();
                MainActivity.masterData.setPlayer(null);
                MainActivity.masterData.setPlayerVideoID(0l);
            }
        }
        if (null == MainActivity.masterData.getPlayer()) {
            player = ExoPlayerFactory.newSimpleInstance(
                    new DefaultRenderersFactory(this.getContext()),
                    new DefaultTrackSelector(), new DefaultLoadControl());
            player.prepare(mediaSource);
            if (video.getCurrentPosition()>1) {
                player.seekTo(video.getCurrentPosition());
            }
        }
        playerView.setPlayer(player);


        player.setPlayWhenReady(true);
        MainActivity.masterData.setPlayer(player);
        MainActivity.masterData.setPlayerVideoID(video.getID());

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != MainActivity.masterData.getPlayer()){

            Long spot = MainActivity.masterData.getPlayer().getCurrentPosition();
            Video tempVideo=MainActivity.masterData.getVideoDao().getvideoById(MainActivity.masterData.getPlayerVideoID());
            if (null != tempVideo){
                tempVideo.setCurrentPosition(spot);
                MainActivity.masterData.getVideoDao().update(tempVideo);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (null != MainActivity.masterData.getPlayer()){

            Long spot = MainActivity.masterData.getPlayer().getCurrentPosition();
            Video tempVideo=MainActivity.masterData.getVideoDao().getvideoById(MainActivity.masterData.getPlayerVideoID());
            if (null != tempVideo){
                tempVideo.setCurrentPosition(spot);
                MainActivity.masterData.getVideoDao().update(tempVideo);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != MainActivity.masterData.getPlayer()){

            Long spot = MainActivity.masterData.getPlayer().getCurrentPosition();
            Video tempVideo=MainActivity.masterData.getVideoDao().getvideoById(MainActivity.masterData.getPlayerVideoID());
            if (null != tempVideo){
                tempVideo.setCurrentPosition(spot);
             MainActivity.masterData.getVideoDao().update(tempVideo);
            }
        }
    }
}
