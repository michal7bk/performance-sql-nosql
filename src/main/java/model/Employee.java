package model;

import com.mongodb.BasicDBObject;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Employee extends BasicDBObject {
    private Long id;
    private String name;
    private String surname;
    private int age;
    private String gender;
    private Adress adress;
    private Company company;
}
