package app.user;

import app.audio.Collections.Podcast;
import app.pages.HostPage;
import app.utils.Notifications;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Host.
 */
public final class Host extends ContentCreator implements Subject {
    private ArrayList<Podcast> podcasts;
    /**
     * -- GETTER --
     *  Gets announcements.
     *
     * @return the announcements
     */
    @Getter
    private ArrayList<Announcement> announcements;
    private List<Observer> observers = new ArrayList<>();


    /**
     * Instantiates a new Host.
     *
     * @param username the username
     * @param age      the age
     * @param city     the city
     */
    public Host(final String username, final int age, final String city) {
        super(username, age, city);
        podcasts = new ArrayList<>();
        announcements = new ArrayList<>();

        super.setPage(new HostPage(this), null, this);
    }

    /**
     * Gets podcasts.
     *
     * @return the podcasts
     */
    public ArrayList<Podcast> getPodcasts() {
        return podcasts;
    }

    public String getName() {
        return super.getUsername();
    }

    /**
     * Sets podcasts.
     *
     * @param podcasts the podcasts
     */
    public void setPodcasts(final ArrayList<Podcast> podcasts) {
        this.podcasts = podcasts;
    }

    /**
     * Sets announcements.
     *
     * @param announcements the announcements
     */
    public void setAnnouncements(final ArrayList<Announcement> announcements) {
        this.announcements = announcements;
    }

    /**
     * Gets podcast.
     *
     * @param podcastName the podcast name
     * @return the podcast
     */
    public Podcast getPodcast(final String podcastName) {
        for (Podcast podcast: podcasts) {
            if (podcast.getName().equals(podcastName)) {
                return podcast;
            }
        }

        return null;
    }

    /**
     * Gets announcement.
     *
     * @param announcementName the announcement name
     * @return the announcement
     */
    public Announcement getAnnouncement(final String announcementName) {
        for (Announcement announcement: announcements) {
            if (announcement.getName().equals(announcementName)) {
                return announcement;
            }
        }

        return null;
    }

    @Override
    public String userType() {
        return "host";
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
}
