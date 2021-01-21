package pl.edu.agh.controller.category;

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
import pl.edu.agh.controller.account.AccountDetailsController;
import pl.edu.agh.model.Category;
import pl.edu.agh.model.Subcategory;

import java.util.List;

public class SubcategoryDialogController extends ModificationController {
    @FXML
    public TextField nameTextField;
    @FXML
    public ChoiceBox<Category> categoryChoiceBox;
    @Setter
    private AccountDetailsController accountDetailsController;

    public void closeDialog(ActionEvent event){
        Node source = (Node)  event.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public void loadData() {
        new Thread(() -> {
            List<Category> categories = categoryService.getAllCategories();
            Platform.runLater(() -> categoryChoiceBox.setItems(FXCollections.observableArrayList(categories)));
        }).start();
    }

    public void handleCancelAction(ActionEvent event) {
        closeDialog(event);
    }

    public void handleAddAction(ActionEvent event) {
        Subcategory subcategory;
        try {
            subcategory = new Subcategory(nameTextField.getText(), categoryChoiceBox.getValue());
        } catch (NumberFormatException | NullPointerException e ){
            System.out.println("Wrong format!");
            return;
        }
        categoryService.createSubcategory(subcategory);
        accountDetailsController.refresh();
        closeDialog(event);
    }
}
