package org.prgrms.kdt.order;

import org.prgrms.kdt.voucher.Voucher;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Order {
    // 식별자는 Long으로 두기도 하고, 보통 UUID로 많이 정의한다.
    private final UUID orderId;
    private final UUID customerId;
    private final List<OrderItem> orderItems;
    private Optional<Voucher> voucher;  // Voucher가 없을 수도 있다는 것을 Optional로 처리
    private OrderStatus orderStatus = OrderStatus.ACCEPTED;

    public Order(UUID orderid, UUID customerId, List<OrderItem> orderItems, Voucher voucher) {
        this.orderId = orderid;
        this.customerId = customerId;
        this.orderItems = orderItems;
        this.voucher = Optional.of(voucher);  // Voucher가 있으면 Optional.of()를 통해 전달
    }

    public Order(UUID orderid, UUID customerId, List<OrderItem> orderItems) {
        this.orderId = orderid;
        this.customerId = customerId;
        this.orderItems = orderItems;
        this.voucher = Optional.empty();      // Voucher가 없으면 Optional.empty() 처리
    }

    public long totalAmount(){
        var beforeDiscount = orderItems.stream().map(v -> v.getProductPrice() * v.getQuantity())  // record객체기 때문에 get말고 그냥 이름으로 접근 가능
                .reduce(0L, Long::sum);                                              // 근데 다시 class로 돌려놓음

        return voucher.map(value -> value.discount(beforeDiscount)).orElse(beforeDiscount);
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public UUID getOrderId(){
        return orderId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public Optional<Voucher> getVoucher() {
        return voucher;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }
}
