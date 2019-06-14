package model;

import com.mongodb.BasicDBObject;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Adress extends BasicDBObject {
    private Long id;
    private String city;
    private String street;
    private String number;
}
