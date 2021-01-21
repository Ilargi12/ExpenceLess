package pl.edu.agh.controller.account;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Setter;
import pl.edu.agh.model.Account;
import pl.edu.agh.service.AccountService;
import pl.edu.agh.viewelements.AccountViewElement;

import java.io.IOException;
import java.util.List;

public class AccountController {
    @Setter
    private AccountService accountService;

    private int accountsNumber = 0;

    @FXML
    private GridPane gridPane;

    public void loadData(){
        new Thread(() -> {
            List<Account> accountList = accountService.getAllAccounts();
            Platform.runLater(() -> accountList.forEach(this::addAccountToPane));
        }).start();
    }

    @FXML
    private void handleAddAction(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/accountsDialog.fxml"));
        Pane pane = fxmlLoader.load();
        AccountDialogController controller = fxmlLoader.getController();
        controller.setAccountService(accountService);

        controller.setAccountController(this);

        Scene scene = new Scene(pane);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
    }

    AccountViewElement addAccountToPane(Account account){
        if (accountsNumber <= 10 ){
            AccountViewElement accountViewElement = new AccountViewElement(account);
            gridPane.add(accountViewElement, accountsNumber % 4, accountsNumber / 4);
            accountsNumber++;
            return accountViewElement;
        }
        return null;
    }
}

