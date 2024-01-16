package statistics;

import app.user.User;

public interface StatisticStrategy {
    WrappedResult generateStatistics(User user);
}
