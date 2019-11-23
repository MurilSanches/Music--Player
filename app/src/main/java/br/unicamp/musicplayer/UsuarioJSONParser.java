package br.unicamp.musicplayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UsuarioJSONParser {
    public static List<Usuario> parseDados(String content) {
        try {

            JSONArray jsonArray = new JSONArray(content);
            List<Usuario> usuarioList = new ArrayList<>();

            for (int i = 0; i< jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                Usuario u = new Usuario();
                u.setId(jsonObject.getInt("id"));
                u.setNome(jsonObject.getString("Nome"));
                u.setSenha(jsonObject.getString("Senha"));
                u.setEmail(jsonObject.getString("Email"));

                usuarioList.add(u);
            }
            return usuarioList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}
