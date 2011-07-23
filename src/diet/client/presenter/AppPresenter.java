package diet.client.presenter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

import diet.client.DietTrackerServiceAsync;
import diet.client.event.FoodAddedEvent;
import diet.client.event.FoodAddedEventHandler;
import diet.client.event.FoodDeletedEvent;
import diet.client.event.FoodDeletedEventHandler;
import diet.shared.StringPair;
import diet.shared.dto.FoodDTO;
import diet.shared.dto.FoodEatenDTO;

public class AppPresenter implements Presenter {
    String[] foodEatenIds;
    List<String> foodServingSizes;

    public interface Display {
        public abstract void setUserName(String userName);
        public abstract void setLogoutURL(String logoutURL);
        public abstract HasClickHandlers getAboutAnchor();
        public abstract HasClickHandlers getInstrAnchor();
        public abstract HasText getAboutDialog();
        public abstract HasText getInstrDialog();
        public abstract HasClickHandlers getAboutCloseButton();
        public abstract HasClickHandlers getInstrCloseButton();

        // Home Tab
        public abstract DateBox getDateBox();
        public abstract HasText getServingSizeLabel();
        public abstract HasChangeHandlers getFoodListBox();
        public abstract FoodEatenDTO getNewFoodEaten();
        public abstract HasClickHandlers getAddFoodEatenButton();
        public abstract List<HasClickHandlers> getDeleteFoodEatenButtons();
        public abstract void setSelectFoodData(List<StringPair> foodNameAndIds);
        public abstract void setFoodEatenTableData(List<FoodEatenDTO> foodEatenDTOs, List<FoodDTO> foodDTOs);
        
        // Store Food Tab
        public abstract HasClickHandlers getSaveFoodButton();
        public abstract FoodDTO getNewFood();
        public abstract void clearTextBoxes();
        public abstract void animateSaveFoodImg();
        
        // Manage Food Tab
        public abstract HasClickHandlers getDeleteFoodButton();
        public abstract List<FoodDTO> getFoodData();
        public abstract void setFoodData(List<FoodDTO> foodDTOs);
        public abstract FoodDTO[] getSelectedFoods();
        
        public abstract Widget asWidget();
    }
    
    private final DietTrackerServiceAsync rpcService;
    private final HandlerManager eventBus;
    private final Display display;

    public AppPresenter(DietTrackerServiceAsync rpcService, HandlerManager eventBus, Display view) {
        this.rpcService = rpcService;
        this.eventBus = eventBus;
        this.display = view;
    }
    
    public void bind() {
        display.getAboutAnchor().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                PopupPanel dialog = (PopupPanel) display.getAboutDialog();
                dialog.center();
                dialog.show();
            }
        });
        
        display.getInstrAnchor().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                PopupPanel dialog = (PopupPanel) display.getInstrDialog();
                dialog.center();
            }
        });
        
        display.getAboutCloseButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ((PopupPanel)(display.getAboutDialog())).hide();
            }
        });
        
        display.getInstrCloseButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ((PopupPanel)(display.getInstrDialog())).hide();
            }
        });
        
        display.getDateBox().getDatePicker().addValueChangeHandler(new ValueChangeHandler<Date>() {
            @Override
            public void onValueChange(ValueChangeEvent<Date> event) {
                updateFoodEatenTable(display.getDateBox().getValue());                
            }
        });
        
        display.getFoodListBox().addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                int index = ((ListBox)display.getFoodListBox()).getSelectedIndex();
                display.getServingSizeLabel().setText(foodServingSizes.get(index));
            }            
        });
        
        display.getAddFoodEatenButton().addClickHandler(new ClickHandler() {
           @Override
           public void onClick(ClickEvent event) {
               saveFoodEaten();
           }
        });
        
        display.getSaveFoodButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                saveFood();
            }
        });
        
        display.getDeleteFoodButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                deleteSelectedFoods();
            }
         });
        
        eventBus.addHandler(FoodAddedEvent.TYPE, new FoodAddedEventHandler() {
            @Override
            public void onFoodAdded(FoodAddedEvent event) {
                display.animateSaveFoodImg();
                display.clearTextBoxes();
                fetchFoodData();
            } 
        });
        
        eventBus.addHandler(FoodDeletedEvent.TYPE, new FoodDeletedEventHandler() {
           @Override
           public void onFoodDeleted(FoodDeletedEvent event) {
               fetchFoodData();
           }
        });
    }
    
    @Override
    public void go(final HasWidgets container) {
        bind();
        
        // Populate display data
        setLogoutPanel();
        fetchFoodData();
        updateFoodEatenTable(display.getDateBox().getValue());
        
        // Show the display
        container.add(display.asWidget());
    }
    
    public void updateFood(FoodDTO foodDTO) {
        List<FoodDTO> foodDTOs = new ArrayList<FoodDTO>();
        foodDTOs.add(foodDTO);
        rpcService.saveFoods(foodDTOs, new AsyncCallback<Void>() {
           @Override
           public void onSuccess(Void result) {
               fetchFoodData();
               updateFoodEatenTable(display.getDateBox().getValue());
           }
           
           @Override
           public void onFailure(Throwable caught) {
               Window.alert("There was an error updating the food data.  Please try again");
               fetchFoodData();
           }
        });
    }
    
    private void setLogoutPanel() {
        rpcService.getUserName(new AsyncCallback<String>() {
           @Override
           public void onSuccess(String result) {
               display.setUserName(result);
           }
           
           @Override
           public void onFailure(Throwable caught) {
               Window.alert("There was an error setting the username.  Please try again.");
           }
        });
        
        rpcService.getLogoutURL(new AsyncCallback<String>() {
           @Override
           public void onSuccess(String result) {
               display.setLogoutURL(result);
           }
           
           @Override
           public void onFailure(Throwable caught) {
               Window.alert("There was an error setting the logout url.  Please try again.");
           }
        });
    }
    
    private void fetchFoodData() {
        rpcService.getAllFoodsByUser(new AsyncCallback<List<FoodDTO>>() {
           @Override
           public void onSuccess(List<FoodDTO> result) {
               List<StringPair> foodNameAndIds = new ArrayList<StringPair>();
               foodServingSizes = new ArrayList<String>();
               for (FoodDTO foodDTO : result) {
                   foodNameAndIds.add(new StringPair(foodDTO.getId(), foodDTO.getName()));
                   foodServingSizes.add(foodDTO.getServingSize());
               }
               
               display.setSelectFoodData(foodNameAndIds);
               display.setFoodData(result);
               display.getServingSizeLabel().setText(foodServingSizes.get(0));
           }
           
           @Override
           public void onFailure(Throwable caught) {
               Window.alert("There was an error fetching the food data.  Please try again.");
           }
        });
    }
    
    private void saveFood() {
        List<FoodDTO> foods = new ArrayList<FoodDTO>();
        foods.add(display.getNewFood());
        rpcService.saveFoods(foods, new AsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                eventBus.fireEvent(new FoodAddedEvent());
            }
            @Override
            public void onFailure(Throwable caught) {
                Window.alert("There was an error adding the new food.  Please try again.");
            }
        });
    }
    
    private void deleteSelectedFoods() {
        FoodDTO[] selectedFoods = display.getSelectedFoods();
        String[] ids = new String[selectedFoods.length];
        
        for (int i=0; i<selectedFoods.length; i++) {
            ids[i] = selectedFoods[i].getId();
        }
        
        rpcService.deleteFoods(ids, new AsyncCallback<Void>() {
           @Override
           public void onSuccess(Void result) {
               eventBus.fireEvent(new FoodDeletedEvent());
           }
           @Override
           public void onFailure(Throwable caught) {
               Window.alert("Error deleting selected foods.  Please try again.");
           }
        });
    }
    
    private void saveFoodEaten() {
        FoodEatenDTO foodEatenDTO = display.getNewFoodEaten();
        if (foodEatenDTO != null) {
            rpcService.saveFoodEaten(display.getNewFoodEaten(), new AsyncCallback<Void>() {
               @Override
               public void onSuccess(Void result) {
                   updateFoodEatenTable(display.getDateBox().getValue());
               }
               
               @Override
               public void onFailure(Throwable caught) {
                   Window.alert("Error saving food eaten.  Please try again.");
               }
            });
        }
        else {
            Window.alert("Number of servings must be greater than zero!  Please try again.");
        }
    }
    
    private void updateFoodEatenTable(Date date) {
        // Query server for food eaten on selected date
        rpcService.getFoodEatenByDate(date, new AsyncCallback<List<FoodEatenDTO>>() {
            @Override
            public void onSuccess(final List<FoodEatenDTO> foodEatenDTOs) {
                // Hold onto the food eaten ids
                foodEatenIds = new String[foodEatenDTOs.size()];
                String[] foodIds = new String[foodEatenDTOs.size()];
                for (int i=0; i<foodEatenDTOs.size(); i++) {
                    foodEatenIds[i] = foodEatenDTOs.get(i).getId();
                    foodIds[i] = foodEatenDTOs.get(i).getFoodId();
                }
                
                // Query server for the food information for each food eaten item
                rpcService.getFoodsById(foodIds, new AsyncCallback<List<FoodDTO>>() {
                   @Override
                   public void onSuccess(List<FoodDTO> foodDTOs) {
                       display.setFoodEatenTableData(foodEatenDTOs, foodDTOs);

                       // Register callbacks for delete food eaten buttons
                       List<HasClickHandlers> buttons = display.getDeleteFoodEatenButtons();
                       if (buttons != null) {
                           for (int i=0; i<buttons.size(); i++) {
                               final int j = i;
                               buttons.get(j).addClickHandler(new ClickHandler() {
                                    @Override
                                    public void onClick(ClickEvent event) {
                                        rpcService.deleteFoodEaten(foodEatenIds[j], new AsyncCallback<Void>() {
                                            @Override
                                            public void onSuccess(Void result) {
                                                updateFoodEatenTable(display.getDateBox().getValue());
                                            }
                                            
                                            @Override
                                            public void onFailure(Throwable caught) {
                                                Window.alert("Error deleting food eaten item.  Please try again.");
                                            }
                                        });
                                    }                    
                               });
                           }
                       }
                   }
                   
                   @Override
                   public void onFailure(Throwable caught) {
                       Window.alert("Error retrieving foods by id for food eaten table.  Please try again.");
                   }
                });
            }
            
            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Error retrieving foods eaten for food eaten table.  Please try again.");
            }
        });
    }
}