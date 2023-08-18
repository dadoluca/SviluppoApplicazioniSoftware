package businesslogic.kitchen;

import businesslogic.turn.KitchenTurn;
import businesslogic.user.User;
import persistence.BatchUpdateHandler;
import persistence.PersistenceManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class KitchenTask {
    private int id;
    private int estimatedMinutes;
    private String amount;
    private int portions;
    private User cook;
    private KitchenTurn turn;

    public void setId(int id) {
        this.id = id;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public int getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public int getPortions() {
        return portions;
    }

    public KitchenTurn getTurn() {
        return turn;
    }

    public String getAmount() {
        return amount;
    }

    public User getCook() {
        return cook;
    }

    public void setCook(User cook) {
        this.cook = cook;
    }

    public void setEstimatedMinutes(int estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }

    public void setPortions(int portions) {
        this.portions = portions;
    }

    public void setTurn(KitchenTurn turn) {
        this.turn = turn;
    }

    public static void updateKitchenTask(KitchenTask kitchenTask) {
        String query = " UPDATE kitchentask  SET amount=?, estimatedMinutes=?, portions=? WHERE task_id=?";
        int[] result = PersistenceManager.executeBatchUpdate(query, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setString(1, kitchenTask.amount);
                ps.setInt(2, kitchenTask.estimatedMinutes);
                ps.setInt(3, kitchenTask.portions);
                ps.setInt(4, kitchenTask.getId());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {

            }
        });

    }



    public static void addKitchenTask(KitchenActivity kitchenActivity, KitchenTask kitchenTask){
        String query = "INSERT INTO kitchentask (activity_id, estimatedMinutes, amount, portions, cook, turn_id) VALUES (?, ?, ?, ?, ?, ?)";
        int[] result = PersistenceManager.executeBatchUpdate(query, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, kitchenActivity.getId());
                ps.setInt(2, kitchenTask.estimatedMinutes);
                ps.setString(3, kitchenTask.amount);
                ps.setInt(4, kitchenTask.portions);
                ps.setInt(5, kitchenTask.cook.getId());
                ps.setInt(6, kitchenTask.turn.getId());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                kitchenTask.id = rs.getInt(1);
            }
        });
    }


    public static void deleteTask(KitchenTask kitchenTask) {
        String query = " DELETE FROM kitchentask  WHERE task_id = ?";
        int[] result = PersistenceManager.executeBatchUpdate(query, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, kitchenTask.getId());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {

            }
        });
    }

    public KitchenTask update(String amount, Integer portion, Integer estimatedMinutes, KitchenTurn turn, User cook) {
        if(amount != null){
            this.amount = amount;
        }
        if(portion != null){
            this.portions = portion;
        }
        if(estimatedMinutes != null){
            this.estimatedMinutes = estimatedMinutes;
        }
        if(turn != null){
            this.turn = turn;
        }
        if(cook != null){
            this.cook = cook;
        }
        return this;
    }

    public KitchenTask(String amount, Integer portion, Integer estimatedMinutes, KitchenTurn turn, User cook){
        this.estimatedMinutes = estimatedMinutes;
        if(portion != null){
            this.portions = portion;
        }
        if(amount != null){
            this.amount = amount;
        }
        this.turn = turn;
        this.cook = cook;
    }


    public int getId(){
        return id;
    }

    @Override
    public String toString() {
        String toString = "";
        toString = "\t[ cdc\tcook: "+cook.getUserName()+
                "\tturn start: "+turn.getStart()+
                "\testimated time: "+ estimatedMinutes+" minutes";
        return toString+" ]";
    }
}
