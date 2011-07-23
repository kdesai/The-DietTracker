package diet.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.RootPanel;

public class DietTracker implements EntryPoint {

    @Override
    public void onModuleLoad() {
        DietTrackerServiceAsync rpcService = GWT.create(DietTrackerService.class);
        HandlerManager eventBus = new HandlerManager(null);
        AppController appViewer = new AppController(rpcService, eventBus);
        appViewer.go(RootPanel.get());
    }
}