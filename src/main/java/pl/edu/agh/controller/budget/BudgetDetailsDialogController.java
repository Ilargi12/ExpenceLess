package pl.edu.agh.controller.budget;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Setter;
import pl.edu.agh.model.Budget;
import pl.edu.agh.model.Category;
import pl.edu.agh.model.Subcategory;
import pl.edu.agh.model.SubcategoryBudget;
import pl.edu.agh.service.BudgetService;
import pl.edu.agh.service.CategoryService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class BudgetDetailsDialogController {

    @Setter
    private BudgetService budgetService;
    @Setter
    private CategoryService categoryService;
    @Setter
    private BudgetDetailsController budgetDetailsController;
    @Setter
    private Budget budget;

    @FXML
    public TextField budgetField;
    @FXML
    public ChoiceBox<Category> categoryChoiceBox;
    @FXML
    public ChoiceBox<Subcategory> subcategoryChoiceBox;

    @FXML
    public void initialize() {
        categoryChoiceBox.setOnAction(event -> subcategoryChoiceBox.
                setItems(FXCollections.observableArrayList(categoryChoiceBox.getValue().getSubcategories())));
    }

    @FXML
    public void handleAddAction(ActionEvent event) {
        BigDecimal budgetAmount = new BigDecimal(budgetField.getText());
        Subcategory subcategory = subcategoryChoiceBox.getSelectionModel().getSelectedItem();

        if (subcategory != null) {
            Optional<SubcategoryBudget> subcategoryBudgetOptional = budgetService.findSubcategoryBudget(subcategory, budget);
            if (subcategoryBudgetOptional.isPresent()){
                SubcategoryBudget subcategoryBudget = subcategoryBudgetOptional.get();
                subcategoryBudget.setPlannedBudget(budgetAmount);
                budgetService.saveSubcategoryBudget(subcategoryBudget);
            } else {
                SubcategoryBudget subcategoryBudget = new SubcategoryBudget(subcategory, budgetAmount);
                budgetService.saveSubcategoryBudget(subcategoryBudget);
                budgetService.addSubcategoryBudget(subcategoryBudget, budget);
            }
            budgetDetailsController.loadData();
            closeDialog(event);
        }
    }

    @FXML
    public void closeDialog(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public void loadData(Subcategory subcategory) {
        new Thread(() -> {
            List<Category> categories = categoryService.getAllCategories();
            Platform.runLater(() -> categoryChoiceBox.setItems(FXCollections.observableArrayList(categories)));
            if (subcategory != null) {
                categoryChoiceBox.setValue(subcategory.getCategory());
                subcategoryChoiceBox.setValue(subcategory);
            }
        }).start();
    }
}
