package com.example.testchatfragment;

public class Story {
    private int id;
    private String image; // Tipe data untuk menyimpan path gambar
    private String description;

    // Constructor yang diperbarui
    public Story(int id, String image, String description) {
        this.id = id;
        this.image = image; // Menyimpan path gambar
        this.description = description;
    }

    public Story() {
        // Constructor default
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id; // Setter untuk ID
    }

    public String getImage() {
        return image; // Mengembalikan path gambar
    }

    public void setImage(String image) {
        this.image = image; // Setter untuk image
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description; // Setter untuk description
    }
}
