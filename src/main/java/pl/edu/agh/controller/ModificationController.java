package pl.edu.agh.controller;

import lombok.Setter;
import pl.edu.agh.service.AccountService;
import pl.edu.agh.service.CategoryService;
import pl.edu.agh.service.TransactionService;

public abstract class ModificationController {
    @Setter
    protected CategoryService categoryService;

    @Setter
    protected AccountService accountService;

    @Setter
    protected TransactionService transactionService;
    public abstract void loadData();
}
