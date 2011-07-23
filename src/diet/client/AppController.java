package diet.client;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasWidgets;

import diet.client.presenter.Presenter;
import diet.client.presenter.AppPresenter;
import diet.client.view.AppView;

public class AppController implements Presenter {
    private final HandlerManager eventBus;
    private final DietTrackerServiceAsync rpcService;
    private HasWidgets container;
    
    public AppController(DietTrackerServiceAsync rpcService, HandlerManager eventBus) {
        this.eventBus = eventBus;
        this.rpcService = rpcService;
    }

    @Override
    public void go(final HasWidgets container) {
        this.container = container;
        AppView appView = new AppView();
        Presenter presenter = new AppPresenter(rpcService, eventBus, appView);
        appView.setPresenter(presenter);
        presenter.go(this.container);
    }
}