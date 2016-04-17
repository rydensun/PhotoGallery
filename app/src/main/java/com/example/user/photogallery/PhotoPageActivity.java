package com.example.user.photogallery;

import android.support.v4.app.Fragment;

/**
 * Created by user on 16/03/16.
 */
public class PhotoPageActivity extends SingleFragmentActivity {
    @Override
    public Fragment createFragment(){
        return new PhotoPageFragment();
    }
}
