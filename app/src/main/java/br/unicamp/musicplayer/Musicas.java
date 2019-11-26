package br.unicamp.musicplayer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.musicplayer.R;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.r0adkll.slidr.model.SlidrInterface;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Musicas extends AppCompatActivity {

    private static final int MY_PERMISSION_REQUEST = 1;
    private ListView lvMusicas;
    private ArrayList<String> musicas, titles, artists, musicFilesList, albunsArt;
    private LinkedList<String> fila;
    private String currentArt, currentSong;
    private TextView tvLetras, tvMusicas, tvFila, tvUsuario, tvTitulo, tvArtista;
    private SeekBar seekbar;
    private MediaPlayer mp;
    private ImageView ivAlbum;
    private Thread updateSeekBar;
    private ImageView pause;
    private String STATUS;
    private int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musicas);

        titles  = new ArrayList<>();
        artists = new ArrayList<>();
        musicFilesList = new ArrayList<>();
        albunsArt = new ArrayList<>();
        fila = new LinkedList<>();

        pause = findViewById(R.id.ivPause);
        ivAlbum = findViewById(R.id.ivImagemMusica);

        seekbar = findViewById(R.id.sbAudio);
        seekbar.setVisibility(View.VISIBLE);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(mp != null)
                    mp.seekTo(seekBar.getProgress());
            }
        });

        tvTitulo = findViewById(R.id.tvTituloMusica );
        tvArtista = findViewById(R.id.tvArtista);
        lvMusicas = findViewById(R.id.lvMusicas);
        tvFila = findViewById(R.id.tvFila);
        tvLetras = findViewById(R.id.tvLetra);
        tvUsuario = findViewById(R.id.tvUsuario);

        tvMusicas = (TextView) findViewById(R.id.tvMusicas);
        SpannableString content = new SpannableString("Musicas");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tvMusicas.setText(content);

        Intent i = getIntent();
        Bundle b = i.getExtras();

        final GoogleSignInAccount account =  (GoogleSignInAccount) b.get("acc");
        tvUsuario.setText("Bem  vindo, " + account.getDisplayName());

        tvLetras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> f = new ArrayList<>();
                while (fila.size()>0)
                    f.add(fila.remove());

                Intent i = new Intent(Musicas.this, Fila.class).putExtra("acc", account)
                        .putExtra("status", STATUS).putExtra("fila", f)
                        .putExtra("musicFile", musicFilesList).putExtra("art", currentArt)
                        .putExtra("titles", titles).putExtra("albuns", albunsArt)
                        .putExtra("songs", musicas).putExtra("activity_name", this.getClass().getName())
                        .putExtra("duration", mp.getDuration()).putExtra("pos", currentPosition);
                if(mp != null && mp.isPlaying()) {
                    i.putExtra("mus", currentSong).putExtra("arts", artists);
                }
                i.putExtra("acc", account);
                startActivityForResult(i, 2);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        tvFila.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> f = new ArrayList<>();
                while (fila.size()>0)
                    f.add(fila.remove());

                Intent i = new Intent(Musicas.this, Fila.class).putExtra("acc", account)
                        .putExtra("status", STATUS).putExtra("fila", f)
                        .putExtra("musicFile", musicFilesList).putExtra("art", currentArt)
                        .putExtra("titles", titles).putExtra("albuns", albunsArt)
                        .putExtra("songs", musicas).putExtra("activity_name", this.getClass().getName());
                if(mp != null && mp.isPlaying()) {
                    i.putExtra("mus", currentSong).putExtra("arts", artists);
                }
                startActivityForResult(i, 1);
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

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mp != null) {
                    ImageView iv = findViewById(R.id.ivPause);
                    if(mp.isPlaying()) {
                        Toast.makeText(getBaseContext(), "Musica Pausada", Toast.LENGTH_SHORT).show();
                        mp.pause();
                        STATUS = "PAUSE";
                        iv.setImageResource(R.drawable.play);
                    }
                    else {
                        mp.start();
                        Toast.makeText(getBaseContext(), "Tocando Musica", Toast.LENGTH_SHORT).show();
                        STATUS = "PLAY";
                        iv.setImageResource(R.drawable.pause);
                    }
                }
            }
        });

        updateSeekBar = new Thread() {
            @Override
            public void run() {
                try {
                    do {

                        int totalDuration = mp.getDuration();
                        int currentPosition = 0, lastPosition = -1;

                        while (currentPosition < totalDuration) {
                            try {
                                sleep(500);
                                currentPosition = mp.getCurrentPosition();
                                seekbar.setProgress(currentPosition);
                                if(!mp.isPlaying() && !STATUS.equals("PAUSE") && currentPosition == lastPosition)
                                    break;
                                lastPosition = currentPosition;
                            }
                            catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        seekbar.setProgress(0);
                        if (fila.size() > 0) {
                            String song = fila.remove();

                            int pos = getMusicPosition(song);
                            playMusic(pos);
                        }
                    }
                    while (mp.isPlaying());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        };

        STATUS = "NO MUSIC";
    }



    public void doStuff()
    {
        musicas = new ArrayList<>();
        getMusic();
        MusicaAdapter adapter = new MusicaAdapter(this, musicas);
        lvMusicas.setAdapter(adapter);

        lvMusicas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final ImageView ivAdd = view.findViewById(R.id.ivAdiciona);
                ivAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getBaseContext(), "Musica adicionada na fila", Toast.LENGTH_SHORT).show();
                        fila.add(musicas.get(position));
                    }
                });

                final LinearLayout llTitulo = view.findViewById(R.id.llTitulo);
                llTitulo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getBaseContext(), "Tocando musica " + titles.get(position), Toast.LENGTH_SHORT).show();
                        tvArtista.setText(artists.get(position));
                        tvTitulo.setText(titles.get(position));
                        playMusic(position);
                    }
                });
            }
        });
    }

    public void playMusic(int position){
        try {
            if (mp != null)
                if (mp.isPlaying()) {
                    mp.stop();
                    mp.release();
                }

            Uri u = Uri.parse(musicFilesList.get(position));
            pause.setImageResource(R.drawable.pause);
            if (albunsArt.get(position) != "") {
                File imgfile = new File(albunsArt.get(position));
                Bitmap myBitmap = BitmapFactory.decodeFile(imgfile.getAbsolutePath());

                ivAlbum.setImageBitmap(myBitmap);
            }
            currentArt = artists.get(position);
            currentSong = titles.get(position);
            currentPosition = position;

            mp = MediaPlayer.create(getApplicationContext(), u);
            mp.start();
            seekbar.setMax(mp.getDuration());
            if(updateSeekBar.getState().toString().equals("NEW"))
                updateSeekBar.start();
            STATUS = "PLAY";
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public int getMusicPosition(String song)
    {
        for(int i = 0; i < musicas.size(); i++)
            if(song == musicas.get(i))
                return i;
        return -1;
    }

    public String getMusicFile(String song)
    {
        for(int i = 0; i < musicFilesList.size(); i++)
            if (song == musicas.get(i))
                return  musicFilesList.get(i);
        return "";
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
                albunsArt.add(currentAlbumArt);
                titles.add(currentTitle);
                artists.add(currentArtist);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1)
        {
            Bundle b = getIntent().getExtras();

            ArrayList<String> f = b.getStringArrayList("f");
            if(f.size()>0) {

                for (int j = 0; j < f.size(); j++)
                    fila.add(f.get(j));
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
