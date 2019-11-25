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

public class Letras extends AppCompatActivity {

    private TextView tvLetras, tvMusicas, tvFila, tvLetra, tvUsuario, tvArtista, tvTitulo;
    private Thread updateSeekBar;
    private MediaPlayer mp;
    private SeekBar seekbar;
    private ImageView ivAlbum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letras);

        try {
            tvFila = findViewById(R.id.tvFila);
            tvMusicas = findViewById(R.id.tvMusicas);
            tvUsuario = findViewById(R.id.tvUsuario);
            ivAlbum = findViewById(R.id.ivImagemMusica);

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

            final GoogleSignInAccount account = (GoogleSignInAccount) b.get("acc");
            tvUsuario.setText("Bem vindo, " + account.getDisplayName());

            tvMusicas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Letras.this, Musicas.class).putExtra("acc", account);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                }
            });

            tvFila.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Letras.this, Fila.class).putExtra("acc", account);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });

            tvLetra = findViewById(R.id.tvLetraDaMusica);
            tvLetra.setMovementMethod(new ScrollingMovementMethod());

            if (b.getString("art") != null && !b.getString("art").equals("") &&
                    b.getString("mus") != null && !b.getString("mus").equals("")) {

                String art = b.getString("art");
                String mus = b.getString("mus");

                ArrayList<String> songs = b.getStringArrayList("songs");
                ArrayList<String> albuns = b.getStringArrayList("albuns");
                ArrayList<String> titles = b.getStringArrayList("titles");
                ArrayList<String> artists = b.getStringArrayList("artists");

                int duration = b.getInt("duration");
                int pos = b.getInt("pos");

                pesquisarMusica(art, mus);
                tocarMusica(songs, albuns, titles, artists, duration, pos);
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

    private void tocarMusica(ArrayList<String> songs, ArrayList<String> albumArt, ArrayList<String> titles, ArrayList<String> artists,int duration, int position){
        Uri u = Uri.parse(songs.get(position));
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

        tvTitulo.setText(titles.get(position));
        tvArtista.setText(artists.get(position));

        mp = MediaPlayer.create(getApplicationContext(), u);
        mp.start();
        mp.seekTo(duration);
        seekbar.setMax(mp.getDuration());
        updateSeekBar.start();
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
