package model.postgre;

import lombok.Data;

@Data
public class Adress {
    private Long id;
    private String city;
    private String street;
    private Long number;

    public Adress(String city, String street, Long number) {
        this.city = city;
        this.street = street;
        this.number = number;
    }
}
