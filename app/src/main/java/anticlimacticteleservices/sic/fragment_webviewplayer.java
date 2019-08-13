package anticlimacticteleservices.sic;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import java.util.ArrayList;


public class fragment_webviewplayer extends Fragment  {
    private static final String PassedUrl = "URL";
    private static final String PassedVideo = "VIDEO";
    private static Uri uri;
    private String url;
    private Video video;
    private ArrayList<Comment> allComments;
    private OnFragmentInteractionListener mListener;

    public fragment_webviewplayer() {
    }
    public static fragment_webviewplayer newInstance(String param1, Video param2) {
        fragment_webviewplayer fragment = new fragment_webviewplayer();
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
            if (null == video){
                video=new Video(url);
            }
            if ((null == url) || (url.isEmpty())){
                url=video.getEmbeddedUrl();
            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_webviewplayer, container, false);
        WebView comments=v.findViewById(R.id.webviewcomments);
        String description=video.getDescription();
        allComments = (ArrayList<Comment>)MainActivity.masterData.getCommentDao().getCommentsByFeedId(video.getID());
        if (!(allComments.isEmpty())) {
            description =description+"<p><p><h2>comments</h2><p>";
            for (Comment c : allComments) {
                description = description + c.toHtml();
            }
        }
        System.out.println(description);
        comments.loadData(description,"text/html","utf-8");
        //TODO actually get rotation to return to existing webview
       if (!(null == MainActivity.masterData.webPlayer)){
            MainActivity.masterData.webPlayer.destroy();
        }
        WebView webView = v.findViewById(R.id.webviewplayer);

        WebViewClient webViewClient= new WebViewClient();
        MainActivity.masterData.webViewClient=webViewClient;
        webView.setWebViewClient(webViewClient);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl(video.getEmbeddedUrl());
        MainActivity.masterData.webPlayer = webView;
        MainActivity.masterData.webPlayerVideo=video;

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
}
