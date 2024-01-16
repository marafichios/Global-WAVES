package app.utils;

public class Notifications {
    private String name;
    private String description;

    public Notifications(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
