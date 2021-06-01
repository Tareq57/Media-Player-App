package com.example.imusicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.textclassifier.ConversationActions;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PlaySong extends AppCompatActivity {
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        thread.interrupt();
    }

    TextView song_name,song_position,song_duration;
    ImageView play,pause,next,previous;
    SeekBar seekBar;
    ArrayList<File>song;
    MediaPlayer mediaPlayer;
    String songName;
    int pos;
    Thread thread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);
        song_name=findViewById(R.id.textView);
        previous=findViewById(R.id.imageView5);
        play=findViewById(R.id.imageView6);
        next=findViewById(R.id.imageView7);
        seekBar=findViewById(R.id.seekBar);
        song_position=findViewById(R.id.textView2);
        song_duration=findViewById(R.id.textView3);
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        song= (ArrayList)bundle.getParcelableArrayList("Songs");
        songName=intent.getStringExtra("currentSong");
        song_name.setText(songName);
        song_name.setSelected(true);
        pos=intent.getIntExtra("position",0);
        intialize_music_player(pos);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying())
                {
                    play.setImageResource(R.drawable.play);
                    mediaPlayer.pause();
                }
                else
                {
                    play.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                }

            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previous();
            }


        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });


    }
     public  void intialize_music_player(int position){
        if(mediaPlayer!=null&&mediaPlayer.isPlaying())
        {
            mediaPlayer.reset();
        }
        String name=song.get(position).getName();
        song_name.setText(name);
        Uri uri=Uri.parse(song.get(position).toString());
        mediaPlayer=MediaPlayer.create(this,uri);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        int poss=mediaPlayer.getCurrentPosition();
        int dur=mediaPlayer.getDuration();
        song_position.setText(Integer.toString(poss));
        song_duration.setText(convert(dur));

        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser)
                {
                    mediaPlayer.start();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
             


            }
        });
         thread=new Thread(){
             int curr_pos=0;
             @Override
             public void run() {
                 try{
                     while (curr_pos<mediaPlayer.getDuration())
                     {
                         curr_pos=mediaPlayer.getCurrentPosition();
                         seekBar.setProgress(curr_pos);
                         Message msg=new Message();
                         msg.obj=convert(curr_pos);
                         mHandler.sendMessage(msg);
                         sleep(1000);


                     }
                 }
                 catch (Exception e){
                     e.printStackTrace();
                 }
             }

         };
         thread.start();

     }
     //previous button
    public  void  previous(){

        if(pos==0)
            pos=song.size()-1;
        else
            pos--;
        intialize_music_player(pos);

    }
    //next button
    public  void next(){
        if(pos==song.size()-1)
            pos=0;
        else
            pos++;
        intialize_music_player(pos);
    }
    public  String convert(int time)
    {
        return  String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(time),
                TimeUnit.MILLISECONDS.toSeconds(time)-
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
    }
    @SuppressLint("HandlerLeak")
    protected final Handler mHandler = new Handler() {

        @Override public void handleMessage(Message msg) {

            String text = (String)msg.obj;

            song_position.setText(text);
        }
    };


}