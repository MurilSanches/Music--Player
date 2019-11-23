package br.unicamp.musicplayer;

public class Traducao {
    private String id;
    private int language;
    private String url;
    private String text;

    public Traducao(String id, int language, String url, String text) {
        this.id = id;
        this.language = language;
        this.url = url;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
