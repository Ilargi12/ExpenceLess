package pl.edu.agh.viewelements;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import pl.edu.agh.controller.RootViewController;
import pl.edu.agh.model.Account;
import pl.edu.agh.util.View;

public class AccountViewElement extends VBox {
    public final Account account;
    private final Text balanceText;
    private final Text accountName;

    public AccountViewElement(Account account) {
        this.account = account;
        Button button = new Button("Otwórz");
        balanceText = new Text(account.getBalance() + " PLN");
        balanceText.setFill(account.getBalance().doubleValue() >= 0 ? Color.GREEN : Color.RED);

        button.setOnAction((event ->
                RootViewController.routeTo(View.ACCOUNTS, View.ACCOUNT_DETAILS, account)
        ));
        accountName = new Text(account.getName());
        getChildren().addAll(accountName, balanceText, button);

        this.getStyleClass().add("account-view-element");
        this.setSpacing(20);
        button.getStyleClass().add("standard-button");
    }

}
