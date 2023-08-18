package businesslogic.recipe;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Recipe extends KitchenDuty{
    private static Map<Integer, Recipe> all = new HashMap<>();
    private ArrayList<Preparation> subDuties;



    public Recipe() {
        this.subDuties = new ArrayList<>();
    }

    public Recipe(String name) {
        setId(0);
        super.setName(name);
        this.subDuties = new ArrayList<>();
    }

    public String getName() {
        return super.getName();
    }

    public int getId() {
        return super.getId();
    }

    public void setId(int id) {
        super.setId(id);
    }


    public String toString() {
        return super.getName();
    }

    // STATIC METHODS FOR PERSISTENCE

    public static ObservableList<Recipe> loadAllRecipes() {
        String query = "SELECT * FROM kitchenduty WHERE is_recipe = TRUE";
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                int id = rs.getInt("duty_id");
                if (all.containsKey(id)) {
                    Recipe rec = all.get(id);
                    rec.setName(rs.getString("name"));
                } else {
                    Recipe rec = new Recipe(rs.getString("name"));
                    rec.setId(id);
                    rec.subDuties = loadAllSubKitchenDuty(id);
                    all.put(rec.getId(), rec);
                }
            }
        });
        ObservableList<Recipe> ret =  FXCollections.observableArrayList(all.values());
        Collections.sort(ret, new Comparator<Recipe>() {
            @Override
            public int compare(Recipe o1, Recipe o2) {
                return (o1.getName().compareTo(o2.getName()));
            }
        });
        return ret;
    }

    public static ObservableList<Recipe> getAllRecipes() {
        return FXCollections.observableArrayList(all.values());
    }

    public static Recipe loadRecipeById(int id) {
        if (all.containsKey(id)) return all.get(id);
        Recipe rec = new Recipe();
        String query = "SELECT * FROM kitchenduty WHERE duty_id = " + id + " AND WHERE is_recipe = TRUE";
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                    rec.setName(rs.getString("name"));
                    rec.setId(id);
                    rec.subDuties = loadAllSubKitchenDuty(id);
                    all.put(rec.getId(), rec);
            }
        });
        return rec;
    }

    @Override
    public List<Preparation> getSubDuties() {
        return subDuties;
    }

}
