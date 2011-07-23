package diet.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface FoodAddedEventHandler extends EventHandler {
    void onFoodAdded(FoodAddedEvent event);
}