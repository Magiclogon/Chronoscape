package ma.ac.emi.tiles;

import lombok.Getter;

@Getter
public enum MapTheme {
    ROBOTS("MapTiles"),

    ALIENS("MapTiles2");

    private final String path;
    MapTheme(String path) { this.path = path; }
}
