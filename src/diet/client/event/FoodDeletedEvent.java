package diet.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class FoodDeletedEvent extends GwtEvent<FoodDeletedEventHandler> {
    public static Type<FoodDeletedEventHandler> TYPE = new Type<FoodDeletedEventHandler>();
    
    @Override
    public Type<FoodDeletedEventHandler> getAssociatedType() {
        return TYPE;
    }
    
    @Override
    protected void dispatch(FoodDeletedEventHandler handler) {
        handler.onFoodDeleted(this);
    }
}