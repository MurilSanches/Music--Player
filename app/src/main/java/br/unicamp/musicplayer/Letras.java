package br.unicamp.musicplayer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.musicplayer.R;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Letras extends AppCompatActivity {

    private TextView tvLetras, tvMusicas, tvFila, tvLetra, tvUsuario, tvArtista, tvTitulo;
    private ArrayList<String> musicFile, artists, titles, songs, albuns, f;
    private LinkedList<String> fila;
    private String currentArt, currentSong;
    private int currentPosition;
    private Thread updateSeekBar;
    private MediaPlayer mp;
    private SeekBar seekbar;
    private ImageView ivAlbum, ivPause;
    private String className;
    private String STATUS;
    private Usuario u;
    private GoogleSignInAccount account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letras);

        try {
            tvFila = findViewById(R.id.tvFila);
            tvMusicas = findViewById(R.id.tvMusicas);
            tvUsuario = findViewById(R.id.tvUsuario);
            ivAlbum = findViewById(R.id.ivImagemMusica);
            ivPause = findViewById(R.id.ivPause);

            fila = new LinkedList<>();

            tvLetras = (TextView) findViewById(R.id.tvLetra);
            SpannableString content = new SpannableString("Letra");
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            tvLetras.setText(content);

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
                    if (mp != null)
                        mp.seekTo(seekBar.getProgress());
                }
            });

            tvTitulo = findViewById(R.id.tvTituloMusica);
            tvArtista = findViewById(R.id.tvArtista);

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

            tvMusicas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Letras.this, Musicas.class)
                            .putExtra("status", STATUS).putExtra("fila", f).putExtra("activity_name", this.getClass().getName());
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

            tvFila.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Letras.this, Fila.class)
                            .putExtra("status", STATUS).putExtra("fila", f)
                            .putExtra("musicFile", musicFile).putExtra("art", currentArt)
                            .putExtra("titles", titles).putExtra("albuns", albuns)
                            .putExtra("songs", songs).putExtra("activity_name", this.getClass().getName())
                            .putExtra("pos", currentPosition);
                    if (u == null)
                    {
                        i.putExtra("acc", account);
                    }
                    else
                        i.putExtra("user", u);
                    if(mp != null && mp.isPlaying()) {
                        i.putExtra("mus", currentSong).putExtra("arts", artists);
                    }

                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });

            tvLetra = findViewById(R.id.tvLetraDaMusica);
            tvLetra.setMovementMethod(new ScrollingMovementMethod());


            if(!b.isEmpty()){

                    String art = b.getString("art");
                    String mus = b.getString("mus");

                    f = b.getStringArrayList("fila");
                    for(int j =0; j < f.size(); j++)
                        fila.add(f.get(j));

                    songs = b.getStringArrayList("songs");
                    albuns = b.getStringArrayList("albuns");
                    titles = b.getStringArrayList("titles");
                    artists = b.getStringArrayList("arts");
                    musicFile = b.getStringArrayList("musicFile");
                    STATUS = b.getString("status");

                    int duration = b.getInt("duration");
                    int pos = b.getInt("pos");

                    if(!STATUS.equals("NO MUSIC")){
                        pesquisarMusica(art, mus);
                        //tocarMusica(songs, albuns, titles, artists, duration, pos);
                    }
                    else
                        tvLetra.setText("Nenhuma musica tocando");
            }


            ImageView pause = findViewById(R.id.ivPause);

            pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mp != null) {
                        ImageView iv = findViewById(R.id.ivPause);
                        if (mp.isPlaying()) {
                            mp.pause();
                            iv.setImageResource(R.drawable.play);
                        } else {
                            mp.start();
                            iv.setImageResource(R.drawable.pause);
                        }
                    }
                }
            });
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void tocarMusica(ArrayList<String> songs, ArrayList<String> albumArt, ArrayList<String> titles, ArrayList<String> artists, int duration, int position){
        try {
            if (mp != null)
                if (mp.isPlaying()) {
                    mp.stop();
                    mp.release();
                }

            Uri u = Uri.parse(musicFile.get(position));
            ivPause.setImageResource(R.drawable.pause);
            if (albuns.get(position) != "") {
                File imgfile = new File(albuns.get(position));
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

    private void pesquisarMusica(String art, String mus)
    {
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "https://api.vagalume.com.br/search.php?art="+art+"&mus="+mus+"&apikey={key}";


        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();

                            String type = jsonObject.get("type").getAsString();

                            if(type.equals("exact") || type.equals("aprox")) {
                                JsonObject mus = (JsonObject) jsonObject.getAsJsonArray("mus").get(0);

                                tvLetra.setText(mus.get("text").getAsString());
                            }
                            else
                            {
                                tvLetra.setText("Musica não encontrada");
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                tvLetra.setText("Musica não encontrada");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    @Override
    public void finish()
    {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
