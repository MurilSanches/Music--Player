package br.unicamp.musicplayer;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService  {
    @GET("{body}")
    Call<List<Musica>> getLetra(@Path("body") String body);
}
