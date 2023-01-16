package org.prgrms.kdt.customer;

import com.wix.mysql.EmbeddedMysql;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.*;
import org.prgrms.kdt.customer.Customer;
import org.prgrms.kdt.customer.CustomerNamedJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.wix.mysql.EmbeddedMysql.anEmbeddedMysql;
import static com.wix.mysql.ScriptResolver.classPathScript;
import static com.wix.mysql.config.Charset.UTF8;
import static com.wix.mysql.config.MysqldConfig.aMysqldConfig;
import static com.wix.mysql.distribution.Version.v5_7_17;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringJUnitConfig
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)  // 라이프사이클을 Class마다 지정 : 클래스인스턴스가 하나만 만들어진다.
// └> 클래스 인스턴스가 하나 만들어지니까, @BeforeAll할 때 static으로 안해도 됨!
class CustomerNamedJdbcRepositoryTest {

    @Configuration
    @ComponentScan(
            basePackages = {"org.prgrms.kdt.customer"}
    )

    static class Config {
        @Bean
        public DataSource dataSource() {
            // 앗 얘 build하면 얘자체가 DataSource를 리턴한다아
//            return new EmbeddedDatabaseBuilder()
//                    .generateUniqueName(true)
//                    .setType(H2)
//                    .setScriptEncoding("UTF-8")
//                    .ignoreFailedDrops(true)
//                    .addScript("schema.sql")
//                    .build();
            var datasource = DataSourceBuilder.create()
                    .url("jdbc:mysql://localhost:2215/test-order_mgmt")
                    .username("test")
                    .password("test1234!")
                    .type(HikariDataSource.class)  // 구현체를 이런식으로 넣는다!
                    .build();
            datasource.setMaximumPoolSize(1000);
            datasource.setMinimumIdle(100);
            return datasource;
        }

        @Bean
        public JdbcTemplate jdbcTemplate(DataSource dataSource){
            return new JdbcTemplate(dataSource);
        }
        @Bean
        public NamedParameterJdbcTemplate namedParameterJdbcTemplate(JdbcTemplate jdbcTemplate){
            return new NamedParameterJdbcTemplate(jdbcTemplate);
        }
    }

    @Autowired
    CustomerNamedJdbcRepository customerJdbcRepository;

    @Autowired
    DataSource dataSource;

    EmbeddedMysql embeddedMysql;


    // customer 정보 하드코딩 하지 말고 이렇게 두기
    Customer newCustomer;

    // 테스트 전 딱 한번만 시행하도록!
    // static 불가능하다. @BeforeAll 그럼 어떡하지?
    // 테스트 클래스에 @TestInstance(TestInstance.Lifecycle.PER_CLASS)를 지정해줘서, static 아니어도 됨!
    @BeforeAll
    void setup() {
        newCustomer = new Customer(UUID.randomUUID(), "test-user", "test1-user@gmail.com", LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        var mysqlConfig = aMysqldConfig(v5_7_17)
                .withCharset(UTF8)
                .withPort(2215)
                .withUser("test", "test1234!")
                .withTimeZone("Asia/Seoul")
                .build();
        embeddedMysql = anEmbeddedMysql(mysqlConfig)
                .addSchema("test-order_mgmt", classPathScript("schema.sql"))
                .start();
        // customerJdbcRepository.deleteAll();
    }

    @AfterAll
    void cleanup() {
        embeddedMysql.stop();
    }







    // 기본적으로 test 실행 순서는 무작위임
    // 필요시 실행 순서를 지정해줘야 한다.

    @Test
    @Order(1)
//    @Disabled
    public void testHikariConnectionPool() {
        assertThat(dataSource.getClass().getName(), is("com.zaxxer.hikari.HikariDataSource"));
    }

    @Test
    @Order(2)
    @DisplayName("고객을 추가할 수 있다.")
    public void testInsert() {
        customerJdbcRepository.insert(newCustomer);

        var retrievedCustomer = customerJdbcRepository.findById(newCustomer.getCustomerId());
        // 일단 넣은게 있어야하고
        assertThat(retrievedCustomer.isEmpty(), is(false));
        // 가지고 온 것의 property value들이 일치하는지 검증
        assertThat(retrievedCustomer.get(), samePropertyValuesAs(newCustomer));
    }


    @Test
    @Order(3)
    @DisplayName("전체 고객을 조회할 수 있다.")
    public void testFindAll() throws InterruptedException {
        List<Customer> customers = customerJdbcRepository.findAll();
        assertThat(customers.isEmpty(), is(false));
    }

    @Test
    @Order(4)
    @DisplayName("이름으로 고객을 조회할 수 있다.")
    public void testFindByName() {
        var customers = customerJdbcRepository.findByName(newCustomer.getName());
        assertThat(customers.isEmpty(), is(false));

        var unknown = customerJdbcRepository.findByName("unknown!!!~~");
        assertThat(unknown.isEmpty(), is(true));
    }

    @Test
    @Order(5)
    @DisplayName("이메일으로 고객을 조회할 수 있다.")
    public void testFindByEmail() {
        var customers = customerJdbcRepository.findByEmail(newCustomer.getEmail());
        assertThat(customers.isEmpty(), is(false));

        var unknown = customerJdbcRepository.findByEmail("unknown!!!~~");
        assertThat(unknown.isEmpty(), is(true));
    }

    @Test
    @Order(6)
    @DisplayName("고객을 수정할 수 있다.")
    public void testUpdate(){
        newCustomer.changeName("updated-user");
        customerJdbcRepository.update(newCustomer);

        List<Customer> all = customerJdbcRepository.findAll();
        assertThat(all, hasSize(1));
        assertThat(all, everyItem(samePropertyValuesAs(newCustomer)));

        Optional<Customer> retrievedCustomer = customerJdbcRepository.findById(newCustomer.getCustomerId());
        assertThat(retrievedCustomer.isEmpty(), is(false));
        assertThat(retrievedCustomer.get(), samePropertyValuesAs(newCustomer));
    }

}