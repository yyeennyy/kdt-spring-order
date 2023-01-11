package org.prgrms.kdt.customer;

import org.prgrms.kdt.JdbcCustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
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
    // 아래 JdbcTemplate이 있으면 DataSource는 사실 필요 없다.
    private final JdbcTemplate jdbcTemplate;

    // 인스턴스마다 있을 필요 없으니까 이 RowMapper는 static 필드로 두자!
    // 이 RowMapper는 그냥, resultSet과 index 받으면 Customer 반환해주는 거임. 말그대로 RowMapper!!!!
    private static RowMapper<Customer> customerRowMapper = (resultSet, i) -> {
        var customerName = resultSet.getString("name");
        var email = resultSet.getString("email");
        var customerId = toUUID(resultSet.getBytes("customer_id"));
        var lastLoginAt = resultSet.getTimestamp("last_login_at") != null ? resultSet.getTimestamp("last_login_at").toLocalDateTime() : null;
        var createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
        return new Customer(customerId, customerName, email, lastLoginAt, createdAt);
    };

    // DataSource 생성자 주입
    public CustomerJdbcRepository(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
        // jdbc template에서는 query()라는 메서드를 제공
        // sql와 RowMapper를 전달
        return jdbcTemplate.query("select * from customers", customerRowMapper);
    }

    @Override
    public Optional<Customer> findById(UUID customerId) {
        // 일반 query()는 List를 가져옴. 하나만 꺼내오는 건 queryForObject 사용
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("select * from customers WHERE customer_id = UUID_TO_BIN(?)",
                    customerRowMapper,
                    customerId.toString().getBytes()));
        } catch (EmptyResultDataAccessException e){
            logger.error("Got empty result", e);
            return Optional.empty();
        }
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

    @Override
    public int count() {
        // 암튼 결과값이 하나인 것에 대해서는 queryyForObject 사용ㅎㅏ면 되고,
        // 이렇게 count()처럼 int 하나 받는 것도 이렇게!
        return jdbcTemplate.queryForObject("select count(*) from customers", Integer.class);
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
