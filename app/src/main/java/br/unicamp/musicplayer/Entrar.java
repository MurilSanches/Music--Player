package br.unicamp.musicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplayer.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Entrar extends AppCompatActivity {

    private TextInputLayout tilEmail, tilSenha;
    private Button btnLogar;
    private br.unicamp.musicplayer.LinkTextView ltvLink;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener  mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrar);

        tilEmail = findViewById(R.id.text_input_email);
        tilSenha = findViewById(R.id.text_input_password);

        btnLogar = findViewById(R.id.btnEntrar);
        ltvLink = findViewById(R.id.ltvLink);

        ltvLink.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent i = new Intent(Entrar.this, Cadastrar.class);
               startActivity(i);
           }
        });


        btnLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String email = tilEmail.getEditText().getText().toString().trim();
                    String nome  = tilSenha.getEditText().getText().toString().trim();





                    Intent i = new Intent(Entrar.this, Musicas.class);
                    startActivity(i);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            FirebaseUser mFirebaseUser = mFirebase
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        }
    }

    private boolean validateEmail(){
        String email = tilEmail.getEditText().getText().toString().trim();
        if(email.isEmpty()) {
            tilEmail.setError("Campo não pode ser nulo");
            return false;
        }
        else{
            tilEmail.setError(null);
            return true;
        }

    }

    private boolean validatePassword(){
        String email = tilSenha.getEditText().getText().toString().trim();
        if(email.isEmpty()) {
            tilSenha.setError("Campo não pode ser nulo");
            return false;
        }
        else{
            tilSenha.setError(null);
            return true;
        }

    }
}
