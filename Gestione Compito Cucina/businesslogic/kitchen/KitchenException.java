package businesslogic.kitchen;

public class KitchenException extends Exception{
    public KitchenException(String msg) throws Exception {
        throw new Exception(msg);
    }
    public KitchenException(){
    }

}
