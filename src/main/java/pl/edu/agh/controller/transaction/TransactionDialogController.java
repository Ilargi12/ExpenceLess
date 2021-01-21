package pl.edu.agh.controller.transaction;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Setter;
import pl.edu.agh.controller.ModificationController;
import pl.edu.agh.model.Account;
import pl.edu.agh.model.Category;
import pl.edu.agh.model.Subcategory;
import pl.edu.agh.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

public class TransactionDialogController extends ModificationController {
    @Setter
    private Account account;

    @Setter
    private Transaction transactionToEdit;

    @FXML
    public TextField nameTextField;
    @FXML
    public TextField priceTextField;
    @FXML
    public TextField dateTextField;
    @FXML
    public TextField descriptionTextField;
    @FXML
    public ChoiceBox<Category> categoryChoiceBox;
    @FXML
    public ChoiceBox<Subcategory> subcategoryChoiceBox;
    @FXML
    public void initialize() {
        dateTextField.setPromptText("rrrr-mm-dd");
        priceTextField.setPromptText("Dodatnia liczba");
        categoryChoiceBox.setOnAction(event -> subcategoryChoiceBox.
                setItems(FXCollections.observableArrayList(categoryChoiceBox.getValue().getSubcategories())));
    }

    @FXML
    public void okButtonClicked(ActionEvent event) {
        String name = nameTextField.getText();
        String priceString = priceTextField.getText();
        Optional<LocalDate> date = parseDateFromString(dateTextField.getText());
        String description = descriptionTextField.getText();
        Subcategory subcategory = subcategoryChoiceBox.getSelectionModel().getSelectedItem();
        try {
            BigDecimal price = new BigDecimal(priceString);
            if(!name.isEmpty() && date.isPresent() && subcategory != null &&
                    price.compareTo(BigDecimal.ZERO) > 0){
                if(transactionToEdit != null){
                    accountService.removeTransaction(account, transactionToEdit);
                    transactionService.deleteTransaction(transactionToEdit);
                }
                Transaction transaction = Transaction.builder().
                        name(name)
                        .price(price)
                        .date(date.get())
                        .description(description)
                        .account(account)
                        .subCategory(subcategory)
                        .type(subcategory.getCategory().getType())
                        .build();
                accountService.addTransaction(account, transaction);
                transactionService.saveTransaction(transaction);
                closeDialog(event);
            } else
                showMissingTransactionInfo(name, price, date, subcategory, true);
        } catch (NumberFormatException e){
            showMissingTransactionInfo(name, BigDecimal.ZERO, date, subcategory, false);
        }
    }

    private void showMissingTransactionInfo(String name, BigDecimal price, Optional<LocalDate> date, Subcategory subcategory, boolean isPriceValid) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        StringBuilder stringBuilder = new StringBuilder();
        alert.setTitle("");
        alert.setHeaderText("Niepoprawne informacje transakcji");
        if(name.isEmpty()) stringBuilder.append("Podano złą nazwę\n");
        if(date.isEmpty()) stringBuilder.append("Podano złą datę\n");
        if(subcategory == null) stringBuilder.append("Transakcja musi mieć wybraną kategorię i podkategorię\n");
        if(price.compareTo(BigDecimal.ZERO) < 0 || !isPriceValid) stringBuilder.append("Kwota transakcji musi być dodatnią liczbą\n");
        alert.setContentText(stringBuilder.toString());
        alert.showAndWait();
    }

    @FXML
    public void closeDialog(ActionEvent event){
        Node source = (Node)  event.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @Override
    public void loadData(){
        new Thread(() -> {
            List<Category> categories = categoryService.getAllCategories();
            if(transactionToEdit != null){
                nameTextField.setText(transactionToEdit.getName());
                priceTextField.setText(transactionToEdit.getPrice().toString());
                dateTextField.setText(transactionToEdit.getDate().toString());
                descriptionTextField.setText(transactionToEdit.getDescription() != null ? transactionToEdit.getDescription() : "");
            } else{
                dateTextField.setText(LocalDate.now().toString());
            }
            Platform.runLater(() -> categoryChoiceBox.setItems(FXCollections.observableArrayList(categories)));
        }).start();
    }

    private Optional<LocalDate> parseDateFromString(String text) {
        try{
            LocalDate date = LocalDate.parse(text);
            return Optional.of(date);
        }catch (DateTimeParseException e){
            return Optional.empty();
        }
    }
}
