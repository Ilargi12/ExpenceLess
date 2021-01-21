package pl.edu.agh.viewelements;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import pl.edu.agh.controller.RootViewController;
import pl.edu.agh.model.Budget;
import pl.edu.agh.util.View;

public class BudgetViewElement extends VBox {
    public final Budget budget;

    public BudgetViewElement(Budget budget) {
        this.budget = budget;
        Button button = new Button("Otwórz");
        Text monthText = new Text(budget.getMonth().toString());
        getChildren().addAll(monthText, button);
        this.getStyleClass().add("account-view-element");
        this.setSpacing(20);
        button.getStyleClass().add("standard-button");
        button.setOnAction(event ->
            RootViewController.routeTo(View.BUDGETS, View.BUDGET_DETAILS, budget)
        );
    }

}
