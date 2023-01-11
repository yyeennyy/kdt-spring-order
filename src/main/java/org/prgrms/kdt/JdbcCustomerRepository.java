package org.prgrms.kdt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JdbcCustomerRepository {
    private static final Logger logger = LoggerFactory.getLogger(JdbcCustomerRepository.class);
    private final String SELECT_BY_NAME_SQL = "select * from customers WHERE name = ?";   // ? 마크를 이용
    private final String SELECT_ALL_SQL = "select * from customers";
    private final String INSERT_SQL = "INSERT INTO customers(customer_id, name, email) VALUES (UUID_TO_BIN(?), ?, ?)";


    public List<String> findNames(String name) {
        List<String> names = new ArrayList<>();

        try (
                var connection = DriverManager.getConnection(("jdbc:mysql://localhost/order_mgmt"), "root", "root1234!");
                // 여기! prepareStatement!!
                var statement = connection.prepareStatement(SELECT_BY_NAME_SQL);
        ) {
            statement.setString(1, name);

            try(var resultSet = statement.executeQuery()){
                while(resultSet.next()){
                    var customerName = resultSet.getString("name");
                    var customerId = UUID.nameUUIDFromBytes(resultSet.getBytes("customer_id"));
                    var createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
                    logger.info("customer id -> {}, name -> {}, createdAt -> {}", customerId, customerName, createdAt);
                    names.add(customerName);
                }
            }
        } catch (SQLException throwable) {
            logger.error("Got error while closing connection", throwable);
        }

        return names;
    }

    public List<String> findAllName() {
        List<String> names = new ArrayList<>();
        try (
                var connection = DriverManager.getConnection(("jdbc:mysql://localhost/order_mgmt"), "root", "root1234!");
                var statement = connection.prepareStatement(SELECT_ALL_SQL);
                var resultSet = statement.executeQuery()
        ) {
            while(resultSet.next()){
                var customerName = resultSet.getString("name");
                var customerId = UUID.nameUUIDFromBytes(resultSet.getBytes("customer_id"));
                var createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
                logger.info("customer id -> {}, name -> {}, createdAt -> {}", customerId, customerName, createdAt);
                names.add(customerName);
            }
        } catch (SQLException throwable) {
            logger.error("Got error while closing connection", throwable);
        }
        return names;
    }

    public int insertCustomer(UUID customerId, String name, String email) {
        try (
                var connection = DriverManager.getConnection(("jdbc:mysql://localhost/order_mgmt"), "root", "root1234!");
                var statement = connection.prepareStatement(INSERT_SQL);
        ) {
            statement.setBytes(1, customerId.toString().getBytes());
            statement.setString(2, name);
            statement.setString(3, email);
            return statement.executeUpdate();
        } catch (SQLException throwable) {
            logger.error("Got error while closing connection", throwable);
        }
        return 0;
    }




    public static void main(String[] args){
        var customerRepository = new JdbcCustomerRepository();

        customerRepository.insertCustomer(UUID.randomUUID(), "new-user", "new-user@email.com");
        customerRepository.insertCustomer(UUID.randomUUID(), "new-user2", "new-user2@email.com");

        var names = customerRepository.findAllName();
        names.forEach(v -> logger.info("Found name : {}", v));
    }
}
