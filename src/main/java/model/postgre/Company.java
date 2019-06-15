package model.postgre;

import lombok.Data;

@Data
public class Company {
    private Long id;
    private String name;
    private Long adressId;

    public Company(String name, Long adressId) {
        this.name = name;
        this.adressId = adressId;
    }
}
