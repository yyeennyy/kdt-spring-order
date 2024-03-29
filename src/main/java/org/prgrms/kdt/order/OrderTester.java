package org.prgrms.kdt.order;

import org.prgrms.kdt.configuration.AppConfiguration;
import org.prgrms.kdt.voucher.FixedAmountVoucher;
import org.prgrms.kdt.voucher.VoucherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.Assert;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;


public class OrderTester {

    private static final Logger logger = LoggerFactory.getLogger(OrderTester.class);


    // OrderContext를 통해 XXXService들을 갖고오도록 변경함
    public static void main(String[] args) throws IOException {
//        // 실제 Spring Application Context 만들기
//        // ┌> java파일 기반으로 Application Configuration을 사용할 때는 이 구현체를 사용하면 된다고 했다.
        var applicationContext = new AnnotationConfigApplicationContext(AppConfiguration.class);


//        var applicationContext = new AnnotationConfigApplicationContext();
//        applicationContext.register(AppConfiguration.class);   // 여기는
//        var environment = applicationContext.getEnvironment(); // profile 실습할때 쓴 부분임
//        environment.setActiveProfiles("dev");
//        applicationContext.refresh();

//        // 실습 : Property 읽어보기! (recall. .properties에 속성들 등록했고, Config파일에 @PropertySource 지정해줬었다.
//        var environment = applicationContext.getEnvironment();
//        var version = environment.getProperty("kdt.version");  // getProperty!!
//        var minimumOrderAmount = environment.getProperty("kdt.minimum-order-amount", Integer.class);
//        var supportVendors = environment.getProperty("kdt.support-vendors", List.class);
//        var description = environment.getProperty("kdt.description", List.class);
//        System.out.println(MessageFormat.format("version => {0}", version));
//        System.out.println(MessageFormat.format("minimumOrderAmount => {0}", minimumOrderAmount));
//        System.out.println(MessageFormat.format("supportVendors => {0}", supportVendors));
//        System.out.println(MessageFormat.format("description => {0}", description));
        //ㄴ> 프로퍼티를 이렇게 가져오지 않고 아래와같이 해보겠다.
        var orderProperties = applicationContext.getBean(OrderProperties.class);  // 이렇게 안하겠다는 말 : var environment = applicationContext.getEnvironment();
        logger.info("logger name => {}", logger.getName());
        logger.info("version => {}", orderProperties.getVersion());
        logger.info("minimumOrderAmount => {}", orderProperties.getMinimumOrderAmount());
        logger.info("supportVendors => {}", orderProperties.getSupportVendors());
        logger.info("description => {}", orderProperties.getDescription());
        // 프로퍼티끝!


        // Resource 실습 부분  - 저 applicatoinContext는 ResourceLoader를 구현하는 애라고 했지
        var resource = applicationContext.getResource("classpath:application.yaml");
        //    └> 얘는 interface기 때문에 실질적으로 어떤 구현체를 가지고 오는지 확인을 해보자.
        var resource2 = applicationContext.getResource("file:sample.txt");
        var resource3 = applicationContext.getResource("https://stackoverflow.com/");  // file갖고오는게 아니잖아요 얘는? 어떤구현체일까요?
        // ㄴ> 얘는 UrlResource다. 그럼 getUrl로 가져와야함
        System.out.println(MessageFormat.format("Resource -> {0}", resource.getClass().getCanonicalName()));
//        var strings = Files.readAllLines(resource.getFile().toPath());
//        System.out.println(strings.stream().reduce("", (a, b) -> a + "\n" + b));  // list를 개행된문자열로 처리해보자

//        var readableByteChannel = Channels.newChannel((resource3.getURL().openStream())); //스트림을 통해 download한다. 그리고 채널을 열고
//        var bufferedReader = new BufferedReader(Channels.newReader(readableByteChannel, StandardCharsets.UTF_8));
//        var contents = bufferedReader.lines().collect(Collectors.joining("\n"));
//        System.out.println(contents);
//

        var customerId = UUID.randomUUID();
        var voucherRepository = applicationContext.getBean(VoucherRepository.class);
        var voucher = voucherRepository.insert(new FixedAmountVoucher(UUID.randomUUID(), 10L));

        // profile실습부분.. environment.setActiveProfiles("dev")로인해 주입이 원한느대로 잘 되었는지?
//        System.out.println(MessageFormat.format("is Jdbc Repo -> {0}", voucherRepository instanceof JDBCVoucherRepository));
//        System.out.println(MessageFormat.format("is Jdbc Repo -> {0}", voucherRepository.getClass().getCanonicalName()));

//        var orderContext = new AppConfiguration();  <- 이렇게 new로 하지 않고, Spring Application Context를 만들어야 한다.
//        var orderService = orderContext.orderService();
        var orderService = applicationContext.getBean(OrderService.class); // OrderService를 얻기위해 Bean을 꺼내쓰면 되는 것임
        var order = orderService.createOrder(customerId, new ArrayList<OrderItem>() {{
            add(new OrderItem(UUID.randomUUID(), 100L, 1));
        }}, voucher.getVoucherId());
        Assert.isTrue(order.totalAmount() == 90L, MessageFormat.format("totalAmount {0} is not 90", order.totalAmount()));

        // lifecycle 실습 - Container의 close! 모든 Bean이 소멸절차를 밝고, ApplicationContext도 소멸된다.
        applicationContext.close();
    }
}
