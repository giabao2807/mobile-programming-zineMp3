package com.example.jingmb3.view.activity.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.jingmb3.R;
import com.example.jingmb3.model.online.GetSongs;
import com.example.jingmb3.model.online.MediaOnline;

import java.util.List;

public class SongOnlineAdapter extends RecyclerView.Adapter<SongOnlineAdapter.ViewHolder> {
    List<GetSongs> getSongs;
    Context context;

    public SongOnlineAdapter(List<GetSongs> getSongs, Context context) {
        this.getSongs = getSongs;
        this.context = context;
    }

    public interface clickSongsOnline{
        void clickSongsOnline(int position);
    }
    public clickSongsOnline clickSong;
    public void setClickSong(clickSongsOnline clickSongsOnl){
        clickSong = clickSongsOnl;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_song_online,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        GetSongs getSong = getSongs.get(position);
        holder.titleSong.setText(getSong.getSongTitle());
        holder.ArtistSong.setText(getSong.getArtist());
        holder.titleSong.setSelected(true);
        holder.ArtistSong.setSelected(true);
        if(getSong.getAlbum_art()!=null && !getSong.getAlbum_art().equals("")) {
            Glide.with(context)
                .asBitmap()
                .load(getSongs.get(position).getAlbum_art())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        holder.imgSong.setImageBitmap(resource);
                        MediaOnline.getInstance().setUrlImage(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
        }
        else{
            holder.imgSong.setImageResource(R.drawable.icon_music);
        }
        holder.itemSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickSong.clickSongsOnline(position);
            }
        });
    }

    public void setGetSongs(List<GetSongs> getSongs) {
        this.getSongs = getSongs;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(getSongs!=null) return getSongs.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleSong;
        TextView ArtistSong;
        LinearLayout itemSong;
        ImageView imgSong;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemSong = itemView.findViewById(R.id.item_song_online);
            titleSong = itemView.findViewById(R.id.nameSongOnline);
            ArtistSong = itemView.findViewById(R.id.nameMyArtistOnline);
            imgSong = itemSong.findViewById(R.id.imageSongOnline);
        }
    }
}
