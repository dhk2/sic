package anticlimacticteleservices.sic;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

public class HandleIntent extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("Handle-Intent","starting to play video from new intent");
        Intent i = getIntent();
        Video vid = MainActivity.masterData.getVideoDao().getvideoById(i.getLongExtra("videoID",1));
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
                MainActivity.masterData.context.startActivity(playerIntent);
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
                MainActivity.masterData.context.startActivity(playerIntent);
                break;
            case 4:
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
                MainActivity.masterData.context.startActivity(playerIntent);
                break;
            case 64:
                fragment_webviewplayer wwfragment = fragment_webviewplayer.newInstance("",vid);
                transaction = MainActivity.masterData.getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment, wwfragment);
                transaction.addToBackStack(null);
                transaction.commitAllowingStateLoss();
                break;
        }
    }
}