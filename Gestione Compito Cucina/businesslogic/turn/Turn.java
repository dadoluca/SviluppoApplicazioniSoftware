package businesslogic.turn;

import businesslogic.user.User;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public abstract class Turn implements KitchenTurnItemInfo {
    private int id;
    private Instant start;
    private Instant end;
    private List<User> staffAvaiable;
    public  Turn(){
        this.staffAvaiable=new ArrayList<>();
    }
    public Turn(int id,Instant start, Instant end) {
        this.start = start;
        this.end = end;
        this.id = id;
        this.staffAvaiable=new ArrayList<>();
    }

    public int getId(){
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEnd(Instant end) {
        this.end = end;
    }

    public void setStart(Instant start) {
        this.start = start;
    }

    public boolean isAvailable(User user){
        return staffAvaiable.contains(user);
    }

    public Instant getEnd() {
        return end;
    }

    public Instant getStart() {
        return start;
    }

    public void addStaffAvaiable(User cook){
        staffAvaiable.add(cook);
    }

    public List<User> getStaffAvailable() {
        return staffAvaiable;
    }

    @Override
    public String toString() {
        return "Turn:" +
                "\tstart=" + start +
                ", end=" + end +
                ", staffAvaiable=" + staffAvaiable;
    }

}
