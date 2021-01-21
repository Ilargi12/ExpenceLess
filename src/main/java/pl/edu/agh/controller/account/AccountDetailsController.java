package pl.edu.agh.controller.account;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Setter;
import pl.edu.agh.controller.ModificationController;
import pl.edu.agh.controller.category.CategoryDialogController;
import pl.edu.agh.controller.category.DeleteCategoryDialogController;
import pl.edu.agh.controller.category.EditCategoryDialogController;
import pl.edu.agh.controller.category.SubcategoryDialogController;
import pl.edu.agh.controller.transaction.TransactionDialogController;
import pl.edu.agh.model.*;

import java.io.IOException;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

// TODO This class is too long!
public class AccountDetailsController extends ModificationController {
    @Setter
    private Account account;

    @FXML
    private Label name;
    @FXML
    private Label balance;
    @FXML
    private TableColumn<Transaction, String> nameColumn;
    @FXML
    private TableColumn<Transaction, Double> priceColumn;
    @FXML
    private TableColumn<Transaction, String> dateColumn;
    @FXML
    private TableColumn<Transaction, String> descriptionColumn;
    @FXML
    private TableColumn<Transaction, String> categoryColumn;
    @FXML
    private TableView<Transaction> transactionsTable;
    @FXML
    private TreeView<Object> categoryTreeView = new TreeView<>();

    @Override
    public void loadData(){
        name.setText(account.getName());
        setTableView();

        categoryTreeView.setOnMouseClicked(event -> {
            List<Transaction> transactions;
            var item = categoryTreeView.getSelectionModel().getSelectedItem().getValue();
            if(item instanceof Category) transactions = transactionService.getTransactionsOfCategoryAndAccount((Category)item, account);
            else transactions = transactionService.getTransactionOfSubcategoryAndAccount((Subcategory)item, account);
            refreshTableView(transactions);
        });

        new Thread(() -> Platform.runLater(this::refresh)).start();
    }

    public void loadController(ModificationController controller, Pane page){
        controller.setCategoryService(categoryService);
        controller.setAccountService(accountService);
        controller.setTransactionService(transactionService);
        controller.loadData();
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
        refresh();
    }

    private void setTableView() {
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        priceColumn.setCellValueFactory(data -> {
            var text = new Text(data.getValue().getPrice().setScale(2, RoundingMode.DOWN).toString());
            if (data.getValue().getType() != null){
                text.setFill(data.getValue().getType() == Type.INCOME ? Color.GREEN : Color.RED);
            }
            return new SimpleObjectProperty(text);
        });
        dateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDate().toString()));
        descriptionColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescription()));
        categoryColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSubCategory() != null ?  data.getValue().getSubCategory().getCategory().getName()+ " / " + data.getValue().getSubCategory().getName() : ""));
    }

    public void refresh(){
        List<Transaction> transactions = transactionService.getAllTransactionsOfAccount(account);
        List<Category> categoryList = categoryService.getAllCategories();
        refreshCategoryTree(categoryList);
        refreshTableView(transactions);
        refreshAccountInfo();
    }

    private void refreshTableView(List<Transaction> transactions) {
        transactions.sort(Comparator.comparing(Transaction::getDate, Comparator.reverseOrder()));
        transactionsTable.setItems(FXCollections.observableList(transactions));
    }

    private void refreshAccountInfo() {
        balance.setText(account.getBalance() + " PLN");
        balance.setTextFill(account.getBalance().doubleValue() >= 0 ? Color.GREEN : Color.RED);
    }

    private void refreshCategoryTree(List<Category> categoryList){
        categoryTreeView.setRoot(null);
        TreeItem<Object> rootItem = new TreeItem<>();
        rootItem.setExpanded(true);

        for (Category cat : categoryList) {
            TreeItem<Object> categoryTreeItem = new TreeItem<>(cat);
            for (Subcategory subcategory : cat.getSubcategories()) {
                if (subcategory != null) {
                    categoryTreeItem.getChildren().add(new TreeItem<>(subcategory));
                }
            }
            rootItem.getChildren().add(categoryTreeItem);
        }
        categoryTreeView.setRoot(rootItem);
        categoryTreeView.setShowRoot(false);
    }

    public void addSubcategory(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/subcategoryDialog.fxml"));
        Pane page = loader.load();

        SubcategoryDialogController controller = loader.getController();
        controller.setAccountDetailsController(this);
        loadController(controller, page);
    }

    public void addCategory(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/categoryDialog.fxml"));
        Pane page = loader.load();

        CategoryDialogController controller = loader.getController();
        controller.setAccountDetailsController(this);
        loadController(controller, page);
    }

    public void editCategory(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/editCategoryDialog.fxml"));
        Pane page = loader.load();

        EditCategoryDialogController controller = loader.getController();
        controller.setAccountDetailsController(this);
        loadController(controller, page);
    }

    public void deleteCategory(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/deleteCategoryDialog.fxml"));
        Pane page = loader.load();

        DeleteCategoryDialogController controller = loader.getController();
        controller.setAccountDetailsController(this);
        loadController(controller, page);
    }

    public void addTransaction(ActionEvent event) throws IOException {
        openTransactionDialog(null);
    }

    public void editTransaction(ActionEvent event) throws IOException {
        Transaction selectedItem = transactionsTable.getSelectionModel().getSelectedItem();
        if(selectedItem != null) openTransactionDialog(selectedItem);
    }

    public void deleteTransaction(ActionEvent event) {
        Transaction transaction = transactionsTable.getSelectionModel().getSelectedItem();
        if(transaction != null) {
            accountService.removeTransaction(account, transaction);
            transactionService.deleteTransaction(transaction);
            refresh();
        }

    }

    private void openTransactionDialog(Transaction transaction) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/transactionDialog.fxml"));
        Pane page = loader.load();

        TransactionDialogController controller = loader.getController();
        controller.setAccount(account);
        controller.setTransactionToEdit(transaction);
        loadController(controller, page);
    }

    public void markOut(ActionEvent event) {
        categoryTreeView.getSelectionModel().clearSelection();
        refreshTableView(transactionService.getAllTransactionsOfAccount(account));
    }


}


