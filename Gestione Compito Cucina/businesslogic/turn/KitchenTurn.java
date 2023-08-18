package businesslogic.turn;


import businesslogic.user.User;
import persistence.BatchUpdateHandler;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class KitchenTurn extends Turn {
    private boolean complete;

    public KitchenTurn(){
        super();
    }
    public KitchenTurn(int id, Instant start, Instant end) {
        super(id, start, end);
    }

    public static void updateKitchenTurnAsComplete(KitchenTurn kitchenTurn) {
        String query = " UPDATE Turn SET complete=true  WHERE turn_id=?";
        int[] result = PersistenceManager.executeBatchUpdate(query, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, kitchenTurn.getId());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {

            }
        });
    }

    @Override
    public void addStaffAvaiable(User cook) {
        super.addStaffAvaiable(cook);
    }

    @Override
    public List<User> getStaffAvailable() {
        if(!complete)
            return super.getStaffAvailable();
        else return null;
    }

    public static ArrayList<KitchenTurn> getAllKitchenTurn(boolean onlyUncomplete) {
        String query = "SELECT * FROM Turn WHERE is_kitchen_turn = true";
        if(onlyUncomplete) query+= " AND complete = false";
        ArrayList<KitchenTurn> kitchenTurns = new ArrayList<>();

        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                KitchenTurn turn = new KitchenTurn();
                turn.complete = rs.getBoolean("complete");
                turn.setStart(rs.getTimestamp("start").toInstant());
                turn.setEnd(rs.getTimestamp("end").toInstant());
                turn.setId(rs.getInt("turn_id"));

                String cooks = "SELECT * FROM availability WHERE turn_id = " + turn.getId();
                PersistenceManager.executeQuery(cooks, new ResultHandler() {
                    @Override
                    public void handle(ResultSet rs) throws SQLException {
                        int user_id = rs.getInt("user_id");
                        User cook = User.loadUserById(user_id);
                        turn.addStaffAvaiable(cook);
                    }
                });

                kitchenTurns.add(turn);
            }
        });

        return kitchenTurns;
    }


    public KitchenTurn(Instant start, Instant end) {
        setStart(start);
        setEnd(end);
        complete = false;
    }

    public void setTurnAsComplete(){
        this.complete = true;
    }

    @Override
    public boolean isAvailable(User user) {
        return super.isAvailable(user);
    }
    @Override
    public String toString() {
        return super.toString()+ ", complete=" + complete;
    }


    public boolean isComplete() {
        return complete;
    }
}
