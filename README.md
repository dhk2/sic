# Sharing Is Caring

[sic] has goals of being a multi-social network feed collector allowing user control of the feeds content. 

Right now it's a Bitchute client that can also access Youtube thanks to VLCs many great features. No internal video player yet, but designed to leverage the capabilities provided by other media players
VlCs ability to lock the screen, play in pop-op, play in background, and play youtube vidoes make it the best player i've found so far. Any good video player should work for a straight bitchute client. 

The bitchute videos are playing from their streaming hosting, but using webtorrents is high on the list of features to be implemented
Fediverse and other network integration as well as some sort of local networking like scuttlebutt. The default will always be your sources listed chronologically, any algorithmic enhancements will always be under the users control.

Thanks so far
https://square.github.io/picasso/ for the image libary
https://jsoup.org/ for their great html/xml parsing library
https://www.videolan.org/vlc/index.html for their player which provides 110% of the solution I was looking for

Still looking for a good java or service based wayway to implement webtorrent natively

I'm using Android Studio to build the softward

Known issues:
Webview player can crash the system if you rotate the device
VLC won't work after installed until it's opened separately and the brief tutorial clicked through.
Sometimes VLC gets confused, Closing it seperately and then trying to play the video again usually works.

