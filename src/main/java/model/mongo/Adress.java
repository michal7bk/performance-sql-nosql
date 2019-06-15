package model.mongo;

import org.mongodb.morphia.annotations.Entity;

@Entity
public class Adress extends BaseEntity {
    private Long id;
    private String city;
    private String street;
    private String number;

    public Adress(Long id, String city, String street, String number) {
        this.id = id;
        this.city = city;
        this.street = street;
        this.number = number;
    }
}
