package pl.edu.agh.model;

import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity(name = "SubcategoryBudgets")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class SubcategoryBudget {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NonNull
    @NotNull
    @ManyToOne(optional = false)
    private Subcategory subcategory;

    @NonNull
    @NotNull
    @Column(nullable = false)
    private BigDecimal plannedBudget;

    public SubcategoryBudget(Subcategory subcategory, BigDecimal bigDecimal) {
        this.subcategory = subcategory;
        this.plannedBudget = bigDecimal;
    }
}
