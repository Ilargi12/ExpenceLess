package pl.edu.agh.service;

import com.google.inject.Inject;
import pl.edu.agh.dao.CategoryDao;
import pl.edu.agh.dao.SubcategoryDao;
import pl.edu.agh.model.Category;
import pl.edu.agh.model.Subcategory;
import pl.edu.agh.util.SessionUtil;

import java.util.List;
import java.util.Optional;

public class CategoryService {

    private final CategoryDao categoryDao;
    private final SubcategoryDao subcategoryDao;

    @Inject
    public CategoryService(CategoryDao categoryDao, SubcategoryDao subcategoryDao) {
        this.categoryDao = categoryDao;
        this.subcategoryDao = subcategoryDao;
    }

    public void createCategory(Category category) {
        SessionUtil.openSession();
        categoryDao.save(category);
        SessionUtil.closeSession();
    }

    public void createSubcategory(Subcategory subcategory) {
        SessionUtil.openSession();
        subcategoryDao.save(subcategory);
        SessionUtil.closeSession();
    }

    public List<Category> getAllCategories() {
        SessionUtil.openSession();
        List<Category> categoryList = categoryDao.getAllCategories();
        SessionUtil.closeSession();

        return categoryList;
    }

    public List<Subcategory> getAllSubcategories() {
        SessionUtil.openSession();
        List<Subcategory> categoryList = subcategoryDao.getAllSubcategories();
        SessionUtil.closeSession();

        return categoryList;
    }

    public Category getCategoryByName(String name) {
        SessionUtil.openSession();
        Category category = categoryDao.findCategoryByName(name);
        SessionUtil.closeSession();

        return category;
    }

    public Optional<Subcategory> getSubcategoryByNameCategory(String name, String category) {

        List<Subcategory> subcategoryList = getSubcategoriesFromCategory(getCategoryByName(category));

        Optional<Subcategory> subcategory = subcategoryList.stream().filter(subcat -> subcat.getName().equals(name)).findFirst();

        return subcategory;
    }

    public List<Subcategory> getSubcategoriesFromCategory(Category category) {
        SessionUtil.openSession();
        List<Subcategory> subcategoryList = subcategoryDao.getSubcategoriesFromCategory(category);
        SessionUtil.closeSession();

        return subcategoryList;
    }

    public void deleteCategory(Category category){
        var i = category.getSubcategories().size();

        for (Subcategory subcategory : category.getSubcategories()) {
            SessionUtil.openSession();
            subcategoryDao.mergeTransactionsToOther(subcategory);
            SessionUtil.closeSession();
        }

        SessionUtil.openSession();
        categoryDao.deleteCategory(category);
        SessionUtil.closeSession();
    }

    public void deleteSubcategory(Subcategory subcategory){
        SessionUtil.openSession();
        subcategoryDao.mergeTransactionsToOther(subcategory);
        SessionUtil.closeSession();

        SessionUtil.openSession();
        subcategoryDao.deleteSubcategory(subcategory);
        SessionUtil.closeSession();


    }
}
