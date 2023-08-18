package businesslogic.kitchen;

import businesslogic.turn.KitchenTurnInfo;
import businesslogic.turn.KitchenTurnItemInfo;
import businesslogic.user.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

public class SummarySheetInfo implements SummarySheetItemInfo {
    private String name;
    private int id;
    private int service_id;
    private ObservableList<KitchenActivity> activities;

    public SummarySheetInfo() {
        id = 0;
    }

    public ObservableList<KitchenActivity> getActivities() {
        return FXCollections.unmodifiableObservableList(this.activities);
    }

    public String toString() {
        return "name: " +name;
    }

    public static ObservableList<SummarySheetInfo> loadAllSummarySheetInfo(){
        String query = "SELECT * FROM summarysheets WHERE true";
        ObservableList<SummarySheetInfo> all= FXCollections.observableArrayList();

        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                SummarySheetInfo s = new SummarySheetInfo();
                s.id = rs.getInt("summary_sheet_id");
                s.service_id = rs.getInt("service_id");
                s.name = rs.getString("name");
                all.add(s);
            }
        });
        for(SummarySheetInfo ssi: all ){
            ssi.activities = KitchenActivity.loadKitchenActivityBySummarySheetId(ssi.id);
        }
        return all;
    }
}
