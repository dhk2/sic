package anticlimacticteleservices.sic;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


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
        Button dialogButton = v.findViewById(R.id.closeButton);
        Button playVlc= v.findViewById(R.id.properties_play_vlc);
        Button playExo=v.findViewById(R.id.properties_play_exo);
        Button playWebTorrent= v.findViewById(R.id.properties_play_webtorrent);
        Button playEmbedded = v.findViewById(R.id.properties_play_embedded);
        Button playSystem = v.findViewById(R.id.properties_play_default);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.masterData.fragmentManager.popBackStack();
            }
        });
        playVlc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri;
                int vlcRequestCode = 42;
                String path ="";
                Intent playerIntent = new Intent(Intent.ACTION_VIEW);
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
        playEmbedded.setOnClickListener(new View.OnClickListener() {
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
        playSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri;
                String path ="";
                Intent playerIntent = new Intent(Intent.ACTION_VIEW);
                if (vid.isBitchute()) {
                    path = vid.getMp4();
                } else {
                    path = vid.getYoutubeUrl();
                }
                uri = Uri.parse(path);
                playerIntent.setData(uri);
                playerIntent.putExtra("title", vid.getTitle());
                v.getContext().startActivity(playerIntent);
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
