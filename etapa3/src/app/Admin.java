package app;

import app.audio.Collections.Album;
import app.audio.Collections.AudioCollection;
import app.audio.Collections.Playlist;
import app.audio.Collections.Podcast;
import app.audio.Files.AudioFile;
import app.audio.Files.Episode;
import app.audio.Files.Song;
import app.audio.LibraryEntry;
import app.pages.*;
import app.player.Player;
import app.searchBar.FilterUtils;
import app.searchBar.Filters;
import app.searchBar.SearchBar;
import app.user.*;
import app.utils.Enums;
import app.utils.Notifications;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.CommandInput;
import fileio.input.EpisodeInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;
import fileio.input.UserInput;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The type Admin.
 */
public final class Admin {
    private List<User> users = new ArrayList<>();
    @Getter
    private List<Artist> artists = new ArrayList<>();
    @Getter
    private List<Host> hosts = new ArrayList<>();
    private List<Song> songs = new ArrayList<>();
    private List<Podcast> podcasts = new ArrayList<>();
    private int timestamp = 0;
    private final int limit = 5;
    private final int dateStringLength = 10;
    private final int dateFormatSize = 3;
    private final int dateYearLowerLimit = 1900;
    private final int dateYearHigherLimit = 2023;
    private final int dateMonthLowerLimit = 1;
    private final int dateMonthHigherLimit = 12;
    private final int dateDayLowerLimit = 1;
    private final int dateDayHigherLimit = 31;
    private final int dateFebHigherLimit = 28;
    private static Admin instance;
    Map<String, User> usersmap = new HashMap<>();


    private Admin() {
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static Admin getInstance() {
        if (instance == null) {
            instance = new Admin();
        }
        return instance;
    }

    /**
     * Reset instance.
     */
    public static void resetInstance() {
        instance = null;
    }

    /**
     * Sets users.
     *
     * @param userInputList the user input list
     */
    public void setUsers(final List<UserInput> userInputList) {
        for (UserInput userInput : userInputList) {
            users.add(new User(userInput.getUsername(), userInput.getAge(), userInput.getCity()));
        }
    }

    /**
     * Sets songs.
     *
     * @param songInputList the song input list
     */
    public void setSongs(final List<SongInput> songInputList) {
        for (SongInput songInput : songInputList) {
            songs.add(new Song(songInput.getName(), songInput.getDuration(), songInput.getAlbum(),
                    songInput.getTags(), songInput.getLyrics(), songInput.getGenre(),
                    songInput.getReleaseYear(), songInput.getArtist()));
        }
    }

    /**
     * Sets podcasts.
     *
     * @param podcastInputList the podcast input list
     */
    public void setPodcasts(final List<PodcastInput> podcastInputList) {
        for (PodcastInput podcastInput : podcastInputList) {
            List<Episode> episodes = new ArrayList<>();
            for (EpisodeInput episodeInput : podcastInput.getEpisodes()) {
                episodes.add(new Episode(episodeInput.getName(),
                                         episodeInput.getDuration(),
                                         episodeInput.getDescription()));
            }
            podcasts.add(new Podcast(podcastInput.getName(), podcastInput.getOwner(), episodes));
        }
    }

    /**
     * Gets songs.
     *
     * @return the songs
     */
    public List<Song> getSongs() {
        return new ArrayList<>(songs);
    }

    /**
     * Gets podcasts.
     *
     * @return the podcasts
     */
    public List<Podcast> getPodcasts() {
        return new ArrayList<>(podcasts);
    }

    /**
     * Gets playlists.
     *
     * @return the playlists
     */
    public List<Playlist> getPlaylists() {
        return users.stream()
                    .flatMap(user -> user.getPlaylists().stream())
                    .collect(Collectors.toList());
    }

    /**
     * Gets albums.
     *
     * @return the albums
     */
    public List<Album> getAlbums() {
        return artists.stream()
                      .flatMap(artist -> artist.getAlbums().stream())
                      .collect(Collectors.toList());
    }

    /**
     * Gets all users.
     *
     * @return the all users
     */
    public List<String> getAllUsers() {
        List<String> allUsers = new ArrayList<>();

        allUsers.addAll(users.stream().map(UserAbstract::getUsername).toList());
        allUsers.addAll(artists.stream().map(UserAbstract::getUsername).toList());
        allUsers.addAll(hosts.stream().map(UserAbstract::getUsername).toList());

        return allUsers;
    }

    /**
     * Gets user.
     *
     * @param username the username
     * @return the user
     */
    public User getUser(final String username) {
        return users.stream()
                    .filter(user -> user.getUsername().equals(username))
                    .findFirst()
                    .orElse(null);
    }

    /**
     * Gets artist.
     *
     * @param username the username
     * @return the artist
     */
    public Artist getArtist(final String username) {
        return artists.stream()
                .filter(artist -> artist.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    /**
     * Gets host.
     *
     * @param username the username
     * @return the host
     */
    public Host getHost(final String username) {
        return hosts.stream()
                .filter(artist -> artist.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    /**
     * Update timestamp.
     *
     * @param newTimestamp the new timestamp
     */
    public void updateTimestamp(final int newTimestamp) {
        int elapsed = newTimestamp - timestamp;
        timestamp = newTimestamp;

        if (elapsed == 0) {
            return;
        } else if (elapsed < 0) {
            throw new IllegalArgumentException("Invalid timestamp" + newTimestamp);
        }

        users.forEach(user -> user.simulateTime(elapsed));
    }

    private UserAbstract getAbstractUser(final String username) {
        ArrayList<UserAbstract> allUsers = new ArrayList<>();

        allUsers.addAll(users);
        allUsers.addAll(artists);
        allUsers.addAll(hosts);

        return allUsers.stream()
                       .filter(userPlatform -> userPlatform.getUsername().equals(username))
                       .findFirst()
                       .orElse(null);
    }

    /**
     * Add new user string.
     *
     * @param commandInput the command input
     * @return the string
     */
    public String addNewUser(final CommandInput commandInput) {
        String username = commandInput.getUsername();
        String type = commandInput.getType();
        int age = commandInput.getAge();
        String city = commandInput.getCity();

        UserAbstract currentUser = getAbstractUser(username);
        if (currentUser != null) {
            return "The username %s is already taken.".formatted(username);
        }

        if (type.equals("user")) {
            users.add(new User(username, age, city));
        } else if (type.equals("artist")) {
            artists.add(new Artist(username, age, city));
        } else {
            hosts.add(new Host(username, age, city));
        }

        return "The username %s has been added successfully.".formatted(username);
    }

    /**
     * Delete user string.
     *
     * @param username the username
     * @return the string
     */
    public String deleteUser(final String username) {
        UserAbstract currentUser = getAbstractUser(username);

        if (currentUser == null) {
            return "The username %s doesn't exist.".formatted(username);
        }

        if (currentUser.userType().equals("user")) {
            return deleteNormalUser((User) currentUser);
        }

        if (currentUser.userType().equals("host")) {
            return deleteHost((Host) currentUser);
        }

        return deleteArtist((Artist) currentUser);
    }

    private String deleteNormalUser(final User user) {
        if (user.getPlaylists().stream().anyMatch(playlist -> users.stream().map(User::getPlayer)
                .filter(player -> player != user.getPlayer())
                .map(Player::getCurrentAudioCollection)
                .filter(Objects::nonNull)
                .anyMatch(collection -> collection == playlist))) {
            return "%s can't be deleted.".formatted(user.getUsername());
        }

        user.getLikedSongs().forEach(Song::dislike);
        user.getFollowedPlaylists().forEach(Playlist::decreaseFollowers);

        users.stream().filter(otherUser -> otherUser != user)
             .forEach(otherUser -> otherUser.getFollowedPlaylists()
                                            .removeAll(user.getPlaylists()));

        users.remove(user);
        return "%s was successfully deleted.".formatted(user.getUsername());
    }

    private String deleteHost(final Host host) {
        if (host.getPodcasts().stream().anyMatch(podcast -> getAudioCollectionsStream()
                .anyMatch(collection -> collection == podcast))
                || users.stream().anyMatch(user -> user.getCurrentPage() == host.getPage())) {
            return "%s can't be deleted.".formatted(host.getUsername());
        }

        host.getPodcasts().forEach(podcast -> podcasts.remove(podcast));
        hosts.remove(host);

        return "%s was successfully deleted.".formatted(host.getUsername());
    }

    private String deleteArtist(final Artist artist) {
        if (artist.getAlbums().stream().anyMatch(album -> album.getSongs().stream()
            .anyMatch(song -> getAudioFilesStream().anyMatch(audioFile -> audioFile == song))
            || getAudioCollectionsStream().anyMatch(collection -> collection == album))
            || users.stream().anyMatch(user -> user.getCurrentPage() == artist.getPage())) {
            return "%s can't be deleted.".formatted(artist.getUsername());
        }

        users.forEach(user -> artist.getAlbums().forEach(album -> album.getSongs().forEach(song -> {
            user.getLikedSongs().remove(song);
            user.getPlaylists().forEach(playlist -> playlist.removeSong(song));
        })));

        songs.removeAll(artist.getAllSongs());
        artists.remove(artist);
        return "%s was successfully deleted.".formatted(artist.getUsername());
    }

    /**
     * Add album string.
     *
     * @param commandInput the command input
     * @return the string
     */
    public String addAlbum(final CommandInput commandInput) {
        String username = commandInput.getUsername();
        String albumName = commandInput.getName();
        UserAbstract currentUser = getAbstractUser(username);

        if (currentUser == null) {
            return "The username %s doesn't exist.".formatted(username);
        } else if (!currentUser.userType().equals("artist")) {
            return "%s is not an artist.".formatted(username);
        }

        Artist currentArtist = (Artist) currentUser;
        if (currentArtist.getAlbums().stream()
            .anyMatch(album -> album.getName().equals(albumName))) {
            return "%s has another album with the same name.".formatted(username);
        }

        List<Song> newSongs = commandInput.getSongs().stream()
                                       .map(songInput -> new Song(songInput.getName(),
                                                                  songInput.getDuration(),
                                                                  albumName,
                                                                  songInput.getTags(),
                                                                  songInput.getLyrics(),
                                                                  songInput.getGenre(),
                                                                  songInput.getReleaseYear(),
                                                                  currentArtist.getUsername()))
                                       .toList();

        Set<String> songNames = new HashSet<>();
        if (!newSongs.stream().filter(song -> !songNames.add(song.getName()))
                  .collect(Collectors.toSet()).isEmpty()) {
            return "%s has the same song at least twice in this album.".formatted(username);
        }

        songs.addAll(newSongs);
        currentArtist.getAlbums().add(new Album(albumName,
                                                commandInput.getDescription(),
                                                username,
                                                newSongs,
                                                commandInput.getReleaseYear()));
        Notifications notification = new Notifications("New Album", "New Album from " + currentArtist.getUsername() + ".");
        currentArtist.notifyObservers(notification);
        return "%s has added new album successfully.".formatted(username);
    }

    /**
     * Remove album string.
     *
     * @param commandInput the command input
     * @return the string
     */
    public String removeAlbum(final CommandInput commandInput) {
        String username = commandInput.getUsername();
        String albumName = commandInput.getName();

        UserAbstract currentUser = getAbstractUser(username);

        if (currentUser == null) {
            return "The username %s doesn't exist.".formatted(username);
        } else if (!currentUser.userType().equals("artist")) {
            return "%s is not an artist.".formatted(username);
        }

        Artist currentArtist = (Artist) currentUser;
        Album searchedAlbum = currentArtist.getAlbum(albumName);
        if (searchedAlbum == null) {
            return "%s doesn't have an album with the given name.".formatted(username);
        }

        if (getAudioCollectionsStream().anyMatch(collection -> collection == searchedAlbum)) {
            return "%s can't delete this album.".formatted(username);
        }

        for (Song song : searchedAlbum.getSongs()) {
            if (getAudioCollectionsStream().anyMatch(collection -> collection.containsTrack(song))
                || getAudioFilesStream().anyMatch(audioFile -> audioFile == song)) {
                return "%s can't delete this album.".formatted(username);
            }
        }

        for (Song song: searchedAlbum.getSongs()) {
            users.forEach(user -> {
                user.getLikedSongs().remove(song);
                user.getPlaylists().forEach(playlist -> playlist.removeSong(song));
            });
            songs.remove(song);
        }

        currentArtist.getAlbums().remove(searchedAlbum);
        return "%s deleted the album successfully.".formatted(username);
    }

    /**
     * Add podcast string.
     *
     * @param commandInput the command input
     * @return the string
     */
    public String addPodcast(final CommandInput commandInput) {
        String username = commandInput.getUsername();
        String podcastName = commandInput.getName();
        UserAbstract currentUser = getAbstractUser(username);

        if (currentUser == null) {
            return "The username %s doesn't exist.".formatted(username);
        } else if (!currentUser.userType().equals("host")) {
            return "%s is not a host.".formatted(username);
        }

        Host currentHost = (Host) currentUser;
        if (currentHost.getPodcasts().stream()
            .anyMatch(podcast -> podcast.getName().equals(podcastName))) {
            return "%s has another podcast with the same name.".formatted(username);
        }

        List<Episode> episodes = commandInput.getEpisodes().stream()
                                             .map(episodeInput ->
                                                     new Episode(episodeInput.getName(),
                                                                 episodeInput.getDuration(),
                                                                 episodeInput.getDescription()))
                                             .collect(Collectors.toList());

        Set<String> episodeNames = new HashSet<>();
        if (!episodes.stream().filter(episode -> !episodeNames.add(episode.getName()))
                     .collect(Collectors.toSet()).isEmpty()) {
            return "%s has the same episode in this podcast.".formatted(username);
        }

        Podcast newPodcast = new Podcast(podcastName, username, episodes);
        currentHost.getPodcasts().add(newPodcast);
        podcasts.add(newPodcast);

        return "%s has added new podcast successfully.".formatted(username);
    }


    /**
     * Remove podcast string.
     *
     * @param commandInput the command input
     * @return the string
     */
    public String removePodcast(final CommandInput commandInput) {
        String username = commandInput.getUsername();
        String podcastName = commandInput.getName();
        UserAbstract currentUser = getAbstractUser(username);

        if (currentUser == null) {
            return "The username %s doesn't exist.".formatted(username);
        } else if (!currentUser.userType().equals("host")) {
            return "%s is not a host.".formatted(username);
        }

        Host currentHost = (Host) currentUser;
        Podcast searchedPodcast = currentHost.getPodcast(podcastName);

        if (searchedPodcast == null) {
            return "%s doesn't have a podcast with the given name.".formatted(username);
        }

        if (getAudioCollectionsStream().anyMatch(collection -> collection == searchedPodcast)) {
            return "%s can't delete this podcast.".formatted(username);
        }

        currentHost.getPodcasts().remove(searchedPodcast);
        podcasts.remove(searchedPodcast);
        return "%s deleted the podcast successfully.".formatted(username);
    }

    /**
     * Add event string.
     *
     * @param commandInput the command input
     * @return the string
     */
    public String addEvent(final CommandInput commandInput) {
        String username = commandInput.getUsername();
        String eventName = commandInput.getName();

        UserAbstract currentUser = getAbstractUser(username);

        if (currentUser == null) {
            return "The username %s doesn't exist.".formatted(username);
        } else if (!currentUser.userType().equals("artist")) {
            return "%s is not an artist.".formatted(username);
        }

        Artist currentArtist = (Artist) currentUser;
        if (currentArtist.getEvent(eventName) != null) {
            return "%s has another event with the same name.".formatted(username);
        }

        String date = commandInput.getDate();

        if (!checkDate(date)) {
            return "Event for %s does not have a valid date.".formatted(username);
        }

        currentArtist.getEvents().add(new Event(eventName,
                                                commandInput.getDescription(),
                                                commandInput.getDate()));
        Notifications notification = new Notifications("New Event", "New Event from " + currentArtist.getUsername() + ".");
        currentArtist.notifyObservers(notification);
        return "%s has added new event successfully.".formatted(username);
    }

    /**
     * Remove event string.
     *
     * @param commandInput the command input
     * @return the string
     */
    public String removeEvent(final CommandInput commandInput) {
        String username = commandInput.getUsername();
        String eventName = commandInput.getName();

        UserAbstract currentUser = getAbstractUser(username);

        if (currentUser == null) {
            return "The username %s doesn't exist.".formatted(username);
        } else if (!currentUser.userType().equals("artist")) {
            return "%s is not an artist.".formatted(username);
        }

        Artist currentArtist = (Artist) currentUser;
        Event searchedEvent = currentArtist.getEvent(eventName);
        if (searchedEvent == null) {
            return "%s doesn't have an event with the given name.".formatted(username);
        }

        currentArtist.getEvents().remove(searchedEvent);
        return "%s deleted the event successfully.".formatted(username);
    }

    private boolean checkDate(final String date) {
        if (date.length() != dateStringLength) {
            return false;
        }

        List<String> dateElements = Arrays.stream(date.split("-", dateFormatSize)).toList();

        if (dateElements.size() != dateFormatSize) {
            return false;
        }

        int day = Integer.parseInt(dateElements.get(0));
        int month = Integer.parseInt(dateElements.get(1));
        int year = Integer.parseInt(dateElements.get(2));

        if (day < dateDayLowerLimit
            || (month == 2 && day > dateFebHigherLimit)
            || day > dateDayHigherLimit
            || month < dateMonthLowerLimit || month > dateMonthHigherLimit
            || year < dateYearLowerLimit || year > dateYearHigherLimit) {
            return false;
        }

        return true;
    }

    /**
     * Add merch string.
     *
     * @param commandInput the command input
     * @return the string
     */
    public String addMerch(final CommandInput commandInput) {
        String username = commandInput.getUsername();
        UserAbstract currentUser = getAbstractUser(username);
        if (currentUser == null) {
            return "The username %s doesn't exist.".formatted(username);
        } else if (!currentUser.userType().equals("artist")) {
            return "%s is not an artist.".formatted(username);
        }

        Artist currentArtist = (Artist) currentUser;
        if (currentArtist.getMerch().stream()
                         .anyMatch(merch -> merch.getName().equals(commandInput.getName()))) {
            return "%s has merchandise with the same name.".formatted(currentArtist.getUsername());
        } else if (commandInput.getPrice() < 0) {
            return "Price for merchandise can not be negative.";
        }

        currentArtist.getMerch().add(new Merchandise(commandInput.getName(),
                                                     commandInput.getDescription(),
                                                     commandInput.getPrice()));
        Notifications notification = new Notifications("New Merchandise", "New Merchandise from " + currentArtist.getUsername() + ".");
        currentArtist.notifyObservers(notification);
        //clear the notification
        return "%s has added new merchandise successfully.".formatted(username);
    }

    /**
     * Add announcement string.
     *
     * @param commandInput the command input
     * @return the string
     */
    public String addAnnouncement(final CommandInput commandInput) {
        String username = commandInput.getUsername();
        String announcementName = commandInput.getName();
        String announcementDescription = commandInput.getDescription();

        UserAbstract currentUser = getAbstractUser(username);

        if (currentUser == null) {
            return "The username %s doesn't exist.".formatted(username);
        } else if (!currentUser.userType().equals("host")) {
            return "%s is not a host.".formatted(username);
        }

        Host currentHost = (Host) currentUser;
        Announcement searchedAnnouncement = currentHost.getAnnouncement(announcementName);
        if (searchedAnnouncement != null) {
            return "%s has already added an announcement with this name.";
        }

        currentHost.getAnnouncements().add(new Announcement(announcementName,
                                                            announcementDescription));
        return "%s has successfully added new announcement.".formatted(username);
    }

    /**
     * Remove announcement string.
     *
     * @param commandInput the command input
     * @return the string
     */
    public String removeAnnouncement(final CommandInput commandInput) {
        String username = commandInput.getUsername();
        String announcementName = commandInput.getName();

        UserAbstract currentUser = getAbstractUser(username);

        if (currentUser == null) {
            return "The username %s doesn't exist.".formatted(username);
        } else if (!currentUser.userType().equals("host")) {
            return "%s is not a host.".formatted(username);
        }

        Host currentHost = (Host) currentUser;
        Announcement searchAnnouncement = currentHost.getAnnouncement(announcementName);
        if (searchAnnouncement == null) {
            return "%s has no announcement with the given name.".formatted(username);
        }

        currentHost.getAnnouncements().remove(searchAnnouncement);
        return "%s has successfully deleted the announcement.".formatted(username);
    }

    /**
     * Change page string.
     *
     * @param commandInput the command input
     * @return the string
     */
    public String changePage(final CommandInput commandInput) {
        String username = commandInput.getUsername();
        String nextPage = commandInput.getNextPage();

        UserAbstract currentUser = getAbstractUser(username);

        if (currentUser == null) {
            return "The username %s doesn't exist.".formatted(username);
        } else if (!currentUser.userType().equals("user")) {
            return "%s is not a normal user.".formatted(username);
        }

        User user = (User) currentUser;
        if (!user.isStatus()) {
            return "%s is offline.".formatted(user.getUsername());
        }

        switch (nextPage) {

            case "Home" -> user.setCurrentPage(user.getHomePage());
            case "LikedContent" -> user.setCurrentPage(user.getLikedContentPage());
            case "Artist" -> {
                Song song = (Song) (user.getPlayer().getSource().getAudioFile());
                Artist artist = Admin.getInstance().getArtist(song.getArtist());
                user.setCurrentPage(artist.getPage());
                artist.setPresentsInterest(true);
            }
            case "Host" -> {
                Podcast podcast = (Podcast) (user.getPlayer().getSource().getAudioCollection());
                Host host = Admin.getInstance().getHost(podcast.getOwner());
                user.setCurrentPage(host.getPage());
            }
            default -> {
                return "%s is trying to access a non-existent page.".formatted(username);
            }
        }
        user.navigateTo(user.getCurrentPage());
        return "%s accessed %s successfully.".formatted(username, nextPage);
    }

    /**
     * Print current page string.
     *
     * @param commandInput the command input
     * @return the string
     */
    public String printCurrentPage(final CommandInput commandInput) {
        String username = commandInput.getUsername();
        UserAbstract currentUser = getAbstractUser(username);

        if (currentUser == null) {
            return "The username %s doesn't exist.".formatted(username);
        } else if (!currentUser.userType().equals("user")) {
            return "%s is not a normal user.".formatted(username);
        }

        User user = (User) currentUser;
        if (!user.isStatus()) {
            return "%s is offline.".formatted(user.getUsername());
        }

        return user.getCurrentPage().printCurrentPage();
    }

    /**
     * Switch status string.
     *
     * @param username the username
     * @return the string
     */
    public String switchStatus(final String username) {
        UserAbstract currentUser = getAbstractUser(username);

        if (currentUser == null) {
            return "The username %s doesn't exist.".formatted(username);
        }

        if (currentUser.userType().equals("user")) {
            ((User) currentUser).switchStatus();
            return username + " has changed status successfully.";
        } else {
            return username + " is not a normal user.";
        }
    }

    /**
     * Gets online users.
     *
     * @return the online users
     */
    public List<String> getOnlineUsers() {
        return users.stream().filter(User::isStatus).map(User::getUsername).toList();
    }

    private Stream<AudioCollection> getAudioCollectionsStream() {
        return users.stream().map(User::getPlayer)
                    .map(Player::getCurrentAudioCollection).filter(Objects::nonNull);
    }

    private Stream<AudioFile> getAudioFilesStream() {
        return users.stream().map(User::getPlayer)
                    .map(Player::getCurrentAudioFile).filter(Objects::nonNull);
    }

    /**
     * Gets top 5 album list.
     *
     * @return the top 5 album list
     */
    public List<String> getTop5AlbumList() {
        List<Album> albums = artists.stream().map(Artist::getAlbums)
                                    .flatMap(List::stream).toList();

        final Map<Album, Integer> albumLikes = new HashMap<>();
        albums.forEach(album -> albumLikes.put(album, album.getSongs().stream()
                                          .map(Song::getLikes).reduce(0, Integer::sum)));

        return albums.stream().sorted((o1, o2) -> {
            if ((int) albumLikes.get(o1) == albumLikes.get(o2)) {
                return o1.getName().compareTo(o2.getName());
            }
            return albumLikes.get(o2) - albumLikes.get(o1);
        }).limit(limit).map(Album::getName).toList();
    }

    /**
     * Gets top 5 artist list.
     *
     * @return the top 5 artist list
     */
    public List<String> getTop5ArtistList() {
        final Map<Artist, Integer> artistLikes = new HashMap<>();
        artists.forEach(artist -> artistLikes.put(artist, artist.getAllSongs().stream()
                                              .map(Song::getLikes).reduce(0, Integer::sum)));

        return artists.stream().sorted(Comparator.comparingInt(artistLikes::get).reversed())
                               .limit(limit).map(Artist::getUsername).toList();
    }

    /**
     * Gets top 5 songs.
     *
     * @return the top 5 songs
     */
    public List<String> getTop5Songs() {
        List<Song> sortedSongs = new ArrayList<>(songs);
        sortedSongs.sort(Comparator.comparingInt(Song::getLikes).reversed());
        List<String> topSongs = new ArrayList<>();
        int count = 0;
        for (Song song : sortedSongs) {
            if (count >= limit) {
                break;
            }
            topSongs.add(song.getName());
            count++;
        }
        return topSongs;
    }

    /**
     * Gets top 5 playlists.
     *
     * @return the top 5 playlists
     */
    public List<String> getTop5Playlists() {
        List<Playlist> sortedPlaylists = new ArrayList<>(getPlaylists());
        sortedPlaylists.sort(Comparator.comparingInt(Playlist::getFollowers)
                .reversed()
                .thenComparing(Playlist::getTimestamp, Comparator.naturalOrder()));
        List<String> topPlaylists = new ArrayList<>();
        int count = 0;
        for (Playlist playlist : sortedPlaylists) {
            if (count >= limit) {
                break;
            }
            topPlaylists.add(playlist.getName());
            count++;
        }
        return topPlaylists;
    }

    private Integer artistListenCount = 0;

    private void updateUser(User user, Song song) {
        if (user.getWrappedResult().getTopAlbums().containsKey(song.getName())) {
            user.getWrappedResult().getTopAlbums().put(song.getName(),
                    user.getWrappedResult().getTopAlbums().get(song.getName()) + 1);
        } else {
            user.getWrappedResult().getTopAlbums().put(song.getName(), 1);
        }

        if (user.getWrappedResult().getTopArtists().containsKey(song.getArtist())) {
            user.getWrappedResult().getTopArtists().put(song.getArtist(),
                    user.getWrappedResult().getTopArtists().get(song.getArtist()) + 1);
        } else {
            user.getWrappedResult().getTopArtists().put(song.getArtist(), 1);
        }

        if (user.getWrappedResult().getTopGenres().containsKey(song.getGenre())) {
            user.getWrappedResult().getTopGenres().put(song.getGenre(),
                    user.getWrappedResult().getTopGenres().get(song.getGenre()) + 1);
        } else {
            user.getWrappedResult().getTopGenres().put(song.getGenre(), 1);
        }

        if (user.getWrappedResult().getTopSongs().containsKey(song.getName())) {
            user.getWrappedResult().getTopSongs().put(song.getName(),
                    user.getWrappedResult().getTopSongs().get(song.getName()) + 1);
        } else {
            user.getWrappedResult().getTopSongs().put(song.getName(), 1);
        }
    }

    public void getUserListens(User user) {
        if (user == null) {
            return;
        }
        if (user.getPlayer() == null || user.getPlayer().getSource() == null) {
            return;
        }
        AudioFile audioFile = user.getPlayer().getCurrentAudioFile();
        Song song = null;
        //chek if type is LIBRARY
        if (user.getPlayer().getSource().getType().equals(Enums.PlayerSourceType.LIBRARY)) {
            //check if from library is song
            if (user.getPlayer().getSource().getAudioFile().getClass().toString().equals("class app.audio.Files.Song")) {
                song = (Song) user.getPlayer().getSource().getAudioFile();
            }
            //song = (Song) user.getPlayer().getSource().getAudioFile();
        } else {
            return;
        }
        if (song == null && audioFile != null) {
            if (user.getWrappedResult().getTopEpisodes().containsKey(audioFile.getName())) {
                user.getWrappedResult().getTopEpisodes().put(audioFile.getName(),
                        user.getWrappedResult().getTopEpisodes().get(audioFile.getName()) + 1);
            } else {
                user.getWrappedResult().getTopEpisodes().put(audioFile.getName(), 1);
            }
        }
        if (song ==null) {
            return;
        }
        updateUser(user, song);

    }

        public ObjectNode generateEndProgramSummary(ObjectMapper objectMapper) {
        ObjectNode resultNode = objectMapper.createObjectNode();

        // Artist summaries
        List<Map<String, Object>> artistSummaries = new ArrayList<>();
        for (Artist artist : artists) {
            if (!artist.isPresentsInterest()) {
                continue;
            }
            Map<String, Object> artistSummary = new HashMap<>();
            double merchRevenue = calculateMerchRevenueForArtist(artist); // Ensure this never returns null
            double songRevenue = calculateSongRevenueForArtist(artist);   // Ensure this never returns null
            String mostProfitableSong = findMostProfitableSongForArtist(artist); // Ensure this never returns null or provide a default value

            artistSummary.put("merchRevenue", merchRevenue);
            artistSummary.put("songRevenue", songRevenue);
            artistSummary.put("mostProfitableSong", mostProfitableSong);
            artistSummary.put("name", artist.getName()); // Temporarily store the name for sorting
            artistSummaries.add(artistSummary);
        }

        // Sort and set rankings
        artistSummaries.sort(Comparator.comparing((Map<String, Object> m) -> (Double) m.get("merchRevenue"))
                .reversed().thenComparing(m -> (String) m.get("name")));
        int rank = 1;
        for (Map<String, Object> summary : artistSummaries) {
            summary.put("ranking", rank++);
        }

        // Convert to JSON structure
        for (Map<String, Object> summary : artistSummaries) {
            String artistName = summary.remove("name").toString(); // Remove the name from the map and use it as the key
            ObjectNode artistNode = objectMapper.createObjectNode();

            artistNode.put("merchRevenue", (Double) summary.get("merchRevenue"));
            artistNode.put("songRevenue", (Double) summary.get("songRevenue"));
            artistNode.put("ranking", (Integer) summary.get("ranking"));
            artistNode.put("mostProfitableSong", (String) summary.get("mostProfitableSong"));

            resultNode.set(artistName, artistNode);
        }

        return resultNode;
    }

    private double calculateMerchRevenueForArtist(Artist artist) {
        return artist.getMerch().stream()
                .mapToDouble(Merchandise::getPrice)
                .sum();
    }

    private double calculateSongRevenueForArtist(Artist artist) {
        return 0.0;
    }

    private String findMostProfitableSongForArtist(Artist artist) {
        return "N/A";
    }

    public String previousPage(final CommandInput commandInput) {
        String username = commandInput.getUsername();
        UserAbstract currentUser = getAbstractUser(username);

        if (currentUser == null) {
            return "The username " + username + " doesn't exist.";
        }

        User user = (User) currentUser;
        PreviousPage previousPage = new PreviousPage();
        String message = previousPage.execute(user);
        return message;

    }

    public String nextPage(final CommandInput commandInput) {
        String username = commandInput.getUsername();
        UserAbstract currentUser = getAbstractUser(username);

        if (currentUser == null) {
            return "The username " + username + " doesn't exist.";
        }

        User user = (User) currentUser;
        NextPage nextPage = new NextPage();
        String message = nextPage.execute(user);
        return message;

    }

    public String updateRecommendationsSongs(User user) {
        if (user.getPlayer() == null || user.getPlayer().getSource() == null) {
            return "No song is playing at the moment.";
        }
        int songDuration = (user.getPlayer().getSource().getAudioFile().getDuration());
        int remainingTime = user.getPlayer().getSource().getDuration();
        int listenedTime = songDuration - remainingTime;
        List<Song> sameGenreSongs = new ArrayList<>();

        if (listenedTime > 30) {
            List<LibraryEntry> entries = new ArrayList<>(getSongs());

            Song song = (Song) user.getPlayer().getSource().getAudioFile();
            String genre = song.getGenre();

            for (LibraryEntry entry : entries) {
                //check if song has same genre
                if (entry instanceof Song) {
                    Song entrySong = (Song) entry;

                    // Check if song has the same genre
                    if (entrySong.getGenre().equalsIgnoreCase(genre)) {
                        // Add to the list of same genre songs
                        sameGenreSongs.add(entrySong);
                    }
                }
            }
            Random random = new Random(listenedTime);
            int randomIndex = random.nextInt(sameGenreSongs.size());
            Song randomSong = sameGenreSongs.get(randomIndex);
            //add to songrecommendations array from user
            user.getSongRecommendations().add(randomSong);
            Artist artist = Admin.getInstance().getArtist(randomSong.getArtist());
            artist.setPresentsInterest(true);
        }


        return "The recommendations for user " + user.getUsername() + " have been updated successfully.";
    }


    public String updateRecommendationsPlaylists(User user, CommandInput commandInput) {
        String username = commandInput.getUsername();
        UserAbstract currentUser = getAbstractUser(username);

        if (currentUser == null) {
            return "The username %s doesn't exist.".formatted(username);
        } else if (!currentUser.userType().equals("user")) {
            return "%s is not a normal user.".formatted(username);
        }

        if (user.getPlayer() == null || user.getPlayer().getSource() == null) {
            return "No song is playing at the moment.";
        }
        user = (User) currentUser;
        Song song = (Song) (user.getPlayer().getSource().getAudioFile());
        Artist artist = Admin.getInstance().getArtist(song.getArtist());
        String artistName = artist.getName();
        //List<Playlist> playlists = new ArrayList<>(Collections.emptyList());
        //here

        List<Song> topSongs = getTopSongForFans(artistName, 5);
        Playlist fanClubPlaylist = new Playlist("%s Fan Club recommendations".formatted(artistName), user.getUsername());

        //fanClubPlaylist.addSong(topSongs);
        //for each song in topSongs add it to fanClubPlaylist
        for (Song topSong : topSongs) {
            fanClubPlaylist.addSong(topSong);
        }

        //playlists.add(fanClubPlaylist);
        user.getPlaylistRecommendations().add(fanClubPlaylist);
        artist.setPresentsInterest(true);



        return "The recommendations for user " + user.getUsername() + " have been updated successfully.";
    }

    public int getLikedSongsCountForArtist(User user, String artistName) {
        ArrayList<Song> likedSongs = user.getLikedSongs();

        long count = likedSongs.stream()
                .filter(song -> song.getArtist().equalsIgnoreCase(artistName))
                .count();

        return (int) count;

    }

    public List<User> getTopFansForArtist(String artistName, int topCount) {
        Map<String, Integer> fanCounts = new HashMap<>();

        // Iterate through all users
        for (User user : usersmap.values()) {
            // Count liked songs for the specified artist
            int likedSongsCount = getLikedSongsCountForArtist(user, artistName);

            // Update fanCounts map
            fanCounts.put(user.getUsername(), likedSongsCount);
        }

        // Sort fans by liked songs count in descending order
        List<User> topFans = fanCounts.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(topCount)
                .map(entry -> usersmap.get(entry.getKey()))
                .collect(Collectors.toList());

        return topFans;
    }

    //now make a method that takes top 5 liked songs from topfans and returns them
    public List<Song> getTopSongForFans(String artistName, int topCount) {
        List<User> topFans = getTopFansForArtist(artistName, topCount);
        List<Song> topSongs = new ArrayList<>();

        for (User user : topFans) {
            topSongs.add(user.getLikedSongs().get(0));
        }
        //sort topSongs by likes in descending order
        topSongs.sort(Comparator.comparingInt(Song::getLikes).reversed());

        //return sorted topSongs
        return topSongs;

    }

//    public String loadRecommendations(User user) {
//        if (!user.isStatus()) {
//            return "%s is offline.".formatted(user.getUsername());
//        }
//        //if list is empty
//        if (user.getSongRecommendations().isEmpty()) {
//            return "You can't load an empty audio collection!";
//        }
//
//        player.setSource(searchBar.getLastSelected(), searchBar.getLastSearchType());
//        searchBar.clearSelection();
//
//        player.pause();
//        // aici
//        Admin.getInstance().getUserListens(this);
//
//        return "Playback loaded successfully.";
//    }
}
