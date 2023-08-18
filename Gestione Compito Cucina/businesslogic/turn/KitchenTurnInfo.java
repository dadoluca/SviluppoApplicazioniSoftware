package businesslogic.turn;

import businesslogic.event.ServiceInfo;
import businesslogic.user.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class KitchenTurnInfo implements KitchenTurnItemInfo {
    private int id;
    private Instant start;
    private Instant end;
    private boolean complete;
    private ObservableList<User> staffAvaiable;

    public KitchenTurnInfo() {
        id = 0;
    }

    public ObservableList<User> getstaffAvaiable() {
        return FXCollections.unmodifiableObservableList(this.staffAvaiable);
    }

    public String toString() {
        return "start: " +start+ "\tend: " + end + "\tcomplete: " + complete;
    }

    public static ObservableList<KitchenTurnInfo> loadAllTurnInfo(){
        String query = "SELECT * FROM Turn WHERE is_kitchen_turn = true";
        ObservableList<KitchenTurnInfo> all= FXCollections.observableArrayList();

        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                KitchenTurnInfo turnInfo = new KitchenTurnInfo();
                turnInfo.complete = rs.getBoolean("complete");
                turnInfo.start = rs.getTimestamp("start").toInstant();
                turnInfo.end = rs.getTimestamp("end").toInstant();
                turnInfo.id = rs.getInt("turn_id");
                all.add(turnInfo);
            }
        });
        for(KitchenTurnInfo kti: all ){
            kti.staffAvaiable = User.loadCooksByKTurnId(kti.id);
        }
        return all;
    }
}
