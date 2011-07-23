package diet.client;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import diet.shared.StringPair;
import diet.shared.dto.*;

public interface DietTrackerServiceAsync {
    public abstract void getUserName(AsyncCallback<String> callback);
    public abstract void getLogoutURL(AsyncCallback<String> callback);
    
    public abstract void saveFoods(List<FoodDTO> foodDTO, AsyncCallback<Void> callback);
    public abstract void getFoodsById(String[] foodIds, AsyncCallback<List<FoodDTO>> callback);
    public abstract void getAllFoodsByUser(AsyncCallback<List<FoodDTO>> callback);
    public abstract void getFoodNamesAndIdsByUser(AsyncCallback<List<StringPair>> callback);
    public abstract void deleteFoods(String[] foodIds, AsyncCallback<Void> callback);
    
    public abstract void saveFoodEaten(FoodEatenDTO foodEatenDTO, AsyncCallback<Void> callback);
    public abstract void getFoodEatenByDate(Date date, AsyncCallback<List<FoodEatenDTO>> callback);
    public abstract void deleteFoodEaten(String foodEatenId, AsyncCallback<Void> callback);
}