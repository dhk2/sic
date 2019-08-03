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
import android.widget.MediaController;
import android.widget.VideoView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link fragment_videoplayer.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link fragment_videoplayer#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_videoplayer extends Fragment  {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PassedUrl = "URL";
    private static final String PassedVideo = "VIDEO";
    private static Uri uri;
    // TODO: Rename and change types of parameters
    private String url;
    private Video video;

    private OnFragmentInteractionListener mListener;

    public fragment_videoplayer() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_videoplayer.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_videoplayer newInstance(String param1, Video param2) {
        fragment_videoplayer fragment = new fragment_videoplayer();
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
                url=video.getMp4();
            }

        }
        System.out.println(url);
        System.out.println(video);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_videoplayer, container, false);
        WebView comments=v.findViewById(R.id.comments);
        WebSettings webSettings = comments.getSettings();
        webSettings.setJavaScriptEnabled(true);
        comments.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                view.loadUrl("javascript:document.getElementById('mb-2').style.display = 'none'; void(0);");
                view.loadUrl("javascript:document.getElementById('hydra-top-bar').style.display = 'none'; void(0);");
            }

        });

        comments.loadUrl("https://dissenter.com/discussion/begin?url="+ video.getBitchuteUrl());
        VideoView simpleVideoView = (VideoView) v.findViewById(R.id.videoview); // initiate a video view
        uri = Uri.parse(url);
        System.out.println(uri);
        System.out.println(simpleVideoView.getId());
        simpleVideoView.setVideoURI(uri);
        MediaController mediaController = new
                MediaController(v.getContext());
        mediaController.setAnchorView(simpleVideoView);
        simpleVideoView.setMediaController(mediaController);

        simpleVideoView.start();
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
