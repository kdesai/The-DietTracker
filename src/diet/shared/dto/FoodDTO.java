package diet.shared.dto;

import java.io.Serializable;

import com.google.gwt.view.client.ProvidesKey;

public class FoodDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String name, servingSize;
    private float calories, totalFat, satFat, transFat, totalCarb,
                  fiber, sugars, protein, cholesterol, sodium;
    private boolean visible = true;
    
    public static final ProvidesKey<FoodDTO> KEY_PROVIDER = new ProvidesKey<FoodDTO>() {
        @Override
        public Object getKey(FoodDTO foodDTO) {
            return foodDTO == null ? null : foodDTO.getId();
        }
    };
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
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
    
    public boolean isVisible() {
        return visible;
    }
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}