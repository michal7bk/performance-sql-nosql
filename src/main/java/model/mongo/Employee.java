package model.mongo;

import org.mongodb.morphia.annotations.Entity;

@Entity
public class Employee extends BaseEntity {

    private Long id;
    private String name;
    private String surname;
    private int age;
    private String gender;
    private Long adressId;
    private Long companyId;

    public Employee(Long id, String name, String surname, int age, String gender, Long adress, Long company) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.gender = gender;
        this.adressId = adress;
        this.companyId = company;
    }
}
