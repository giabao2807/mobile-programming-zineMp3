package com.example.jingmb3.view.offline.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.ColorUtils;
import androidx.palette.graphics.Palette;



import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.jingmb3.R;
import com.example.jingmb3.databinding.ActivityPlayerSongBinding;
import com.example.jingmb3.model.offline.FavoriteDatabase;
import com.example.jingmb3.model.offline.FavoriteObject;
import com.example.jingmb3.model.offline.MyAlbumDatabase;
import com.example.jingmb3.model.offline.MyAlbumObject;
import com.example.jingmb3.model.offline.MyArtistDatabase;
import com.example.jingmb3.model.offline.MyArtistObject;
import com.example.jingmb3.model.offline.MyMediaPlayer;
import com.example.jingmb3.model.offline.MySongObject;
import com.example.jingmb3.model.offline.MySongsDatabase;
import com.example.jingmb3.model.online.MediaOnline;
import com.example.jingmb3.view.offline.fragment.MyMusic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class PlayerSong extends AppCompatActivity {

    private  Bitmap bitmap=null;
    private ActivityPlayerSongBinding binding;
    private ArrayList<MySongObject> myListSong;
    private int position;
    boolean fav=false;
    Thread updateSeekbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerSongBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //S??? ki???n khi nh???n n??t ????ng m??n h??nh ch??i nh???c
        binding.closePlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0, R.anim.slide_down_out);
            }
        });

        //Ki???m tra ch??? ????? l???p l???i ??ang b???t hay t???t ????? set background cho n??t t????ng ???ng
        if(MyMediaPlayer.getInstance().isCheckRepeat())
            binding.repeatBtn.setImageResource(R.drawable.ic_current_repeat);
        //S??? ki???n nh???n n??t l???p l???i
        binding.repeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!MyMediaPlayer.getInstance().isCheckRepeat()){
                    binding.repeatBtn.setImageResource(R.drawable.ic_current_repeat);
                    MyMediaPlayer.getInstance().setCheckRepeat(true);
                    Toast.makeText(PlayerSong.this,"L???p l???i hi???n t???i",Toast.LENGTH_SHORT).show();
                }else {
                    binding.repeatBtn.setImageResource(R.drawable.ic_repeat);
                    MyMediaPlayer.getInstance().setCheckRepeat(false);
                    Toast.makeText(PlayerSong.this,"L???p l???i B???T",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Ki???m tra ch??? ????? ng???u nhi??n ??ang b???t hay t???t ????? set background cho n??t t????ng ???ng
        if(MyMediaPlayer.getInstance().isCheckRandom())
            binding.randomBtn.setImageResource(R.drawable.ic_shuffle_on);
        //S??? ki???n nh???n n??t ng???u nhi??n
        binding.randomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!MyMediaPlayer.getInstance().isCheckRandom()){
                    binding.randomBtn.setImageResource(R.drawable.ic_shuffle_on);
                    MyMediaPlayer.getInstance().setCheckRandom(true);
                    Toast.makeText(PlayerSong.this,"Ph??t ng???u nhi??n B???T",Toast.LENGTH_SHORT).show();
                }else {
                    binding.randomBtn.setImageResource(R.drawable.ic_shuffle);
                    MyMediaPlayer.getInstance().setCheckRandom(false);
                    Toast.makeText(PlayerSong.this,"Ph??t ng???u nhi??n T???T",Toast.LENGTH_SHORT).show();
                }
            }
        });


        //KI???M TRA DANH S??CH CH??I NH???C
        //Ki???m tra danh s??ch nh???c ??ang ph??t trong m???t album
        if(MyMediaPlayer.getInstance().isCheckSongAlbum()){
            int IdAlbum=MyMediaPlayer.getInstance().getIdAlbum();
            MyAlbumObject myAlbumObject=MyAlbumDatabase.getInstance(this).myAlbumDAO().getAlbumById(IdAlbum);
            ArrayList<String> listIdAlbum=new ArrayList<>();
            listIdAlbum=myAlbumObject.getId_song();
            myListSong=new ArrayList<>();
            for(String id:listIdAlbum) {
                myListSong.add(MySongsDatabase.getInstance(this).mySongsDAO().getMySongByID(Integer.valueOf(id)));
            }
            if(myListSong.isEmpty()) return;
            Arrange(myListSong);
            MyMediaPlayer.getInstance().setListPlaySong(myListSong);
        }
        //Ki???m tra danh s??ch nh???c ??ang ph??t trong m???t ngh??? s??
        else if(MyMediaPlayer.getInstance().isCheckSongArtist()){
            int IdArtist=MyMediaPlayer.getInstance().getIdArtist();
            MyArtistObject myArtistObject= MyArtistDatabase.getInstance(this).myArtistDAO().getArtistById(IdArtist);
            myListSong=new ArrayList<>();
            myListSong= (ArrayList<MySongObject>) MySongsDatabase.getInstance(this).mySongsDAO().getListSongByArtist(myArtistObject.getNameArtist());
            if(myListSong.isEmpty()) return;
            Arrange(myListSong);
            MyMediaPlayer.getInstance().setListPlaySong(myListSong);
        }
        //Ki???m tra danh s??ch nh???c ??ang ph??t trong danh s??ch c??c b??i h??t y??u th??ch
        else if(MyMediaPlayer.getInstance().isCheckFavSong()){
            myListSong=new ArrayList<>();
            List<Integer> IdSong=FavoriteDatabase.getInstance(this).favoriteDAO().getListIdSong();
            for(int id:IdSong){
                myListSong.add(MySongsDatabase.getInstance(this).mySongsDAO().getMySongByID(id));
            }
            Arrange(myListSong);
            MyMediaPlayer.getInstance().setListPlaySong(myListSong);
        }
        //Danh s??ch ch??i nh???c l?? danh s??ch t???t c??? c??c b??i h??t
        else{
            myListSong= (ArrayList<MySongObject>) MySongsDatabase.getInstance(this).mySongsDAO().getListSong();
            Arrange(myListSong);
            MyMediaPlayer.getInstance().setListPlaySong(myListSong);
        }


        //L???y v??? tr?? c???a m???t b??i h??t trong danh s??ch ph??t v?? hi???n th??? l??n m??n h??nh t????ng ???ng
        position=getIntent().getIntExtra("pos",0);
        binding.songName.setSelected(true);
        binding.songName.setText(myListSong.get(position).getNameSong());
        binding.artistName.setText(myListSong.get(position).getNameArtist());
        bitmap = BitmapFactory.decodeByteArray(myListSong.get(position).getImageSong(),
                0,myListSong.get(position).getImageSong().length);
        binding.imageSong.setImageBitmap(bitmap);

        //?????t m??u n???n m??n h??nh ch??i nh???c theo m??u ch??? ?????o trong ???nh c???a b??i h??t
        ScreenBackground();

        //Ki???m tra xem b??i h??t ??ang ph??t c?? ???????c y??u htisch hay kh??ng
        favoriteSong();


        //Ki???m tra logic khi ??i v??o m??n h??nh ch??i nh???c ????? ph??t b??i h??t ho???c kh??ng
        if(MyMediaPlayer.getInstance().getMediaPlayer()==null){
            if(MyMusic.isPressMiniPlay()){
                binding.playBtn.setImageResource(R.drawable.icon_play);
                MyMediaPlayer.getInstance().playAudioFile(getApplicationContext(),myListSong.get(position).getLinkSong(),position);
                MyMusic.setPressMiniPlay(false);
            }
            else{
                MyMediaPlayer.getInstance().playAudioFile(getApplicationContext(),myListSong.get(position).getLinkSong(),position);
                MyMediaPlayer.getInstance().Start();
                imageAnimation();
                binding.playBtn.setImageResource(R.drawable.icon_pause);
            }
        }
        else if(MyMediaPlayer.getInstance().isCheckStopMedia()){
            if(MyMusic.isPressMiniPlay()){
                binding.playBtn.setImageResource(R.drawable.icon_play);
                MyMediaPlayer.getInstance().playAudioFile(getApplicationContext(),myListSong.get(position).getLinkSong(),position);
                MyMusic.setPressMiniPlay(false);
            }
            else{
                MyMediaPlayer.getInstance().playAudioFile(getApplicationContext(),myListSong.get(position).getLinkSong(),position);
                MyMediaPlayer.getInstance().Start();
                MyMediaPlayer.getInstance().setStopMedia();
                imageAnimation();
                binding.playBtn.setImageResource(R.drawable.icon_pause);
            }
        }
        else
        {
            if(MyMusic.isPressMiniPlay()) MyMusic.setPressMiniPlay(false);
            if(MyMediaPlayer.getInstance().chechMedia()){
                binding.playBtn.setImageResource(R.drawable.icon_pause);
                imageAnimation();
            }
            else{
                binding.playBtn.setImageResource(R.drawable.icon_play);
            }
        }


        //s??? ki???n nh???n n??t ch??i nh???c ho???c d???ng
        binding.playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(MyMediaPlayer.getInstance().chechMedia()){
                    binding.playBtn.setImageResource(R.drawable.icon_play);
                    binding.songName.setSelected(false);
                    MyMediaPlayer.getInstance().pauseAudioFile();
                    binding.imageSong.clearAnimation();
                }
                else{
                    if(!MediaOnline.getInstance().isCheckStop()){
                        MediaOnline.getInstance().stopPlaySong();
                    }
                    binding.playBtn.setImageResource(R.drawable.icon_pause);
                    MyMediaPlayer.getInstance().getMediaPlayer().start();
                    MyMediaPlayer.getInstance().setStopMedia();
                    binding.songName.setSelected(true);
                    imageAnimation();
                }
            }
        });

        //X??? l?? chuy???n ph??t b??i h??t ti???p theo khi b??i h??t hi???n t???i ho??n th??nh
        mediaComplete();

        //S??? ki???n nh???n n??t chuy???n sang b??i h??t ti???p theo
        binding.nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Ki???m tra ch??? ????? ng???u nhi??n hay l???p l???i c?? ??ang b???t hay kh??ng
                if(MyMediaPlayer.getInstance().isCheckRepeat()){
                    repeatSong();
                    return;
                }
                else if(MyMediaPlayer.getInstance().isCheckRandom()){
                    RandomPlay();
                    return;
                }
                //N???u hai ch??? ????? tr??n kh??ng b???t th?? th???c hi???n chuy???n ph??t b??i h??t ti???p theo
                nextSong();
            }
        });

        //S??? ki???n nh???n n??t chuy???n sang b??i h??t ??? tr?????c
        binding.prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Ki???m tra ch??? ????? ng???u nhi??n hay l???p l???i c?? ??ang b???t hay kh??ng
                if(MyMediaPlayer.getInstance().isCheckRepeat()){
                    repeatSong();
                    return;
                }
                else if(MyMediaPlayer.getInstance().isCheckRandom()){
                    RandomPlay();
                    return;
                }
                //N???u hai ch??? ????? tr??n kh??ng b???t th?? th???c hi???n chuy???n ph??t b??i h??t tr?????c ????
                previousSong();
            }
        });

        ///C???p nh???t ti???n tr??nh c???a Seekbar
        updateSeekbar=new Thread(){
            @Override
            public void run() {
                super.run();
                int totalDuration=MyMediaPlayer.getInstance().getMediaPlayer().getDuration();
                int currentPosition=0;
                while (currentPosition<totalDuration){
                    try{
                        sleep(500);
                        currentPosition=MyMediaPlayer.getInstance().getMediaPlayer().getCurrentPosition();
                        binding.seekBar.setProgress(currentPosition);
                    }catch (InterruptedException | IllegalStateException e){
                        e.printStackTrace();
                    }
                }
            }
        };
        Seekbar();

        //Set th???i gian k???t th??c b??i h??t
        binding.timeEnd.setText(createTime(MyMediaPlayer.getInstance().getMediaPlayer().getDuration()));
    }

    //H??m set m??u n???n m??n h??nh d???a theo m??u ch??? ?????o trong ???nh c???a b??i h??t t????ng th??ch
    private void ScreenBackground() {
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(@Nullable Palette palette) {
                int color=palette.getDominantColor(1);
                binding.screenPlay.setBackgroundColor(color);
                binding.screenPlay.getBackground().setAlpha(185);
                int color1=((ColorDrawable) binding.screenPlay.getBackground()).getColor();
                binding.screenPlay.getBackground().setAlpha(230);
                int color2=((ColorDrawable) binding.screenPlay.getBackground()).getColor();
                int[] colors = {color2,color1};


                GradientDrawable gd = new GradientDrawable(
                        GradientDrawable.Orientation.BOTTOM_TOP, colors);

                binding.screenPlay.setBackground(gd);
                if(isColorDark(color2)) {
                    binding.playBtn.setColorFilter(Color.WHITE);
                    binding.nextBtn.setColorFilter(Color.WHITE);
                    binding.prevBtn.setColorFilter(Color.WHITE);
                    binding.randomBtn.setColorFilter(Color.WHITE);
                    binding.repeatBtn.setColorFilter(Color.WHITE);
                    binding.timeStart.setTextColor(Color.WHITE);
                    binding.timeEnd.setTextColor(Color.WHITE);
                    binding.songName.setTextColor(Color.WHITE);
                    binding.artistName.setTextColor(Color.WHITE);
                    binding.textPlay.setTextColor(Color.WHITE);
                    binding.favBtn.setColorFilter(Color.WHITE);
                    binding.closePlayer.setColorFilter(Color.WHITE);
                    binding.seekBar.getThumb().setColorFilter(getResources().getColor(R.color.gray), PorterDuff.Mode.SRC_IN);
                    binding.seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
                }else {
                    binding.playBtn.setColorFilter(Color.BLACK);
                    binding.nextBtn.setColorFilter(Color.BLACK);
                    binding.prevBtn.setColorFilter(Color.BLACK);
                    binding.randomBtn.setColorFilter(Color.BLACK);
                    binding.repeatBtn.setColorFilter(Color.BLACK);
                    binding.timeStart.setTextColor(Color.BLACK);
                    binding.timeEnd.setTextColor(Color.BLACK);
                    binding.songName.setTextColor(Color.BLACK);
                    binding.artistName.setTextColor(Color.BLACK);
                    binding.textPlay.setTextColor(Color.BLACK);
                    binding.favBtn.setColorFilter(Color.BLACK);
                    binding.closePlayer.setColorFilter(Color.BLACK);
                    binding.seekBar.getThumb().setColorFilter(getResources().getColor(R.color.gray), PorterDuff.Mode.SRC_IN);
                    binding.seekBar.getThumb().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.MULTIPLY);
                    binding.seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.MULTIPLY);
                }
            }
        });
    }

    //Ki???m tra ph??n bi???t m??u s??ng hay t???i
    public boolean isColorDark(int color){
        return ColorUtils.calculateLuminance(color) < 0.35;
    }

    //H??m ki???m tra b??i h??t ??ang ph??t ???????c y??u th??ch hay kh??ng v?? x??? l?? s??? ki???n khi nh???n v??o nsut y??u th??ch
    private void favoriteSong(){
        ArrayList<Integer> IdFavSong= (ArrayList<Integer>) FavoriteDatabase.getInstance(this).favoriteDAO().getListIdSong();
        if (!IdFavSong.isEmpty()){
            if(IdFavSong.contains(myListSong.get(position).getId_song())){
                binding.favBtn.setImageResource(R.drawable.ic_favorite);
                fav=true;
            }
            else {
                binding.favBtn.setImageResource(R.drawable.ic_favorite_border);
                fav=false;
            }
        }
        binding.favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fav){
                    FavoriteObject favoriteObject=FavoriteDatabase.getInstance(PlayerSong.this).favoriteDAO().getMyFavSongByID
                            (myListSong.get(position).getId_song());
                    FavoriteDatabase.getInstance(PlayerSong.this).favoriteDAO().deleteSong(favoriteObject);
                    binding.favBtn.setImageResource(R.drawable.ic_favorite_border);
                    fav=false;
                    Toast.makeText(PlayerSong.this,"???? x??a kh???i danh s??ch y??u th??ch!",Toast.LENGTH_SHORT).show();
                    MyMediaPlayer.getInstance().setCheckUpdateFavorite(true);
                    if(MyMediaPlayer.getInstance().isCheckFavSong()){
                        MyMediaPlayer.getInstance().setCheckStopFavSong(true);
                        for(MySongObject mySongObject:myListSong){
                            if(mySongObject.getId_song()==MyMediaPlayer.getInstance().getIdSong()){
                                myListSong.remove(mySongObject);
                                break;
                            }
                        }
                        MyMediaPlayer.getInstance().setListPlaySong(myListSong);
                        MyMediaPlayer.getInstance().setPosition(0);
                        if(myListSong.size()==0) {
                            MyMediaPlayer.getInstance().setCheckFavSong(false);
                        }
                        finish();
                        overridePendingTransition(0, R.anim.slide_down_out);
                    }
                }
                else {
                    String nameSong=myListSong.get(position).getNameSong();
                    String nameArtist=myListSong.get(position).getNameArtist();
                    byte[] imgSong=myListSong.get(position).getImageSong();
                    String uriSong=myListSong.get(position).getLinkSong();
                    int IdSong=myListSong.get(position).getId_song();
                    FavoriteObject favoriteObject;
                    favoriteObject=new FavoriteObject(nameSong,nameArtist,imgSong,uriSong,IdSong);
                    FavoriteDatabase.getInstance(PlayerSong.this).favoriteDAO().insertSong(favoriteObject);
                    binding.favBtn.setImageResource(R.drawable.ic_favorite);
                    fav=true;
                    Toast.makeText(PlayerSong.this,"???? th??m v??o danh s??ch y??u th??ch!",Toast.LENGTH_SHORT).show();
                    MyMediaPlayer.getInstance().setCheckUpdateFavorite(true);
                }
            }
        });
    }

    //Hi???u ???ng ???nh chuy???n ?????ng quay tr??n
    public void imageAnimation(){
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(10000);
        rotateAnimation.setRepeatCount(Animation.INFINITE);

        binding.imageSong.startAnimation(rotateAnimation);

    }

    //H??m t??nh to??n th???i gian b???t ?????u v?? k???t th??c c???a b??i h??t
    public String createTime(int duration){
        String time="";
        int min=duration/1000/60;
        int sec=duration/1000%60;
        time=time+min+":";
        if(sec<10){
            time+=0;
        }
        time+=sec;
        return time;
    }

    //H??m thi???t l???p v?? qu???n l?? thanh Seekbar khi nh???c ??ang ch??i
    private void Seekbar(){
        //set ????? r???ng thanh seekbar theo t???ng th???i gian c???a b??i h??t
        binding.seekBar.setMax(MyMediaPlayer.getInstance().getMediaPlayer().getDuration());
        updateSeekbar.start();
        //B???t s??? ki???n khi nh???n v??o thanh Seekbar thay ?????i
        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //Set th???i gian b???t ?????u b??i h??t s??? ???????c c???p nh???t li??n t???c d???a theo ti???n tr??nh thay ?????i c???a thanh Seekbar
                binding.timeStart.setText(createTime(MyMediaPlayer.getInstance().getMediaPlayer().getCurrentPosition()));
                if(b){
                    MyMediaPlayer.getInstance().getMediaPlayer().seekTo(i);
                    seekBar.setProgress(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MyMediaPlayer.getInstance().getMediaPlayer().seekTo(seekBar.getProgress());
            }
        });
    }

    //H??m th???c hi???n chuy???n ph??t b??i h??t ti???p theo
    private void nextSong(){
        if(!MediaOnline.getInstance().isCheckStop()){
            MediaOnline.getInstance().stopPlaySong();
        }
        MyMediaPlayer.getInstance().stopAudioFile();
        MyMediaPlayer.getInstance().setStopMedia();
        binding.playBtn.setImageResource(R.drawable.icon_pause);
        imageAnimation();
        binding.songName.setSelected(true);
        position=((position+1)%myListSong.size());
        MyMediaPlayer.getInstance().playAudioFile(getApplicationContext(),myListSong.get(position).getLinkSong(),position);
        MyMediaPlayer.getInstance().Start();
        favoriteSong();
        binding.seekBar.setMax(MyMediaPlayer.getInstance().getMediaPlayer().getDuration());
        mediaComplete();
        binding.timeEnd.setText(createTime(MyMediaPlayer.getInstance().getMediaPlayer().getDuration()));
        binding.songName.setText(myListSong.get(position).getNameSong());
        binding.artistName.setText(myListSong.get(position).getNameArtist());
        bitmap = BitmapFactory.decodeByteArray(myListSong.get(position).getImageSong(),
                0,myListSong.get(position).getImageSong().length);
        binding.imageSong.setImageBitmap(bitmap);
        ScreenBackground();
    }

    //H??m th???c hi???n chuy???n ph??t b??i h??t tr?????c ????
    private void previousSong(){
        if(!MediaOnline.getInstance().isCheckStop()){
            MediaOnline.getInstance().stopPlaySong();
        }
        MyMediaPlayer.getInstance().stopAudioFile();
        MyMediaPlayer.getInstance().setStopMedia();
        binding.playBtn.setImageResource(R.drawable.icon_pause);
        imageAnimation();
        position=((position-1)<0?(myListSong.size()-1):position-1);
        MyMediaPlayer.getInstance().playAudioFile(getApplicationContext(),myListSong.get(position).getLinkSong(),position);
        MyMediaPlayer.getInstance().Start();
        favoriteSong();
        binding.seekBar.setMax(MyMediaPlayer.getInstance().getMediaPlayer().getDuration());
        mediaComplete();
        binding.timeEnd.setText(createTime(MyMediaPlayer.getInstance().getMediaPlayer().getDuration()));
        binding.songName.setSelected(true);
        binding.songName.setText(myListSong.get(position).getNameSong());
        binding.artistName.setText(myListSong.get(position).getNameArtist());
        bitmap = BitmapFactory.decodeByteArray(myListSong.get(position).getImageSong(),
                0,myListSong.get(position).getImageSong().length);
        binding.imageSong.setImageBitmap(bitmap);
        ScreenBackground();
    }

    //H??m th???c hi???n chuy???n ph??t b??i h??t l???p l???i
    private void repeatSong(){
        if(!MediaOnline.getInstance().isCheckStop()){
            MediaOnline.getInstance().stopPlaySong();
        }
        MyMediaPlayer.getInstance().stopAudioFile();
        MyMediaPlayer.getInstance().setStopMedia();
        MyMediaPlayer.getInstance().playAudioFile(getApplicationContext(),myListSong.get(position).getLinkSong(),position);
        MyMediaPlayer.getInstance().Start();
        binding.playBtn.setImageResource(R.drawable.icon_pause);
        imageAnimation();
        favoriteSong();
        binding.seekBar.setMax(MyMediaPlayer.getInstance().getMediaPlayer().getDuration());
        mediaComplete();
        binding.timeEnd.setText(createTime(MyMediaPlayer.getInstance().getMediaPlayer().getDuration()));
        binding.songName.setSelected(true);
    }

    //H??m th???c hi???n chuy???n ph??t b??i h??t ng???u nhi??n
    private void RandomPlay(){
        if(!MediaOnline.getInstance().isCheckStop()){
            MediaOnline.getInstance().stopPlaySong();
        }
        MyMediaPlayer.getInstance().stopAudioFile();
        MyMediaPlayer.getInstance().setStopMedia();
        Random random=new Random();
        int valueRandom=0;
        do{
            valueRandom=random.nextInt(myListSong.size());
        }while(valueRandom==position);
        position=valueRandom;
        MyMediaPlayer.getInstance().playAudioFile(getApplicationContext(),myListSong.get(position).getLinkSong(),position);
        MyMediaPlayer.getInstance().Start();
        binding.playBtn.setImageResource(R.drawable.icon_pause);
        imageAnimation();
        favoriteSong();
        binding.seekBar.setMax(MyMediaPlayer.getInstance().getMediaPlayer().getDuration());
        mediaComplete();
        binding.timeEnd.setText(createTime(MyMediaPlayer.getInstance().getMediaPlayer().getDuration()));
        binding.songName.setSelected(true);
        binding.songName.setText(myListSong.get(position).getNameSong());
        binding.artistName.setText(myListSong.get(position).getNameArtist());
        bitmap = BitmapFactory.decodeByteArray(myListSong.get(position).getImageSong(),
                0,myListSong.get(position).getImageSong().length);
        binding.imageSong.setImageBitmap(bitmap);
        ScreenBackground();
    }

    //H??m x??? l?? chuy???n ph??t b??i h??t ti???p theo khi b??i h??t hi???n t???i ???? ho??n th??nh
    public void mediaComplete(){
        MyMediaPlayer.getInstance().getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                if(MyMediaPlayer.getInstance().isCheckRepeat()){
                    repeatSong();
                    return;
                }
                else if(MyMediaPlayer.getInstance().isCheckRandom()){
                    RandomPlay();
                    return;
                }
                else binding.nextBtn.performClick();
            }
        });
    }

    //H??m s???p x???p danh s??ch ph??t nh???c theo th??? t???
    public void Arrange(ArrayList<MySongObject> myListSong){
        Collections.sort(myListSong, new Comparator<MySongObject>() {
            @Override
            public int compare(MySongObject mySongObject, MySongObject t1) {
                return mySongObject.getNameSong().compareToIgnoreCase(t1.getNameSong());
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent();
        intent.putExtra("pos",position);
        setResult(23,intent);
        finish();
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_down_in, R.anim.slide_down_out);
    }
}