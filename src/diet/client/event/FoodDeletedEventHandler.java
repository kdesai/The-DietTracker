package diet.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface FoodDeletedEventHandler extends EventHandler {
    void onFoodDeleted(FoodDeletedEvent event);
}
