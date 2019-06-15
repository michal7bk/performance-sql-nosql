package model.mongo;

import org.mongodb.morphia.annotations.Entity;

@Entity
public class Company extends BaseEntity {
    private Long id;
    private String name;
    private Adress adress;

    public Company(String name, Adress adress) {
        this.name = name;
        this.adress = adress;
    }
}
