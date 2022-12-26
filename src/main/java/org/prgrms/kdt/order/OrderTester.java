package org.prgrms.kdt.order;

import org.prgrms.kdt.AppConfiguration;
import org.prgrms.kdt.voucher.FixedAmountVoucher;
import org.prgrms.kdt.voucher.VoucherRepository;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.Assert;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.UUID;


public class OrderTester {
    // OrderContext를 통해 XXXService들을 갖고오도록 변경함
    public static void main(String[] args) {
        // 실제 Spring Application Context 만들기
        // ┌> java파일 기반으로 Application Configuration을 사용할 때는 이 구현체를 사용하면 된다고 했다.
        var applicationContext = new AnnotationConfigApplicationContext(AppConfiguration.class);
        var voucherRepository = applicationContext.getBean(VoucherRepository.class);
        var voucher = voucherRepository.insert(new FixedAmountVoucher(UUID.randomUUID(), 10));
        var orderService = applicationContext.getBean(OrderService.class); // OrderService를 얻기위해 Bean을 꺼내쓰면 되는 것임

        var customerId = UUID.randomUUID();
//        var orderContext = new AppConfiguration();  <- 이렇게 new로 하지 않고, Spring Application Context를 만들어야 한다.
//        var orderService = orderContext.orderService();
        var order = orderService.createOrder(customerId, new ArrayList<OrderItem>() {{
            add(new OrderItem(UUID.randomUUID(), 100L, 1));
        }}, voucher.getVoucherId());
        Assert.isTrue(order.totalAmount() == 90L, MessageFormat.format("totalAmount {0} is not 90", order.totalAmount()));

        // lifecycle 실습 - Container의 close! 모든 Bean이 소멸절차를 밝고, ApplicationContext도 소멸된다.
        applicationContext.close();
    }
}
