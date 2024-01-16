package statistics;

import java.util.HashMap;
import java.util.Map;

public class WrappedResult {
    private Map<String, Integer> topArtists;
    private Map<String, Integer> topGenres;
    private Map<String, Integer> topSongs;
    private Map<String, Integer> topAlbums;
    private Map<String, Integer> topEpisodes;
    // Add more fields as needed for Artist and Host statistics

    // Constructors, getters, and setters
    public WrappedResult() {
        // Initialize the fields
        this.topArtists = new HashMap<>();
        this.topGenres = new HashMap<>();
        this.topSongs = new HashMap<>();
        this.topAlbums = new HashMap<>();
        this.topEpisodes = new HashMap<>();
        // Initialize other fields
    }

    // Example of getters and setters
    public void setTopArtists(Map<String, Integer> topArtists) {
        this.topArtists = topArtists;
    }

    public Map<String, Integer> getTopArtists() {
        return topArtists;
    }

    public void setTopGenres(Map<String, Integer> topGenres) {
        this.topGenres = topGenres;
    }

    public Map<String, Integer> getTopGenres() {
        return topGenres;
    }

    public void setTopSongs(Map<String, Integer> topSongs) {
        this.topSongs = topSongs;
    }

    public Map<String, Integer> getTopSongs() {
        return topSongs;
    }

    public void setTopAlbums(Map<String, Integer> topAlbums) {
        this.topAlbums = topAlbums;
    }

    public Map<String, Integer> getTopAlbums() {
        return topAlbums;
    }

    public void setTopEpisodes(Map<String, Integer> topEpisodes) {
        this.topEpisodes = topEpisodes;
    }

    public Map<String, Integer> getTopEpisodes() {
        return topEpisodes;
    }

    private UserStatisticsStrategy userStatisticsStrategy;

    public void setUserStatisticsStrategy(UserStatisticsStrategy userStatisticsStrategy) {
        this.userStatisticsStrategy = userStatisticsStrategy;
    }

    public UserStatisticsStrategy getUserStatisticsStrategy() {
        return userStatisticsStrategy;
    }
}
