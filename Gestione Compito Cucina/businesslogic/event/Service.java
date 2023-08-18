package businesslogic.event;

import businesslogic.CatERing;
import businesslogic.kitchen.Proposal;
import businesslogic.kitchen.SummarySheet;
import businesslogic.menu.Menu;
import businesslogic.menu.MenuManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;

public class Service implements EventItemInfo {
    private int id;
    private int event_id;
    private String name;
    private int dayOffset;
    private Time startHour;
    private Time endHour;
    private String typology;
    private ServiceState state;

    private String place;

    private Menu menu;

    private SummarySheet summarySheet;
    private List<Proposal> proposals;
    private int numberOfParticipants;

    public Service(String name, int dayOffset, Time startHour, Time endHour, String typology, String place, int numberOfParticipants) {
        this.name = name;
        this.dayOffset = dayOffset;
        this.startHour = startHour;
        this.endHour = endHour;
        this.typology = typology;
        this.state = ServiceState.TOBECONFIRMED;
        this.place = place;
        this.menu = null;
        this.summarySheet = null;
        this.proposals = null;
        this.numberOfParticipants = numberOfParticipants;
    }

    public SummarySheet createSummarySheet(){
        SummarySheet newSummarySheet = new SummarySheet(this.getMenu());
        this.summarySheet = newSummarySheet;
        return newSummarySheet;
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

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public boolean isConfirmed() {
        return state == ServiceState.CONFIRMED;
    }

    public void setState(ServiceState state){
        this.state = state;
    }


    public static ObservableList<Service> loadAllServicesForEvent(Event event) {
        ObservableList<Service> result = FXCollections.observableArrayList();
        String query = "SELECT * FROM Services WHERE event_id = " + event.getId();
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                String name = rs.getString("name");
                int serviceId = rs.getInt("id");
                int menuId = rs.getInt("menu_id");
                int dayOffset = rs.getInt("day_offset");
                Time startHour = rs.getTime("start_hour");
                Time endHour = rs.getTime("end_hour");
                String typology = rs.getString("typology");
                String place = rs.getString("place");
                String s = rs.getString("state");
                int numberOfParticipants = rs.getInt("number_of_participants");
                ServiceState state = ServiceState.TOBECONFIRMED;
                if (s.equals("confirmed")) {
                    state = ServiceState.CONFIRMED;
                } else if (s.equals("ended")) {
                    state = ServiceState.ENDED;
                } else if (s.equals("cancelled")) {
                    state = ServiceState.CANCELLED;
                }

                Service service = new Service(name, dayOffset, startHour, endHour, typology, place, numberOfParticipants);
                service.setId(serviceId);
                service.setState(state);

                        service.setMenu(Menu.loadMenuById(menuId));

                result.add(service);
            }
        });

        return result;
    }

}

enum ServiceState {
    CONFIRMED, ENDED, CANCELLED, TOBECONFIRMED
}