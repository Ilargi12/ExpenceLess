package pl.edu.agh.controller.category;

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
import pl.edu.agh.model.Type;

public class CategoryDialogController extends ModificationController {
    @FXML
    public TextField nameTextField;
    @FXML
    public ChoiceBox<Type> typeChoiceBox;

    @Setter
    private AccountDetailsController accountDetailsController;

    @FXML
    public void initialize(){
        loadData();
    }

    @FXML
    public void handleCancelAction(ActionEvent event) {
        closeDialog(event);
    }

    @FXML
    public void handleAddAction(ActionEvent event) {
        Category category;
        try {
            category = new Category(nameTextField.getText(), typeChoiceBox.getValue());
        } catch (NumberFormatException e){
            System.out.println("Wrong format!");
            return;
        }
        categoryService.createCategory(category);
        accountDetailsController.refresh();
        closeDialog(event);
    }

    private void closeDialog(ActionEvent event){
        Node source = (Node)  event.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @Override
    public void loadData() {
        typeChoiceBox.setItems(FXCollections.observableArrayList(Type.values()));
    }
}
