package app.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import app.audio.Collections.Album;
import app.audio.Collections.AlbumOutput;
import app.audio.Files.Song;
import app.pages.ArtistPage;
import app.pages.HostPage;
import app.pages.Page;
import app.utils.Notifications;
import fileio.input.CommandInput;
import lombok.Getter;
import lombok.Setter;

/**
 * The type Artist.
 */
public final class Artist extends ContentCreator implements Subject {
    private ArrayList<Album> albums;
    private ArrayList<Merchandise> merch;
    private ArrayList<Event> events;
    private List<Observer> observers = new ArrayList<>();
    @Getter
    @Setter
    private List<Merchandise> purchasedMerch = new ArrayList<>();

    private boolean presentsInterest = false;

    public boolean isPresentsInterest() {
        return presentsInterest;
    }

    public void setPresentsInterest(boolean presentsInterest) {
        this.presentsInterest = presentsInterest;
    }



    public List<Merchandise> addPurchasedMerch(Merchandise merch) {
        List<Merchandise> allMerch = new ArrayList<>();
        allMerch.addAll(purchasedMerch);
        return allMerch;
    }


    /**
     * Instantiates a new Artist.
     *
     * @param username the username
     * @param age      the age
     * @param city     the city
     */
    public Artist(final String username, final int age, final String city) {
        super(username, age, city);
        albums = new ArrayList<>();
        merch = new ArrayList<>();
        events = new ArrayList<>();
        super.setPage(new ArtistPage(this), this, null);
    }

    /**
     * Gets albums.
     *
     * @return the albums
     */
    public ArrayList<Album> getAlbums() {
        return albums;
    }

    /**
     * Gets merch.
     *
     * @return the merch
     */
    public ArrayList<Merchandise> getMerch() {
        return merch;
    }

    /**
     * Gets events.
     *
     * @return the events
     */
    public ArrayList<Event> getEvents() {
        return events;
    }

    public String getName() {
        return super.getUsername();
    }

    /**
     * Gets event.
     *
     * @param eventName the event name
     * @return the event
     */
    public Event getEvent(final String eventName) {
        for (Event event : events) {
            if (event.getName().equals(eventName)) {
                return event;
            }
        }

        return null;
    }

    /**
     * Gets album.
     *
     * @param albumName the album name
     * @return the album
     */
    public Album getAlbum(final String albumName) {
        for (Album album : albums) {
            if (album.getName().equals(albumName)) {
                return album;
            }
        }

        return null;
    }

    /**
     * Gets all songs.
     *
     * @return the all songs
     */
    public List<Song> getAllSongs() {
        List<Song> songs = new ArrayList<>();
        albums.forEach(album -> songs.addAll(album.getSongs()));

        return songs;
    }

    /**
     * Show albums array list.
     *
     * @return the array list
     */
    public ArrayList<AlbumOutput> showAlbums() {
        ArrayList<AlbumOutput> albumOutput = new ArrayList<>();
        for (Album album : albums) {
            albumOutput.add(new AlbumOutput(album));
        }

        return albumOutput;
    }

    /**
     * Get user type
     *
     * @return user type string
     */
    public String userType() {
        return "artist";
    }

    @Override
    public void attach(Observer o) {
        observers.add(o);
    }

    @Override
    public void detach(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers(Notifications notification) {
        for (Observer observer : observers) {
            observer.update(notification);
        }
    }


    public String subscribe(User user) {

        Artist subject = ((ArtistPage) user.getCurrentPage()).getArtist();
        if (subject != this) {
            return "To subscribe you need to be on the page of an artist or host.";
        }

        String username = user.getUsername();
        if (observers.contains(user)) {
            detach(user); // Unsubscribe
            return username + " unsubscribed from " + this.getUsername() + " successfully.";
        } else {
            attach(user); // Subscribe
            return username + " subscribed to " + this.getUsername() + " successfully.";
        }
    }


}
