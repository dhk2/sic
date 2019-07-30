# sic

Sharing is caring has goals of being a multi-social network feed collector allowing the user to control his feed and not a corporataion or government.

Right now it's a Bitchute client that can also access Youtube thanks to VLCs many great features. No internal video player, but designed to leverage the capabilities provided by other media players
VlCs ability to lock the screen, play in pop-op, and play in background mean in several ways this is a superior youtube client than their free service.

The bitchute videos are playing from their streaming hosting, but using webtorrents is high on the list of features to be implemented
Fediverse and other network integration as well as some sort of local networking like scuttlebutt. The default will always be your sources listed chronologically, any algorithmic enhancements will always be under the users control, not a nefarious corporate or government agent. Sharing is caring, and if you care you should have the control.

So far it's a couple weekends worth of work. Hope to have something usable in a month. The code will be going through a lot of changes before then so not looking for help there yet, but the XML isn't something that I'm good at and it could use a lot of work, so help there is always welcome.

Thanks so far
https://square.github.io/picasso/ for the image libary
https://jsoup.org/ for their great html/xml parsing library
https://www.videolan.org/vlc/index.html for their player which provides 110% of the solution I was looking for

Still looking for a good webtorrent selection, may have to end up adding webrtc to a current bit torrent service or modifying an existing webtorrent tool.

Built in Android Studio.

Known issues:
soooo many

VLC won't work after installed until it's opened separately and the brief tutorial clicked through.
Sometimes VLC gets confused, Closing it seperately and then trying to play the video again usually works.

