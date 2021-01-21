package pl.edu.agh.model;

import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;
import java.time.Month;
import java.util.LinkedList;
import java.util.List;

@Entity
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<SubcategoryBudget> subcategoryBudgetList = new LinkedList<>();

    @NonNull
    @NotNull
    private int year;

    @NonNull
    @NotNull
    private Month month;

    public void addSubcategoryBudget(SubcategoryBudget subcategoryBudget){
        subcategoryBudgetList.add(subcategoryBudget);
    }

    public void removeSubcategoryBudget(SubcategoryBudget subcategoryBudget){
        subcategoryBudgetList.remove(subcategoryBudget);
    }

    @Override
    public String toString() {
        return year + " " + month.toString();
    }
}
