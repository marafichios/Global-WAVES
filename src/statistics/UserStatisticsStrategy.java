package statistics;

import app.Admin;
import app.user.User;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserStatisticsStrategy implements StatisticStrategy{
    @Override
    public WrappedResult generateStatistics(User user) {
        // Implement logic for user statistics
        // The WrappedResult object will hold the result of the statistics
        WrappedResult result = new WrappedResult();
        // Example: Calculate top artists for the user
        Map<String, Integer> topArtists = calculateTopArtists(user);
        result.setTopArtists(topArtists);

        // Similarly, calculate other statistics
        Map<String, Integer> topGenres = calculateTopGenres(user);
        result.setTopGenres(topGenres);

        Map<String, Integer> topSongs = calculateTopSongs(user);
        result.setTopSongs(topSongs);

        Map<String, Integer> topAlbums = calculateTopAlbums(user);
        result.setTopAlbums(topAlbums);

        Map<String, Integer> topEpisodes = calculateTopEpisodes(user);
        result.setTopEpisodes(topEpisodes);

        return result;
    }

    private Map<String, Integer> calculateTopEpisodes(User user) {
        return null;
    }

    private Map<String, Integer> calculateTopAlbums(User user) {
        return null;
    }

    private Map<String, Integer> calculateTopSongs(User user) {
        return null;
    }

    private Map<String, Integer> calculateTopGenres(User user) {
        return null;
    }

    private Map<String, Integer> calculateTopArtists(User user) {
        if (user == null) {
            return null;
        }
        Map<String, Integer> artistCounts;
        Admin.getInstance().getUserListens(user); // Method to fetch listens
        artistCounts = user.getWrappedResult().getTopArtists();
        //iterate over listens and sort by descending order
        sortAndLimitTopArtists(artistCounts);
        return user.getWrappedResult().getTopArtists();
    }

    private void sortAndLimitTopArtists(Map<String, Integer> artistCounts) {
        // Sort by count (desc) and then by name (asc), and limit to top N artists if needed
        artistCounts = artistCounts.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, HashMap::new));

    }
}
