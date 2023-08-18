package businesslogic.event;

import businesslogic.user.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class SingleEvent extends Event{
    ObservableList<Service> services;
    Location location;
    int numberOfParticipants;
    Date startDate;
    Date endDate;
    String name;
    EventState state;
    String notes;
    String finalNotes;
    String finalDocuments;
    RecurringEvent recurringEvent;
    int organize_id;
    User chef;
    public boolean hasService(Service service){
        return services.contains(service);
    }
    public SingleEvent(int organize_id, String name, String customer, Date startDate, Date endDate, int numberOfParticipants, Location location){
        this.name = name;
        this.customer = customer;
        this.startDate = startDate;
        this.endDate = endDate;
        this.numberOfParticipants = numberOfParticipants;
        this.location = location;
        this.state = EventState.PLANNED;
        this.notes = "";
        this.finalDocuments = null;
        this.recurringEvent = null;
        this.chef = null;
        this.organize_id = organize_id;
    }


    public void setState(EventState state) {
        this.state = state;
    }

    public boolean hasRecurring() {
        return recurringEvent != null;
    }

    public void setFinalDocuments(String finalDocuments) {
        this.finalDocuments = finalDocuments;
    }

    public void setFinalNotes(String finalNotes) {
        this.finalNotes = finalNotes;
    }


    public static ObservableList<SingleEvent> loadAllSingleEvent() {
        ObservableList<SingleEvent> result = FXCollections.observableArrayList();
        String query = "SELECT * FROM singleevent WHERE true";
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                String name = rs.getString("name");
                int id = rs.getInt("id");
                String customer = rs.getString("customer");
                String finalNotes = rs.getString("finalNotes");
                String finalDocuments = rs.getString("finalDocuments");
                Date startDate = rs.getDate("start_date");
                Date endDate = rs.getDate("end_date");
                int organizer_id = rs.getInt("organizer_id");
                String s = rs.getString("state");
                int numberOfParticipants = rs.getInt("number_of_participants");
                int location_id = rs.getInt("location_id");
                EventState state = EventState.PLANNED;
                if (s.equals("ended")) {
                    state = EventState.ENDED;
                } else if (s.equals("ended")) {
                    state = EventState.ENDED;
                } else if (s.equals("canceled")) {
                    state = EventState.CANCELED;
                }
                Location location = Location.loadLocationById(location_id);
                SingleEvent singleEvent = new SingleEvent(organizer_id, name, customer, startDate, endDate,numberOfParticipants, location);
                singleEvent.setId(id);
                singleEvent.setState(state);
                singleEvent.setFinalDocuments(finalDocuments);
                singleEvent.setFinalNotes(finalNotes);




                singleEvent.services = Service.loadAllServicesForEvent(singleEvent);


                result.add(singleEvent);
            }
        });

        return result;
    }


    public Service getService(int index) {
        return services.get(index) != null ? services.get(index): null;
    }
}

enum EventState {PLANNED, INPROGRESS, CANCELED, ENDED}
