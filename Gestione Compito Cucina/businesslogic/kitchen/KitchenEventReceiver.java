package businesslogic.kitchen;

import businesslogic.event.Service;
import businesslogic.leftover.LeftOver;
import businesslogic.turn.KitchenTurn;

import java.util.List;

public interface KitchenEventReceiver {
    public void updateSheetGenerated(SummarySheet sheet, Service service);

    public void updateKitchenActivityOutOfMenuAdded(SummarySheet currentSumSheet, List<KitchenActivity> kitchenActivity);
    public void updateKitchenActivityRemoved(SummarySheet summarySheet, KitchenActivity kitchenActivity);
    public void updateUseLeftover(KitchenActivity kitchenActivity, LeftOver leftover);
    public void updateKitchenActivityModified(KitchenActivity kitchenActivity);
    public void updateKitchenActivityRearranged(SummarySheet currentSheet);
    public void updateKitchenTaskAdded(KitchenActivity kitchenActivity, KitchenTask newKitchenTask);
    public void updateKitchenTaskUpdated(KitchenTask updatedKitchenTask);
    public void updateKitchenTaskRemoved(KitchenTask kitchenTask);
    public void updateKitchenTurnComplete(KitchenTurn kitchenTurn);
}
