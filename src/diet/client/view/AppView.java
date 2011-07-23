package diet.client.view;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.adamtacy.client.ui.effects.events.EffectCompletedEvent;
import org.adamtacy.client.ui.effects.events.EffectCompletedHandler;
import org.adamtacy.client.ui.effects.events.EffectStartingEvent;
import org.adamtacy.client.ui.effects.events.EffectStartingHandler;
import org.adamtacy.client.ui.effects.impl.Fade;
import org.adamtacy.client.ui.effects.transitionsphysics.EaseInTransitionPhysics;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.cellview.client.CellTable;

import diet.shared.StringPair;
import diet.shared.dto.FoodDTO;
import diet.shared.dto.FoodEatenDTO;

import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DatePicker;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;

import diet.client.presenter.AppPresenter;
import diet.client.presenter.Presenter;

public class AppView extends Composite implements AppPresenter.Display {
    private Presenter presenter;
    private MultiSelectionModel<FoodDTO> selectionModel;
    private ListDataProvider<FoodDTO> dataProvider;
    private ListHandler<FoodDTO> sortHandler;
    private Button addFoodEatenButton, saveFoodButton, deleteFoodButton, aboutCloseButton, instrCloseButton;
    private Image saveFoodImg;
    private Fade saveFoodFade;
    private TextBox[] saveFoodTextBoxes;
    private TextBox numServingsBox;
    private ListBox foodListBox;
    private Label userNameLabel, servingSizeLabel;
    private Anchor logoutAnchor, aboutAnchor, instrAnchor;
    private DialogBox aboutDialog, instrDialog;
    private DateBox dateBox;
    private FlexTable foodEatenTable;
    private List<HasClickHandlers> deleteFoodEatenButtons; 
    
    // Number Formatters
    NumberFormat nf  = NumberFormat.getFormat("0");
    NumberFormat nf1 = NumberFormat.getFormat("0.0");
    NumberFormat nf2 = NumberFormat.getFormat("0.##");
    
    private static final String[] FIELDS = {"Name", "Servings", "Serving Size", "Calories", "Saturated Fat", "Trans Fat", "Total Fat",
            "Dietary Fiber", "Sugars", "Total Carbohydrate", "Protein", "Cholesterol", "Sodium"}; 
    
    public AppView() {
        // Create the main vertical panel
        VerticalPanel mainPanel = new VerticalPanel();
        initWidget(mainPanel);
        mainPanel.setStylePrimaryName("mainPanel");
        
        // Add the logout panel
        mainPanel.add(createLogoutPanel());
        
        // Add the application tab panel
        DecoratedTabPanel appPanel = new DecoratedTabPanel();
        appPanel.setStylePrimaryName("appPanel");
        mainPanel.add(appPanel);
        
        // Add the FoodEaten tab
        appPanel.add(createFoodEatenTab(), "Home");
        
        // Add the Store Food tab
        appPanel.add(createStoreFoodTab(), "Store Food");
        
        // Add the Manage Food tab
        appPanel.add(createManageFoodTab(), "Manage Food");
        
        // Add the footer panel
        mainPanel.add(createFooterPanel());
        
        appPanel.selectTab(0);
    }
    
    private Widget createLogoutPanel() {
        FlowPanel logoutPanel = new FlowPanel();
        
        // Display userName and logout link
        userNameLabel = new Label();
        logoutAnchor = new Anchor();
        logoutPanel.add(userNameLabel);
        logoutPanel.add(logoutAnchor);
        
        logoutPanel.getWidget(0).addStyleName("floatLeft");
        logoutPanel.getWidget(1).addStyleName("floatRight");
        
        return logoutPanel;
    }
    
    private Widget createFoodEatenTab() {
        VerticalPanel foodEatenPanel = new VerticalPanel();
        foodEatenPanel.setStyleName("foodEatenPanel");
        
        /** FIRST LAYER OF FOODEATEN PANEL **/
        HorizontalPanel datePanel = new HorizontalPanel();
        Label dateLabel = new Label("Select Date:");
        
        DatePicker datePicker = new DatePicker();
        dateBox = new DateBox(datePicker, new Date(), new DateBox.DefaultFormat(DateTimeFormat.getFormat("MMMM d, yyyy")));
        
        datePanel.add(dateLabel);
        datePanel.add(dateBox);
        foodEatenPanel.add(datePanel);
        
        /** SECOND LAYER OF FOODEATEN PANEL **/
        foodEatenPanel.add(createFoodEatenTable());
        
        /** THIRD LAYER OF FOODEATEN PANEL **/
        HorizontalPanel foodMealPanel = new HorizontalPanel();
        foodMealPanel.setStylePrimaryName("foodMealPanel");
        
        // Select Food Panel
        VerticalPanel selectFoodPanel = new VerticalPanel();
        selectFoodPanel.setStylePrimaryName("selectFoodPanel");
        
        FlexTable selectFoodTable = new FlexTable();
        
        numServingsBox = new TextBox();
        numServingsBox.setVisibleLength(5);
        selectFoodTable.setWidget(0, 0, new Label("Number of Servings:"));
        selectFoodTable.setWidget(0, 1, numServingsBox);
        
        servingSizeLabel = new Label();
        selectFoodTable.setWidget(1, 0, new Label("Serving Size:"));
        selectFoodTable.setWidget(1, 1, servingSizeLabel);
        
        foodListBox = new ListBox();
        foodListBox.setVisibleItemCount(1);
        selectFoodTable.setWidget(2, 0, new Label("Select Food Item:"));
        selectFoodTable.setWidget(2, 1, foodListBox);
        
        addFoodEatenButton = new Button("Add Food Item");
        selectFoodTable.setWidget(3, 1, addFoodEatenButton);
        
        selectFoodPanel.add(selectFoodTable);
        foodMealPanel.add(selectFoodPanel);
        foodMealPanel.setCellWidth(selectFoodPanel, "50%");
        
        // Select Meal Panel
        VerticalPanel selectMealPanel = new VerticalPanel();
        selectMealPanel.setStylePrimaryName("selectMealPanel");
        
        FlexTable selectMealTable = new FlexTable();
        
        ListBox mealListBox = new ListBox();
        mealListBox.setVisibleItemCount(1);
        selectMealTable.setWidget(0, 0, new Label("Select Meal:"));
        selectMealTable.setWidget(0, 1, mealListBox);
        
        Button addMealButton = new Button("Add Meal Item");
        selectMealTable.setWidget(1, 1, addMealButton);
        
        //selectMealPanel.add(selectMealTable);
        foodMealPanel.add(selectMealPanel);
        foodMealPanel.setCellWidth(selectMealPanel, "50%");
        
        foodEatenPanel.add(foodMealPanel);
        return foodEatenPanel;
    }
    
    private Widget createFoodEatenTable() {
        foodEatenTable = new FlexTable();
        
        for (int col=0; col<FIELDS.length; col++) {
            foodEatenTable.setText(0, col, FIELDS[col]);
        }
        
        foodEatenTable.setStylePrimaryName("foodEatenTable");
        foodEatenTable.getRowFormatter().setStyleName(0, "foodEatenTableHeader");
        
        return foodEatenTable;
    }
    
    private Widget createStoreFoodTab() {
        VerticalPanel storeFoodPanel = new VerticalPanel();
        storeFoodPanel.setStylePrimaryName("storeFoodPanel");
        
        FlexTable saveFoodTable = new FlexTable();
        
        // Include all fields EXCEPT Servings
        saveFoodTextBoxes = new TextBox[FIELDS.length-1];
        
        int row = 0;
        for (int i=0; i<FIELDS.length; i++) {
            if (i!=1) {
                TextBox textBox = new TextBox();
                saveFoodTable.setText(row, 0, FIELDS[i]+":");
                saveFoodTable.setWidget(row, 1, textBox);
                if (i > 10) {
                    saveFoodTable.setWidget(row, 2, new Label("mg"));
                }
                else if (i > 3) {
                    saveFoodTable.setWidget(row, 2, new Label("g"));
                }
                saveFoodTextBoxes[row] = textBox;
                row++;
            }
        }
        
        saveFoodButton = new Button("Save Food");
        saveFoodButton.setWidth("100%");
        saveFoodTable.setWidget(row, 1, saveFoodButton);
        
        // Create an image to signal to the user that the food was successfully saved
        saveFoodImg = new Image("images/green-checkbox.png");
        saveFoodImg.setHeight("25px");
        saveFoodImg.setVisible(false);
        saveFoodTable.setWidget(row, 2, saveFoodImg);
        
        saveFoodFade = new Fade(saveFoodImg.getElement());
        saveFoodFade.setDuration(2.0);
        saveFoodFade.setTransitionType(new EaseInTransitionPhysics());
        saveFoodFade.addEffectStartingHandler(new EffectStartingHandler() {
            @Override
            public void onEffectStarting(EffectStartingEvent event) {
                saveFoodImg.setVisible(true);
            }
        });
        saveFoodFade.addEffectCompletedHandler(new EffectCompletedHandler() {
            @Override
            public void onEffectCompleted(EffectCompletedEvent event) {
                saveFoodImg.setVisible(false);
            }
        });
        
        // Add the FlexTable to the main panel
        storeFoodPanel.add(saveFoodTable);
        
        return storeFoodPanel;
    }
    
    private Widget createManageFoodTab() {
        VerticalPanel manageFoodPanel = new VerticalPanel();
        manageFoodPanel.add(createFoodTable());
        
        return manageFoodPanel;
    }
    
    private Widget createFoodTable() {
        // VerticalPanel to contain the table and its pager
        VerticalPanel foodTablePanel = new VerticalPanel();
        
        final CellTable<FoodDTO> foodTable = new CellTable<FoodDTO>(10, FoodDTO.KEY_PROVIDER);
        foodTable.setFocus(false);
        foodTable.setTableLayoutFixed(true);
        foodTable.setWidth("100%");
        foodTable.setStylePrimaryName("manageFoodTable");
        
        // Attach a column sort handler to the ListDataProvider to sort the list.
        dataProvider = new ListDataProvider<FoodDTO>();
        sortHandler = new ListHandler<FoodDTO>(dataProvider.getList());
        foodTable.addColumnSortHandler(sortHandler);
        
        // Add selection model to handle user selection
        selectionModel = new MultiSelectionModel<FoodDTO>(FoodDTO.KEY_PROVIDER);
        foodTable.setSelectionModel(selectionModel, DefaultSelectionEventManager.<FoodDTO> createCheckboxManager());
        
        // Initialize table columns
        Column<FoodDTO, Boolean> checkboxCol = new Column<FoodDTO, Boolean>(new CheckboxCell(true, false)) {
            @Override
            public Boolean getValue(FoodDTO foodDTO) {
                return selectionModel.isSelected(foodDTO);
            }
        };
        checkboxCol.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
        foodTable.addColumn(checkboxCol, "");
        foodTable.setColumnWidth(checkboxCol, "40px");
        
        
        Column<FoodDTO, String> nameCol = new Column<FoodDTO, String>(new EditTextCell()) {
            @Override
            public String getValue(FoodDTO foodDTO) {
                return foodDTO.getName();
            }
        };
        nameCol.setSortable(true);
        sortHandler.setComparator(nameCol, new Comparator<FoodDTO>() {
            @Override
            public int compare(FoodDTO food1, FoodDTO food2) {
                return food1.getName().compareTo(food2.getName());
            }
        });
        nameCol.setFieldUpdater(new FieldUpdater<FoodDTO, String>() {
            @Override
            public void update(int index, FoodDTO foodDTO, String value) {
                foodDTO.setName(value);
                ((AppPresenter) presenter).updateFood(foodDTO);
            }
        });
        nameCol.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
        foodTable.addColumn(nameCol, FIELDS[0]);
        foodTable.setColumnWidth(nameCol, "170px");
        
        
        Column<FoodDTO, String> servingSizeCol = new Column<FoodDTO, String>(new EditTextCell()) {
            @Override
            public String getValue(FoodDTO foodDTO) {
                return foodDTO.getServingSize();
            }
        };
        servingSizeCol.setFieldUpdater(new FieldUpdater<FoodDTO, String>() {
            @Override
            public void update(int index, FoodDTO foodDTO, String value) {
                foodDTO.setServingSize(value);
                ((AppPresenter)presenter).updateFood(foodDTO);
            }
        });
        foodTable.addColumn(servingSizeCol, FIELDS[2]);
        foodTable.setColumnWidth(servingSizeCol, "85px");
        
        
        final EditTextCell caloriesCell = new EditTextCell();
        Column<FoodDTO, String> caloriesCol = new Column<FoodDTO, String>(caloriesCell) {
            @Override
            public String getValue(FoodDTO foodDTO) {
                //return Float.toString(foodDTO.getCalories());
                return nf.format(foodDTO.getCalories());
            }
        };
        caloriesCol.setSortable(true);
        sortHandler.setComparator(caloriesCol, new Comparator<FoodDTO>() {
            @Override
            public int compare(FoodDTO food1, FoodDTO food2) {
                if (food1.getCalories() > food2.getCalories()) {
                    return 1;
                }
                else if (food1.getCalories() < food2.getCalories()) {
                    return -1;
                }
                else {
                    return 0;
                }
            }
        });
        caloriesCol.setFieldUpdater(createFieldUpdater(foodTable, caloriesCell, FIELDS[3]));
        foodTable.addColumn(caloriesCol, FIELDS[3]);
        foodTable.setColumnWidth(caloriesCol, "90px");
        
        
        final EditTextCell satFatCell = new EditTextCell();
        Column<FoodDTO, String> satFatCol = new Column<FoodDTO, String>(satFatCell) {
            @Override
            public String getValue(FoodDTO foodDTO) {
                //return Float.toString(foodDTO.getSatFat());
                return nf1.format(foodDTO.getSatFat());
            }
        };
        satFatCol.setSortable(true);
        sortHandler.setComparator(satFatCol, new Comparator<FoodDTO>() {
            @Override
            public int compare(FoodDTO food1, FoodDTO food2) {
                if (food1.getSatFat() > food2.getSatFat()) {
                    return 1;
                }
                else if (food1.getSatFat() < food2.getSatFat()) {
                    return -1;
                }
                else {
                    return 0;
                }
            }
        });
        satFatCol.setFieldUpdater(createFieldUpdater(foodTable, satFatCell, FIELDS[4]));
        foodTable.addColumn(satFatCol, FIELDS[4]);
        foodTable.setColumnWidth(satFatCol, "100px");
        
        
        final EditTextCell transFatCell = new EditTextCell();
        Column<FoodDTO, String> transFatCol = new Column<FoodDTO, String>(transFatCell) {
            @Override
            public String getValue(FoodDTO foodDTO) {
                //return Float.toString(foodDTO.getTransFat());
                return nf1.format(foodDTO.getTransFat());
            }
        };
        transFatCol.setSortable(true);
        sortHandler.setComparator(transFatCol, new Comparator<FoodDTO>() {
            @Override
            public int compare(FoodDTO food1, FoodDTO food2) {
                if (food1.getTransFat() > food2.getTransFat()) {
                    return 1;
                }
                else if (food1.getTransFat() < food2.getTransFat()) {
                    return -1;
                }
                else {
                    return 0;
                }
            }
        });
        transFatCol.setFieldUpdater(createFieldUpdater(foodTable, transFatCell, FIELDS[5]));
        foodTable.addColumn(transFatCol, FIELDS[5]);
        foodTable.setColumnWidth(transFatCol, "70px");
        
        
        final EditTextCell totalFatCell = new EditTextCell();
        Column<FoodDTO, String> totalFatCol = new Column<FoodDTO, String>(totalFatCell) {
            @Override
            public String getValue(FoodDTO foodDTO) {
                //return Float.toString(foodDTO.getTotalFat());
                return nf1.format(foodDTO.getTotalFat());
            }
        };
        totalFatCol.setSortable(true);
        sortHandler.setComparator(totalFatCol, new Comparator<FoodDTO>() {
            @Override
            public int compare(FoodDTO food1, FoodDTO food2) {
                if (food1.getTotalFat() > food2.getTotalFat()) {
                    return 1;
                }
                else if (food1.getTotalFat() < food2.getTotalFat()) {
                    return -1;
                }
                else {
                    return 0;
                }
            }
        });
        totalFatCol.setFieldUpdater(createFieldUpdater(foodTable, totalFatCell, FIELDS[6]));
        foodTable.addColumn(totalFatCol, FIELDS[6]);
        foodTable.setColumnWidth(totalFatCol, "65px");
        
        
        final EditTextCell fiberCell = new EditTextCell();
        Column<FoodDTO, String> fiberCol = new Column<FoodDTO, String>(fiberCell) {
            @Override
            public String getValue(FoodDTO foodDTO) {
                //return Float.toString(foodDTO.getFiber());
                return nf1.format(foodDTO.getFiber());
            }
        };
        fiberCol.setSortable(true);
        sortHandler.setComparator(fiberCol, new Comparator<FoodDTO>() {
            @Override
            public int compare(FoodDTO food1, FoodDTO food2) {
                if (food1.getFiber() > food2.getFiber()) {
                    return 1;
                }
                else if (food1.getFiber() < food2.getFiber()) {
                    return -1;
                }
                else {
                    return 0;
                }
            }
        });
        fiberCol.setFieldUpdater(createFieldUpdater(foodTable, fiberCell, FIELDS[7]));
        foodTable.addColumn(fiberCol, FIELDS[7]);
        foodTable.setColumnWidth(fiberCol, "80px");
        
        
        final EditTextCell sugarCell = new EditTextCell();
        Column<FoodDTO, String> sugarCol = new Column<FoodDTO, String>(sugarCell) {
            @Override
            public String getValue(FoodDTO foodDTO) {
                //return Float.toString(foodDTO.getSugars());
                return nf1.format(foodDTO.getSugars());
            }
        };
        sugarCol.setSortable(true);
        sortHandler.setComparator(sugarCol, new Comparator<FoodDTO>() {
            @Override
            public int compare(FoodDTO food1, FoodDTO food2) {
                if (food1.getSugars() > food2.getSugars()) {
                    return 1;
                }
                else if (food1.getSugars() < food2.getSugars()) {
                    return -1;
                }
                else {
                    return 0;
                }
            }
        });
        sugarCol.setFieldUpdater(createFieldUpdater(foodTable, sugarCell, FIELDS[8]));
        foodTable.addColumn(sugarCol, FIELDS[8]);
        foodTable.setColumnWidth(sugarCol, "80px");
        
        
        final EditTextCell totalCarbCell = new EditTextCell();
        Column<FoodDTO, String> totalCarbCol = new Column<FoodDTO, String>(totalCarbCell) {
            @Override
            public String getValue(FoodDTO foodDTO) {
                //return Float.toString(foodDTO.getTotalCarb());
                return nf1.format(foodDTO.getTotalCarb());
            }
        };
        totalCarbCol.setSortable(true);
        sortHandler.setComparator(totalCarbCol, new Comparator<FoodDTO>() {
            @Override
            public int compare(FoodDTO food1, FoodDTO food2) {
                if (food1.getTotalCarb() > food2.getTotalCarb()) {
                    return 1;
                }
                else if (food1.getTotalCarb() < food2.getTotalCarb()) {
                    return -1;
                }
                else {
                    return 0;
                }
            }
        });
        totalCarbCol.setFieldUpdater(createFieldUpdater(foodTable, totalCarbCell, FIELDS[9]));
        foodTable.addColumn(totalCarbCol, FIELDS[9]);
        foodTable.setColumnWidth(totalCarbCol, "120px");
        
        
        final EditTextCell proteinCell = new EditTextCell();
        Column<FoodDTO, String> proteinCol = new Column<FoodDTO, String>(proteinCell) {
            @Override
            public String getValue(FoodDTO foodDTO) {
                //return Float.toString(foodDTO.getProtein());
                return nf1.format(foodDTO.getProtein());
            }
        };
        proteinCol.setSortable(true);
        sortHandler.setComparator(proteinCol, new Comparator<FoodDTO>() {
            @Override
            public int compare(FoodDTO food1, FoodDTO food2) {
                if (food1.getProtein() > food2.getProtein()) {
                    return 1;
                }
                else if (food1.getProtein() < food2.getProtein()) {
                    return -1;
                }
                else {
                    return 0;
                }
            }
        });
        proteinCol.setFieldUpdater(createFieldUpdater(foodTable, proteinCell, FIELDS[10]));
        foodTable.addColumn(proteinCol, FIELDS[10]);
        foodTable.setColumnWidth(proteinCol, "80px");
        
        
        final EditTextCell cholesterolCell = new EditTextCell();
        Column<FoodDTO, String> cholesterolCol = new Column<FoodDTO, String>(cholesterolCell) {
            @Override
            public String getValue(FoodDTO foodDTO) {
                //return Float.toString(foodDTO.getCholesterol());
                return nf.format(foodDTO.getCholesterol());
            }
        };
        cholesterolCol.setSortable(true);
        sortHandler.setComparator(cholesterolCol, new Comparator<FoodDTO>() {
            @Override
            public int compare(FoodDTO food1, FoodDTO food2) {
                if (food1.getCholesterol() > food2.getCholesterol()) {
                    return 1;
                }
                else if (food1.getCholesterol() < food2.getCholesterol()) {
                    return -1;
                }
                else {
                    return 0;
                }
            }
        });
        cholesterolCol.setFieldUpdater(createFieldUpdater(foodTable, cholesterolCell, FIELDS[11]));
        foodTable.addColumn(cholesterolCol, FIELDS[11]);
        foodTable.setColumnWidth(cholesterolCol, "110px");
        
        
        final EditTextCell sodiumCell = new EditTextCell();
        Column<FoodDTO, String> sodiumCol = new Column<FoodDTO, String>(sodiumCell) {
            @Override
            public String getValue(FoodDTO foodDTO) {
                //return Float.toString(foodDTO.getSodium());
                return nf.format(foodDTO.getSodium());
            }
        };
        sodiumCol.setSortable(true);
        sortHandler.setComparator(sodiumCol, new Comparator<FoodDTO>() {
            @Override
            public int compare(FoodDTO food1, FoodDTO food2) {
                if (food1.getSodium() > food2.getSodium()) {
                    return 1;
                }
                else if (food1.getSodium() < food2.getSodium()) {
                    return -1;
                }
                else {
                    return 0;
                }
            }
        });
        sodiumCol.setFieldUpdater(createFieldUpdater(foodTable, sodiumCell, FIELDS[12]));
        foodTable.addColumn(sodiumCol, FIELDS[12]);
        foodTable.setColumnWidth(sodiumCol, "85px");
        
        
        // Link the data provider with the food table
        dataProvider.addDataDisplay(foodTable);
        
        // Create a HorizontalPanel to hold the pager and delete button
        HorizontalPanel controlsPanel = new HorizontalPanel();
        controlsPanel.setWidth("100%");
        
        deleteFoodButton = new Button();
        deleteFoodButton.setText("Delete");
        controlsPanel.add(deleteFoodButton);
        controlsPanel.setCellWidth(deleteFoodButton, "1px");

        // Create a pager for the table
        SimplePager foodPager = new SimplePager();
        foodPager.setDisplay(foodTable);
        controlsPanel.add(foodPager);
        controlsPanel.setCellHorizontalAlignment(foodPager, HasHorizontalAlignment.ALIGN_CENTER);
        
        // Add the child widgets to the panel
        foodTablePanel.add(foodTable);
        foodTablePanel.add(controlsPanel);
        
        return foodTablePanel;
    }
    
    private Widget createFooterPanel() {
        HorizontalPanel footerPanel = new HorizontalPanel();
        footerPanel.setSpacing(10);
        
        aboutAnchor = new Anchor("About");
        instrAnchor = new Anchor("Instructions");
        
        aboutDialog = new DialogBox();
        aboutDialog.setGlassEnabled(true);
        aboutDialog.setAnimationEnabled(true);
        aboutDialog.setText("The DietTracker");
        
        instrDialog = new DialogBox();
        instrDialog.setGlassEnabled(true);
        instrDialog.setAnimationEnabled(true);
        instrDialog.setText("Instructions");
        
        // Create and add content to the About dialog
        VerticalPanel aboutDialogContents = new VerticalPanel();
        aboutDialogContents.setWidth("500px");
        aboutDialogContents.setSpacing(4);
        
        aboutDialogContents.add(new HTML(
            "<p>The DietTracker was created by me (Kunal Desai) to provide a more convenient way to track daily nutrition.  " +
            "I was inspired to create it when I saw my roommate attempting to track his calories using Excel, which was a tedious process of " +
            "copying and pasting values over and over again.  This web app is most suitable for people who tend to eat the same foods that have " +
            "access to the nutrition facts of what they are eating.  Click on the Instructions link if you would like more information on " +
            "how to use it!<p/>" +
            "<p>Feel free to send me suggestions or feedback at <a href=\"mailto:kunal.desai2@gmail.com\">kunal.desai2@gmail.com</a>.<p/>" +
            "<hr/>"
        ));
        
        aboutCloseButton = new Button("Close");
        aboutDialogContents.add(aboutCloseButton);
        aboutDialogContents.setCellHorizontalAlignment(aboutCloseButton, HasHorizontalAlignment.ALIGN_RIGHT);
        
        aboutDialog.setWidget(aboutDialogContents);
        
        // Create and add content to the Instructions dialog
        VerticalPanel instrDialogContents = new VerticalPanel();
        instrDialogContents.setWidth("500px");
        instrDialogContents.setSpacing(4);
        
        instrDialogContents.add(new HTML(
            "<div style=\"font-weight: bold; font-size: 18px;\">Step One:</div>" +
            "Use the Store Food tab to save the nutrition facts for each of the foods you eat.<br/><br/>" +
            "<div style=\"font-weight: bold; font-size: 18px;\">Step Two:</div>" +
            "Click the Home tab to return to the main page.  Select the date, specify the number of servings, and " +
            "select the food that you ate from the listbox.  Click the Add Food Item button to add it to the log for the selected date.<br/><br/>" +
            "<div style=\"font-weight: bold; font-size: 18px;\">Step Three:</div>" +
            "Rinse. Repeat. Enjoy!<br/><br/>" +
            "<span style=\"font-weight: bold;\">Tip: </span>" +
            "You can use the Manage Food tab to delete foods you no longer eat, as well as edit the data on a food directly within the table!  " +
            "The columns are also sortable by clicking on the column header!" +
            "<hr/>"
        ));
        
        instrCloseButton = new Button("Close");
        instrDialogContents.add(instrCloseButton);
        instrDialogContents.setCellHorizontalAlignment(instrCloseButton, HasHorizontalAlignment.ALIGN_RIGHT);
        
        instrDialog.setWidget(instrDialogContents);
        
        footerPanel.add(aboutAnchor);
        footerPanel.add(instrAnchor);
        footerPanel.setStylePrimaryName("footerPanel");
        return footerPanel;
    }
    
    private FieldUpdater<FoodDTO, String> createFieldUpdater(final CellTable<FoodDTO> table, final EditTextCell cell, final String name) {
        return new FieldUpdater<FoodDTO, String>() {
            @Override
            public void update(int index, FoodDTO foodDTO, String value) {
                float floatValue = 0.0f;
                try {
                    floatValue = Float.parseFloat(value);
                    
                    if (floatValue < 0.0f) {
                        Window.alert(name + " value cannot be less than zero!  Please try again.");
                        cell.clearViewData(FoodDTO.KEY_PROVIDER.getKey(foodDTO));
                    }
                    else {
                        if (name.equals(FIELDS[3])) {
                            foodDTO.setCalories(floatValue);
                        }
                        else if (name.equals(FIELDS[4])) {
                            foodDTO.setSatFat(floatValue);
                        }
                        else if (name.equals(FIELDS[5])) {
                            foodDTO.setTransFat(floatValue);
                        }
                        else if (name.equals(FIELDS[6])) {
                            foodDTO.setTotalFat(floatValue);
                        }
                        else if (name.equals(FIELDS[7])) {
                            foodDTO.setFiber(floatValue);
                        }
                        else if (name.equals(FIELDS[8])) {
                            foodDTO.setSugars(floatValue);
                        }
                        else if (name.equals(FIELDS[9])) {
                            foodDTO.setTotalCarb(floatValue);
                        }
                        else if (name.equals(FIELDS[10])) {
                            foodDTO.setProtein(floatValue);
                        }
                        else if (name.equals(FIELDS[11])) {
                            foodDTO.setCholesterol(floatValue);
                        }
                        else if (name.equals(FIELDS[12])) {
                            foodDTO.setSodium(floatValue);
                        }
                        
                        ((AppPresenter)presenter).updateFood(foodDTO);
                    }
                }
                catch (NumberFormatException nfe) {
                    Window.alert(name + " value is not a valid number!  Please try again.");
                    cell.clearViewData(FoodDTO.KEY_PROVIDER.getKey(foodDTO));

                }
                catch (Exception ex) {
                    Window.alert("Error in submitted calories value!  Please try again.");
                    cell.clearViewData(FoodDTO.KEY_PROVIDER.getKey(foodDTO));
                }
                finally {
                    table.redraw();
                }
            }
         };
    }
    
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }
    
    @Override
    public void setUserName(String userName) {
        userNameLabel.setText("Welcome " + userName + "!");
    }
    
    @Override
    public void setLogoutURL(String logoutURL) {
        logoutAnchor.setText("Logout");
        logoutAnchor.setHref(logoutURL);
    }
    
    public HasClickHandlers getAboutAnchor() {
        return aboutAnchor;
    }
    
    public HasClickHandlers getInstrAnchor() {
        return instrAnchor;
    }
    
    public HasText getAboutDialog() {
        return aboutDialog;
    }
    
    public HasText getInstrDialog() {
        return instrDialog;
    }
    
    public HasClickHandlers getAboutCloseButton() {
        return aboutCloseButton;
    }
    
    public HasClickHandlers getInstrCloseButton() {
        return instrCloseButton;
    }
    
    @Override
    public DateBox getDateBox() {
        return dateBox;
    }
    
    @Override
    public void setSelectFoodData(List<StringPair> foodNameAndIds) {
        foodListBox.clear();
        
        for (StringPair foodNameAndId : foodNameAndIds) {
            foodListBox.addItem(foodNameAndId.getValue(), foodNameAndId.getKey());
        }
    }
    
    @Override
    public FoodEatenDTO getNewFoodEaten() {
        float servings = Float.parseFloat(numServingsBox.getValue());
        FoodEatenDTO foodEatenDTO = null;
        if (servings > 0) {
            foodEatenDTO = new FoodEatenDTO();
            foodEatenDTO.setDateEaten(dateBox.getValue());
            foodEatenDTO.setFoodId(foodListBox.getValue(foodListBox.getSelectedIndex()));
            foodEatenDTO.setServings(servings);
        }
        
        return foodEatenDTO;
    }
    
    @Override
    public HasChangeHandlers getFoodListBox() {
        return foodListBox;
    }

    @Override
    public HasText getServingSizeLabel() {
        return servingSizeLabel;
    }
    
    @Override
    public HasClickHandlers getAddFoodEatenButton() {
        return addFoodEatenButton;
    }
    
    @Override
    public List<HasClickHandlers> getDeleteFoodEatenButtons() {
        return deleteFoodEatenButtons;
    }

    @Override
    public HasClickHandlers getSaveFoodButton() {
        return saveFoodButton;
    }
    
    @Override
    public FoodDTO getNewFood() {
        FoodDTO foodDTO = new FoodDTO();
        foodDTO.setName(saveFoodTextBoxes[0].getText());
        foodDTO.setServingSize(saveFoodTextBoxes[1].getText());
        foodDTO.setCalories(Float.parseFloat(saveFoodTextBoxes[2].getValue()));
        foodDTO.setSatFat(Float.parseFloat(saveFoodTextBoxes[3].getValue()));
        foodDTO.setTransFat(Float.parseFloat(saveFoodTextBoxes[4].getValue()));
        foodDTO.setTotalFat(Float.parseFloat(saveFoodTextBoxes[5].getValue()));
        foodDTO.setFiber(Float.parseFloat(saveFoodTextBoxes[6].getValue()));
        foodDTO.setSugars(Float.parseFloat(saveFoodTextBoxes[7].getValue()));
        foodDTO.setTotalCarb(Float.parseFloat(saveFoodTextBoxes[8].getValue()));
        foodDTO.setProtein(Float.parseFloat(saveFoodTextBoxes[9].getValue()));
        foodDTO.setCholesterol(Float.parseFloat(saveFoodTextBoxes[10].getValue()));
        foodDTO.setSodium(Float.parseFloat(saveFoodTextBoxes[11].getValue()));
        
        return foodDTO;
    }
    
    @Override
    public void clearTextBoxes() {
        for (TextBox textBox : saveFoodTextBoxes) {
            textBox.setText("");
        }
    }
    
    @Override
    public void animateSaveFoodImg() {
        saveFoodFade.play();
    }
    
    @Override
    public HasClickHandlers getDeleteFoodButton() {
        return deleteFoodButton;
    }
    
    @Override
    public List<FoodDTO> getFoodData() {
        return dataProvider.getList();
    }
    
    @Override
    public void setFoodData(List<FoodDTO> foodDTOs) {
        dataProvider.getList().clear();
        dataProvider.getList().addAll(foodDTOs);
        selectionModel.clear();
    }
    
    @Override
    public void setFoodEatenTableData(List<FoodEatenDTO> foodEatenDTOs, List<FoodDTO> foodDTOs) {
        // Clear the foodEatenTable
        while (foodEatenTable.getRowCount() > 1) {
            foodEatenTable.removeRow(1);
        }
        deleteFoodEatenButtons = new ArrayList<HasClickHandlers>();
        
        // Create variables to hold the sum for each column
        float sumCalories = 0.0f, sumSatFat = 0.0f, sumTransFat = 0.0f, sumTotalFat = 0.0f, sumFiber = 0.0f,
              sumSugars = 0.0f, sumTotalCarb = 0.0f, sumProtein = 0.0f, sumCholesterol = 0.0f, sumSodium = 0.0f;
        
        // Loop through the foods and display the row data
        for (int i=0, row=1; i<foodEatenDTOs.size(); i++, row++) {
            FoodDTO foodDTO = foodDTOs.get(i);
            
            float servings = foodEatenDTOs.get(i).getServings();
            float calories = servings*foodDTO.getCalories();
            float satFat = servings*foodDTO.getSatFat();
            float transFat = servings*foodDTO.getTransFat();
            float totalFat = servings*foodDTO.getTotalFat();
            float fiber = servings*foodDTO.getFiber();
            float sugars = servings*foodDTO.getSugars();
            float totalCarb = servings*foodDTO.getTotalCarb();
            float protein = servings*foodDTO.getProtein();
            float cholesterol = servings*foodDTO.getCholesterol();
            float sodium = servings*foodDTO.getSodium();
            
            sumCalories += calories;
            sumSatFat += satFat;
            sumTransFat += transFat;
            sumTotalFat += totalFat;
            sumFiber += fiber;
            sumSugars += sugars;
            sumTotalCarb += totalCarb;
            sumProtein += protein;
            sumCholesterol += cholesterol;
            sumSodium += sodium;
            
            foodEatenTable.setText(row, 0, foodDTO.getName());
            foodEatenTable.setText(row, 1, nf2.format(servings));
            foodEatenTable.setText(row, 2, foodDTO.getServingSize());
            foodEatenTable.setText(row, 3, nf.format(calories));
            foodEatenTable.setText(row, 4, nf1.format(satFat));
            foodEatenTable.setText(row, 5, nf1.format(transFat));
            foodEatenTable.setText(row, 6, nf1.format(totalFat));
            foodEatenTable.setText(row, 7, nf1.format(fiber));
            foodEatenTable.setText(row, 8, nf1.format(sugars));
            foodEatenTable.setText(row, 9, nf1.format(totalCarb));
            foodEatenTable.setText(row, 10, nf1.format(protein));
            foodEatenTable.setText(row, 11, nf.format(cholesterol));
            foodEatenTable.setText(row, 12, nf.format(sodium));
            
            // Add Delete button
            Button button = new Button("X");
            foodEatenTable.setWidget(row, 13, button);
            deleteFoodEatenButtons.add(button);
            
            foodEatenTable.getRowFormatter().addStyleName(row, "foodEatenRowStyle");
        }
        
        // Create the final sum row
        int lastRow = foodEatenTable.getRowCount();
        foodEatenTable.setText(lastRow, 0, "Totals");
        foodEatenTable.setText(lastRow, 3, nf.format(sumCalories));
        foodEatenTable.setText(lastRow, 4, nf1.format(sumSatFat));
        foodEatenTable.setText(lastRow, 5, nf1.format(sumTransFat));
        foodEatenTable.setText(lastRow, 6, nf1.format(sumTotalFat));
        foodEatenTable.setText(lastRow, 7, nf1.format(sumFiber));
        foodEatenTable.setText(lastRow, 8, nf1.format(sumSugars));
        foodEatenTable.setText(lastRow, 9, nf1.format(sumTotalCarb));
        foodEatenTable.setText(lastRow, 10, nf1.format(sumProtein));
        foodEatenTable.setText(lastRow, 11, nf.format(sumCholesterol));
        foodEatenTable.setText(lastRow, 12, nf.format(sumSodium));
        
        foodEatenTable.getRowFormatter().addStyleName(lastRow, "foodEatenTotalsRowStyle");
    }

    @Override
    public FoodDTO[] getSelectedFoods() {
        FoodDTO[] selectedFoods = new FoodDTO[selectionModel.getSelectedSet().size()];
        return selectionModel.getSelectedSet().toArray(selectedFoods);
    }
}