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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Cadastrar extends AppCompatActivity {

    private TextInputLayout tilNome, tilEmail, tilSenha1, tilSenha2;
    private Button btnCadastrar;
    private br.unicamp.musicplayer.LinkTextView ltvLink;
    private FirebaseAuth mFirebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar);

        tilNome = findViewById(R.id.text_input_nome);
        tilEmail = findViewById(R.id.text_input_email);
        tilSenha1 = findViewById(R.id.text_input_password_1);
        tilSenha2 = findViewById(R.id.text_input_password_2);
        btnCadastrar = findViewById(R.id.btnCadastrar);
        ltvLink = findViewById(R.id.ltvLink);

        ltvLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Cadastrar.this, Entrar.class);
                startActivity(i);
            }
        });

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               try{
                   String email = tilEmail.getEditText().getText().toString().trim();
                   String nome = tilNome.getEditText().getText().toString().trim();
                   String senha1 = tilSenha1.getEditText().getText().toString().trim();
                   String senha2 = tilSenha2.getEditText().getText().toString().trim();

                   if(validateEmail() && validateNome() && validatePassword1() && validatePassword2() && validateSenhas()) {

                       final Usuario u = new Usuario(nome, senha1, email);

                       mFirebaseAuth.createUserWithEmailAndPassword(email, senha1).addOnCompleteListener(Cadastrar.this, new OnCompleteListener<AuthResult>() {
                           @Override
                           public void onComplete(@NonNull Task<AuthResult> task) {
                               if(!task.isSuccessful())
                                    Toast.makeText(getBaseContext(), "Cadastro falho, tente novamente", Toast.LENGTH_SHORT).show();
                               else {
                                   Intent i = new Intent(Cadastrar.this, Musicas.class);
                                   i.putExtra("User", u);
                                   startActivity(i);
                               }
                           }
                       });
                   }
               }
               catch (Exception e)
               {
                    e.printStackTrace();
               }
            }
        });
    }

    private boolean validateSenhas()
    {
        String senha1 = tilSenha1.getEditText().getText().toString().trim();
        String senha2 = tilSenha2.getEditText().getText().toString().trim();

        if(!senha1.equals(senha2))
        {
            tilSenha1.setError("Senhas diferentes");
            tilSenha2.setError("Senhas diferentes");
            return false;
        }
        else
        {
            tilSenha1.setError(null);
            tilSenha1.setError(null);
            return true;
        }
    }

    private boolean validateNome(){
        String email = tilNome.getEditText().getText().toString().trim();
        if(email.isEmpty()) {
            tilNome.setError("Campo não pode ser nulo");
            return false;
        }
        else{
            tilNome.setError(null);
            return true;
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

    private boolean validatePassword1(){
        String email = tilSenha1.getEditText().getText().toString().trim();
        if(email.isEmpty()) {
            tilSenha1.setError("Campo não pode ser nulo");
            return false;
        }
        else{
            tilSenha1.setError(null);
            return true;
        }
    }

    private boolean validatePassword2(){
        String email = tilSenha2.getEditText().getText().toString().trim();
        if(email.isEmpty()) {
            tilSenha2.setError("Campo não pode ser nulo");
            return false;
        }
        else{
            tilSenha2.setError(null);
            return true;
        }
    }
}
