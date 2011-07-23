package diet.server;

import com.google.appengine.api.datastore.KeyFactory;

import diet.server.bo.*;
import diet.shared.dto.*;

public class DTOMaker {
    public static FoodDTO makeDTO(Food food) {
        FoodDTO foodDTO = new FoodDTO();
        foodDTO.setCalories(food.getCalories());
        foodDTO.setCholesterol(food.getCholesterol());
        foodDTO.setFiber(food.getFiber());
        foodDTO.setId(KeyFactory.keyToString(food.getId()));
        foodDTO.setName(food.getName());
        foodDTO.setProtein(food.getProtein());
        foodDTO.setSatFat(food.getSatFat());
        foodDTO.setServingSize(food.getServingSize());
        foodDTO.setSodium(food.getSodium());
        foodDTO.setSugars(food.getSugars());
        foodDTO.setTotalCarb(food.getTotalCarb());
        foodDTO.setTotalFat(food.getTotalFat());
        foodDTO.setTransFat(food.getTransFat());
        foodDTO.setVisible(food.isVisible());
        
        return foodDTO;
    }
    
    public static FoodEatenDTO makeDTO(FoodEaten foodEaten) {
        FoodEatenDTO foodEatenDTO = new FoodEatenDTO();
        foodEatenDTO.setDateEaten(foodEaten.getDateEaten());
        foodEatenDTO.setDateEntered(foodEaten.getDateEntered());
        foodEatenDTO.setFoodId(KeyFactory.keyToString(foodEaten.getFoodId()));
        foodEatenDTO.setId(KeyFactory.keyToString(foodEaten.getId()));
        foodEatenDTO.setServings(foodEaten.getServings());
        
        return foodEatenDTO;
    }
    
    // Prevent instantiation of class!
    private DTOMaker() {}
}