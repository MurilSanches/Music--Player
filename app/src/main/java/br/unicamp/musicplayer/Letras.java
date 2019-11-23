package br.unicamp.musicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.musicplayer.R;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Letras extends AppCompatActivity {
    TextView tvLetras,
             tvMusicas,
             tvFila,
             tvLetra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letras);

        tvFila = findViewById(R.id.tvFila);
        tvMusicas = findViewById(R.id.tvMusicas);

        tvLetras = (TextView) findViewById(R.id.tvLetra);
        SpannableString content = new SpannableString("Letras");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tvLetras.setText(content);

        tvMusicas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Letras.this, Musicas.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        tvFila.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Letras.this, Fila.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        tvLetra = findViewById(R.id.tvLetraDaMusica);
        tvLetra.setMovementMethod(new ScrollingMovementMethod());

        Intent i = getIntent();
        Bundle b = i.getExtras();

        String art = b.getString("art");
        String mus = b.getString("mus");

        pesquisarMusica(art, mus);
    }

    public void pesquisarMusica(String art, String mus)
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
