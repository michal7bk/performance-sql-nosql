package mongo;

import com.mongodb.*;
import model.Adress;
import model.Company;
import model.Employee;
import org.apache.commons.lang3.RandomStringUtils;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

public class MongoTester {

    private List<Adress> adresses;
    private List<Company> companies;
    private List<Employee> employees;

    public static void main(String[] args) throws UnknownHostException {

        MongoClient mongoClient;

        MongoCredential mongoCredential = MongoCredential.createScramSha1Credential("c8y-root", "admin", "c8y-root".toCharArray());

        mongoClient = new MongoClient(new ServerAddress("mongodb.default.svc.cluster.local", 27017), Arrays.asList(mongoCredential));
        DB db = mongoClient.getDB("testdb");
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


//    private void populateDatabase() {
//
//        DBCollection employeeCollection = null;
//        employeeCollection = db.getCollection(Employee.COLLECTION_NAME);
//
//        employeeCollection.save(employee);
//
//        System.err.println(employeeCollection.findOne());
//    }


    private void generateRandomData() {
        String randomAdress = RandomStringUtils.randomAlphanumeric(10);
        String randomNumber = RandomStringUtils.randomNumeric(3);
        Adress adress = Adress.builder()
                .city(randomAdress)
                .number(randomNumber)
                .street(randomAdress)
                .build();
        String randomCompany = RandomStringUtils.randomAlphanumeric(10);
        Company company = Company.builder()
                .adress(adress)
                .name(randomCompany)
                .build();
        int randomAge = Integer.parseInt(RandomStringUtils.randomNumeric(3));
        String randomEmployee = RandomStringUtils.randomAlphanumeric(10);
        Employee employee = Employee.builder()
                .adress(adress)
                .company(company)
                .age(randomAge)
                .gender(randomEmployee)
                .name(randomEmployee)
                .surname(randomEmployee)
                .build();

        employees.add(employee);
        adresses.add(adress);
        companies.add(company);
    }


}


