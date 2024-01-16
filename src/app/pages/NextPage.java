package app.pages;

import app.user.User;

public class NextPage implements NavigatePages{
    @Override
    public String execute(User user) {
        // Implement logic to navigate to the next page
        // Update user's navigation history

        return user.nextPage();
    }
}
