package model.mongo;

import org.mongodb.morphia.annotations.Entity;

@Entity
public class Company extends BaseEntity {
    private Long id;
    private String name;
    private Long adressId;

    public Company(Long id, String name, long adress) {
        this.id = id;
        this.name = name;
        this.adressId = adress;
    }
}
