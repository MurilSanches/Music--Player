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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.musicplayer.R;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.r0adkll.slidr.model.SlidrInterface;

import java.io.File;
import java.util.ArrayList;

public class Musicas extends AppCompatActivity {

    private static final int MY_PERMISSION_REQUEST = 1;
    private ListView lvMusicas;
    private ArrayList<String> musicas, titulo, artista, musicFilesList, albumArt, fila;
    private String currentArt, currentSong;
    private TextView tvLetras, tvMusicas, tvFila, tvUsuario, tvTitulo, tvArtista;
    private int currentPosition, proxima = 0;
    private SeekBar seekbar;
    private MediaPlayer mp;
    private ImageView ivAlbum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musicas);

        titulo  = new ArrayList<>();
        artista = new ArrayList<>();
        musicFilesList = new ArrayList<>();
        albumArt = new ArrayList<>();
        fila = new ArrayList<>();

        ivAlbum = findViewById(R.id.ivImagemMusica);

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
                Intent i = new Intent(Musicas.this, Letras.class);
                if(mp != null && mp.isPlaying()) {
                    mp.pause();
                    i.putExtra("art", currentArt).putExtra("mus", currentSong)
                            .putExtra("songs", musicFilesList).putExtra("duration", mp.getDuration())
                            .putExtra("pos", currentPosition).putExtra("albuns", albumArt)
                            .putExtra("titles", titulo).putExtra("artists", artista);
                }
                i.putExtra("acc", account);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        tvFila.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Musicas.this, Fila.class).putExtra("acc", account)
                        .putExtra("fila", fila);
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
        MusicaAdapter adapter = new MusicaAdapter(this, musicas);
        lvMusicas.setAdapter(adapter);

        lvMusicas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final ImageView ivAdd = view.findViewById(R.id.ivAdiciona);
                ivAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getBaseContext(), "Musica adiciona na fila", Toast.LENGTH_SHORT).show();
                        fila.add(musicas.get(position));
                    }
                });

                final LinearLayout llTitulo = view.findViewById(R.id.llTitulo);
                llTitulo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playMusic(position);
                    }
                });
            }
        });
    }

    public void playMusic(int position){
        try {
            if (mp != null)
                if (mp.isPlaying())
                    mp.pause();

            Uri u = Uri.parse(musicFilesList.get(position));
            if (albumArt.get(position) != "") {
                File imgfile = new File(albumArt.get(position));
                Bitmap myBitmap = BitmapFactory.decodeFile(imgfile.getAbsolutePath());

                ivAlbum.setImageBitmap(myBitmap);
            }
            seekbar.setVisibility(View.VISIBLE);

            Thread updateSeekBar = new Thread() {
                @Override
                public void run() {
                    try {
                        int totalDuration = mp.getDuration();
                        int currentPosition = 0;
                        while (currentPosition < totalDuration) {
                            try {
                                sleep(500);
                                currentPosition = mp.getCurrentPosition();
                                seekbar.setProgress(currentPosition);
                            } catch (InterruptedException e) {

                            }
                        }
                        seekbar.setProgress(0);
                        if (currentPosition >= totalDuration && fila.size() > 0) {
                            String song = fila.get(proxima);
                            fila.remove(proxima);
                            proxima++;
                            int pos = getMusicPosition(song);
                            playMusic(pos);
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            };
            tvTitulo.setText(titulo.get(position));
            tvArtista.setText(artista.get(position));

            currentPosition = position;
            currentArt = artista.get(position);
            currentSong = titulo.get(position);

            mp = MediaPlayer.create(getApplicationContext(), u);
            mp.start();
            seekbar.setMax(mp.getDuration());
            updateSeekBar.start();
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
