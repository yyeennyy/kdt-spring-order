package org.prgrms.kdt.voucher;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile("local")                                                                         //┌>소멸시 콜백 또한 부르도록..
public class MemoryVoucherRepository implements VoucherRepository, InitializingBean, DisposableBean {  // lifecycle관련 실습 : InitializingBean을 구현하도록 했다.

    // Thread-safe를 위해 HashMap말고 이거 쓰셨다고함
    private final Map<UUID, Voucher> storage = new ConcurrentHashMap<>();

    @Override
    public Optional<Voucher> findById(UUID voucherId) {
        return Optional.ofNullable(storage.get(voucherId));  // null safety! 없을 경우 Optional.empty()되도록
    }

    @Override
    public Voucher insert(Voucher voucher) {
        storage.put(voucher.getVoucherId(), voucher);
        return voucher;
    }


    // lifecycle 관련 메서드
    @PostConstruct
    public void postConstruct(){
        System.out.println("postConstruct called!");
    }

    @Override
    public void afterPropertiesSet() throws Exception {  // 이 Bean이 InitializingBean을 implememts했기에 구현해야하는 메서드
        System.out.println("afterPropertiesSet called!");
    }

    @Override
    public void destroy() throws Exception { // 소멸 콜백 메서드ㅜ
        System.out.println("destroy called!");
    }

    @PreDestroy
    public void preDestroy(){  // 이 Bean이 InitializingBean을 implememts했기에 구현해야하는 메서드
        System.out.println("preDestroy called!");
    }
//   호출되는 순서를 확인해보자ㅓ!
}
