package businesslogic.recipe;

import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Preparation extends KitchenDuty{

    public String getName(){
        return super.getName();
    }

    public int getId(){
        return super.getId();
    }
    ArrayList<Preparation> subDuties;

    public Preparation(){
        subDuties = new ArrayList<>();
    }

    public void setName(String name) {
        super.setName(name);
    }

    public void setId(int id) {
        super.setId(id);
    }

    public static Preparation getPreparationById(int id) {
        String query = "SELECT * FROM kitchenduty WHERE duty_id =" + id + " AND is_recipe = FALSE";
        Preparation preparation = new Preparation();

        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                preparation.setId(rs.getInt("duty_id"));
                preparation.setName(rs.getString("name"));
                preparation.subDuties = loadAllSubKitchenDuty(preparation.getId());
            }
        });
        return preparation;
    }

    @Override
    public List<Preparation> getSubDuties() {
        return subDuties;
    }
}
