package diet.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class FoodAddedEvent extends GwtEvent<FoodAddedEventHandler> {
    public static Type<FoodAddedEventHandler> TYPE = new Type<FoodAddedEventHandler>();
    
    @Override
    public Type<FoodAddedEventHandler> getAssociatedType() {
        return TYPE;
    }
    
    @Override
    protected void dispatch(FoodAddedEventHandler handler) {
        handler.onFoodAdded(this);
    }
}