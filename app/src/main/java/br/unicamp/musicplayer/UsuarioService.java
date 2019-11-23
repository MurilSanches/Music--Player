package br.unicamp.musicplayer;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UsuarioService {
    @GET("usuarios")
    Call<List<Usuario>> getUsuarios();

    @GET("usuarios/{Usuario}")
    Call<List<Usuario>> getUsuario(@Path("Usuario") String Usuario);

    @FormUrlEncoded
    @POST("usuarios/getUsuario")
    Call<Usuario> getUsuarioSenha(@Field("Usuario") String Usuario, @Field("Senha") String Senha);

    @FormUrlEncoded
    @POST("usuarios")
    Call<Void> inserir(@Field("Usuario") String Usuario, @Field("Nome") String Nome, @Field("Senha") String Senha, @Field("Email") String Email);

    @DELETE("usuarios/{Usuario}")
    public Call<Void> deletar(@Path("Usuario") String Usuario);

    @FormUrlEncoded
    @PUT("usuarios/alterarNome")
    Call<Void> alterarNome(@Field("Nome") String Nome, @Field("Usuario") String Usuario);

    @FormUrlEncoded
    @PUT("usuarios/alterarSenha")
    Call<Void> alterarSenha(@Field("Senha") String Senha, @Field("Usuario") String Usuario);

    @FormUrlEncoded
    @PUT("usuarios/alterarEmail")
    Call<Void> alterarEmail(@Field("Email") String Email, @Field("Usuario") String Usuario);

    @FormUrlEncoded
    @PUT("usuarios/alterarUsuario")
    Call<Void> alterarUsuario(@Field("Usuario") String Usuario, @Field("Email") String Email);
}
