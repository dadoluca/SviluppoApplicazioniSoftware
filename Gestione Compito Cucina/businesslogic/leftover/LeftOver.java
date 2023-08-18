package businesslogic.leftover;

import businesslogic.kitchen.KitchenActivity;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class LeftOver {
    private int id;
    public String amount;
    public int portions;
    public KitchenActivity activityProducer;

    public int getId() {
        return id;
    }


    public static ArrayList<LeftOver> loadAllLeftOversUnused(){
        String query = "SELECT * FROM leftover WHERE activity_id_consumer IS NULL";
        ArrayList<LeftOver> leftOvers = new ArrayList<>();

        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                //boolean isRecipe = rs.getBoolean("is_recipe");
              LeftOver leftOver = new LeftOver();
              leftOver.id = rs.getInt("leftover_id");
              leftOver.amount = rs.getString("amount");
              leftOver.portions = rs.getInt("portions");
              leftOver.activityProducer = KitchenActivity.loadKitchenActivityById(rs.getInt("activity_id_producer"));
              leftOvers.add(leftOver);
            }
        });
        return leftOvers;
    }


    public static ArrayList<LeftOver> loadAllLeftOversUsedByActivityId(int activity_id){
        String query = "SELECT * FROM leftover WHERE activity_id_consumer =" + activity_id;
        ArrayList<LeftOver> leftOvers = new ArrayList<>();

        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                //boolean isRecipe = rs.getBoolean("is_recipe");
                LeftOver leftOver = new LeftOver();
                leftOver.id = rs.getInt("leftover_id");
                leftOver.amount = rs.getString("amount");
                leftOver.portions = rs.getInt("portions");
                leftOver.activityProducer = KitchenActivity.loadKitchenActivityById(rs.getInt("activity_id_producer"));
                leftOvers.add(leftOver);
            }


        });
        return leftOvers;
    }


}
