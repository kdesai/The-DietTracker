package diet.server.bo;


import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import diet.shared.dto.FoodDTO;

@PersistenceCapable
public class Food {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key id;
    
    @Persistent
    private String name, servingSize;
    
    @Persistent
    private float calories, totalFat, satFat, transFat, totalCarb,
                  fiber, sugars, protein, cholesterol, sodium;
    
    @Persistent
    private String userId;

    @Persistent
    private boolean visible = true;
    
    public Food() {}
    
    public Food(FoodDTO foodDTO) {
        if (foodDTO.getId() != null) {
            id = KeyFactory.stringToKey(foodDTO.getId());
        }
        
        name = foodDTO.getName();
        servingSize = foodDTO.getServingSize();
        calories = foodDTO.getCalories();
        totalFat = foodDTO.getTotalFat();
        satFat = foodDTO.getSatFat();
        transFat = foodDTO.getTransFat();
        totalCarb = foodDTO.getTotalCarb();
        fiber = foodDTO.getFiber();
        sugars = foodDTO.getSugars();
        protein = foodDTO.getProtein();
        cholesterol = foodDTO.getCholesterol();
        sodium = foodDTO.getSodium();
        visible = foodDTO.isVisible();
    }
    
    public Key getId() {
        return id;
    }
    public void setId(Key id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getServingSize() {
        return servingSize;
    }
    public void setServingSize(String servingSize) {
        this.servingSize = servingSize;
    }

    public float getCalories() {
        return calories;
    }
    public void setCalories(float calories) {
        this.calories = calories;
    }

    public float getTotalFat() {
        return totalFat;
    }
    public void setTotalFat(float totalFat) {
        this.totalFat = totalFat;
    }

    public float getSatFat() {
        return satFat;
    }
    public void setSatFat(float satFat) {
        this.satFat = satFat;
    }

    public float getTransFat() {
        return transFat;
    }
    public void setTransFat(float transFat) {
        this.transFat = transFat;
    }

    public float getTotalCarb() {
        return totalCarb;
    }
    public void setTotalCarb(float totalCarb) {
        this.totalCarb = totalCarb;
    }

    public float getFiber() {
        return fiber;
    }
    public void setFiber(float fiber) {
        this.fiber = fiber;
    }

    public float getSugars() {
        return sugars;
    }
    public void setSugars(float sugars) {
        this.sugars = sugars;
    }

    public float getProtein() {
        return protein;
    }
    public void setProtein(float protein) {
        this.protein = protein;
    }

    public float getCholesterol() {
        return cholesterol;
    }
    public void setCholesterol(float cholesterol) {
        this.cholesterol = cholesterol;
    }

    public float getSodium() {
        return sodium;
    }
    public void setSodium(float sodium) {
        this.sodium = sodium;
    }
    
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public boolean isVisible() {
        return visible;
    }
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}