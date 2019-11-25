package br.unicamp.musicplayer;

import android.content.Intent;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplayer.R;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Fila extends AppCompatActivity {

    private TextView tvLetras, tvMusicas, tvFila, tvUsuario;
    private ListView lvMusicas;
    private LinkedList<String> fila;
    private SeekBar seekBar;
    private String STATUS;
    private MediaPlayer mp;
    private Thread updateSeekbar;
    private String currentArt, currentSong;
    private ImageView ivPause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fila);

        tvLetras = findViewById(R.id.tvLetra);
        tvMusicas = findViewById(R.id.tvMusicas);
        tvUsuario = findViewById(R.id.tvUsuario);
        lvMusicas = findViewById(R.id.lvMusicas);
        ivPause = findViewById(R.id.ivPause);

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
                if(mp != null)
                    mp.seekTo(seekBar.getProgress());
            }
        });

        tvFila = (TextView) findViewById(R.id.tvFila);
        SpannableString content = new SpannableString("Fila");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tvFila.setText(content);

        Intent i = getIntent();
        Bundle b = i.getExtras();

        final GoogleSignInAccount account = (GoogleSignInAccount)b.get("acc");
        tvUsuario.setText("Bem vindo, " + account.getDisplayName());

        tvMusicas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent i = new Intent(Fila.this, Musicas.class).putExtra("acc", account);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        tvLetras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Fila.this, Letras.class).putExtra("acc", account);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        fila = (LinkedList<String>) b.get("fila");
        FilaAdapter adapter = new FilaAdapter(this, fila);
        lvMusicas.setAdapter(adapter);

        currentArt = b.getString("art");
        currentSong = b.getString("mus");
        mp = b.getParcelable("media");
        STATUS = b.getString("status");

        ivPause.setOnClickListener(new View.OnClickListener() {
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
    }

    @Override
    public void finish()
    {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
