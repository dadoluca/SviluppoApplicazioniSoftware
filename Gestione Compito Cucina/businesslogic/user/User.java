package businesslogic.user;

import businesslogic.turn.KitchenTurn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class User {

    private static Map<Integer, User> loadedUsers = FXCollections.observableHashMap();


    public static enum Role {SERVIZIO, CUOCO, CHEF, ORGANIZZATORE}
    private List<KitchenTurn> availabilities;
    private int id;
    private String username;
    private Set<Role> roles;

    public boolean isChef() {
        return roles.contains(Role.CHEF);
    }
    public boolean isOrganizer() {
        return roles.contains(Role.ORGANIZZATORE);
    }
    public boolean isCook() {
        return roles.contains(Role.CUOCO);
    }
    public boolean isServiceStaff() {
        return roles.contains(Role.SERVIZIO);
    }

    public User() {
        id = 0;
        username = "";
        this.roles = new HashSet<>();
    }

    public String getUserName() {
        return username;
    }

    public int getId() {
        return this.id;
    }

    public String toString() {
        String result = username;
        if (roles.size() > 0) {
            result += ": ";

            for (User.Role r : roles) {
                result += r.toString() + " ";
            }
        }
        return result;
    }

    // STATIC METHODS FOR PERSISTENCE

    public static User loadUserById(int uid) {
        if (loadedUsers.containsKey(uid)) return loadedUsers.get(uid);

        User load = new User();
        String userQuery = "SELECT * FROM Users WHERE id='" + uid + "'";
        PersistenceManager.executeQuery(userQuery, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                load.id = rs.getInt("id");
                load.username = rs.getString("username");
            }
        });
        if (load.id > 0) {
            loadedUsers.put(load.id, load);
            String roleQuery = "SELECT * FROM UserRoles WHERE user_id=" + load.id;
            PersistenceManager.executeQuery(roleQuery, new ResultHandler() {
                @Override
                public void handle(ResultSet rs) throws SQLException {
                    String role = rs.getString("role_id");
                    switch (role.charAt(0)) {
                        case 'c':
                            load.roles.add(User.Role.CUOCO);
                            break;
                        case 'h':
                            load.roles.add(User.Role.CHEF);
                            break;
                        case 'o':
                            load.roles.add(User.Role.ORGANIZZATORE);
                            break;
                        case 's':
                            load.roles.add(User.Role.SERVIZIO);
                    }
                }
            });
        }
        return load;
    }

    public static User loadUser(String username) {
        User u = new User();
        String userQuery = "SELECT * FROM Users WHERE username='" + username + "'";
        PersistenceManager.executeQuery(userQuery, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                u.id = rs.getInt("id");
                u.username = rs.getString("username");
            }
        });
        if (u.id > 0) {
            loadedUsers.put(u.id, u);
            String roleQuery = "SELECT * FROM UserRoles WHERE user_id=" + u.id;
            PersistenceManager.executeQuery(roleQuery, new ResultHandler() {
                @Override
                public void handle(ResultSet rs) throws SQLException {
                    String role = rs.getString("role_id");
                    switch (role.charAt(0)) {
                        case 'c':
                            u.roles.add(User.Role.CUOCO);
                            break;
                        case 'h':
                            u.roles.add(User.Role.CHEF);
                            break;
                        case 'o':
                            u.roles.add(User.Role.ORGANIZZATORE);
                            break;
                        case 's':
                            u.roles.add(User.Role.SERVIZIO);
                    }
                }
            });
        }
        return u;
    }

    public static ArrayList<User> loadCooks() {

        User load = new User();
        String query = "SELECT * FROM userroles, users WHERE userroles.user_id = users.id AND role_id = 'c' ";
        ArrayList<User> cooks = new ArrayList<>();
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                //boolean isRecipe = rs.getBoolean("is_recipe");
                User c = new User();
                c.id = rs.getInt("id");
                c.username = rs.getString("username");
                c.roles.add(User.Role.CUOCO);
                cooks.add(c);
                if (!loadedUsers.containsKey(c.getId())) loadedUsers.put(c.getId(), c);
                String roleQuery = "SELECT * FROM UserRoles WHERE user_id=" + c.id;
                PersistenceManager.executeQuery(roleQuery, new ResultHandler() {
                    @Override
                    public void handle(ResultSet rs) throws SQLException {
                        String role = rs.getString("role_id");
                        switch (role.charAt(0)) {
                            case 'c':
                                break;
                            case 'h':
                                c.roles.add(User.Role.CHEF);
                                break;
                            case 'o':
                                c.roles.add(User.Role.ORGANIZZATORE);
                                break;
                            case 's':
                                c.roles.add(User.Role.SERVIZIO);
                        }
                    }
                });
            }
        });
        return cooks;
    }

    public static ObservableList<User> loadCooksByKTurnId(int kitchenTurn_id) {
        String query = "SELECT * FROM userroles, users, availability" +
                " WHERE userroles.user_id = users.id " +
                " AND availability.user_id = users.id" +
                " AND availability.turn_id = "+ kitchenTurn_id+
                " AND role_id = 'c' ";
        ObservableList<User> cooks = FXCollections.observableArrayList();
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                User c = new User();
                c.id = rs.getInt("id");
                c.username = rs.getString("username");
                c.roles.add(User.Role.CUOCO);
                cooks.add(c);
                if (!loadedUsers.containsKey(c.getId())) loadedUsers.put(c.getId(), c);
            }
        });
        return cooks;
    }

}

