package mongo;

import com.mongodb.*;
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

    private static List<Adress> adresses = new ArrayList<Adress>();
    private static List<Company> companies = new ArrayList<Company>();
    private static List<Employee> employees = new ArrayList<Employee>();
    private static MongoClient mongoClient;

    public static void main(String[] args) throws IOException {
        generateRandomData(1000);
        DB db = connectToDatabase();
        populateDatabase(Employee.class, employees);
        populateDatabase(Company.class, companies);
        populateDatabase(Adress.class, adresses);
        clearDataBase(db);

    }


    private static void agregation(DB db) {
        DBCollection coll = db.getCollection("employee");

        // create the pipeline operations, first with the $match
        DBObject match = new BasicDBObject("$match",
                new BasicDBObject("_id", 10)
        );

        // build the $lookup operations
        DBObject lookupFields = new BasicDBObject("from", "company");
        lookupFields.put("localField", "companyId");
        lookupFields.put("foreignField", "_id");
        lookupFields.put("as", "company");
        DBObject lookup = new BasicDBObject("$lookup", lookupFields);

        // build the $project operations
        DBObject projectFields = new BasicDBObject("name", 1);
        projectFields.put("lastName", 1);
        projectFields.put("companyId", 1);
        projectFields.put("companyName", "$company.companyName");
        DBObject project = new BasicDBObject("$project", projectFields);

        List<DBObject> pipeline = Arrays.asList(match, lookup, project);

        AggregationOutput output = coll.aggregate(pipeline);

        for (DBObject result : output.results()) {
            System.out.println(result);
        }
    }

    private static void populateDatabase(Class clazz, List<?> data) throws IOException {

        Morphia morphia = new Morphia();
        Datastore datastore = morphia.createDatastore(mongoClient, DATABASE);
        Instant start = Instant.now();
        for (Object object : data) {
            Key<?> savedRecord = datastore.save(object);
        }
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        FileUtils.writeToFileNOSQL("Czas załadowania do tabeli " + clazz + "  danych o rozmiarze " + data.size() + " zajął " + timeElapsed + " milisekund ");

    }

    private static void generateRandomData(int numberRecords) {
        for (int i = 0; i < numberRecords; i++) {
            String randomAdress = RandomStringUtils.randomAlphanumeric(10);
            Adress adress = new Adress(randomAdress, randomAdress, randomAdress);
            String randomCompany = RandomStringUtils.randomAlphanumeric(10);
            Company company = new Company(randomCompany, adress);
            int randomAge = Integer.parseInt(RandomStringUtils.randomNumeric(3));
            String randomEmployee = RandomStringUtils.randomAlphanumeric(10);
            Employee employee = new Employee(randomEmployee, randomEmployee, randomAge, randomEmployee, adress, company);

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


