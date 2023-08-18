package businesslogic.kitchen;


import businesslogic.event.Service;
import businesslogic.menu.Menu;
import businesslogic.menu.MenuItem;
import businesslogic.menu.Section;
import businesslogic.recipe.KitchenDuty;
import businesslogic.recipe.Preparation;
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

public class SummarySheet {
    private String name;
    private int id;
    private ObservableList<KitchenActivity> activities;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SummarySheet(Menu menu){
        activities = FXCollections.observableArrayList();
        List<MenuItem> menuItems = new ArrayList<>();

        for(Section sec : menu.getSections()){
            for(MenuItem menuItem : sec.getItems()) {
                menuItems.add(menuItem);
            }
        }

        for(MenuItem menuItem: menu.getFreeItems()){
            menuItems.add(menuItem);
        }


        for(MenuItem menuItem : menuItems){
            KitchenActivity activity = new KitchenActivity(menuItem.getItemRecipe(), false);
            activities.add(activity);
            List<Preparation> subDuties = activity.getKitchenDuty().getSubDuties();
            if(subDuties.size() > 0){
                for(KitchenDuty subDuty: subDuties){
                    KitchenActivity sub_activity = new KitchenActivity(subDuty, false);
                    activities.add(sub_activity);
                }
            }
        }
    }

    public SummarySheet() {
        activities=FXCollections.observableArrayList();
    }

    public int getKitchenActivityPosition(KitchenActivity kitchenActivity){
        return activities.indexOf(kitchenActivity);
    }

    public List<KitchenActivity> addOutOfMenu(KitchenDuty kitchenDuty){
        List<KitchenActivity> newActivities = new ArrayList<>();
        KitchenActivity kitchenActivity = new KitchenActivity(kitchenDuty, true);
        newActivities.add(kitchenActivity);
        for(KitchenDuty subDuty: kitchenDuty.getSubDuties()){
            KitchenActivity ac = new KitchenActivity(subDuty, true);
            newActivities.add(ac);
        }
        this.activities.addAll(newActivities);

        return newActivities;
    }


    public static void addOutOfMenu(SummarySheet summarySheet, List<KitchenActivity> kitchenActivities){
        String query = "INSERT INTO catering.kitchenactivities (summary_sheet_id, outOfMenu, estimatedMinutes, amount, portions, position, kitchen_duty_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        int[] result = PersistenceManager.executeBatchUpdate(query, kitchenActivities.size(), new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, summarySheet.getId());
                ps.setBoolean(2, kitchenActivities.get(batchCount).getOutOfMenu());
                ps.setInt(3, kitchenActivities.get(batchCount).getEstimatedMinutes());
                ps.setString(4, kitchenActivities.get(batchCount).getAmount());
                ps.setInt(5, kitchenActivities.get(batchCount).getPortions());
                ps.setInt(6, summarySheet.getKitchenActivityPosition(kitchenActivities.get(batchCount)));
                ps.setInt(7, kitchenActivities.get(batchCount).getKitchenDuty().getId());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                kitchenActivities.get(count).setId(rs.getInt(1));
            }
        });
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static void saveSummarySheet(SummarySheet summarySheet, Service service){
        String sheetInsert = "INSERT INTO catering.summarysheets (service_id) VALUES (?);";
        int[] result = PersistenceManager.executeBatchUpdate(sheetInsert, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, service.getId());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // should be only one
                if (count == 0) {
                    summarySheet.id = rs.getInt(1);
                }
            }
        });


        String activityInsert = "INSERT INTO catering.kitchenactivities (summary_sheet_id, outOfMenu, estimatedMinutes, amount, portions, position, kitchen_duty_id) VALUES (?, ?, ?, ?, ?, ?, ?);";
        int[] resultActivity = PersistenceManager.executeBatchUpdate(activityInsert, summarySheet.activities.size(), new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, summarySheet.getId());
                ps.setBoolean(2, summarySheet.activities.get(batchCount).getOutOfMenu());
                ps.setInt(3, summarySheet.activities.get(batchCount).getEstimatedMinutes());
                ps.setString(4, summarySheet.activities.get(batchCount).getAmount());
                ps.setInt(5, summarySheet.activities.get(batchCount).getPortions());
                ps.setInt(6, batchCount);
                ps.setInt(7, summarySheet.activities.get(batchCount).getKitchenDuty().getId());

            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                summarySheet.activities.get(count).setId(rs.getInt(1));
            }
        });
    }
    public List<KitchenActivity> getActivities() {
        return activities;
    }

    public KitchenActivity getActivityById(int id) {
        for(KitchenActivity k : activities){
            if(k.getId() == id){
                return k;
            }
        }
        return null;
    }

    public boolean containsKitchenActivity(KitchenActivity kitchenActivity) {
        for(KitchenActivity k:activities){
            if(k.getId() == kitchenActivity.getId()){
                return true;
            }
        }
        return false;
    }

    public void moveKitchenActivity(int position, KitchenActivity kitchenActivity) {
        this.activities.remove(kitchenActivity);
        this.activities.add(position, kitchenActivity);
    }

    public void removeKitchenActivity(KitchenActivity kitchenActivity) {
        this.activities.remove(kitchenActivity);
    }

    public int ActivitiesListSize() {
        return this.activities.size();
    }

    public static void saveActivityOrder(SummarySheet summarySheet) {
        String upd = "UPDATE kitchenactivities SET position = ? WHERE activity_id = ?";
        PersistenceManager.executeBatchUpdate(upd, summarySheet.activities.size(), new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, batchCount);
                ps.setInt(2, summarySheet.activities.get(batchCount).getId());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // no generated ids to handle
            }
        });
    }

    public static void deleteKitchenActivity(KitchenActivity kitchenActivity) {
        String upd = "DELETE FROM kitchenactivities WHERE activity_id=?;";
        PersistenceManager.executeBatchUpdate(upd, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, kitchenActivity.getId());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // no generated ids to handle
            }
        });
    }


    public void printPrecompiledSheet(){
        System.out.println("Attività di cucina create durante la precompilazione del foglio riepilogativo:");
        for(KitchenActivity activity: activities){
            System.out.println(activity.toString_precompiled());
        }
    }

    public void printSummarySheet(){
        System.out.println("Summary Sheet:");
        for(KitchenActivity activity: activities){
            System.out.println(activity.toString());
        }
    }

    public static ArrayList<SummarySheet>  loadAllSummarySheet(){
            String query = "SELECT * FROM summarysheets WHERE true";
            ArrayList<SummarySheet> summarySheets = new ArrayList<>();
            PersistenceManager.executeQuery(query, new ResultHandler() {
                @Override
                public void handle(ResultSet rs) throws SQLException {
                    SummarySheet summarySheet = new SummarySheet();
                    int serviceId = rs.getInt("service_id");
                    int id = rs.getInt("summary_sheet_id");
                    String name = rs.getString("name");
                    summarySheet.setName(name);
                    summarySheet.setId(id);
                    summarySheet.activities = KitchenActivity.loadKitchenActivityBySummarySheetId(id);
                    summarySheets.add(summarySheet);
                }
            });

            return summarySheets;


    }

}
