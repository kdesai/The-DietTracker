package diet.server.bo;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import diet.shared.dto.FoodEatenDTO;

@PersistenceCapable
public class FoodEaten {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key id;
    
    @Persistent
    private Key foodId;
    
    @Persistent
    private String userId;
    
    @Persistent
    private Date dateEaten;
    
    @Persistent
    private Date dateEntered;
    
    @Persistent
    private float servings;
    
    public FoodEaten(FoodEatenDTO foodEatenDTO) {
        if (foodEatenDTO.getId() != null) {
            id = KeyFactory.stringToKey(foodEatenDTO.getId());
        }
        
        foodId = KeyFactory.stringToKey(foodEatenDTO.getFoodId());
        dateEaten = foodEatenDTO.getDateEaten();
        servings = foodEatenDTO.getServings();
    }
    
    public Key getId() {
        return id;
    }
    public void setId(Key id) {
        this.id = id;
    }
    
    public Key getFoodId() {
        return foodId;
    }
    public void setFoodId(Key foodId) {
        this.foodId = foodId;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getDateEaten() {
        return dateEaten;
    }
    public void setDateEaten(Date dateEaten) {
        this.dateEaten = dateEaten;
    }
    
    public Date getDateEntered() {
        return dateEntered;
    }
    public void setDateEntered(Date dateEntered) {
        this.dateEntered = dateEntered;
    }

    public float getServings() {
        return servings;
    }
    public void setServings(float servings) {
        this.servings = servings;
    }
}