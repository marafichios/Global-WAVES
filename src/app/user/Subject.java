package app.user;

import app.utils.Notifications;

public interface Subject {
    void attach(Observer o);
    void detach(Observer o);
    void notifyObservers(Notifications notification);

    //getName() method
    String getName();
}
