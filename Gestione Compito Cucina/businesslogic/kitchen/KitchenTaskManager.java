package businesslogic.kitchen;

import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.event.*;
import businesslogic.recipe.KitchenDuty;
import businesslogic.leftover.LeftOver;
import businesslogic.turn.KitchenTurn;
import businesslogic.user.User;
import javafx.collections.ObservableList;
import persistence.KitchenPersistence;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class KitchenTaskManager {
    private ArrayList<KitchenEventReceiver> eventReceivers;
    private SummarySheet currentSummarySheet;
    public KitchenTaskManager() {
        eventReceivers = new ArrayList<>();
    }

    public void addEventReceiver(KitchenPersistence kitchenPersistence) {
        eventReceivers.add(kitchenPersistence);
    }
    public SummarySheet generateSummarySheet(SingleEvent event, Service serv) throws UseCaseLogicException, KitchenException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        SummarySheet newSumSheet;
        if(!user.isChef()){
            throw new UseCaseLogicException();
        } else if(!event.hasService(serv) || serv.getMenu() == null || !serv.isConfirmed() ){
            throw new KitchenException();
        }
        newSumSheet = serv.createSummarySheet();
        this.currentSummarySheet = newSumSheet;
        notifySheetGenerated(newSumSheet, serv);
        return newSumSheet;
    }

    public List<KitchenActivity> addOutOfMenu(KitchenDuty duty) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if(!user.isChef() || currentSummarySheet == null){
            throw new UseCaseLogicException();
        }
        List<KitchenActivity> newActivities = currentSummarySheet.addOutOfMenu(duty);
        notifyKitchenActivityOutOfMenuAdded(currentSummarySheet, newActivities);

        return newActivities;
    }

    public void removeKitchenActivity(KitchenActivity kitchenActivity) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if(!user.isChef() || this.currentSummarySheet == null){
            throw new UseCaseLogicException();
        }
        this.currentSummarySheet.removeKitchenActivity(kitchenActivity);
        notifyKitchenActivityRemoved(currentSummarySheet, kitchenActivity);

    }
    public SummarySheet openSummarySheet(SummarySheet selectedSheet){
        this.currentSummarySheet = selectedSheet;
        return this.currentSummarySheet;
    }
    public KitchenActivity useLeftover(KitchenActivity kitchenActivity, LeftOver leftOver){
        KitchenActivity kitchenActivityUpdated = kitchenActivity.useLeftOver(leftOver);
        notifyUseLeftover(kitchenActivityUpdated, leftOver);
        return kitchenActivityUpdated;
    }

    public KitchenActivity indicatesEstimate(KitchenActivity kitchenActivity, String amount, Integer portion, Integer estimatedMinutes) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if(!user.isChef() || this.currentSummarySheet == null){
            throw new UseCaseLogicException();
        }
        KitchenActivity kitchenActivityModified = kitchenActivity.setData(amount, portion, estimatedMinutes);
        notifyKitchenActivityModified(kitchenActivityModified);
        return kitchenActivityModified;
    }
    public void changeActivityData(KitchenActivity kitchenActivity, String amount, Integer portion, Integer estimatedMinutes) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if(!user.isChef() || this.currentSummarySheet == null){
            throw new UseCaseLogicException();
        }
        KitchenActivity kitchenActivityUpdated = kitchenActivity.changeData(amount, portion, estimatedMinutes);
        notifyActivityModified(kitchenActivityUpdated);
    }


    public SummarySheet moveKitchenActivity(KitchenActivity kitchenActivity, int position) throws UseCaseLogicException, KitchenException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if(!user.isChef() || this.currentSummarySheet == null || !this.currentSummarySheet.containsKitchenActivity(kitchenActivity)){
            throw new UseCaseLogicException();
        } else if(position < 0 || position >= currentSummarySheet.ActivitiesListSize()){
            throw new KitchenException();
        }
        this.currentSummarySheet.moveKitchenActivity(position, kitchenActivity);
        this.notifyKitchenActivityRearranged(this.currentSummarySheet);
        return currentSummarySheet;
    }
    public KitchenTask assignKitchenTask(KitchenActivity kitchenActivity, String amount, int portion, int estimatedMinutes, KitchenTurn turn, User cook) throws UseCaseLogicException, KitchenException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if(!user.isChef() || currentSummarySheet == null || Instant.now().compareTo(turn.getEnd()) > 0){
            throw new UseCaseLogicException();
        } else if(turn.isComplete() || !currentSummarySheet.containsKitchenActivity(kitchenActivity) || !turn.isAvailable(cook)){
            throw new KitchenException();
        }
        KitchenTask newKitchenTask = kitchenActivity.assignTask(amount, portion, estimatedMinutes, turn, cook);
        notifyKitchenTaskAdded(kitchenActivity, newKitchenTask);
        return newKitchenTask;
    }
    public KitchenTask updateKitchenTask(KitchenTask kitchenTask, String amount, Integer portion, int estimatedMinutes, KitchenTurn turn, User cook) throws Exception {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if(!user.isChef()){
            throw new UseCaseLogicException();
        } else if((cook == null && turn != null && !turn.isAvailable(kitchenTask.getCook())) ||
                (cook != null && turn == null && !kitchenTask.getTurn().isAvailable(cook)) ||
                (cook != null && turn != null && !turn.isAvailable(cook)) ||
                (turn != null && Instant.now().compareTo(turn.getEnd()) > 0) ||
                (turn == null && Instant.now().compareTo(kitchenTask.getTurn().getEnd()) >0 )) {
            throw new KitchenException();
        }
        KitchenTask updatedKitchentask = kitchenTask.update(amount, portion, estimatedMinutes, turn, cook);
        notifyKitchenTaskUpdated(kitchenTask);
        return updatedKitchentask;
    }


    public void setTurnAsComplete(KitchenTurn kitchenTurn) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if(!user.isChef()){
            throw new UseCaseLogicException();
        }
        kitchenTurn.setTurnAsComplete();
        notifyKitchenTurnComplete(kitchenTurn);
    }



    public void removeKictchenTask(KitchenActivity kitchenActivity, KitchenTask kitchenTask){
        kitchenActivity.removeTask(kitchenTask);
        notifyKitchenTaskRemoved(kitchenTask);
    }


    public ObservableList<SummarySheetInfo> getSummaryInfo() {
        return SummarySheetInfo.loadAllSummarySheetInfo();
    }

    private void notifySheetGenerated(SummarySheet summarySheet, Service service){
        for (KitchenEventReceiver eventReceiver : eventReceivers) {
            eventReceiver.updateSheetGenerated(summarySheet, service);
        }
    }

    private void notifyKitchenActivityModified(KitchenActivity kitchenActivity){
        for (KitchenEventReceiver eventReceiver : eventReceivers) {
            eventReceiver.updateKitchenActivityModified(kitchenActivity);
        }
    }

    private void notifyKitchenActivityRearranged(SummarySheet summarySheet){
        for (KitchenEventReceiver eventReceiver : eventReceivers) {
            eventReceiver.updateKitchenActivityRearranged(summarySheet);
        }
    }

    private void notifyKitchenActivityRemoved(SummarySheet summarySheet, KitchenActivity kitchenActivity){
        for (KitchenEventReceiver eventReceiver : eventReceivers) {
            eventReceiver.updateKitchenActivityRemoved(summarySheet, kitchenActivity);
        }
    }

    private void notifyKitchenTaskUpdated(KitchenTask kitchenTask){
        for (KitchenEventReceiver eventReceiver : eventReceivers) {
            eventReceiver.updateKitchenTaskUpdated(kitchenTask);
        }
    }

    private void notifyKitchenActivityOutOfMenuAdded(SummarySheet currentSummarySheet, List<KitchenActivity> kitchenActivities){
        for (KitchenEventReceiver eventReceiver : eventReceivers) {
            eventReceiver.updateKitchenActivityOutOfMenuAdded(currentSummarySheet, kitchenActivities);
        }
    }

    private void notifyUseLeftover(KitchenActivity kitchenActivity, LeftOver leftOver){
        for(KitchenEventReceiver eventReceiver: eventReceivers){
            eventReceiver.updateUseLeftover(kitchenActivity, leftOver);
        }
    }

    private void notifyKitchenTaskAdded(KitchenActivity kitchenActivity, KitchenTask kitchenTask){
        for(KitchenEventReceiver eventReceiver: eventReceivers){
            eventReceiver.updateKitchenTaskAdded(kitchenActivity, kitchenTask);
        }
    }

    private void notifyActivityModified(KitchenActivity kitchenActivity){
        for(KitchenEventReceiver eventReceiver: eventReceivers){
            eventReceiver.updateKitchenActivityModified(kitchenActivity);
        }
    }

    private void notifyKitchenTaskRemoved(KitchenTask kitchenTask){
        for(KitchenEventReceiver eventReceiver: eventReceivers){
            eventReceiver.updateKitchenTaskRemoved(kitchenTask);
        }
    }

    private void notifyKitchenTurnComplete(KitchenTurn kitchenTurn) {
        for(KitchenEventReceiver eventReceiver: eventReceivers){
            eventReceiver.updateKitchenTurnComplete(kitchenTurn);
        }
    }

}
