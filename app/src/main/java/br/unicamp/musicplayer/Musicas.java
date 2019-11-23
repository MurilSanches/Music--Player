package br.unicamp.musicplayer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.musicplayer.R;
import com.r0adkll.slidr.model.SlidrInterface;

import java.io.File;
import java.util.ArrayList;

public class Musicas extends AppCompatActivity {

    private static final int MY_PERMISSION_REQUEST = 1;

    ListView lvMusicas;
    String[] items;
    ArrayList<String> musicas, titulo = new ArrayList<>(), artista = new ArrayList<>();
    private SlidrInterface slidr;
    ArrayList<String> musicFilesList = new ArrayList<>();
    ArrayList<String> albumArt = new ArrayList<>();
    String currentArt, currentSong;

    TextView tvLetras,
            tvMusicas,
            tvFila;
    SeekBar seekbar;

    Thread updateSeekBar;

    MediaPlayer mp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musicas);

        seekbar = findViewById(R.id.sbAudio);

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mp.seekTo(seekBar.getProgress());

            }
        });

        lvMusicas = findViewById(R.id.lvMusicas);
        tvFila = findViewById(R.id.tvFila);
        tvLetras = findViewById(R.id.tvLetra);

        tvMusicas = (TextView) findViewById(R.id.tvMusicas);
        SpannableString content = new SpannableString("Musicas");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tvMusicas.setText(content);

        tvLetras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Musicas.this, Letras.class);
                i.putExtra("art", currentArt).putExtra("mus", currentSong);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        tvFila.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Musicas.this, Fila.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        if(ContextCompat.checkSelfPermission(Musicas.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(Musicas.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)){
                ActivityCompat.requestPermissions(Musicas.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            } else{
                ActivityCompat.requestPermissions(Musicas.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
        } else {
            doStuff();
        }

        ImageView pause = findViewById(R.id.ivPause);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mp != null) {
                    ImageView iv = findViewById(R.id.ivPause);
                    if(mp.isPlaying()) {
                        mp.pause();
                        iv.setImageResource(R.drawable.play);
                    }
                    else {
                        mp.start();
                        iv.setImageResource(R.drawable.pause);
                    }
                }
            }
        });

    }



    public void doStuff()
    {
        musicas = new ArrayList<>();
        getMusic();
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, musicas);
        lvMusicas.setAdapter(adapter);
        final ImageView ivAlbum = findViewById(R.id.ivImagemMusica);
        final TextView tvTitulo  = findViewById(R.id.tvTituloMusica);
        final TextView tvArtista = findViewById(R.id.tvArtista);

        lvMusicas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try{
                    if(mp != null)
                        if (mp.isPlaying())
                            mp.pause();

                    Uri u = Uri.parse(musicFilesList.get(position));
                    if(albumArt.get(position) != "")
                    {
                        File imgfile = new File(albumArt.get(position));
                        Bitmap myBitmap = BitmapFactory.decodeFile(imgfile.getAbsolutePath());

                        ivAlbum.setImageBitmap(myBitmap);
                    }
                    seekbar.setVisibility(View.VISIBLE);

                    updateSeekBar=new Thread(){
                        @Override
                        public void run(){
                            int totalDuration = mp.getDuration();
                            int currentPosition = 0;
                            while(currentPosition < totalDuration){
                                try{
                                    sleep(500);
                                    currentPosition=mp.getCurrentPosition();
                                    seekbar.setProgress(currentPosition);
                                }
                                catch (InterruptedException e){

                                }
                            }
                        }
                    };

                    tvArtista.setText(artista.get(position));
                    currentArt = artista.get(position);
                    currentSong = titulo.get(position);
                    tvTitulo.setText(titulo.get(position));
                    mp = MediaPlayer.create(getApplicationContext(), u);
                    mp.start();
                    seekbar.setMax(mp.getDuration());
                    updateSeekBar.start();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }



    public void getMusic(){
        ContentResolver contentResolver = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);

        if(songCursor != null && songCursor.moveToFirst())
        {
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songLocation = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int songAlbum = songCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);


            do{
                String currentTitle = songCursor.getString(songTitle);
                String currentArtist = songCursor.getString(songArtist);
                String currentLocation = songCursor.getString(songLocation);
                String currentAlbumArt = "";
                if(songAlbum != -1)
                    currentAlbumArt = songCursor.getString(songAlbum);
                musicas.add("Title: " + currentTitle + "\nArtist: " + currentArtist);
                musicFilesList.add(currentLocation);
                albumArt.add(currentAlbumArt);
                titulo.add(currentTitle);
                artista.add(currentArtist);
            }
            while (songCursor.moveToNext());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(Musicas.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();

                        doStuff();
                    }
                } else {
                    Toast.makeText(this, "No permission granted!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }

    @Override
    public void finish()
    {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}
