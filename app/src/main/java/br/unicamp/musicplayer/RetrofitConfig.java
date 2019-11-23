package br.unicamp.musicplayer;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitConfig {

    private static Retrofit retrofit;

    public RetrofitConfig(String url) {
        this.retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public ApiService getApiService() { return retrofit.create(ApiService.class); }

    public UsuarioService getService()
    {
        return retrofit.create(UsuarioService.class);
    }
}
