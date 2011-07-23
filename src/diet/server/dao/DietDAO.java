package diet.server.dao;

import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;

import com.google.appengine.api.datastore.Key;

import diet.server.bo.Food;
import diet.server.bo.FoodEaten;

public interface DietDAO {
    public abstract void saveFoods(PersistenceManager pm, List<Food> foods);
    public abstract List<Food> getFoodsById(PersistenceManager pm, Key[] foodIds);
    public abstract List<Food> getAllFoodsByUserId(PersistenceManager pm, String userId);
    public abstract void deleteFoods(PersistenceManager pm, List<Food> foods);
    
    public abstract void saveFoodEaten(PersistenceManager pm, FoodEaten foodEaten);
    public abstract FoodEaten getFoodEatenById(PersistenceManager pm, Key foodEatenId);
    public abstract List<FoodEaten> getFoodEatenByUserDate(PersistenceManager pm, String userId, Date date);
    public abstract boolean[] areFoodsEaten(PersistenceManager pm, Key[] foodIds);
    public abstract void deleteFoodEaten(PersistenceManager pm, FoodEaten foodEaten);
}