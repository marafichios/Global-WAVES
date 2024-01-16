package app.pages;

import app.user.User;

public class PreviousPage implements NavigatePages{
    @Override
    public String execute(User user) {
        return user.previousPage();
    }
}
