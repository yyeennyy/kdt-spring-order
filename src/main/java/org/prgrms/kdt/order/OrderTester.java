package org.prgrms.kdt.order;

import org.prgrms.kdt.configuration.AppConfiguration;
import org.prgrms.kdt.voucher.FixedAmountVoucher;
import org.prgrms.kdt.voucher.JDBCVoucherRepository;
import org.prgrms.kdt.voucher.VoucherRepository;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.Assert;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class OrderTester {
    // OrderContext를 통해 XXXService들을 갖고오도록 변경함
    public static void main(String[] args) {
//        // 실제 Spring Application Context 만들기
//        // ┌> java파일 기반으로 Application Configuration을 사용할 때는 이 구현체를 사용하면 된다고 했다.
        var applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(AppConfiguration.class);
        var environment = applicationContext.getEnvironment();
        environment.setActiveProfiles("dev");
        applicationContext.refresh();

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
//        var orderProperties = applicationContext.getBean(OrderProperties.class);// 이렇게 안하겠다는 말 : var environment = applicationContext.getEnvironment();
//        System.out.println(MessageFormat.format("version => {0}", orderProperties.getVersion()));
//        System.out.println(MessageFormat.format("minimumOrderAmount => {0}", orderProperties.getMinimumOrderAmount()));
//        System.out.println(MessageFormat.format("supportVendors => {0}", orderProperties.getSupportVendors()));
//        System.out.println(MessageFormat.format("description => {0}", orderProperties.getDescription()));
        // 프로퍼티끝!

        var customerId = UUID.randomUUID();
        var voucherRepository = applicationContext.getBean(VoucherRepository.class);
        var voucher = voucherRepository.insert(new FixedAmountVoucher(UUID.randomUUID(), 10L));

        // profile실습부분.. environment.setActiveProfiles("dev")로인해 주입이 원한느대로 잘 되었는지?
        System.out.println(MessageFormat.format("is Jdbc Repo -> {0}", voucherRepository instanceof JDBCVoucherRepository));
        System.out.println(MessageFormat.format("is Jdbc Repo -> {0}", voucherRepository.getClass().getCanonicalName()));

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
