package br.unicamp.musicplayer;

public class Musica {
    private String id;
    private String name;
    private Artista artist;
    private int language;
    private String url;
    private String text;
    private Traducao[] tr;

    public Musica(String title, String artist)
    {
        this.name = title;
        this.artist = new Artista(null, artist, null);
    }

    public Musica(String id, String name, Artista artist, int language, String url, String text, Traducao[] tr) {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.language = language;
        this.url = url;
        this.text = text;
        this.tr = tr;
    }


    public int getLanguage() {
        return language;
    }

    public void setLanguage(int language) {
        this.language = language;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Artista getArtist() {
        return artist;
    }

    public void setArtist(Artista artist) {
        this.artist = artist;
    }

    public Traducao[] getTr() {
        return tr;
    }

    public void setTr(Traducao[] tr) {
        this.tr = tr;
    }
}
