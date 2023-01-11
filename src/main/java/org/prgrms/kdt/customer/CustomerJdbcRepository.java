package org.prgrms.kdt.customer;

import org.prgrms.kdt.JdbcCustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.nio.ByteBuffer;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CustomerJdbcRepository implements CustomerRepository {

    private static final Logger logger = LoggerFactory.getLogger(JdbcCustomerRepository.class);

    // DataSource를 통해 connection을 얻을 것임
    private final DataSource dataSource;

    // DataSource 생성자 주입
    public CustomerJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Customer insert(Customer customer) {
        try (
                var connection = DriverManager.getConnection(("jdbc:mysql://localhost/order_mgmt"), "root", "root1234!");
                var statement = connection.prepareStatement("INSERT INTO customers(customer_id, name, email, created_at) VALUES (UUID_TO_BIN(?), ?, ?, ?)");
        ) {
            statement.setBytes(1, customer.getCustomerId().toString().getBytes());
            statement.setString(2, customer.getName());
            statement.setString(3, customer.getEmail());
            statement.setTimestamp(4, Timestamp.valueOf(customer.getCreatedAt()));
            var executeUpdate = statement.executeUpdate();
            if (executeUpdate != 1) {
                throw new RuntimeException("Noting was inserted");
            }
            return customer;
        } catch (SQLException throwable) {
            logger.error("Got error while closing connection", throwable);
            throw new RuntimeException(throwable);
        }
    }

    @Override
    public Customer update(Customer customer) {
        try (
                var connection = dataSource.getConnection();
                var statement = connection.prepareStatement("UPDATE customers SET name = ?, email = ?, last_login_at = ? WHERE customer_id = UUID_TO_BIN(?)");
        ) {
            statement.setString(1, customer.getName());
            statement.setString(2, customer.getEmail());
            statement.setTimestamp(3, customer.getLastLoginAt() != null ? Timestamp.valueOf(customer.getLastLoginAt()) : null);
            statement.setBytes(4, customer.getCustomerId().toString().getBytes());
            var executeUpdate = statement.executeUpdate();
            if (executeUpdate != 1){
                throw new RuntimeException("Nothing was updated");
            }
            return customer;
        } catch (SQLException throwable) {
            logger.error("Got error while closing connection", throwable);
            throw new RuntimeException(throwable);
        }
    }

    @Override
    public List<Customer> findAll() {
        List<Customer> allCustomers = new ArrayList<>();
        try (
//                var connection = DriverManager.getConnection(("jdbc:mysql://localhost/order_mgmt"), "root", "root1234!");
                var connection = dataSource.getConnection();  // dataSource의 구현체에 의해서 connection을 가져온다 (Simple..쓰면 DriverManager에서 가져오는 것과 동일, HikariCP쓰면 pool에서 가져옴)
                // └> 참고 : 관심사의 분리가 잘 적용됨 - dataSource에서 어떤방식으로 connection을 가져오는지는 상관x, 그냥 connection만 가져오면 됨.
                var statement = connection.prepareStatement("select * from customers");
                var resultSet = statement.executeQuery()
        ) {
            while(resultSet.next()){
                mapToCustomer(allCustomers, resultSet);
            }
        } catch (SQLException throwable) {
            logger.error("Got error while closing connection", throwable);
            throw new RuntimeException(throwable);
        }
        return allCustomers;
    }

    @Override
    public Optional<Customer> findById(UUID customerId) {
        List<Customer> allCustomers = new ArrayList<>();
        try (
                var connection = dataSource.getConnection();
                var statement = connection.prepareStatement("select * from customers WHERE customer_id = UUID_TO_BIN(?)");
        ) {
            statement.setBytes(1, customerId.toString().getBytes());
            try(var resultSet = statement.executeQuery()){
                while(resultSet.next()){ // 결과(resultSet)를 while 돌며 Customer로 매핑
                    mapToCustomer(allCustomers, resultSet);
                }
            }
        } catch (SQLException throwable) {
            logger.error("Got error while closing connection", throwable);
            throw new RuntimeException(throwable);
        }

        return allCustomers.stream().findFirst();
    }

    @Override
    public Optional<Customer> findByName(String name) {
        List<Customer> allCustomers = new ArrayList<>();
        try (
                var connection = dataSource.getConnection();
                var statement = connection.prepareStatement("select * from customers WHERE name = ?");
        ) {
            statement.setString(1, name);
            try(var resultSet = statement.executeQuery()){
                while(resultSet.next()){ // 결과(resultSet)를 while 돌며 Customer로 매핑
                    mapToCustomer(allCustomers, resultSet);
                }
            }
        } catch (SQLException throwable) {
            logger.error("Got error while closing connection", throwable);
            throw new RuntimeException(throwable);
        }

        return allCustomers.stream().findFirst();
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        List<Customer> allCustomers = new ArrayList<>();
        try (
                var connection = dataSource.getConnection();
                var statement = connection.prepareStatement("select * from customers WHERE email = ?");
        ) {
            statement.setString(1, email);
            try(var resultSet = statement.executeQuery()){
                while(resultSet.next()){ // 결과(resultSet)를 while 돌며 Customer로 매핑
                    mapToCustomer(allCustomers, resultSet);
                }
            }
        } catch (SQLException throwable) {
            logger.error("Got error while closing connection", throwable);
            throw new RuntimeException(throwable);
        }

        return allCustomers.stream().findFirst();
    }

    @Override
    public void deleteAll() {
        try (
                var connection = DriverManager.getConnection(("jdbc:mysql://localhost/order_mgmt"), "root", "root1234!");
                var statement = connection.prepareStatement("DELETE FROM customers");
        ) {
            statement.executeUpdate();
        } catch (SQLException throwable) {
            logger.error("Got error while closing connection", throwable);
            throw new RuntimeException(throwable);
        }
    }


    private static void mapToCustomer(List<Customer> allCustomers, ResultSet resultSet) throws SQLException {
        var customerName = resultSet.getString("name");
        var email = resultSet.getString("email");
        var customerId = toUUID(resultSet.getBytes("customer_id"));
        var lastLoginAt = resultSet.getTimestamp("last_login_at") != null ? resultSet.getTimestamp("last_login_at").toLocalDateTime() : null;
        var createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
        allCustomers.add(new Customer(customerId, customerName, email, lastLoginAt, createdAt));
    }

    static UUID toUUID(byte[] bytes) {
        var byteBuffer = ByteBuffer.wrap(bytes);
        return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
    }

}
