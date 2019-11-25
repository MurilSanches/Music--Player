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
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class FilaAdapter extends ArrayAdapter<String>
{
    private Context context;
    private Queue<String> songs;

    public FilaAdapter(@NonNull Context context, @NonNull LinkedList<String> songs)
    {
        super(context,  0, songs);
        this.songs = songs;
        this.context = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        String s = songs.remove();
        view = LayoutInflater.from(context).inflate(R.layout.activity_lista_fila, null);

        TextView tvTitle = view.findViewById(R.id.tvTitulo);
        tvTitle.setText(s);

        return view;
    }
}
