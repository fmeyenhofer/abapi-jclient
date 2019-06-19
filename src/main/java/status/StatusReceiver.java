package status;

import java.util.Observable;
import java.util.Observer;

/**
 * @author Felix Meyenhofer
 */
public class StatusReceiver implements Observer {

    @Override
    public void update(Observable o, Object arg) {
        StatusBroadcaster status = (StatusBroadcaster) o;
        System.out.println(status.getMessage());

        if (status.getCurrentIterations() > -1 && status.getTotalIterations() > -1) {
            System.out.println("\titeration " + status.getCurrentIterations() + " of " + status.getTotalIterations());
        }
    }
}
