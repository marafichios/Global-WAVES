package app.pages;

import app.audio.Collections.Podcast;
import app.user.Announcement;
import app.user.Host;
import app.user.Subject;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * The type Host page.
 */
public final class HostPage implements Page {
    private List<Podcast> podcasts;
    private List<Announcement> announcements;
    @Getter
    @Setter
    private Host host;


    /**
     * Instantiates a new Host page.
     *
     * @param host the host
     */
    public HostPage(final Host host) {
        podcasts = host.getPodcasts();
        announcements = host.getAnnouncements();
    }

    @Override
    public String printCurrentPage() {
        return "Podcasts:\n\t%s\n\nAnnouncements:\n\t%s"
               .formatted(podcasts.stream().map(podcast -> "%s:\n\t%s\n"
                          .formatted(podcast.getName(),
                                     podcast.getEpisodes().stream().map(episode -> "%s - %s"
                          .formatted(episode.getName(), episode.getDescription())).toList()))
                          .toList(),
                          announcements.stream().map(announcement -> "%s:\n\t%s\n"
                          .formatted(announcement.getName(), announcement.getDescription()))
                          .toList());
    }
}
