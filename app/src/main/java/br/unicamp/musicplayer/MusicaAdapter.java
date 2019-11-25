package br.unicamp.musicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.musicplayer.R;

import java.util.ArrayList;
import java.util.List;

public class MusicaAdapter extends ArrayAdapter<String>
{
    private Context context;
    private List<String> songs;

    public MusicaAdapter(@NonNull Context context, @NonNull List<String> songs) {
        super(context,  0, songs);
        this.songs = songs;
        this.context = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        String s = songs.get(position);
        view = LayoutInflater.from(context).inflate(R.layout.activity_lista_modelo, null);

        TextView tvTitle = view.findViewById(R.id.tvTitulo);
        tvTitle.setText(songs.get(position));

        return view;
    }
}
