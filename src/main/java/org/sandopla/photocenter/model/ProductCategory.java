package org.sandopla.photocenter.model;

public enum ProductCategory {
    CAMERA("Фотоапарати"),
    FILM("Фотоплівки"),
    PAPER("Фотопапір"),
    CHEMICALS("Хімічні реактиви"),
    ACCESSORIES("Фотоприладдя"),
    FRAMES("Рамки"),
    ALBUMS("Альбоми");

    private final String displayName;

    ProductCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}