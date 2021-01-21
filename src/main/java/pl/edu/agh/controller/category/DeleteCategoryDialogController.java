package pl.edu.agh.controller.category;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import lombok.Setter;
import pl.edu.agh.controller.ModificationController;
import pl.edu.agh.controller.account.AccountDetailsController;
import pl.edu.agh.model.Category;
import pl.edu.agh.model.Subcategory;

import java.util.List;
import java.util.stream.Collectors;

public class DeleteCategoryDialogController extends ModificationController {
    @FXML
    public ChoiceBox<Category> categoryChoiceBox;
    @FXML
    public ChoiceBox<Subcategory> subcategoryChoiceBox;
    @Setter
    private AccountDetailsController accountDetailsController;

    @FXML
    public void initialize() {
        categoryChoiceBox.setOnAction(event -> subcategoryChoiceBox.
                setItems(FXCollections.observableArrayList(categoryChoiceBox.getValue().getSubcategories().stream().filter(Subcategory::isCanBeDeleted).collect(Collectors.toList()))));
    }

    public void handleCancelAction(ActionEvent event) {
        closeDialog(event);
    }

    @Override
    public void loadData() {
        new Thread(() -> {
            List<Category> categories = categoryService.getAllCategories();
            Platform.runLater(() -> categoryChoiceBox.setItems(FXCollections.observableArrayList(categories)));
        }).start();
    }

    public void handleDeleteAction(ActionEvent event) {

        try {
            Category category = categoryChoiceBox.getValue();
            Subcategory subcategory = subcategoryChoiceBox.getValue();
            if (subcategory != null){
                if (!subcategory.isCanBeDeleted()){
                    throw new IllegalArgumentException();
                }
                categoryService.deleteSubcategory(subcategory);
            } else {
                if (!category.isCanBeDeleted()){
                    throw new IllegalArgumentException();
                }
                categoryService.deleteCategory(category);
            }

        } catch (NullPointerException | IllegalArgumentException e ){
            System.out.println("Wrong format!");
            return;
        }
        accountDetailsController.refresh();
        closeDialog(event);
    }

    public void closeDialog(ActionEvent event){
        Node source = (Node)  event.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }




}
