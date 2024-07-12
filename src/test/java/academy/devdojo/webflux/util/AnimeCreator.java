package academy.devdojo.webflux.util;

import academy.devdojo.webflux.domain.Anime;

public class AnimeCreator {
    public static Anime createAnimeToBeSaved() {
        return Anime.builder()
                .name("One Piece")
                .build();
    }

    public static Anime createValidAnime() {
        return Anime.builder()
                .id(1)
                .name("One Piece")
                .build();
    }
    public static Anime createdValidUpdateAnime() {
        return Anime.builder()
                .id(1)
                .name("One Piece 2")
                .build();
    }
}
