

import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

import java.util.concurrent.ExecutionException;


/**
 * A class that can be implemented to handle the response to an http request.
 */
public abstract class HttpResponseHandler implements EventHandler<WorkerStateEvent> {
    public abstract void handle(String response);

    protected HttpResponseHandler() {

    }

    @Override
    public void handle(WorkerStateEvent workerStateEvent) {
        HttpRequestTask task = (HttpRequestTask) workerStateEvent.getSource();

        try {
            String r = task.get();
            handle(r);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
