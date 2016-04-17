package com.example.user.photogallery;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.GpsStatus;
import android.media.session.MediaSession;
import android.net.sip.SipAudioCall;
import android.net.sip.SipSession;
import android.os.Handler;
import android.os.HandlerThread;

import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by user on 7/03/16.
 */
public class ThumbnailDownloader<Token> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";
    private static final int MESSAGE_DOWNLOAD = 0 ;
    Handler mhandler;
    Handler mResponseHandler;
    Listener<Token> mListener;
    public interface Listener<Token> {
        void onThumbnailDownloaded(Token token, Bitmap thumbnail);
    }
    public void setListener(Listener<Token> listener){
        mListener = listener;
    }
    Map<Token, String> requestMap = Collections.synchronizedMap(new HashMap< Token, String>());
    public ThumbnailDownloader(Handler responseHandler){

        super(TAG);
        mResponseHandler = responseHandler;
    }
    @SuppressLint("HandlerLeak")
    @Override
    protected  void onLooperPrepared(){
        mhandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.what == MESSAGE_DOWNLOAD){
                    @SuppressWarnings("unchecked")
                            Token token = (Token)msg.obj;
                    Log.i(TAG, "Got a request for url"+requestMap.get(token));
                    handleRequest(token);
                    }
                }
            };
        }



    public void queueThumbnail(Token token, String url){
        Log.i(TAG,  "Got an URL"+ url);
        requestMap.put(token,url);
        mhandler.obtainMessage(MESSAGE_DOWNLOAD,token)
                .sendToTarget();
    }
    public void handleRequest(final Token token){
        try{
            final String url = requestMap.get(token);
            if(url == null) return;
            byte[] bitMapBytes = new FlickrFetchr().getUrlBytes(url);
            final Bitmap bitmap = BitmapFactory.decodeByteArray(bitMapBytes, 0, bitMapBytes.length);
            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(requestMap.get(token)!=url){
                        return;
                    }
                    requestMap.remove(token);
                    mListener.onThumbnailDownloaded(token,bitmap);
                }
            });
            Log.i(TAG, "bitmap created");
        }catch (IOException ioe){
            Log.e(TAG, "Error downloading bitmaps" ,ioe);
        }
    }

    public void clearQueue(){
        mhandler.removeMessages(MESSAGE_DOWNLOAD);
        requestMap.clear();
    }

}
