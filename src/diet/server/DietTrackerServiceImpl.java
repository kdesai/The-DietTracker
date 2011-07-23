package diet.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.users.UserService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import diet.client.DietTrackerService;

import diet.server.bo.*;
import diet.server.dao.*;
import diet.shared.DietServiceException;
import diet.shared.StringPair;
import diet.shared.dto.*;

@SuppressWarnings("serial")
public class DietTrackerServiceImpl extends RemoteServiceServlet implements DietTrackerService {
    private DietDAO dietDAO = new DietDAOImpl();
    private UserService userService = UserServiceFactory.getUserService();
    private PersistenceManagerFactory pmf = PMF.get();
    
    private static final String APP_URL = "http://thediettracker.appspot.com";
    
    public String getUserName() throws DietServiceException {
        try {
            return userService.getCurrentUser().getNickname();
        }
        catch (Exception ex) {
            throw new DietServiceException(ex.getMessage());
        }
    }
    
    public String getLogoutURL() throws DietServiceException {
        try {
            return userService.createLogoutURL(APP_URL);
        }
        catch (Exception ex) {
            throw new DietServiceException(ex.getMessage());
        }
    }
    
    @Override
    public void saveFoods(List<FoodDTO> foodDTOs) throws DietServiceException {
        PersistenceManager pm = pmf.getPersistenceManager();
        List<Food> foods = new ArrayList<Food>();
        try {
            String userId = userService.getCurrentUser().getUserId();
            for (FoodDTO foodDTO : foodDTOs) {
                Food food = new Food(foodDTO);
                food.setUserId(userId);
                foods.add(food);
            }

            dietDAO.saveFoods(pm, foods);
        }
        catch (Exception ex) {
            throw new DietServiceException(ex.getMessage());
        }
        finally {
            pm.close();
        }
    }
    
    @Override
    public List<FoodDTO> getFoodsById(String[] foodIds) throws DietServiceException {
        PersistenceManager pm = pmf.getPersistenceManager();
        List<Food> foods = null;
        List<FoodDTO> foodDTOs = new ArrayList<FoodDTO>();
        
        Key[] foodKeys = new Key[foodIds.length];
        for (int i=0; i<foodIds.length; i++) {
            foodKeys[i] = KeyFactory.stringToKey(foodIds[i]);
        }
        
        try {
            foods = dietDAO.getFoodsById(pm, foodKeys);
            
            for (Food food : foods) {
                foodDTOs.add(DTOMaker.makeDTO(food));
            }
        }
        catch (Exception ex) {
            throw new DietServiceException(ex.getMessage());
        }
        finally {
            pm.close();
        }
        
        return foodDTOs;
    }
    
    @Override
    public List<FoodDTO> getAllFoodsByUser() throws DietServiceException {
        PersistenceManager pm = pmf.getPersistenceManager();
        List<Food> foods = null;
        List<FoodDTO> foodDTOs = new ArrayList<FoodDTO>();
        try {
            foods = dietDAO.getAllFoodsByUserId(pm, userService.getCurrentUser().getUserId());
            
            if (foods.size() != 0) {
                for (Food food : foods) {
                    if (food.isVisible()) {
                        foodDTOs.add(DTOMaker.makeDTO(food));
                    }
                }
            }
        }
        catch (Exception ex) {
            throw new DietServiceException(ex.getMessage());
        }
        finally {
            pm.close();
        }
        
        return foodDTOs;
    }
    
    @Override
    public List<StringPair> getFoodNamesAndIdsByUser() throws DietServiceException {
        PersistenceManager pm = pmf.getPersistenceManager();
        List<StringPair> foodNamesAndIds = new ArrayList<StringPair>();
        try {
            List<Food> foods = dietDAO.getAllFoodsByUserId(pm, userService.getCurrentUser().getUserId());
            
            if (foods.size() != 0) {
                for (Food food : foods) {
                    if (food.isVisible()) {
                        StringPair foodNameAndId = new StringPair(KeyFactory.keyToString(food.getId()), food.getName());
                        foodNamesAndIds.add(foodNameAndId);
                    }
                }
            }
        }
        catch (Exception ex) {
            throw new DietServiceException(ex.getMessage());
        }
        finally {
            pm.close();
        }
        
        return foodNamesAndIds;
    }
    
    @Override
    public void deleteFoods(String[] foodIds) throws DietServiceException {
        // Only delete the foods that have not referenced in the FoodEaten table
        PersistenceManager pm = pmf.getPersistenceManager();
        List<Key> foodIdsToDelete = new ArrayList<Key>();
        List<Key> foodIdsToUpdate = new ArrayList<Key>();
        boolean[] isFoodEaten;

        Key[] foodKeys = new Key[foodIds.length];
        for (int i=0; i<foodIds.length; i++) {
            foodKeys[i] = KeyFactory.stringToKey(foodIds[i]);
        }
        
        try {
            isFoodEaten = dietDAO.areFoodsEaten(pm, foodKeys);
        }
        catch (Exception ex) {
            throw new DietServiceException(ex.getMessage());
        }
        finally {
            pm.close();
        }
        
        for (int i=0; i<isFoodEaten.length; i++) {
            if (isFoodEaten[i]) {
                foodIdsToUpdate.add(foodKeys[i]);
            }
            else {
                foodIdsToDelete.add(foodKeys[i]);
            }
        }
        
        // Update the foods not to be deleted by setting them invisible
        pm = pmf.getPersistenceManager();
        foodKeys = new Key[foodIdsToUpdate.size()];
        try {
            List<Food> foodsToUpdate = dietDAO.getFoodsById(pm, foodIdsToUpdate.toArray(foodKeys));
            for (Food food : foodsToUpdate) {
                food.setVisible(false);
            }
            
            dietDAO.saveFoods(pm, foodsToUpdate);
        }
        catch (Exception ex) {
            throw new DietServiceException(ex.getMessage());
        }
        finally {
            pm.close();
        }

        // Delete the foods that have never been referenced in the FoodEaten table
        pm = pmf.getPersistenceManager();
        foodKeys = new Key[foodIdsToDelete.size()];
        try {
            List<Food> foodsToDelete = dietDAO.getFoodsById(pm, foodIdsToDelete.toArray(foodKeys));
            dietDAO.deleteFoods(pm, foodsToDelete);
        }
        catch (Exception ex) {
            throw new DietServiceException(ex.getMessage());
        }
        finally {
            pm.close();
        }
    }
    
    @Override
    public void saveFoodEaten(FoodEatenDTO foodEatenDTO) throws DietServiceException {
        PersistenceManager pm = pmf.getPersistenceManager();
        try {
            FoodEaten foodEaten = new FoodEaten(foodEatenDTO);
            foodEaten.setUserId(userService.getCurrentUser().getUserId());
            foodEaten.setDateEntered(new Date());
            dietDAO.saveFoodEaten(pm, foodEaten);
        }
        catch (Exception ex) {
            throw new DietServiceException(ex.getMessage());
        }
        finally {
            pm.close();
        }
    }

    @Override
    public List<FoodEatenDTO> getFoodEatenByDate(Date date) throws DietServiceException {
        PersistenceManager pm = pmf.getPersistenceManager();
        List<FoodEaten> foodsEaten = null;
        List<FoodEatenDTO> foodEatenDTOs = new ArrayList<FoodEatenDTO>();
        try {
            foodsEaten = dietDAO.getFoodEatenByUserDate(pm, userService.getCurrentUser().getUserId(), date);
            
            for (FoodEaten foodEaten : foodsEaten) {
                foodEatenDTOs.add(DTOMaker.makeDTO(foodEaten));
            }
        }
        catch (Exception ex) {
            throw new DietServiceException(ex.getMessage());
        }
        finally {
            pm.close();
        }
        
        return foodEatenDTOs;
    }
    
    @Override
    public void deleteFoodEaten(String foodEatenId) throws DietServiceException {
        PersistenceManager pm = pmf.getPersistenceManager();
        try {
            FoodEaten foodEaten = dietDAO.getFoodEatenById(pm, KeyFactory.stringToKey(foodEatenId));
            dietDAO.deleteFoodEaten(pm, foodEaten);
        }
        catch (Exception ex) {
            throw new DietServiceException(ex.getMessage());
        }
        finally {
            pm.close();
        }
    }
}