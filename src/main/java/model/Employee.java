package model;

import org.mongodb.morphia.annotations.Entity;

@Entity
public class Employee extends BaseEntity {

    private Long id;
    private String name;
    private String surname;
    private int age;
    private String gender;
    private Adress adress;
    private Company company;

    public Employee(String name, String surname, int age, String gender, Adress adress, Company company) {
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.gender = gender;
        this.adress = adress;
        this.company = company;
    }
}
