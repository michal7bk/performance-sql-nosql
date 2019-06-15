package mongo;

import com.mongodb.*;
import com.mongodb.operation.OrderBy;
import file.FileUtils;
import model.mongo.Adress;
import model.mongo.Company;
import model.mongo.Employee;
import org.apache.commons.lang3.RandomStringUtils;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.Morphia;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MongoTester {

    private static final String DATABASE = "testdb";
    private static final int numberOfRecords = 1000;

    private static List<Adress> adresses = new ArrayList<Adress>();
    private static List<Company> companies = new ArrayList<Company>();
    private static List<Employee> employees = new ArrayList<Employee>();
    private static MongoClient mongoClient;

    public static void main(String[] args) throws IOException {
        generateRandomData(numberOfRecords);
        DB db = connectToDatabase();
//        clearDataBase(db);
        populateDatabase(Employee.class, employees);
        populateDatabase(Company.class, companies);
        populateDatabase(Adress.class, adresses);
        agregation(db);
        sort(db);

    }


    private static void agregation(DB db) throws IOException {
        DBCollection coll = db.getCollection("Employee");

        // create the pipeline operations, first with the $match
        DBObject match = new BasicDBObject("$match",
                new BasicDBObject("id", 10)
        );

        // build the $lookup operations
        DBObject lookupFields = new BasicDBObject("from", "Company");
        lookupFields.put("localField", "companyId");
        lookupFields.put("foreignField", "id");
        lookupFields.put("as", "company");
        DBObject lookup = new BasicDBObject("$lookup", lookupFields);

        // build the $project operations
        DBObject projectFields = new BasicDBObject("name", 1);
        projectFields.put("surname", 1);
        projectFields.put("companyId", 1);
        projectFields.put("companyName", "$company.name");
        DBObject project = new BasicDBObject("$project", projectFields);

        List<DBObject> pipeline = Arrays.asList(match, lookup, project);

        Instant start = Instant.now();
        AggregationOutput output = coll.aggregate(pipeline);
        Instant end = Instant.now();
        long timeElapsed = Duration.between(start, end).toMillis();
        FileUtils.writeToNoSqlFile("Czas agregacji dla : " + numberOfRecords + " rekordów " + "zajął " + timeElapsed + " milisekund ");

//        for (DBObject result : output.results()) {
//            System.out.println(result);
//        }
    }

    private static void sort(DB db) throws IOException {
        DBCollection collection = db.getCollection("Employee");
        BasicDBObject getQuery = new BasicDBObject();
        getQuery.put("age", new BasicDBObject("$gt", 2).append("$lt", 612));
        Instant start = Instant.now();
        DBCursor cursor = collection.find(getQuery).sort(new BasicDBObject("age", OrderBy.DESC.getIntRepresentation()));
        Instant end = Instant.now();
        long timeElapsed = Duration.between(start, end).toMillis();
        FileUtils.writeToNoSqlFile("Czas sortowania dla : " + numberOfRecords + " rekordów " + "zajął " + timeElapsed + " milisekund ");


        }

    /*
    * pipeline = [
    {
        "$match": {
            "id": employeeId
        }
    },
    {
        "$lookup": {
            "from": "company",
            "localField": "companyId",
            "foreignField": "id",
            "as": "company"
        }
    },
    {
        "$project": {
            "name": 1,
            "lastName": 1,
            "companyId": 1,
            "companyName": "$company.companyName"
        }
    }
];
db.employee.aggregate(pipeline);*/

    private static void populateDatabase(Class clazz, List<?> data) throws IOException {

        Morphia morphia = new Morphia();
        Datastore datastore = morphia.createDatastore(mongoClient, DATABASE);
        Instant start = Instant.now();
        for (Object object : data) {
            Key<?> savedRecord = datastore.save(object);
        }
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        FileUtils.writeToNoSqlFile("Czas załadowania do tabeli " + clazz + "  danych o rozmiarze " + data.size() + " zajął " + timeElapsed + " milisekund ");

    }

    private static void generateRandomData(int numberRecords) {
        for (long i = 0; i < numberRecords; i++) {
            String randomAdress = RandomStringUtils.randomAlphanumeric(10);
            long randomId = Long.parseLong(RandomStringUtils.randomNumeric(2));
            Adress adress = new Adress(randomId, randomAdress, randomAdress, randomAdress);
            String randomCompany = RandomStringUtils.randomAlphanumeric(10);
            Company company = new Company(randomId, randomCompany, randomId);
            int randomAge = Integer.parseInt(RandomStringUtils.randomNumeric(3));
            String randomEmployee = RandomStringUtils.randomAlphanumeric(10);
            Employee employee = new Employee(randomId, randomEmployee, randomEmployee, randomAge, randomEmployee, randomId, randomId);

            employees.add(employee);
            adresses.add(adress);
            companies.add(company);
        }
    }


    private static DB connectToDatabase() {
        MongoCredential mongoCredential = MongoCredential.createScramSha1Credential("c8y-root", "admin", "c8y-root".toCharArray());
        mongoClient = new MongoClient(new ServerAddress("mongodb.default.svc.cluster.local", 27017), Arrays.asList(mongoCredential));
        DB db = mongoClient.getDB(DATABASE);
        return db;
    }

    private static void clearDataBase(DB db) {
        for (String string : db.getCollectionNames()) {
            DBCollection collection = db.getCollection(string);
            collection.drop();
            ;
        }


    }

}


