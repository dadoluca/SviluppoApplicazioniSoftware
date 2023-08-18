package businesslogic.recipe;

import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class KitchenDuty {
    private String name;
    public String getName(){
        return this.name;
    }

    private int id;
    public int getId(){
        return this.id;
    }
    public void setName(String name) {
        this.name = name;
    }

    public abstract List<Preparation> getSubDuties();

    @Override
    public String toString() {
        return "KitchenDuty{" +
                "name='" + name + '\'' +
                '}';
    }


    public static ArrayList<Preparation> loadAllSubKitchenDuty(int kitchenDuty_id) {
        String query = "SELECT * FROM SubDuties WHERE duty_id =" + kitchenDuty_id;
        ArrayList<Preparation> subDuties = new ArrayList<>();

        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                int subKitchenDuty_id = rs.getInt("subduty_id");
                Preparation kitchenDuty;
                kitchenDuty = Preparation.getPreparationById(subKitchenDuty_id);
                subDuties.add(kitchenDuty);
            }


        });
        return subDuties;
    }



    public static KitchenDuty loadKitchenDutyById(int dutyId) {
        String query = "SELECT * FROM kitchenduty WHERE duty_id =" + dutyId;
        final KitchenDuty[] kitchenDuty = new KitchenDuty[1];
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                boolean isRecipe = rs.getBoolean("is_recipe");
                String name = rs.getString("name");
                int id = rs.getInt("duty_id");
                if(isRecipe){
                    Recipe recipe = new Recipe();
                    recipe.setName(name);
                    recipe.setId(id);
                    kitchenDuty[0] = recipe;
                } else {
                    Preparation preparation = new Preparation();
                    preparation.setId(id);
                    preparation.setName(name);
                    kitchenDuty[0] = preparation;
                }

            }


        });
        return kitchenDuty[0];
    }

    protected void setId(int id) {
        this.id = id;
    }
}
