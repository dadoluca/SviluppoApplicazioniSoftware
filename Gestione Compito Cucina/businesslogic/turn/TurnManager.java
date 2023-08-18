package businesslogic.turn;


import javafx.collections.ObservableList;

public class TurnManager {

    public ObservableList<KitchenTurnInfo> getTurnInfo() {
        return KitchenTurnInfo.loadAllTurnInfo();
    }
}