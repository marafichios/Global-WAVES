package app.pages;

import app.audio.Collections.Playlist;
import app.audio.Files.Song;
import app.user.Subject;
import app.user.User;

import java.util.Comparator;
import java.util.List;

/**
 * The type Home page.
 */
public final class HomePage implements Page {
    private List<Song> likedSongs;
    private List<Playlist> followedPlaylists;
    private final int limit = 5;
    private List<Song> songRecommendations;
    private List<Playlist> playlistRecommendations;

    /**
     * Instantiates a new Home page.
     *
     * @param user the user
     */
    public HomePage(final User user) {
        likedSongs = user.getLikedSongs();
        followedPlaylists = user.getFollowedPlaylists();
        songRecommendations = user.getSongRecommendations();
        playlistRecommendations = user.getPlaylistRecommendations();
    }

    @Override
    public String printCurrentPage() {
        return "Liked songs:\n\t%s\n\nFollowed playlists:\n\t%s\n\nSong recommendations:\n\t%s\n\nPlaylists recommendations:\n\t%s"
               .formatted(likedSongs.stream()
                                    .sorted(Comparator.comparing(Song::getLikes)
                                    .reversed()).limit(limit).map(Song::getName)
                          .toList(),
                          followedPlaylists.stream().sorted((o1, o2) ->
                                  o2.getSongs().stream().map(Song::getLikes)
                                    .reduce(Integer::sum).orElse(0)
                                  - o1.getSongs().stream().map(Song::getLikes).reduce(Integer::sum)
                                  .orElse(0)).limit(limit).map(Playlist::getName)
                          .toList(),
                          //get first song from list of recommendations
                          songRecommendations.stream().sorted(Comparator.comparing(Song::getName))
                                    .limit(1).map(Song::getName).toList(),
                          playlistRecommendations.stream().sorted(Comparator.comparing(Playlist::getName)).limit(limit).map(Playlist::getName).toList());
    }

}
