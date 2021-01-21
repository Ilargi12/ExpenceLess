package pl.edu.agh.model;

public enum Type {
    INCOME ("Przychody"),
    EXPENSE ("Wydatki");

    String name;

    Type(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
