package postgresql;

import file.FileUtils;
import model.postgre.Adress;
import model.postgre.Company;
import model.postgre.Employee;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.IOException;
import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class PostgreTester {

    private static List<Adress> adresses = new ArrayList<>();
    private static List<Company> companies = new ArrayList<Company>();
    private static List<Employee> employees = new ArrayList<Employee>();
    private static final int numberOfRecords = 250000;


    public static void main(String[] args) throws SQLException, IOException {

        Connection connection = connect();
        generateRandomData(numberOfRecords);
        dropTables(connection);
        createTables(connection);
        populateTables(connection);
        agregation(connection);
        sort(connection);
    }


    private static void agregation(Connection connection) throws SQLException, IOException {
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(" EXPLAIN ANALYZE SELECT Employee.id , Employee.name, Employee.surname, Company.id, Company.name " +
                "FROM Employee " +
                "inner join Company on Employee.companyId= Company.id " +
                "WHERE CompanyId=10");
        if (rs.next()) {
            FileUtils.writeToSqlFile("Czas agregacji dla " + numberOfRecords + " rekordów " + "zajął " + rs.getString(1) + " milisekund ");
        }

    }

    private static void sort(Connection connection) throws SQLException, IOException {
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery("EXPLAIN ANALYZE SELECT * FROM EMPLOYEE WHERE AGE > 2 AND AGE < 612 ORDER BY AGE DESC ");

        if (rs.next()) {
            FileUtils.writeToSqlFile("Czas sortowania dla " + numberOfRecords + " rekordów " + "zajął " + rs.getString(1) + " milisekund ");
        }
    }

    private static void populateTables(Connection connection) throws SQLException, IOException {
        Instant start1 = Instant.now();
        insertAdress(connection, adresses);
        Instant finish1 = Instant.now();
        long timeElapsed1 = Duration.between(start1, finish1).toMillis();
        FileUtils.writeToSqlFile("Czas załadowania do tabeli Adres  danych o rozmiarze " + adresses.size() + " zajął " + timeElapsed1 + " milisekund ");

        Instant start2 = Instant.now();
        insertCompanies(connection, companies);
        Instant finish2 = Instant.now();
        long timeElapsed2 = Duration.between(start2, finish2).toMillis();
        FileUtils.writeToSqlFile("Czas załadowania do tabeli Company  danych o rozmiarze " + companies.size() + " zajął " + timeElapsed2 + " milisekund ");

        Instant start3 = Instant.now();
        insertEmployees(connection, employees);
        Instant finish3 = Instant.now();
        long timeElapsed3 = Duration.between(start3, finish3).toMillis();
        FileUtils.writeToSqlFile("Czas załadowania do tabeli Employee  danych o rozmiarze " + employees.size() + " zajął " + timeElapsed3 + " milisekund ");


    }

    private static void dropTables(Connection connection) throws SQLException {
        dropAllTable(connection, "ADRESS");
        dropAllTable(connection, "COMPANY");
        dropAllTable(connection, "EMPLOYEE");
    }

    private static void createTables(Connection connection) throws SQLException {
        createTableAdress(connection);
        createTableCompany(connection);
        createTableEmployee(connection);
    }

    private static void createTableAdress(Connection connection) throws SQLException {
        Statement stmt = connection.createStatement();
        String sql = "CREATE TABLE ADRESS " +
                "(id serial PRIMARY KEY     NOT NULL," +
                " city           VARCHAR(50)    NOT NULL, " +
                " number            INT     NOT NULL, " +
                " street        CHAR(50) )";
        stmt.executeUpdate(sql);
    }

    private static void createTableCompany(Connection connection) throws SQLException {
        Statement stm = connection.createStatement();
        String sql = " CREATE TABLE COMPANY " +
                "(id serial PRIMARY KEY NOT NULL, " +
                "name VARCHAR(50) NOT NULL, " +
                "adressId INT NOT NULL )";
        stm.execute(sql);
    }

    private static void createTableEmployee(Connection connection) throws SQLException {
        Statement stm = connect().createStatement();
        String sql = " CREATE TABLE EMPLOYEE" +
                "( id serial PRIMARY KEY NOT NULL, " +
                "name VARCHAR(50) NOT NULL," +
                "surname VARCHAR(50) NOT NULL," +
                "age INT NOT NULL," +
                "gender VARCHAR(50) NOT NULL," +
                "adressId INT NOT NULL," +
                "companyId INT NOT NULL ) ";
        stm.execute(sql);
    }

    private static void dropAllTable(Connection connection, String tableName) {
        try {
            Statement stm = connection.createStatement();
            String sql = "DROP TABLE " + tableName;
            stm.execute(sql);
        } catch (SQLException e) {
            System.out.println("Nie ma tabeli  : " + tableName);
        }
    }

    private static void insertAdress(Connection conn, List<Adress> adresses) throws SQLException {
        String SQL = "INSERT INTO Adress(city,street,number) "
                + "VALUES(?,?,?)";

        for (Adress adress : adresses) {
            try (PreparedStatement pstmt = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, adress.getCity());
                pstmt.setString(2, adress.getStreet());
                pstmt.setLong(3, adress.getNumber());
                pstmt.executeUpdate();
            }
        }
    }

    private static void insertCompanies(Connection conn, List<Company> companies) throws SQLException {
        String SQL = "INSERT INTO Company(name,adressId) "
                + "VALUES(?,?)";

        for (Company company : companies) {
            try (PreparedStatement pstmt = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, company.getName());
                pstmt.setLong(2, company.getAdressId());
                pstmt.executeUpdate();
            }
        }
    }

    private static void insertEmployees(Connection conn, List<Employee> employees) throws SQLException {
        String SQL = "INSERT INTO EMPLOYEE(name,surname, age, gender, adressId,companyId) "
                + "VALUES(?,?,?,?,?,?)";

        for (Employee employee : employees) {
            try (PreparedStatement pstmt = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, employee.getName());
                pstmt.setString(2, employee.getSurname());
                pstmt.setInt(3, employee.getAge());
                pstmt.setString(4, employee.getGender());
                pstmt.setLong(5, employee.getAdressId());
                pstmt.setLong(6, employee.getCompanyId());
                pstmt.executeUpdate();
            }
        }
    }

    private static void generateRandomData(int numberRecords) {
        for (int i = 0; i < numberRecords; i++) {
            long randomId = Long.parseLong(RandomStringUtils.randomNumeric(2));
            String randomAdress = RandomStringUtils.randomAlphanumeric(10);
            String randomCompany = RandomStringUtils.randomAlphanumeric(10);
            String randomNumber = RandomStringUtils.randomNumeric(5);
            Adress adress = new Adress(randomAdress, randomAdress, Long.valueOf(randomNumber));
            Company company = new Company(randomCompany, randomId);
            String randomEmployee = RandomStringUtils.randomAlphanumeric(10);
            int randomAge = Integer.parseInt(RandomStringUtils.randomNumeric(3));
            Employee employee = new Employee(randomEmployee, randomEmployee, randomAge, randomEmployee, randomId, randomId);

            employees.add(employee);
            adresses.add(adress);
            companies.add(company);
        }
    }

    private static Connection connect() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:postgresql://127.0.0.1:5432/testdb?loggerLevel=TRACE&loggerFile=pgjdbc.log", "michal", "admin");
    }

}
