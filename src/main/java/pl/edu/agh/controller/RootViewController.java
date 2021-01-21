package pl.edu.agh.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import lombok.Setter;
import pl.edu.agh.controller.account.AccountController;
import pl.edu.agh.controller.account.AccountDetailsController;
import pl.edu.agh.controller.budget.BudgetController;
import pl.edu.agh.controller.budget.BudgetDetailsController;
import pl.edu.agh.controller.report.ReportController;
import pl.edu.agh.model.Account;
import pl.edu.agh.model.Budget;
import pl.edu.agh.service.AccountService;
import pl.edu.agh.service.BudgetService;
import pl.edu.agh.service.CategoryService;
import pl.edu.agh.service.TransactionService;
import pl.edu.agh.util.View;

public class RootViewController {

    @Setter
    private CategoryService categoryService;
    @Setter
    private AccountService accountService;
    @Setter
    private TransactionService transactionService;
    @Setter
    private BudgetService budgetService;
    @Setter
    private static RootViewController mvc;
    @Setter
    private View previousView;

    @FXML
    private Text backButton;

    @FXML
    private BorderPane borderPane;

    // TODO expand functionality on report view
    @FXML
    public void backButtonClicked(MouseEvent event) {
        toggleBackBtnVisibility(false);
        setMainScene(previousView, null);
    }

    @FXML
    public void accountsButtonClicked(MouseEvent mouseEvent) {
        setMainScene(View.ACCOUNTS, null);
    }

    @FXML
    public void budgetsButtonClicked(MouseEvent mouseEvent) {
        setMainScene(View.BUDGETS, null);
    }

    @FXML
    public void reportButtonClicked(MouseEvent mouseEvent) {
        setMainScene(View.REPORT, null);
    }

    public void toggleBackBtnVisibility(boolean isVisible) {
        backButton.setVisible(isVisible);
    }

    public static void routeTo(View prevView, View view, Object object) {
        mvc.setPreviousView(prevView);
        mvc.toggleBackBtnVisibility(true);
        mvc.setMainScene(view, object);
    }

    public static void routeTo(View view) {
        mvc.setMainScene(view, null);
    }

    public void setMainScene(View view, Object object) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            switch (view) {
                case ACCOUNTS -> {
                    fxmlLoader.setLocation(RootViewController.class.getResource("/view/accountsView.fxml"));
                    Pane pane = fxmlLoader.load();
                    AccountController controller = fxmlLoader.getController();
                    controller.setAccountService(accountService);
                    controller.loadData();
                    mvc.toggleBackBtnVisibility(false);
                    borderPane.setCenter(pane);
                }
                case ACCOUNT_DETAILS -> {
                    if (object == null) {
                        throw new IllegalArgumentException("Account is required");
                    }

                    Account account = (Account) object;
                    fxmlLoader.setLocation(RootViewController.class.getResource("/view/categoriesView.fxml"));
                    Pane pane = fxmlLoader.load();
                    AccountDetailsController controller = fxmlLoader.getController();
                    controller.setAccount(account);
                    controller.setCategoryService(categoryService);
                    controller.setAccountService(accountService);
                    controller.setTransactionService(transactionService);
                    controller.loadData();
                    borderPane.setCenter(pane);
                }
                case BUDGETS -> {

                    fxmlLoader.setLocation(RootViewController.class.getResource("/view/budgetsView.fxml"));
                    Pane pane = fxmlLoader.load();
                    BudgetController budgetController = fxmlLoader.getController();
                    budgetController.setBudgetService(budgetService);
                    budgetController.setCategoryService(categoryService);
                    budgetController.refreshData();
                    mvc.toggleBackBtnVisibility(false);
                    borderPane.setCenter(pane);
                }
                case BUDGET_DETAILS -> {
                    if (object == null) {
                        throw new IllegalArgumentException("Account is required");
                    }

                    fxmlLoader.setLocation(RootViewController.class.getResource("/view/budgetDetailsView.fxml"));
                    Pane pane = fxmlLoader.load();
                    BudgetDetailsController controller = fxmlLoader.getController();
                    controller.setBudget((Budget)object);
                    controller.setBudgetService(budgetService);
                    controller.setCategoryService(categoryService);
                    controller.loadData();
                    borderPane.setCenter(pane);
                }
                case REPORT -> {
                    fxmlLoader.setLocation(RootViewController.class.getResource("/view/reportView.fxml"));
                    Pane pane = fxmlLoader.load();
                    ReportController controller = fxmlLoader.getController();
                    controller.setBudgetService(budgetService);
                    controller.loadData();
                    borderPane.setCenter(pane);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
