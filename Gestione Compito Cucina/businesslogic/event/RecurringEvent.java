package businesslogic.event;

import java.util.List;

public class RecurringEvent extends Event {
    private int frequency;
    List<SingleEvent> occurrences;

}
