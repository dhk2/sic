package anticlimacticteleservices.sic;

import android.Manifest;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.text.HtmlCompat;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static android.content.Context.DOWNLOAD_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link fragment_video_properties.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link fragment_video_properties#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_video_properties extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PassedVideo = "video";
    private static final String ARG_PARAM2 = "param2";
    private static final int PERMISSION_REQUEST_CODE = 1;
    // TODO: Rename and change types of parameters
    private Video mVideo;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public fragment_video_properties() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_video_properties.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_video_properties newInstance(Video param1, String param2) {
        fragment_video_properties fragment = new fragment_video_properties();
        Bundle args = new Bundle();
        args.putSerializable(PassedVideo, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mVideo = (Video)getArguments().getSerializable(PassedVideo);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_video_properties, container, false);
        Video vid = mVideo;
        new VideoScrape().execute(vid);
        List<Comment> comments = new ArrayList<>();
        comments = MainActivity.masterData.getCommentDao().getCommentsByFeedId(vid.getID());


        Spanned spanned = HtmlCompat.fromHtml(vid.getDescription(), HtmlCompat.FROM_HTML_MODE_COMPACT);
        String description=vid.getDescription()+"<p>";
        description = description + vid.toHtmlString();
        if (comments.size()>0 && MainActivity.masterData.isUseComments()){
            description=description+"<p><h2>Comments:</h2><p>";
            for (Comment c : comments) {
                if (MainActivity.masterData.isDissenterComments()) {
                    description = description + c.toHtml();
                }
                if (MainActivity.masterData.isKittenComments()){
                    description = description + "<img src=\"https://cataas.com/cat?"+ Integer.toString(ThreadLocalRandom.current().nextInt(1, 1001)) +"\" width=\"240\" ><p>";

                }
            }
        }
        System.out.println(description);
        WebView descriptionWebView = null;
        descriptionWebView=v.findViewById(R.id.videopropertieswebview);
        WebViewClient webViewClient= new WebViewClient();
        descriptionWebView.setWebViewClient(webViewClient);
        descriptionWebView.loadData(description,"text/html","utf-8");
        ImageView image = v.findViewById(R.id.thumbNailView);
        Picasso.get().load(vid.getThumbnail()).into(image);
        TextView title = v.findViewById(R.id.video_name);
        title.setText(vid.getTitle());
        Button closeButton = v.findViewById(R.id.closeButton);
        Button channelButton = v.findViewById(R.id.gotochannel);
        Button playBitchuteVlc= v.findViewById(R.id.properties_play_bc_vlc);
        Button playYoutubeVlc= v.findViewById(R.id.properties_play_yt_vlc);
        Button playExo=v.findViewById(R.id.properties_play_exo);
        Button playWebTorrent= v.findViewById(R.id.properties_play_webtorrent);
        Button playYoutubeEmbedded = v.findViewById(R.id.properties_play_yt_embedded);
        Button playBitchuteEmbedded = v.findViewById(R.id.properties_play_bc_embedded);
        Button playBitchuteSystem = v.findViewById(R.id.properties_play_bc_default);
        Button playYoutubeSystem = v.findViewById(R.id.properties_play_yt_default);
        Button playVlcLocal = v.findViewById(R.id.properties_play_local_vlc);
        Button playExoLocal = v.findViewById(R.id.properties_play_local_exo);
        Button playLocalSystem=v.findViewById(R.id.properties_play_local_default);
        Button playYoutube = v.findViewById(R.id.properties_play_youtube);
        Button playNewpipe = v.findViewById(R.id.properties_play_newpipe);
        Button playBitchuteChrome = v.findViewById(R.id.properties_play_bc_chrome);
        Button playYoutubeChrome = v.findViewById(R.id.properties_play_yt_chrome);
        Button download = v.findViewById(R.id.properties_download);
        System.out.println(MainActivity.masterData.youtubeInstalled);
        System.out.println((MainActivity.masterData.chromeInstalled));
        System.out.println((MainActivity.masterData.newpipeInstalled));
        // turn off bitchute options if not bitchute
        if (!vid.isBitchute()){
            playExo.setVisibility(View.GONE);
            playWebTorrent.setVisibility(View.GONE);
            playBitchuteChrome.setVisibility(View.GONE);
            playBitchuteEmbedded.setVisibility(View.GONE);
            playBitchuteSystem.setVisibility((View.GONE));
            playBitchuteVlc.setVisibility((View.GONE));
        }
        if (!vid.isYoutube()){
            playNewpipe.setVisibility(View.GONE);
            playYoutube.setVisibility(View.GONE);
            playYoutubeChrome.setVisibility(View.GONE);
            playYoutubeEmbedded.setVisibility(View.GONE);
            playYoutubeSystem.setVisibility((View.GONE));
            playYoutubeVlc.setVisibility((View.GONE));
        }
        if (null == vid.getLocalPath()){
            playLocalSystem.setVisibility(View.GONE);
            playExoLocal.setVisibility(View.GONE);
            playVlcLocal.setVisibility(View.GONE);
        }
        else{
            playExo.setVisibility(View.GONE);
        }
        if (vid.getMp4().equals("")){
            playExo.setVisibility(View.GONE);
            download.setVisibility(View.GONE);
            playBitchuteSystem.setVisibility((View.GONE));
            playBitchuteVlc.setVisibility((View.GONE));

        }
        if (vid.getMagnet().equals("")){
            playWebTorrent.setVisibility(View.GONE);
        }
        if (!MainActivity.masterData.vlcInstalled){
            playYoutubeVlc.setVisibility(View.GONE);
            playBitchuteVlc.setVisibility(View.GONE);
            playVlcLocal.setVisibility(View.GONE);
        }
        if (!MainActivity.masterData.newpipeInstalled){
            playNewpipe.setVisibility(View.GONE);
        }
        if (!MainActivity.masterData.chromeInstalled){
            playYoutubeChrome.setVisibility(View.GONE);
            playBitchuteChrome.setVisibility(View.GONE);
        }
        if (!MainActivity.masterData.youtubeInstalled){
            playYoutube.setVisibility(View.GONE);
        }

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.masterData.fragmentManager.popBackStack();
            }
        });
        channelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vid.getAuthorID()>0) {
                    Channel chan = MainActivity.masterData.getChannelDao().getChannelById(vid.getAuthorID());
                    fragment_channel_properties cpfragment = fragment_channel_properties.newInstance(chan, "");
                    FragmentTransaction transaction = MainActivity.masterData.getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment, cpfragment);
                    transaction.addToBackStack(null);
                    transaction.commitAllowingStateLoss();
                }
            }
        });
        playYoutubeVlc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri;
                int vlcRequestCode = 42;
                String path ="";
                Intent playerIntent = new Intent(Intent.ACTION_VIEW);
                playerIntent.setPackage("org.videolan.vlc");
                path = vid.getYoutubeUrl();
                uri = Uri.parse(path);
                playerIntent.setDataAndTypeAndNormalize(uri, "video/*");
                playerIntent.putExtra("title", vid.getTitle());
                v.getContext().startActivity(playerIntent);
            }
        });

        playBitchuteVlc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri;
                int vlcRequestCode = 42;
                String path ="";
                Intent playerIntent = new Intent(Intent.ACTION_VIEW);
                playerIntent.setPackage("org.videolan.vlc");
                path = vid.getMp4();
                uri= Uri.parse(path);
                System.out.println(path);
                playerIntent.setDataAndTypeAndNormalize(uri, "video/*");
                playerIntent.putExtra("title", vid.getTitle());
                v.getContext().startActivity(playerIntent);
            }
        });

        playVlcLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.masterData.useComments && MainActivity.masterData.isDissenterComments()){
                    Util.writeSubtitles(MainActivity.masterData.context, vid);
                }
                Uri uri;
                int vlcRequestCode = 42;
                String path ="";
                Intent playerIntent = new Intent(Intent.ACTION_VIEW);
                playerIntent.setPackage("org.videolan.vlc");
                path = vid.getLocalPath();
                uri= Uri.parse(path);
                System.out.println(path);
                playerIntent.setDataAndTypeAndNormalize(uri, "video/*");
                playerIntent.putExtra("title", vid.getTitle());
                v.getContext().startActivity(playerIntent);
            }
        });

        playNewpipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri;
                String path ="";
                Intent playerIntent = new Intent(Intent.ACTION_VIEW);
                playerIntent.setPackage("org.schabi.newpipe");
                path = vid.getYoutubeUrl();
                uri = Uri.parse(path);
                playerIntent.setData(uri);
               // playerIntent.setDataAndTypeAndNormalize(uri, "video/*");
               // playerIntent.putExtra("title", vid.getTitle());
                v.getContext().startActivity(playerIntent);
            }
        });
        playYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri;
                String path ="";
                Intent playerIntent = new Intent(Intent.ACTION_VIEW);
                playerIntent.setPackage( "com.google.android.youtube" );
                path = vid.getYoutubeUrl();
                uri = Uri.parse(path);
                playerIntent.setData(uri);
                v.getContext().startActivity(playerIntent);
            }
        });
        playExo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction;
                fragment_exoplayer efragment = fragment_exoplayer.newInstance("",vid);
                transaction = MainActivity.masterData.getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment, efragment);
                transaction.addToBackStack(null);
                transaction.commitAllowingStateLoss();
            }
        });

        playExoLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction;
                fragment_exoplayer efragment = fragment_exoplayer.newInstance("",vid);
                transaction = MainActivity.masterData.getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment, efragment);
                transaction.addToBackStack(null);
                transaction.commitAllowingStateLoss();
            }
        });

        playBitchuteEmbedded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.masterData.setWebViewOption(0);
                FragmentTransaction transaction;
                fragment_webviewplayer wwfragment = fragment_webviewplayer.newInstance("",vid);
                transaction = MainActivity.masterData.getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment, wwfragment);
                transaction.addToBackStack(null);
                transaction.commitAllowingStateLoss();
            }
        });
        playYoutubeEmbedded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.masterData.setWebViewOption(0);
                FragmentTransaction transaction;
                fragment_webviewplayer wwfragment = fragment_webviewplayer.newInstance("",vid);
                transaction = MainActivity.masterData.getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment, wwfragment);
                transaction.addToBackStack(null);
                transaction.commitAllowingStateLoss();
            }
        });
        playWebTorrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.masterData.setWebViewOption(1);
                FragmentTransaction transaction;
                fragment_webviewplayer wwfragment = fragment_webviewplayer.newInstance("",vid);
                transaction = MainActivity.masterData.getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment, wwfragment);
                transaction.addToBackStack(null);
                transaction.commitAllowingStateLoss();
            }
        });
        playYoutubeSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri;
                String path ="";
                Intent playerIntent = new Intent(Intent.ACTION_VIEW);
                path = vid.getYoutubeUrl();
                uri = Uri.parse(path);
                playerIntent.setData(uri);
                playerIntent.putExtra("title", vid.getTitle());
                v.getContext().startActivity(playerIntent);
            }
        });
        playBitchuteSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri;
                String path ="";
                Intent playerIntent = new Intent(Intent.ACTION_VIEW);
                path = vid.getMp4();
                uri = Uri.parse(path);
                playerIntent.setData(uri);
                playerIntent.putExtra("title", vid.getTitle());
                v.getContext().startActivity(playerIntent);
            }
        });

        playLocalSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri;
                String path ="";
                File file=new File(Environment.DIRECTORY_DOWNLOADS,vid.getSourceID()+".mp4");
                Intent playerIntent = new Intent(Intent.ACTION_VIEW);
                path = vid.getLocalPath();
                uri = Uri.fromFile(file);
                playerIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                playerIntent.setData(uri);
                playerIntent.putExtra("title", vid.getTitle());
                playerIntent.setDataAndTypeAndNormalize(uri, "video/*");
                v.getContext().startActivity(playerIntent);
            }
        });

        playYoutubeChrome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri;
                String path ="";
                Intent playerIntent = new Intent(Intent.ACTION_VIEW);
                path = vid.getYoutubeEmbeddedUrl();
                uri = Uri.parse(path);
                playerIntent.setPackage( "com.android.chrome" );
                playerIntent.setData(uri);
                playerIntent.putExtra("title", vid.getTitle());
                v.getContext().startActivity(playerIntent);
            }
        });
        playBitchuteChrome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri;
                String path ="";
                Intent playerIntent = new Intent(Intent.ACTION_VIEW);
                path = vid.getBitchuteEmbeddedUrl();
                uri = Uri.parse(path);
                playerIntent.setPackage( "com.android.chrome" );
                playerIntent.setData(uri);
                playerIntent.putExtra("title", vid.getTitle());
                v.getContext().startActivity(playerIntent);
            }
        });
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.masterData.context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE );
                }
                else {
                    Uri target = Uri.parse(vid.getMp4());
                    File fpath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    vid.setLocalPath(fpath.getAbsolutePath() + "/" + vid.getSourceID() + ".mp4");
                    MainActivity.masterData.updateVideo(vid);
                    System.out.println(vid.getLocalPath());
                    System.out.println(requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS));

                    System.out.println(fpath.getAbsolutePath());
                    DownloadManager downloadManager = (DownloadManager) MainActivity.masterData.context.getApplicationContext().getSystemService(DOWNLOAD_SERVICE);
                    DownloadManager.Request request = new DownloadManager.Request(target);
                    request.allowScanningByMediaScanner();
                    //request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                    //request.setAllowedOverRoaming(false);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setTitle(vid.getAuthor());
                    request.setDescription(vid.getTitle());
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, vid.getSourceID() + ".mp4");
                    request.setVisibleInDownloadsUi(true);
                    MainActivity.masterData.downloadVideoID = vid.getID();
                    MainActivity.masterData.downloadSourceID = vid.getSourceID();
                    MainActivity.masterData.downloadID = downloadManager.enqueue(request);
                }

            }
        });

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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
