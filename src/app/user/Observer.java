package app.user;

import app.utils.Notifications;

public interface Observer {
    void update(Notifications notifications);
}
