package diet.client;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import diet.shared.DietServiceException;
import diet.shared.StringPair;
import diet.shared.dto.*;

@RemoteServiceRelativePath("data-service")
public interface DietTrackerService extends RemoteService {
    public abstract String getUserName() throws DietServiceException;
    public abstract String getLogoutURL() throws DietServiceException;
    
    public abstract void saveFoods(List<FoodDTO> foodDTOs) throws DietServiceException;
    public abstract List<FoodDTO> getFoodsById(String[] foodIds) throws DietServiceException;
    public abstract List<FoodDTO> getAllFoodsByUser() throws DietServiceException;
    public abstract List<StringPair> getFoodNamesAndIdsByUser() throws DietServiceException;
    public abstract void deleteFoods(String[] foodIds) throws DietServiceException;
    
    public abstract void saveFoodEaten(FoodEatenDTO foodEatenDTO) throws DietServiceException;
    public abstract List<FoodEatenDTO> getFoodEatenByDate(Date date) throws DietServiceException;
    public abstract void deleteFoodEaten(String foodEatenId) throws DietServiceException;
}