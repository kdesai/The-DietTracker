package diet.shared.dto;

import java.io.Serializable;
import java.util.Date;

import com.google.gwt.view.client.ProvidesKey;

public class FoodEatenDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id, foodId;
    private Date dateEaten;
    private Date dateEntered;
    private float servings;
    
    public static final ProvidesKey<FoodEatenDTO> KEY_PROVIDER = new ProvidesKey<FoodEatenDTO>() {
        @Override
        public Object getKey(FoodEatenDTO foodEatenDTO) {
            return foodEatenDTO == null ? null : foodEatenDTO.getId();
        }
    };
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    
    public String getFoodId() {
        return foodId;
    }
    public void setFoodId(String foodId) {
        this.foodId = foodId;
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