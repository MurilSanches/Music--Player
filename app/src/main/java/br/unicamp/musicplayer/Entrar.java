package br.unicamp.musicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
                    final String email = tilEmail.getEditText().getText().toString().trim();
                    final String senha  = tilSenha.getEditText().getText().toString().trim();

                    if(validateEmail() && validatePassword()) {
                        final FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("users")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            QuerySnapshot queryDocument = task.getResult();
                                            List<DocumentSnapshot> list = queryDocument.getDocuments();

                                            Usuario u = null;
                                            for(DocumentSnapshot d : list)
                                            {
                                                if(d.get("Email").equals(email) && d.get("Senha").equals(senha)) {
                                                    Toast.makeText(getBaseContext(), "Entrando . . .", Toast.LENGTH_SHORT).show();
                                                    u = new Usuario(d.get("Nome").toString(), d.get("Senha").toString(), d.get("Email").toString());
                                                    break;
                                                }
                                            }

                                            if(u == null) {
                                                tilEmail.setError("Email não encontrado");
                                                tilSenha.setError("Senha não encontrada");
                                                Toast.makeText(getBaseContext(), "Email ou senha erradas", Toast.LENGTH_SHORT).show();
                                            }

                                            else {
                                                Intent i = new Intent(Entrar.this, Musicas.class).putExtra("user", u);
                                                startActivity(i);
                                            }
                                        } else {
                                            Toast.makeText(getBaseContext(), "Algum erro aconteceu", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


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
