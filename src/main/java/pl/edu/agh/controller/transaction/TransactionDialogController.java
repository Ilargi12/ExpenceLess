package pl.edu.agh.controller.transaction;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
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
        categoryChoiceBox.setOnAction(event -> subcategoryChoiceBox.
                setItems(FXCollections.observableArrayList(categoryChoiceBox.getValue().getSubcategories())));
    }

    @FXML
    public void okButtonClicked(ActionEvent event) {
        try {
            String name = nameTextField.getText();
            BigDecimal price = new BigDecimal(priceTextField.getText());
            Optional<LocalDate> date = parseDateFromString(dateTextField.getText());
            String description = descriptionTextField.getText();
            Subcategory subcategory = subcategoryChoiceBox.getSelectionModel().getSelectedItem();

            if(!name.isEmpty() && date.isPresent() && subcategory != null && price.compareTo(BigDecimal.ZERO) > 0){
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
            }
        } catch (Exception e){
            System.out.println("Wrong format");
        }
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
        LocalDate date = LocalDate.parse(text);
        return Optional.of(date);
    }
}
