package businesslogic.leftover;


import businesslogic.turn.KitchenTurnInfo;
import javafx.collections.ObservableList;

import java.util.List;

public class LeftOverManager {

    public List<LeftOver> getLeftOvers() {
        return LeftOver.loadAllLeftOversUnused();
    }
}