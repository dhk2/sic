package anticlimacticteleservices.sic;

//import android.app.Fragment;
import android.app.Dialog;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SettingsFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    Dialog dialogHandle;
    WebView webviewHandle;
    TextView feedAge;
    RadioButton useKittens,useDissenter;
    CheckBox useComments,backgroundSync,wifiOnly,muteErrors,bcSearchGoogle,bcSearchBitchute,hideWatched;


    public SettingsFragment() {
    }
    public static SettingsFragment newInstance(String param1, String param2) {
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);

        SettingsFragment fragment = new SettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Button importBitchute = view.findViewById(R.id.load_bitchute);
        final Button importYoutube = view.findViewById(R.id.load_youtube);
        final Button subscribeToUrl = view.findViewById(R.id.subscribetourl);
        final Button help = view.findViewById(R.id.help);
        feedAge = view.findViewById(R.id.feed_age);
        feedAge.setText(Long.toString(MainActivity.masterData.getFeedAge()));
        backgroundSync = view.findViewById(R.id.backgroundsyncenabled);
        backgroundSync.setChecked(MainActivity.masterData.isUseComments());
        wifiOnly = view.findViewById(R.id.wifisynconly);
        wifiOnly.setChecked(MainActivity.masterData.isWifionly());
        useComments = view.findViewById(R.id.commentsenabled);
        useComments.setChecked(MainActivity.masterData.isUseComments());
        useDissenter = view.findViewById(R.id.dissentercommentsenabled);
        useKittens = view.findViewById(R.id.kittencommentsenabled);
        useKittens.setChecked(MainActivity.masterData.isKittenComments());
        useDissenter.setChecked(MainActivity.masterData.isDissenterComments());
        muteErrors=view.findViewById(R.id.muteerrors);
        hideWatched=view.findViewById(R.id.hidewatched);
        muteErrors.setChecked(MainActivity.masterData.isMuteErrors());
        hideWatched.setChecked(MainActivity.masterData.isHideWatched());
        bcSearchBitchute=view.findViewById(R.id.searchbitchute);
        bcSearchGoogle=view.findViewById(R.id.searchgoogle);
        bcSearchBitchute.setChecked(MainActivity.masterData.isBitchuteSearchBitchute());
        bcSearchGoogle.setChecked(MainActivity.masterData.isBitchuteSearchGoogle());

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent playerIntent = new Intent(Intent.ACTION_VIEW);
                String path = "https://github.com/dhk2/sic/wiki/Settings";
                Uri uri = Uri.parse(path);
                playerIntent.setData(uri);
                v.getContext().startActivity(playerIntent);
            }
        });


        subscribeToUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                if (clipboard.hasPrimaryClip()) {
                    android.content.ClipDescription description = clipboard.getPrimaryClipDescription();
                    android.content.ClipData data = clipboard.getPrimaryClip();
                    if (data != null && description != null ) {
                        new ChannelInit().execute(String.valueOf(data.getItemAt(0).getText()));
                        System.out.println(String.valueOf(data.getItemAt(0).getText()));
                    }
                }


            }
        });
        importBitchute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  final Dialog dialog = new Dialog(v.getContext());
                dialog.setContentView(R.layout.importdialog);
                final WebView webView = dialog.findViewById(R.id.idplayer_window);
                dialogHandle=dialog;
                webviewHandle = webView;
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        webView.loadUrl("javascript:window.HtmlHandler.handleHtml" +
                                "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                    }
                });
                webView.addJavascriptInterface(new MyJavaScriptInterface(), "HtmlHandler");
                WebSettings webSettings = webView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                webView.getSettings().setUseWideViewPort(true);
                webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
                webView.setScrollbarFadingEnabled(false);
                webView.loadUrl("https://www.bitchute.com/subscriptions/");
                Button closeButton = dialog.findViewById(R.id.idclosebutton);
                closeButton.setText("close");
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        webView.destroy();
                        dialog.dismiss();
                    }
                });
            dialog.show();
            }
        });
        importYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(v.getContext());
                dialog.setContentView(R.layout.importdialog);
                final WebView webView = dialog.findViewById(R.id.idplayer_window);
                dialogHandle=dialog;
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        webView.loadUrl("javascript:window.HtmlHandler.handleHtml" +
                                "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                    }
                });
                webView.addJavascriptInterface(new MyJavaScriptInterface(), "HtmlHandler");
                WebSettings webSettings = webView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                webView.getSettings().setUseWideViewPort(true);
                webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
                webView.setScrollbarFadingEnabled(false);
                webView.loadUrl("https://www.youtube.com/subscription_manager");
                Button closeButton = dialog.findViewById(R.id.idclosebutton);
                closeButton.setText("close");
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        webView.destroy();
                        dialog.dismiss();
                    }
                });
           dialog.show();
            }
        });
        RadioGroup youtubeRadioGroup = view.findViewById(R.id.youtubeplayerradioGroup);
        RadioButton youtubeUseVlc =view.findViewById(R.id.youtubeuse_vlc);
        RadioButton youtubeUseDefault =view.findViewById(R.id.youtubeuse_default);
        RadioButton youtubeUseWebview =view.findViewById(R.id.youtubeuse_webview);
       // RadioButton youtubeUseExoview = view.findViewById(R.id.youtubeuse_exo);
        RadioButton youtubeUseNewpipe = view.findViewById(R.id.youtubeusenewpipe);
        RadioButton youtubeUseYoutube = view.findViewById(R.id.youtubeuse_youtube);
        RadioGroup bitchuteRadioGroup = view.findViewById(R.id.bitchuteplayerradioGroup);
        RadioButton bitchuteUseVlc =view.findViewById(R.id.bitchuteuse_vlc);
        RadioButton bitchuteUseDefault =view.findViewById(R.id.bitchuteuse_default);
        RadioButton bitchuteUseWebview =view.findViewById(R.id.bitchuteuse_webview);
        RadioButton bitchuteUseChrome =view.findViewById(R.id.bitchuteuse_chrome);
        RadioButton bitchuteUseProperties =view.findViewById(R.id.bitchuteuse_properties);
        RadioButton youtubeUseChrome =view.findViewById(R.id.youtubeuse_chrome);
        RadioButton youtubeUseProperties =view.findViewById(R.id.youtubeuse_properties);
       // RadioButton bitchuteUseNative = view.findViewById(R.id.bitchuteuse_native);
        RadioButton bitchuteUseExo = view.findViewById(R.id.bitchuteuse_exo);
        //RadioButton bitchuteUseKodi = view.findViewById(R.id.bitchuteuse_kodi);
        //RadioButton bitchuteUseNewpipe = view.findViewById(R.id.bitchuteuse_newpipe);
        RadioButton bitchuteUseWebtorrentWebview = view.findViewById(R.id.bitchuteuse_webtorrentwebview);
        if (!MainActivity.masterData.youtubeInstalled){
            youtubeUseYoutube.setVisibility((View.GONE));
        }
        if (!MainActivity.masterData.vlcInstalled){
            youtubeUseVlc.setVisibility(View.GONE);
            bitchuteUseVlc.setVisibility(View.GONE);
        }
        if (!MainActivity.masterData.chromeInstalled){
            youtubeUseChrome.setVisibility(View.GONE);
            bitchuteUseChrome.setVisibility(View.GONE);
        }
        if (!MainActivity.masterData.newpipeInstalled){
            youtubeUseNewpipe.setVisibility(View.GONE);
        }
        switch(MainActivity.masterData.getYoutubePlayerChoice()){
            case 1:
                youtubeUseVlc.setChecked(true);
                break;
            case 2:
                youtubeUseDefault.setChecked(true);
                break;
            case 4:
                youtubeUseWebview.setChecked(true);
                break;
            case 32:
                youtubeUseNewpipe.setChecked(true);
            case 256:
                youtubeUseYoutube.setChecked(true);
            case 512:
                youtubeUseChrome.setChecked(true);
            case 1024:
                youtubeUseProperties.setChecked(true);
        }
        youtubeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                switch(checkedId) {
                    case R.id.youtubeuse_vlc:
                        MainActivity.masterData.setYoutubePlayerChoice(1);
                        break;
                    case R.id.youtubeuse_default:
                        MainActivity.masterData.setYoutubePlayerChoice(2);
                        break;
                    case R.id.youtubeuse_webview:
                        MainActivity.masterData.setYoutubePlayerChoice(4);
                        break;
                    case R.id.youtubeusenewpipe:
                        MainActivity.masterData.setYoutubePlayerChoice(32);
                        break;
                    case R.id.youtubeuse_youtube:
                        MainActivity.masterData.setYoutubePlayerChoice(256);
                        break;
                    case R.id.youtubeuse_chrome:
                        MainActivity.masterData.setYoutubePlayerChoice(512);
                        break;
                    case R.id.youtubeuse_properties:
                        MainActivity.masterData.setYoutubePlayerChoice(1024);
                        break;
                }
            }
        });
        switch(MainActivity.masterData.getBitchutePlayerChoice()){
            case 1:
                bitchuteUseVlc.setChecked(true);
                break;
            case 2:
                bitchuteUseDefault.setChecked(true);
                break;
            case 4:
                bitchuteUseWebview.setChecked(true);
                break;
            case 16:
                bitchuteUseExo.setChecked(true);
                break;
            case 64:
                bitchuteUseWebtorrentWebview.setChecked(true);
                break;
            case 512:
                bitchuteUseChrome.setChecked(true);
                break;
            case 1024:
                bitchuteUseProperties.setChecked(true);
                break;

        }
        bitchuteRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.bitchuteuse_vlc:
                        MainActivity.masterData.setBitchutePlayerChoice(1);
                        break;
                    case R.id.bitchuteuse_default:
                        MainActivity.masterData.setBitchutePlayerChoice(2);
                        break;
                    case R.id.bitchuteuse_webview:
                        MainActivity.masterData.setBitchutePlayerChoice(4);
                        break;
                    case R.id.bitchuteuse_exo:
                        MainActivity.masterData.setBitchutePlayerChoice(16);
                        break;
                    case R.id.bitchuteuse_webtorrentwebview:
                        MainActivity.masterData.setBitchutePlayerChoice(64);
                        break;
                    case R.id.bitchuteuse_chrome:
                        MainActivity.masterData.setBitchutePlayerChoice(512);
                        break;
                    case R.id.bitchuteuse_properties:
                        MainActivity.masterData.setBitchutePlayerChoice(1024);
                        break;
                }
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
    private class MyJavaScriptInterface {
        @JavascriptInterface
        public void handleHtml(String html) {
            Document doc = Jsoup.parse(html);
            Log.v("Settings-Import","["+doc.title()+"]");
            if (doc.title().equals("Subscriptions - BitChute")) {
                Elements subscriptions = doc.getElementsByClass("subscription-container");
                for (Element s : subscriptions) {
                   new ChannelInit().execute("https://www.bitchute.com"+s.getElementsByTag("a").first().attr("href"));
                }
                dialogHandle.dismiss();
                Toast.makeText(MainActivity.masterData.context,"adding "+subscriptions.size()+ " possible channels from bitchute.",Toast.LENGTH_SHORT).show();
            }
            if (doc.title().equals("Subscription manager - YouTube")) {
                Elements channels = doc.getElementsByClass("guide-item yt-uix-sessionlink yt-valign spf-link    ");
                String url;
                for (Element c : channels){
                  url = "https://www.youtube.com"+c.attr("href");
                    if (url.indexOf("channel/")>0) {
                        new ChannelInit().execute(url);
                    }
                }
                dialogHandle.dismiss();
                Toast.makeText(MainActivity.masterData.context,"adding "+channels.size()+ " possible channels from youtube.",Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        MainActivity.masterData.setFeedAge(Long.parseLong( feedAge.getText().toString()));
        MainActivity.masterData.setUseComments(useComments.isChecked());
        MainActivity.masterData.setKittenComments(useKittens.isChecked());
        MainActivity.masterData.setDissenterComments(useDissenter.isChecked());
        MainActivity.masterData.setBackgroundSync(backgroundSync.isChecked());
        MainActivity.masterData.setWifionly(wifiOnly.isChecked());
        MainActivity.masterData.setMuteErrors(muteErrors.isChecked());
        MainActivity.masterData.setHideWatched(hideWatched.isChecked());
        MainActivity.masterData.setBitchuteSearchBitchute(bcSearchBitchute.isChecked());
        MainActivity.masterData.setBitchuteSearchGoogle(bcSearchGoogle.isChecked());
        MainActivity.masterData.saveUserData();
    }
}

