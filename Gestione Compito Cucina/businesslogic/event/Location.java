package businesslogic.event;

import businesslogic.menu.Menu;
import businesslogic.recipe.Recipe;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Location {
    private static Map<Integer, Location> all = new HashMap<>();
    private int id;
    private String name;
    private String address;
    public Location(String name, String address){
        this.name = name;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Location(){
        this.name = "";
        this.address = "";
        this.id = 0;
    }

    public static Location loadLocationById(int id) {
        if (all.containsKey(id)) return all.get(id);
        String query ="SELECT * FROM Location WHERE id=" + id;
        Location location = new Location();
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                String name = rs.getString("name");
                int id = rs.getInt("id");
                String address = rs.getString("address");
                location.setAddress(address);
                location.setName(name);
                location.setId(id);
                all.put(location.id, location);
            }
        });
        return location;
    }

}
