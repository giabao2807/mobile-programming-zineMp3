package com.example.jingmb3.view.activity.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.jingmb3.view.offline.fragment.MyAlbum;
import com.example.jingmb3.view.offline.fragment.MyArtists;
import com.example.jingmb3.view.offline.fragment.MyFavoriteSongs;
import com.example.jingmb3.view.offline.fragment.MyMusic;
import com.example.jingmb3.view.offline.fragment.MySongs;

public class MyMusicViewPagerAdapter extends FragmentStateAdapter {


    public MyMusicViewPagerAdapter(@NonNull MyMusic fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new MySongs();
            case 1:
                return new MyAlbum();
            case 2:
                return new MyArtists();
            case 3:
                return new MyFavoriteSongs();
            default:
                return new MySongs();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
