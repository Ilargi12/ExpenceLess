package pl.edu.agh.controller.budget;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Setter;
import pl.edu.agh.model.Budget;
import pl.edu.agh.service.BudgetService;
import pl.edu.agh.service.CategoryService;

import java.time.Month;
import java.util.*;

public class BudgetDialogController {
    @Setter
    private CategoryService categoryService;
    @Setter
    private BudgetService budgetService;
    @Setter
    private BudgetController budgetController;
    @Setter
    private int year;

    @FXML
    public ChoiceBox<Month> monthBox;
    @FXML
    public VBox mainBox;

    @FXML
    public void cancelButtonClicked(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void okButtonClicked(ActionEvent event) {
        try {
            Budget budget = new Budget();
            budget.setMonth(monthBox.getValue());
            budget.setYear(year);
            budget.setSubcategoryBudgetList(new ArrayList<>());
            budgetService.createBudget(budget);

        } catch (NumberFormatException e) {
            System.out.println("Wrong format!");
            return;
        }
        budgetController.refreshData();

        cancelButtonClicked(event);
    }

    @FXML
    public void initialize() {
    }

    public void loadData() {
        monthBox.setItems(FXCollections.observableArrayList(budgetService.getFreeMonths(year)));
    }
}
