package br.unicamp.musicplayer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplayer.R;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Fila extends AppCompatActivity {

    private TextView tvLetras, tvMusicas, tvFila, tvUsuario;
    private ListView lvFila;
    private LinkedList<String> fila = new LinkedList<>();
    private SeekBar seekBar;
    private String STATUS;
    private MediaPlayer mp;
    private String currentArt, currentSong;
    private ImageView ivPause, ivAlbum;
    private Thread updateSeekBar;
    private ArrayList<String> artistas, titles, musicFilesList, albunsArt, musicas, f;
    private String className;
    private Usuario u;
    private GoogleSignInAccount account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fila);

        try {
            tvLetras = findViewById(R.id.tvLetra);
            tvMusicas = findViewById(R.id.tvMusicas);
            tvUsuario = findViewById(R.id.tvUsuario);

            lvFila = findViewById(R.id.lvFila);
            ivPause = findViewById(R.id.ivPause);
            ivAlbum = findViewById(R.id.ivImagemMusica);

            seekBar = findViewById(R.id.sbAudio);
            seekBar.setVisibility(View.VISIBLE);
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (mp != null)
                        mp.seekTo(seekBar.getProgress());
                }
            });

            tvFila = (TextView) findViewById(R.id.tvFila);
            SpannableString content = new SpannableString("Fila");
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            tvFila.setText(content);

            Intent i = getIntent();
            Bundle b = i.getExtras();

            className = b.getString("activity_name");

            if(b.get("acc") != null) {
                account = (GoogleSignInAccount) b.get("acc");
                tvUsuario.setText("Bem  vindo, " + account.getDisplayName());
            }
            else {
                u = (Usuario) b.get("user");
                tvUsuario.setText("Bem vindo, " + u.getNome());
            }


            f = b.getStringArrayList("fila");
            if (f.size() > 0) {

                for (int j = 0; j < f.size(); j++)
                    fila.addLast(f.get(j));
                FilaAdapter adapter = new FilaAdapter(this, fila);
                lvFila.setAdapter(adapter);
            }

            if (!b.isEmpty()) {

                if (className.equals("br.unicamp.musicplayer.Musicas$3")) {
                    musicas = b.getStringArrayList("songs");
                    titles = b.getStringArrayList("titles");
                    artistas = b.getStringArrayList("arts");
                    albunsArt = b.getStringArrayList("albuns");
                    musicFilesList = b.getStringArrayList("musicFile");
                    STATUS = b.getString("status");
                    if (!STATUS.equals("NO MUSIC")) {
                        currentArt = b.getString("art");
                        currentSong = b.getString("mus");
                    }
                } else {

                }
            }


            tvMusicas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Fila.this, Musicas.class)
                            .putExtra("fila", f)
                            .putExtra("activity_name", this.getClass().getName());
                    if (u == null)
                    {
                        i.putExtra("acc", account);
                    }
                    else
                        i.putExtra("user", u);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                }
            });

            tvLetras.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Fila.this, Letras.class)
                            .putExtra("status", STATUS).putExtra("fila", f)
                            .putExtra("musicFile", musicFilesList).putExtra("art", currentArt)
                            .putExtra("titles", titles).putExtra("albuns", albunsArt)
                            .putExtra("songs", musicas).putExtra("activity_name", this.getClass().getName());
                    if (u == null)
                    {
                        i.putExtra("acc", account);
                    }
                    else
                        i.putExtra("user", u);
                    if(mp != null && mp.isPlaying()) {
                        i.putExtra("mus", currentSong).putExtra("arts", artistas);
                    }
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                }
            });

            ivPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (mp != null) {
                            ImageView iv = findViewById(R.id.ivPause);
                            if (mp.isPlaying()) {
                                Toast.makeText(getBaseContext(), "Musica Pausada", Toast.LENGTH_SHORT).show();
                                mp.pause();
                                STATUS = "PAUSE";
                                iv.setImageResource(R.drawable.play);
                            } else {
                                mp.start();
                                Toast.makeText(getBaseContext(), "Tocando Musica", Toast.LENGTH_SHORT).show();
                                STATUS = "PLAY";
                                iv.setImageResource(R.drawable.pause);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
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
                                    seekBar.setProgress(currentPosition);
                                    if (!mp.isPlaying() && !STATUS.equals("PAUSE") && currentPosition == lastPosition)
                                        break;
                                    lastPosition = currentPosition;
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            seekBar.setProgress(0);
                            if (fila.size() > 0) {
                                String song = fila.remove();

                                int pos = getMusicPosition(song);
                                playMusic(pos);
                            }
                        }
                        while (mp.isPlaying());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            lvFila.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    final ImageView ivRemove = view.findViewById(R.id.ivRemove);
                    ivRemove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            fila.remove(position);
                            Toast.makeText(getBaseContext(), "Música removida da lista", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
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

    public void playMusic(int position){
        try {
            if (mp != null)
                if (mp.isPlaying()) {
                    mp.stop();
                    mp.release();
                }

            Uri u = Uri.parse(musicFilesList.get(position));
            ivPause.setImageResource(R.drawable.pause);
            if (albunsArt.get(position) != "") {
                File imgfile = new File(albunsArt.get(position));
                Bitmap myBitmap = BitmapFactory.decodeFile(imgfile.getAbsolutePath());

                ivAlbum.setImageBitmap(myBitmap);
            }
            currentArt = artistas.get(position);
            currentSong = titles.get(position);

            mp = MediaPlayer.create(getApplicationContext(), u);
            mp.start();
            seekBar.setMax(mp.getDuration());
            if(updateSeekBar.getState().toString().equals("NEW"))
                updateSeekBar.start();
            STATUS = "PLAY";
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void finish()
    {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
