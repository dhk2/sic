package anticlimacticteleservices.sic;

//import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private VideoAdapter vAdapter;
    private RecyclerView videoRecyclerView;
    private RecyclerView channelRecyclerView;
    private ChannelAdapter cAdapter;
    private List<Video> sfVideos;
    private CheckBox youtubeSearch;
    private CheckBox bitchuteSearch;
    private RadioButton channelSearch;
    private RadioButton videoSearch;
    private View inflated;

    public SearchFragment() {
        // Required empty public constructor
    }
    public static SearchFragment newInstance(String param1, String param2) {
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sfVideos = new ArrayList<>();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         inflated = inflater.inflate(R.layout.fragment_search, container, false);
         return inflated;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button searchButton =inflated.findViewById(R.id.search_button);
        final EditText sText = inflated.findViewById(R.id.search_text);
        youtubeSearch = inflated.findViewById(R.id.search_youtube);
        bitchuteSearch = inflated.findViewById(R.id.search_bitchute);
        channelSearch = inflated.findViewById(R.id.radio_channel);
        videoSearch= inflated.findViewById(R.id.radio_video);
        bitchuteSearch.setChecked(true);


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Search target;
                String searchText = sText.getText().toString();
                target = new Search(searchText, videoSearch.isChecked(),youtubeSearch.isChecked(),bitchuteSearch.isChecked());
                Fragment subFragment = new VideoFragment();
                ((VideoFragment) subFragment).setVideos(sfVideos);
                FragmentManager supervisor =getChildFragmentManager();
                MainActivity.masterData.fragmentManager=supervisor;
                supervisor.beginTransaction().replace(R.id.search_subfragment, subFragment).commit();

           }
        });
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
    @Override
    public void onDetach() {
        super.onDetach();
    }
}

