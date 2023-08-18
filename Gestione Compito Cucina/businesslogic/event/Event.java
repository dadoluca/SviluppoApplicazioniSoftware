package businesslogic.event;

import businesslogic.menu.Menu;
import businesslogic.user.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.Instant;

public abstract class Event {
    private int id;
    public String customer;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
