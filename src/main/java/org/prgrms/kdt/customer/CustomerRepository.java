package org.prgrms.kdt.customer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {

    Customer insert(Customer customer);

    Customer update(Customer customer);

    // Customer save(Customer customer);

    List<Customer> findAll();



    // 유효하지 않은 id가 전달될 수 있음
    // 이때 단순히 리턴타입을 Customer로 하면 null이 담길 텐데
    // 그러지 말고 Optional을 사용하는 것을 권장
    // (NPE를 피할 수 있다. null처리를 고민하지 않을 수 있다.)
    // 항상 Optional을 쓸까말까를 고민하자!

    Optional<Customer> findById(UUID customerId);

    Optional<Customer> findByName(String name);

    Optional<Customer> findByEmail(String email);

    void deleteAll();



}
