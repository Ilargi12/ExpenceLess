package pl.edu.agh.controller.budget;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Setter;
import org.hibernate.Hibernate;
import pl.edu.agh.model.*;
import pl.edu.agh.service.BudgetService;
import pl.edu.agh.service.CategoryService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class BudgetDetailsController {
    @Setter
    private BudgetService budgetService;
    @Setter
    private Budget budget;
    @Setter
    private CategoryService categoryService;

    @FXML
    private Label nameLabel;
    @FXML
    private TreeView<Object> categoryTree;
    @FXML
    private HBox summary;


    @FXML
    public void editButtonClicked(MouseEvent event) throws IOException {
        if (categoryTree.getSelectionModel().getSelectedItem() == null) {
            return;
        }

        GridPane selected = (GridPane) categoryTree.getSelectionModel().getSelectedItem().getValue();
        GridPane parent = (GridPane) categoryTree.getSelectionModel().getSelectedItem().getParent().getValue();

        for (Node node : selected.getChildren()) {
            if (GridPane.getRowIndex(node) == 0 && GridPane.getColumnIndex(node) == 0) {
                Text textP = null;
                for (Node node1 : parent.getChildren()) {
                    if (GridPane.getRowIndex(node1) == 0 && GridPane.getColumnIndex(node1) == 0) {
                        textP = (Text) node1;
                    }
                }

                Text text = (Text) node;
                Optional<Subcategory> subcategory = categoryService.getSubcategoryByNameCategory(text.getText(), textP.getText());


                if (subcategory.isPresent()) {
                    openSubcategoryBudgetDialog(subcategory.get());
                }
            }
        }
    }

    @FXML
    public void addButtonClicked(MouseEvent event) throws IOException {
        openSubcategoryBudgetDialog(null);
    }

    @FXML
    public void deleteButtonClicked(MouseEvent event) throws IOException {
        if (categoryTree.getSelectionModel().getSelectedItem() == null) {
            return;
        }

        GridPane selected = (GridPane) categoryTree.getSelectionModel().getSelectedItem().getValue();
        GridPane parent = (GridPane) categoryTree.getSelectionModel().getSelectedItem().getParent().getValue();

        for (Node node : selected.getChildren()) {
            if (GridPane.getRowIndex(node) == 0 && GridPane.getColumnIndex(node) == 0) {
                Text textP = null;
                for (Node node1 : parent.getChildren()) {
                    if (GridPane.getRowIndex(node1) == 0 && GridPane.getColumnIndex(node1) == 0) {
                        textP = (Text) node1;
                    }
                }
                Text text = (Text) node;
                Optional<Subcategory> subcategory = categoryService.getSubcategoryByNameCategory(text.getText(), textP.getText());


                if (subcategory.isPresent()) {
                    Optional<SubcategoryBudget> subcategoryBudgetOptional = budgetService.findSubcategoryBudget(subcategory.get(), budget);
                    if (subcategoryBudgetOptional.isPresent()) {
                        SubcategoryBudget subcategoryBudget = subcategoryBudgetOptional.get();
                        budgetService.deleteSubcategoryBudget(subcategoryBudget, budget);
                        loadData();
                    }
                }
            }
        }
    }

    private void openSubcategoryBudgetDialog(Subcategory subcategory) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/budgetDetailsDialog.fxml"));
        Pane pane = fxmlLoader.load();
        BudgetDetailsDialogController controller = fxmlLoader.getController();
        controller.setBudgetService(budgetService);
        controller.setCategoryService(categoryService);
        controller.setBudget(budget);
        controller.loadData(subcategory);
        controller.setBudgetDetailsController(this);

        Scene scene = new Scene(pane);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
    }

    public void loadData() {
        nameLabel.setText(budget.toString());
        new Thread(() -> {
            Hibernate.initialize(budget.getSubcategoryBudgetList());
            List<SubcategoryBudget> subcategoryBudgetList = budget.getSubcategoryBudgetList();
            TreeItem<Object> rootItem = new TreeItem<>("Categories");
            rootItem.setExpanded(true);
            List<Category> categories = categoryService.getAllCategories();
            BigDecimal sumBalanceIncome = BigDecimal.ZERO;
            BigDecimal sumBalanceExpense = BigDecimal.ZERO;
            BigDecimal sumPlannedIncome = BigDecimal.ZERO;
            BigDecimal sumPlannedExpense = BigDecimal.ZERO;
            for (Category cat : categories) {
                List<TreeItem<Object>> tmp_items = new LinkedList<>();
                BigDecimal sumBalance = BigDecimal.ZERO;
                BigDecimal sumPlanned = BigDecimal.ZERO;
                for (SubcategoryBudget subcat : subcategoryBudgetList) {
                    if (subcat.getSubcategory().getCategory().getId().equals(cat.getId())) {
                        BigDecimal balance = budgetService.calculateBudgetBalance(budget, subcat.getSubcategory());
                        Text text = new Text(balance + " / " + subcat.getPlannedBudget().toString());
                        sumBalance = sumBalance.add(balance);
                        sumPlanned = sumPlanned.add(subcat.getPlannedBudget());
                        text.setFill(subcat.getPlannedBudget().subtract(balance).doubleValue() >= 0 ? Color.GREEN : Color.RED);
                        GridPane gridPane = new GridPane();
                        gridPane.add(new Text(subcat.getSubcategory().getName()), 0, 0);
                        gridPane.add(text, 1, 0);
                        gridPane.setHgap(30);
                        tmp_items.add(new TreeItem<>(gridPane));
                    }
                }
                Text text = new Text(sumBalance + " / " + sumPlanned);
                text.setFill(sumPlanned.subtract(sumBalance).doubleValue() >= 0 ? Color.GREEN : Color.RED);
                GridPane gridPane = new GridPane();
                gridPane.add(new Text(cat.getName()), 0, 0);
                gridPane.add(text, 1, 0);
                gridPane.setHgap(30);
                TreeItem<Object> categoryTreeItem = new TreeItem<>(gridPane);

                for (TreeItem<Object> pane : tmp_items) {
                    categoryTreeItem.getChildren().add(pane);
                }
                categoryTreeItem.setExpanded(true);
                rootItem.getChildren().add(categoryTreeItem);

                if (cat.getType() == Type.EXPENSE) {
                    sumBalanceExpense = sumBalanceExpense.add(sumBalance);
                    sumPlannedExpense = sumPlannedExpense.add(sumPlanned);
                } else {
                    sumBalanceIncome = sumBalanceIncome.add(sumBalance);
                    sumPlannedIncome = sumPlannedIncome.add(sumPlanned);
                }
            }
            Text text1 = new Text(sumBalanceIncome + " / " + sumPlannedIncome);
            text1.setFont(new Font(20));
            Text text2 = new Text(sumBalanceExpense + " / " + sumPlannedExpense);
            text2.setFont(new Font(20));
            text1.setFill(sumPlannedIncome.subtract(sumBalanceIncome).doubleValue() >= 0 ? Color.GREEN : Color.RED);
            text2.setFill(sumPlannedExpense.subtract(sumBalanceExpense).doubleValue() >= 0 ? Color.GREEN : Color.RED);
            GridPane gridPane = new GridPane();
            Text text3 = new Text("Przychody: ");
            text3.setFont(new Font(20));
            Text text4 = new Text("Wydatki: ");
            text4.setFont(new Font(20));

            gridPane.add(text3, 0, 0);
            gridPane.add(text1, 1, 0);
            gridPane.setHgap(10);

            GridPane gridPane2 = new GridPane();
            gridPane2.add(text4, 0, 0);
            gridPane2.add(text2, 1, 0);
            gridPane2.setHgap(10);


            Platform.runLater(() -> {
                summary.getChildren().clear();
                summary.getChildren().add(gridPane);
                summary.getChildren().add(gridPane2);
                categoryTree.setRoot(rootItem);
                categoryTree.setShowRoot(false);
            });
        }).start();
    }
}
