package br.unicamp.musicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplayer.R;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class Fila extends AppCompatActivity {

    private TextView tvLetras, tvMusicas, tvFila, tvUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fila);

        tvLetras = findViewById(R.id.tvLetra);
        tvMusicas = findViewById(R.id.tvMusicas);
        tvUsuario = findViewById(R.id.tvUsuario);

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
    }

    @Override
    public void finish()
    {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
