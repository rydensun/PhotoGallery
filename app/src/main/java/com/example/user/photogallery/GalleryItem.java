package com.example.user.photogallery;

/**
 * Created by user on 5/03/16.
 */
public class GalleryItem {
    public String getmCaption() {
        return mCaption;
    }

    public void setmCaption(String mCaption) {
        this.mCaption = mCaption;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    private String mCaption;
    private String mId;
    private String mUrl;

    public String getmOwner() {
        return mOwner;
    }

    public void setmOwner(String mOwner) {
        this.mOwner = mOwner;
    }

    private String mOwner;
    public String getPhotoUrl(){
        //test return
       // return "http://www.baidu.com/index.php?tn=20041099_oem_dg&ch=33";
        return "https://www.flickr.com/photos/" + mOwner + "/" + mId;
    }
    public String toString(){
        return mCaption;
    }
}
