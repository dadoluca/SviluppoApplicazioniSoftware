package businesslogic.kitchen;

import businesslogic.recipe.KitchenDuty;
import businesslogic.leftover.LeftOver;
import businesslogic.turn.KitchenTurn;
import businesslogic.user.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.BatchUpdateHandler;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class KitchenActivity {
    private int id;
    private boolean outOfMenu;
    private int estimatedMinutes;
    private String amount;
    private int portions;
    private KitchenDuty kitchenDuty;
    private List<LeftOver> leftOversUsed;
    private List<KitchenTask> tasks;
    public KitchenActivity (KitchenDuty kitchenDuty, boolean outOfMenu){
        this.kitchenDuty = kitchenDuty;
        this.outOfMenu = outOfMenu;
        this.leftOversUsed = new ArrayList<>();
        this.tasks = new ArrayList<>();
    }

    public boolean getOutOfMenu(){
        return outOfMenu;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setEstimatedMinutes(int estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }

    public void setKitchenDuty(KitchenDuty kitchenDuty) {
        this.kitchenDuty = kitchenDuty;
    }

    public void setLeftOversUsed(List<LeftOver> leftOversUsed) {
        this.leftOversUsed = leftOversUsed;
    }

    public void setOutOfMenu(boolean outOfMenu) {
        this.outOfMenu = outOfMenu;
    }

    public void setPortions(int portions) {
        this.portions = portions;
    }

    public void setTasks(List<KitchenTask> tasks) {
        this.tasks = tasks;
    }

    public KitchenDuty getKitchenDuty() {
        return kitchenDuty;
    }

    public List<KitchenTask> getTasks() {
        return tasks;
    }

    public List<LeftOver> getLeftOversUsed() {
        return leftOversUsed;
    }

    public KitchenActivity setData(String amount, Integer portion, Integer estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
        if(amount != null){
            this.amount = amount;
        }
        if(portion != null){
            this.portions = portion;
        }
        return this;
    }

    public int getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public String getAmount() {
        return this.amount;
    }
    public int getPortions() {
        return this.portions;
    }



    public KitchenActivity useLeftOver(LeftOver leftOver) {
        leftOversUsed.add(leftOver);
        return this;
    }

    public static void addLeftOver(KitchenActivity kitchenActivity, LeftOver leftOver){
        String query = "INSERT INTO leftover (activity_id_producer, amount, portions) VALUES (?, ?, ?)";
        int[] result = PersistenceManager.executeBatchUpdate(query, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, kitchenActivity.getId());
                ps.setString(2, leftOver.amount);
                ps.setInt(3, leftOver.portions);
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
            }
        });
    }


    public KitchenTask assignTask(String amount, int portion, int estimatedMinutes, KitchenTurn turn, User cook) {
        KitchenTask kitchenTask = new KitchenTask(amount, portion, estimatedMinutes, turn, cook);
        tasks.add(kitchenTask);
        return kitchenTask;
    }

    public KitchenActivity changeData(String amount, Integer portion, Integer estimatedMinutes) {
        if(amount != null){
            this.amount = amount;
        }
        if(portion != null){
            this.portions = portion;
        }
        if(estimatedMinutes != null){
            this.estimatedMinutes = estimatedMinutes;
        }
        return this;
    }


    public static void updateKitchenActivity(KitchenActivity kitchenActivity){
        String query = " UPDATE kitchenactivities  SET amount=?, estimatedMinutes=?, portions=? WHERE activity_id=?";
        int[] result = PersistenceManager.executeBatchUpdate(query, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setString(1, kitchenActivity.amount);
                ps.setInt(2, kitchenActivity.estimatedMinutes);
                ps.setInt(3, kitchenActivity.portions);
                ps.setInt(4, kitchenActivity.getId());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {

            }
        });
    }

    public void removeTask(KitchenTask kitchenTask) {
        tasks.remove(kitchenTask);
    }

    public String toString_precompiled() {
        String toString = "";
        String duty = kitchenDuty==null ? "..":kitchenDuty.getName();
        toString = "[ adc per " + duty + " (nel menu:" + !outOfMenu + ")";
        if (kitchenDuty.getSubDuties().size() < 1) return toString + "]";
        for (KitchenDuty k : kitchenDuty.getSubDuties()) {
            toString += "\n\tsubduty:" + k.getName();
        }
        return toString+" ]";

    }
    @Override
    public String toString() {
        String toString = "";
        String duty = kitchenDuty==null ? "..":kitchenDuty.getName();
        toString = "[ adc per "+duty+", nel menu:"+!outOfMenu+
                ", portions: "+portions+", amount: "+ amount+" estmated time: "+
                estimatedMinutes+" minutes ";
        if(leftOversUsed.size()<1)return toString+"]";
        for(LeftOver l: leftOversUsed){
            toString += "\n\tleftover:" + l.activityProducer;
        }
        return toString+" ]";
    }

    public String toString_simple() {
        String toString = "";
        toString = "[ adc per "+kitchenDuty.getName();
        return toString+" ]";
    }

    public KitchenActivity(){}

    public static KitchenActivity loadKitchenActivityById(int kitchenActivityId){
        String query = "SELECT * FROM kitchenactivities WHERE activity_id ="+kitchenActivityId;
        KitchenActivity kitchenActivity = new KitchenActivity();

        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                //kitchenActivity = new KitchenActivity(null, rs.getBoolean("outOfMenu"));
                kitchenActivity.estimatedMinutes = rs.getInt("estimatedMinutes");
                kitchenActivity.amount = rs.getString("amount");
                kitchenActivity.portions = rs.getInt("portions");
                kitchenActivity.kitchenDuty = KitchenDuty.loadKitchenDutyById(rs.getInt("kitchen_duty_id"));
                kitchenActivity.leftOversUsed = LeftOver.loadAllLeftOversUsedByActivityId(kitchenActivityId);
            }
        });
        return kitchenActivity;
    }


    public static ObservableList<KitchenActivity> loadKitchenActivityBySummarySheetId(int summarySheetId){
        String query = "SELECT * FROM kitchenactivities WHERE summary_sheet_id ="+summarySheetId;
        ObservableList<KitchenActivity> kitchenActivities = FXCollections.observableArrayList();

        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                KitchenActivity kitchenActivity = new KitchenActivity();
                kitchenActivity.id = rs.getInt("activity_id");
                kitchenActivity.estimatedMinutes = rs.getInt("estimatedMinutes");
                kitchenActivity.amount = rs.getString("amount");
                kitchenActivity.portions = rs.getInt("portions");
                kitchenActivity.kitchenDuty = KitchenDuty.loadKitchenDutyById(rs.getInt("kitchen_duty_id"));
                kitchenActivity.leftOversUsed = LeftOver.loadAllLeftOversUsedByActivityId(kitchenActivity.id);
                kitchenActivities.add(kitchenActivity);
            }
        });
        return kitchenActivities;
    }


    public void printTasks(){
        System.out.println("Task for "+this.toString_simple()+" :");
        for(KitchenTask task: tasks){
            System.out.println(task.toString());
        }
    }
}
