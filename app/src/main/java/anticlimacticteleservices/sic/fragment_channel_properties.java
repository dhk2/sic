package anticlimacticteleservices.sic;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.text.HtmlCompat;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class fragment_channel_properties extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PassedChannel = "channel";
    private static final String ARG_PARAM2 = "param2";
    private Button save;
    private Button cancel;
    private Button channelVideos;
    private Channel mPassedChannel;
    private String mParam2;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private OnFragmentInteractionListener mListener;

    public fragment_channel_properties() {
    }

    public static fragment_channel_properties newInstance(Channel param1, String param2) {
        fragment_channel_properties fragment = new fragment_channel_properties();
        Bundle args = new Bundle();
        args.putSerializable(PassedChannel, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
          mPassedChannel = (Channel)getArguments().getSerializable(PassedChannel);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        int orientation = getResources().getConfiguration().orientation;
        View v=null;
       // if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
       //     dialog = inflater.inflate(R.layout.fragment_channel_properties_landscape, container, false);
      //  }
     //    else {
            v = inflater.inflate(R.layout.fragment_channel_properties, container, false);
      //   }
        Channel chan = mPassedChannel;

        TextView description = v.findViewById(R.id.channel_property_description);

        Spanned spanned = HtmlCompat.fromHtml(chan.getDescription(), HtmlCompat.FROM_HTML_MODE_COMPACT);
        description.setText(spanned);
        description.append(System.getProperty("line.separator"));
        description.append(chan.toString());
        /*
        description.append("Subscribers:"+chan.getSubscribers()+System.getProperty("line.separator"));
        description.append("last synch:"+new Date(chan.getLastsync())+System.getProperty("line.separator"));
        description.append("source url:"+chan.getUrl()+System.getProperty("line.separator"));
        description.append("Started:"+new Date(chan.getJoined())+System.getProperty("line.separator"));
        //description.append("youtube:"+chan.getSubscribers()+System.getProperty("line.separator"));
*/
        ImageView image = v.findViewById(R.id.thumbNailView);
        if (!chan.getThumbnail().isEmpty()){
            Picasso.get().load(chan.getThumbnail()).resize(320,240).centerInside().into(image);
        }
        save = v.findViewById(R.id.closeButton);
        cancel = v.findViewById(R.id.cancel_button);
        channelVideos=v.findViewById(R.id.channelvideos);
        TextView name = v.findViewById(R.id.channel_name);
        name.setText(chan.getTitle());
        CheckBox archive =v.findViewById(R.id.channel_archive);
        archive.setChecked(chan.isArchive());
        CheckBox notify = v.findViewById(R.id.channel_notify);
        notify.setChecked(chan.isNotify());
        CheckBox subscribed = v.findViewById(R.id.subscribecheckbox);
        subscribed.setChecked(MainActivity.masterData.getChannelDao().getChannelsBySourceID(chan.getSourceID()).size()>0);
        //               System.out.println(chan);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chan.setNotify(notify.isChecked());
                chan.setArchive(archive.isChecked());
                if (archive.isChecked()){

                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE );
                    }

                }
                if (!subscribed.isChecked()) {
                    MainActivity.masterData.removeChannel(chan);
                }
                if (subscribed.isChecked()){
                    MainActivity.masterData.addChannel(chan);
                    if (chan.isBitchute()){
                        new ChannelInit().execute(chan.getBitchuteUrl());
                    }
                    if (chan.isYoutube()){
                        new ChannelInit().execute(chan.getYoutubeUrl());
                    }

                }
                MainActivity.masterData.getChannelDao().update(chan);
                MainActivity.masterData.fragmentManager.popBackStack();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.masterData.fragmentManager.popBackStack();
            }
        });
        channelVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Video> channelVideos = new ArrayList<Video>();
                channelVideos = (ArrayList<Video>) MainActivity.masterData.getVideoDao().getVideosByAuthorId(chan.getID());
                if (!channelVideos.isEmpty()) {
                    Fragment fragment = new VideoFragment();
                    ((VideoFragment) fragment).setVideos(channelVideos);
                    FragmentTransaction transaction = MainActivity.masterData.fragmentManager.beginTransaction();
                    transaction.replace(R.id.fragment, fragment);
                    transaction.addToBackStack(null);
                    transaction.commitAllowingStateLoss();
                } else {
                    Toast.makeText(MainActivity.masterData.context,  "no videos for channel in feed currently", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return v;
    }
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
