package pl.edu.agh.controller.budget;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Setter;
import pl.edu.agh.model.Budget;
import pl.edu.agh.service.BudgetService;
import pl.edu.agh.service.CategoryService;
import pl.edu.agh.viewelements.BudgetViewElement;

import java.io.IOException;
import java.util.Calendar;


public class BudgetController {
    @Setter
    private BudgetService budgetService;
    @Setter
    private CategoryService categoryService;

    private int currentYear;
    private int budgetNumber = 0;

    @FXML
    private GridPane gridPane;
    @FXML
    private Label yearLabel;

    @FXML
    public void initialize(){
        currentYear = Calendar.getInstance().get(Calendar.YEAR);
        this.yearLabel.setText(Integer.toString(currentYear));
    }

    @FXML
    public void addBudget(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/budgetDialog.fxml"));
        Pane pane = fxmlLoader.load();
        BudgetDialogController controller = fxmlLoader.getController();
        controller.setCategoryService(categoryService);
        controller.setBudgetService(budgetService);
        controller.setBudgetController(this);
        controller.setYear(currentYear);
        controller.loadData();
        Scene scene = new Scene(pane);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
    }

    private void addBudgetToPane(Budget budget){
        if (budgetNumber <= 10 ){
            BudgetViewElement budgetViewElement = new BudgetViewElement(budget);
            gridPane.add(budgetViewElement, budgetNumber % 4, budgetNumber / 4);
            budgetNumber++;
        }
    }

    public void refreshData(){
        this.yearLabel.setText(Integer.toString(currentYear));
        this.gridPane.getChildren().clear();
        this.budgetNumber = 0;
        if (budgetService != null){
            for (Budget budget : budgetService.getBudgetsByYear(currentYear)){
                Platform.runLater(() -> addBudgetToPane(budget));
            }
        }
    }

    public void nextYear(ActionEvent event) {
        currentYear++;
        refreshData();
    }

    public void prevYear(ActionEvent event) {
        currentYear--;
        refreshData();
    }
}
