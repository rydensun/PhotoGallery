package com.example.user.photogallery;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by user on 2/03/16.
 */
public class FlickrFetchr {
    public static final String TAG = "FlickrFetchr";
    public static final String PREF_SEARCH_QUERY = "searchQuery";
    public static final String PREF_LAST_RESULT_ID = "lastResultId";
    private static final String ENDPOINT = "https://api.flickr.com/services/rest/";
    private static final String API_KEY = "5464800b912a46dbc264ddfa84d0f28a";
    private static final String METHOD_GET_RECENT = "flickr.photos.getRecent";
    private static final String PARAM_EXTRAS = "extras";
    private static final String EXTRA_SMALL_URL = "url_s";
    private static final String XML_PHOTO = "photo";
    private static final String METHOD_SEARCH = "flickr.photos.search";


    private static final String PARAM_TEXT = "text";
    byte[] getUrlBytes(String urlSpec) throws IOException{
        URL url = new URL(urlSpec);
        HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
        try{
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream input = connection.getInputStream();
            if(connection.getResponseCode()!=HttpsURLConnection.HTTP_OK){
                return null;
            }

            int byteRead = 0; // number of bytes read from InputStream

            byte[] buffer = new byte[1024];//store the bytes as a buffer between out and inputStream
            //input.read method returns the number of bytes read from stream
            //and store the contents in the parameters(buffer)
            while((byteRead= input.read(buffer))>0){
                //write the buffer contents to outStream
                out.write(buffer, 0, byteRead);
            }
            out.close();
            return out.toByteArray();

        }finally {
            connection.disconnect();
        }
    }
    public String  getUrl(String urlSpec) throws IOException{
        return new String(getUrlBytes(urlSpec));
    }
    public ArrayList<GalleryItem> fetchItems() {


            String url = Uri.parse(ENDPOINT).buildUpon()
                    .appendQueryParameter("method", METHOD_GET_RECENT)
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter(PARAM_EXTRAS, EXTRA_SMALL_URL)
                    .build().toString();
        return downloadGalleryItem(url);

    }
    public ArrayList<GalleryItem> downloadGalleryItem(String url) {
        ArrayList<GalleryItem> items = new ArrayList<GalleryItem>();
        try {

            String xmlString = getUrl(url);
            Log.i(TAG, "Received xml: " + xmlString);
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xmlString));
            parseItems(items, parser);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        } catch (XmlPullParserException xppe) {
            Log.e(TAG, "Failed to parse itmes", xppe);
        }
        return  items;

    }
    void parseItems(ArrayList<GalleryItem> items, XmlPullParser parser) throws XmlPullParserException, IOException{
        int eventType = parser.next();
        while(eventType != XmlPullParser.END_DOCUMENT){
            if(eventType == XmlPullParser.START_TAG && XML_PHOTO.equals(parser.getName())){
                String id = parser.getAttributeValue(null, "id");
                String caption = parser.getAttributeValue(null, "title");
                String smallUrl = parser.getAttributeValue(null, EXTRA_SMALL_URL);
                String owner = parser.getAttributeValue(null, "owner");
                GalleryItem item = new GalleryItem();
                item.setmId(id);
                item.setmCaption(caption);
                item.setmUrl(smallUrl);
                item.setmOwner(owner);
                items.add(item);

            }
            eventType = parser.next();
        }

    }
    public ArrayList<GalleryItem> search(String query){
        String url = Uri.parse(ENDPOINT).buildUpon()
                .appendQueryParameter("method", METHOD_SEARCH)
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter(PARAM_EXTRAS, EXTRA_SMALL_URL)
                .appendQueryParameter(PARAM_TEXT, query)
                .build().toString();
        return  downloadGalleryItem(url);
    }

}
