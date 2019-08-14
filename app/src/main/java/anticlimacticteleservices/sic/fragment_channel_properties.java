package anticlimacticteleservices.sic;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.text.HtmlCompat;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link fragment_channel_properties.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link fragment_channel_properties#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_channel_properties extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PassedChannel = "channel";
    private static final String ARG_PARAM2 = "param2";
    private Button save;
    private Button cancel;
    private Channel mPassedChannel;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public fragment_channel_properties() {
        // Required empty public constructor
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
        TextView description = v.findViewById(R.id.channel_description);
        Spanned spanned = HtmlCompat.fromHtml(chan.getDescription(), HtmlCompat.FROM_HTML_MODE_COMPACT);
        description.setText(spanned);
        description.append(System.getProperty("line.separator"));
        description.append("Subscribers:"+chan.getSubscribers()+System.getProperty("line.separator"));
        description.append("last synch:"+new Date(chan.getLastsync())+System.getProperty("line.separator"));
        description.append("source url:"+chan.getUrl()+System.getProperty("line.separator"));
        description.append("Started:"+new Date(chan.getJoined())+System.getProperty("line.separator"));
        //description.append("youtube:"+chan.getSubscribers()+System.getProperty("line.separator"));

        ImageView image = v.findViewById(R.id.thumbNailView);
        if (!chan.getThumbnail().isEmpty()){
            Picasso.get().load(chan.getThumbnail()).resize(320,240).centerInside().into(image);
        }
        save = v.findViewById(R.id.closeButton);
        cancel = v.findViewById(R.id.cancel_button);
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
                //TODO put in actual subscribe/unsubscribe ability from here.
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
