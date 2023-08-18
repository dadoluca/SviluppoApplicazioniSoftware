package businesslogic.turn;

import businesslogic.user.User;

import java.time.Instant;
import java.util.List;

public class ServiceTurn extends Turn{
    public List<User> members;

    public ServiceTurn(int id, Instant start, Instant end) {
        super(id, start, end);
    }
}
