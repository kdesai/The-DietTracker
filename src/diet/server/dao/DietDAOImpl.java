package diet.server.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Key;

import diet.server.bo.*;

public class DietDAOImpl implements DietDAO {

    @Override
    public void saveFoods(PersistenceManager pm, List<Food> foods) {
        try {
            pm.makePersistentAll(foods);
        }
        catch (Exception ex) {
            throw new DietDAOException("Error saving food in DB"+ex, ex);
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<Food> getFoodsById(PersistenceManager pm, Key[] foodIds) {
        List<Object> ids = new ArrayList<Object>();
        for (Key foodId : foodIds) {
            ids.add(pm.newObjectIdInstance(Food.class, foodId));
        }
        
        List<Food> foods = null;
        try {
            if (ids.size() != 0) {
                foods = (List<Food>) pm.getObjectsById(ids);
            }
            else {
                foods = new ArrayList<Food>();
            }
        }
        catch (Exception ex) {
            throw new DietDAOException("Error retrieving food "+ex, ex);
        }
        
        return foods;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<Food> getAllFoodsByUserId(PersistenceManager pm, String userId) {
        // Execute query to get user's food items
        Query query = pm.newQuery(Food.class);
        query.setFilter("userId == userIdParam");
        query.declareParameters("String userIdParam");
        query.setOrdering("name asc");
        
        List<Food> foods = null;
        try {
            foods = (List<Food>) query.execute(userId);
            foods.size();
            return foods;
        }
        catch (Exception ex) {
            throw new DietDAOException("Error retrieving foods by userId "+ex, ex);
        }
        finally {
            query.closeAll();
        }
    }

    @Override
    public void deleteFoods(PersistenceManager pm, List<Food> foods) {
        try {
            pm.deletePersistentAll(foods);
        }
        catch (Exception ex) {
            throw new DietDAOException("Error deleting foods "+ex, ex);
        }
    }

    @Override
    public void saveFoodEaten(PersistenceManager pm, FoodEaten foodEaten) {
        try {
            pm.makePersistent(foodEaten);
        }
        catch (Exception ex) {
            throw new DietDAOException("Error saving food eaten in DB"+ex, ex);
        }
    }
    
    @Override
    public FoodEaten getFoodEatenById(PersistenceManager pm, Key foodEatenId) {
        FoodEaten foodEaten = null;
        try {
            foodEaten = pm.getObjectById(FoodEaten.class, foodEatenId);
        }
        catch (Exception ex) {
            throw new DietDAOException("Error retrieving food eaten by id"+ex, ex);
        }
        
        return foodEaten;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<FoodEaten> getFoodEatenByUserDate(PersistenceManager pm, String userId, Date date) {
        Query query = pm.newQuery(FoodEaten.class);
        query.setFilter("userId == userIdParam && dateEaten == dateParam");
        query.declareParameters("String userIdParam, java.util.Date dateParam");
        query.setOrdering("dateEntered asc");
        
        List<FoodEaten> foodsEaten = null;
        
        try {
            foodsEaten = (List<FoodEaten>) query.execute(userId, date);
        }
        catch (Exception ex) {
            throw new DietDAOException("Error retrieving foods by userId and date"+ex, ex);
        }
        
        return foodsEaten;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean[] areFoodsEaten(PersistenceManager pm, Key[] foodIds) {
        Query query = pm.newQuery(FoodEaten.class);
        query.setFilter("foodId == foodIdParam");
        query.declareImports("import com.google.appengine.api.datastore.Key");
        query.declareParameters("Key foodIdParam");
        
        boolean[] isFoodEaten = new boolean[foodIds.length];
        
        try {
            for (int i=0; i<foodIds.length; i++) {
                List<FoodEaten> foodsEaten = (List<FoodEaten>) query.execute(foodIds[i]);
                if (foodsEaten.size() != 0) {
                    isFoodEaten[i] = true;
                }
                else {
                    isFoodEaten[i] = false;
                }
            }
        }
        catch (Exception ex) {
            throw new DietDAOException("Error checking if foods are referenced in food eaten table"+ex, ex);
        }
        
        return isFoodEaten;
    }
    
    @Override
    public void deleteFoodEaten(PersistenceManager pm, FoodEaten foodEaten) {
        try {
            pm.deletePersistent(foodEaten);
        }
        catch (Exception ex) {
            throw new DietDAOException("Error deleting food eaten from DB"+ex, ex);
        }
    }
}