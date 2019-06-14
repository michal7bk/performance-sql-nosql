package model;

import com.mongodb.BasicDBObject;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Company extends BasicDBObject {

    private Long id;
    private String name;
    private Adress adress;
}
