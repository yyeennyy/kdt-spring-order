package org.prgrms.kdt;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


// 초기에 OrderContext라고 이름지었으나
// Configuration이라는 이름을 사용해보자

// 스프링에게 '이건 Bean을 정의한 도면이다. Configuration Metadata다'라고 알려주어야 함
// 평범한 class가 아니라, Bean을 정의한 Configuration Metadata가 된다. "설정 클래스"
@Configuration
//@ComponentScan(basePackages = {"org.prgrms.kdt.order", "org.prgrms.kdt.voucher"},
//        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = MemoryVoucherRepository.class)})
//                                                                       ㄴ> 특정타입 빈을 제거
@ComponentScan(basePackages = {"org.prgrms.kdt.order", "org.prgrms.kdt.voucher"})
// Base package를 설정! cf. 원하는것만 Scan되는 느낌도 있고 암튼..
//@ComponentScan(basePackageClasses = {Order.class, Voucher.class})
public class AppConfiguration {


    // lifecycle 실습 - 해당 Bean에 콜백메서드 직접 지정하기
    @Bean(initMethod = "init")
    public BeanOne beanOne(){
        return new BeanOne();
    }



    // 각 Service와 Repo 생성에 대한 책임
    // 각 Service와 Repo간 wiring(or 의존관계) 맺어주는 책임


    //=> 모든 Service, Repo를 Component처리 (@Service, @Repository) 했으므로 수동등록 빈 코드는 다 지워주자


    // 각 메서드에 Bean annotation 붙여주기
//    @Bean
//    public VoucherRepository voucherRepository() {
//        return new VoucherRepository() {
//            @Override
//            public Optional<Voucher> findById(UUID voucherId) {
//                return Optional.empty();
//            }
//        };
//    }
//
//    @Bean
//    public OrderRepository getOrderRepository() {
//        return new OrderRepository() {
//            @Override
//            public Order insert(Order order) {
//            }
//        };
//    }

    //=> Service는 @Service를 통해 컴포넌트로 등록했으므로 주석처리해주자

////    @Bean
////    public VoucherService voucherService() {
////        return new VoucherService(voucherRepository());  // 메서드를 호출하는 방식으로 DI해줬는데, 이렇게 말고
////    }
//    // Bean Definition을 통해 넣어줄 수 있다.
//    @Bean
//    public VoucherService voucherService(VoucherRepository voucherRepository){  // @Bean 메서드의 매개변수로 이렇게 넣어주면, Spring이 알아서 찾아넣어준다.
//        return new VoucherService(voucherRepository);
//    }
//    @Bean
//    public OrderService orderService(VoucherService voucherService, OrderRepository orderRepository) {
//        return new OrderService(voucherService, orderRepository);
//    }
}


class BeanOne implements InitializingBean {
    public void init() {
        System.out.println("[BeanOne] init called!");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("[BeanOne] afterPropertiesSet called!");
    }
}