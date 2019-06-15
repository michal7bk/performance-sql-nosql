package model.postgre;

import lombok.Data;

@Data
public class Employee {

    private Long id;
    private String name;
    private String surname;
    private int age;
    private String gender;
    private Long adressId;
    private Long companyId;

    public Employee(String name, String surname, int age, String gender, Long adressId, Long companyId) {
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.gender = gender;
        this.adressId = adressId;
        this.companyId = companyId;
    }
}
