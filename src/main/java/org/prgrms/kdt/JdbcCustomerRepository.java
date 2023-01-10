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

    public List<String> findNames(String name) {
        var SELECT_SQL = "select * from customers WHERE name = ?";   // ? 마크를 이용
        List<String> names = new ArrayList<>();

        try (
                var connection = DriverManager.getConnection(("jdbc:mysql://localhost/order_mgmt"), "root", "root1234!");
                // 여기! prepareStatement!!
                var statement = connection.prepareStatement(SELECT_SQL);
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


    public static void main(String[] args){
        // 아래 SQL INJECTION은 안된다! 위에서 statement.setString(1, name)처럼 다루기 때문.
        // 전처럼 문자열조합이 아니라서 sql injection이 되지 않는 것임
        List<String> tmp = new JdbcCustomerRepository().findNames("tester01' OR 'a'='a");

        List<String> names = new JdbcCustomerRepository().findNames("tester01");
        names.forEach(v -> logger.info("Found name : {}", v));
    }
}
