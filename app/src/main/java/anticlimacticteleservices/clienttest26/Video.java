package anticlimacticteleservices.clienttest26;

import java.util.Date;
import java.util.HashSet;

public class Video implements Comparable<Video>
{
    private String title;
    private String author;
    private String url;
    private boolean watched;
    private Date date;
    private String thumbnailurl;
    private String magnet;
    private String mp4;
    private String description;
    private String viewCount;
    private String rating;
    private String upCount;
    private String downCount;
    private String ID;
    private String commentCount;
    private String hashtags;
    private String category;

    public Video()
    {
        this.title = "";
        this.author = "";
        this.url = "";
        System.out.println("blank constructor url set to:" + this.url);
        this.watched = false;
        this.date = new Date();
        this.thumbnailurl = "";
        this.magnet = "";
        this.description = "";
        this.mp4 = "";
        this.rating = "0";
        this.viewCount = "0";
        this.upCount = "0";
        this.downCount = "0";
        this.ID = "";
        this.commentCount = "0";
        this.hashtags = "";
        this.category = "";
    }

    public Video(String location)
    {
        this.title = "";
        this.author = "";
        this.url = location;
        System.out.println("new video created with" + this.url);
        if (location.indexOf("youtube") > 0)
        {
            ID = location.substring(location.lastIndexOf("?v=") + 3);
        }
        else
        {
            String[] segments = location.split("/");
            ID = segments[segments.length - 2];
        }
        this.watched = false;
        this.date = new Date();
        this.thumbnailurl = "";
        this.magnet = "";
        this.description = "";
        this.mp4 = "";
        this.rating = "0";
        this.viewCount = "0";
        this.upCount = "0";
        this.downCount = "0";
        this.commentCount = "0";
        this.hashtags = "";
        this.category = "";
    }


    public Video(final HashSet<String> encoded, boolean pointless)
    {
        String label="";
        String value="";

        for (String g : encoded)
        {
            System.out.println(label + ":-:" + value);
            label = g.substring(0, g.indexOf(":"));
            value = g.substring(g.indexOf(":") + 1);
            switch (label)
            {
                case "ID" :
                    ID = value;
                    break;
                case "url" :
                    url = value;
                    break;
                case "thumbnailurl":
                    thumbnailurl = value;
                    break;
                case "mp4":
                    mp4 = value;
                    break;
                case "title":
                    title = value;
                    break;
                case "author":
                    author = value;
                    break;
                default :
                    System.out.println("Can;t find match for |" + label + "|");
                    break;
            }
        }
        System.out.println("new video created from encoded set with" + this.url);
        this.watched = false;
        this.date = new Date();
        this.magnet = "";
        this.description = "";
        this.rating = "0";
        this.viewCount = "0";
        this.upCount = "0";
        this.downCount = "0";
        this.commentCount = "0";
        this.hashtags = "";
        this.category = "";
        System.out.println("new video created from encoded set with" + this.url);
    }

//  	     Getters

    public String getUrl()
    {
        return this.url;
    }
    public Date getDate()
    {
        return this.date;
    }
    public String getDescription()
    {
        return this.description;
    }
    public String getTitle()
    {
        return this.title;
    }
    public String getAuthor()
    {
        return this.author;
    }
    public String getThumbnail()
    {
        return this.thumbnailurl;
    }
    public String getMp4()
    {
        return this.mp4;
    }
    public String getViewCount()
    {
        return this.viewCount;
    }
    public String getRating()
    {
        return this.rating;
    }
    public boolean getWatched()
    {
        return this.watched;
    }
    public String getID()
    {
        return this.ID;
    }


//			Setters

    public void setUrl(String value)
    {
        this.url = value;
    }
    public void setTitle(String value)
    {
        this.title = value;
    }
    public void setAuthor(String value)
    {
        this.author = value;
    }
    public void setThumbnail(String value)
    {
        this.thumbnailurl = value;
    }
    public void setMagnet(String value)
    {
        this.magnet = value;
    }
    public void setDate(Date date)
    {
        this.date = date;
    }
    public void setMp4(String value)
    {
        this.mp4 = value;
    }
    public void setDescription(String value)
    {
        this.description = value;
    }
    public void setRating(String value)
    {
        this.rating = value;
    }
    public void setUpCount(String value)
    {
        this.upCount = value;
    }
    public void setDownCount(String value)
    {
        this.downCount = value;
    }
    public void setViewCount(String value)
    {
        this.viewCount = value;
    }
    public void commentCount(String value)
    {
        this.rating = value;
    }
    public void category(String value)
    {
        this.category = value;
    }
    public void setHashtags(String value)
    {
        this.hashtags = value;
    }
    public void setID(String value)
    {
        this.ID = value;
    }
//			Functions

    public String toString()
    {
        return ("title:" + title + "\n" +
                "url:" + url + "\n" +
                "thumbnail:" + thumbnailurl + "\n" +
                "author:" + author + "\n" +
//			"watched:" + watched.toString() + "\n" +
                "uploaded:" + date.toString() + "\n" +
                "magnet link:" + magnet + "\n" +
                "description:" + description + "\n" +
                "mp4 file" + mp4 + "\n" +
                "Rating:" + rating + "\n" +
                "Views:" + viewCount + "\n" +
                "Up votes:" + upCount + "\n" +
                "Down votes:" + downCount + "\n" +
                "ID:" + ID + "\n" +
                "Comments:" + commentCount + "\n" +
                "Hash tags:" + hashtags + "\n" +
                "Category:" + category);
    }

    public HashSet<String> getStringEncodedSet()
    {
        HashSet<String> encoded=new HashSet();
        encoded.add("ID:" + ID);
        encoded.add("url:" + url);
        encoded.add("thumbnailurl:" + thumbnailurl);
        encoded.add("mp4:" + mp4);
        encoded.add("title:" + title);
        encoded.add("author:" + author);
        return encoded;
    }

    @Override
    public int compareTo(Video candidate)
    {
        return (this.getDate().after(candidate.getDate())  ? -1 :
                this.getDate().equals(candidate.getDate()) ? 0 : 1);
    }

}