package pl.edu.agh;

import com.google.inject.Guice;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import pl.edu.agh.controller.NavbarController;
import pl.edu.agh.service.AccountService;
import pl.edu.agh.service.BudgetService;
import pl.edu.agh.service.CategoryService;
import pl.edu.agh.service.TransactionService;
import pl.edu.agh.util.View;

import java.io.IOException;


public class Main extends Application {

    private Pane mainPane;

    private FXMLLoader fxmlLoader;

    @Override
    public void start(Stage primaryStage) {
        Injector injector = Guice.createInjector();

        // TODO consider injection everywhere
        var accountService = injector.getInstance(AccountService.class);
        var categoryService = injector.getInstance(CategoryService.class);
        var transactionService = injector.getInstance(TransactionService.class);
        var budgetService = injector.getInstance(BudgetService.class);

        try{
            initializeMenu();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        MockData mockData = new MockData(accountService, budgetService, categoryService, transactionService);
        mockData.init();

        Scene mainScene = new Scene(mainPane);
        mainScene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());

        NavbarController navbarController = fxmlLoader.getController();
        navbarController.setAccountService(accountService);
        navbarController.setBudgetService(budgetService);
        navbarController.setCategoryService(categoryService);
        navbarController.setTransactionService(transactionService);
        NavbarController.setMvc(navbarController);
        NavbarController.routeTo(View.ACCOUNTS);


        primaryStage.setTitle("ExpensLESS");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    private void initializeMenu() throws IOException {
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/navbar.fxml"));
        mainPane = fxmlLoader.load();
    }
}
