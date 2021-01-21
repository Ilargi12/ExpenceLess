package pl.edu.agh.model;

import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;

@Entity(name = "Subcategories")
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class Subcategory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String name;

    private boolean canBeDeleted = true;

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    private Category category;

    public Subcategory(String name, Category category) {
        this.name = name;
        this.category = category;
        category.addSubcategory(this);
    }

    @Override
    public String toString() {
        return name;
    }
}
