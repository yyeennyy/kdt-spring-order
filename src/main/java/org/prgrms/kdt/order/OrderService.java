package org.prgrms.kdt.order;

import org.prgrms.kdt.configuration.VersionProvider;
import org.prgrms.kdt.voucher.VoucherService;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.List;

@Service
public class OrderService {
    private final VoucherService voucherService;
    private final OrderRepository orderRepository;

    public OrderService(VoucherService voucherService, OrderRepository orderRepository) {
        this.voucherService = voucherService;
        this.orderRepository = orderRepository;
    }

    public Order createOrder(UUID customerId, List<OrderItem> orderItems, UUID voucherId) {
        var voucher = voucherService.getVoucher(voucherId);
        var order = new Order(UUID.randomUUID(), customerId, orderItems, voucher);
        orderRepository.insert(order);
        // └> 만들어진 order가 어딘가에 기록됨! 영속성이 보장되는 것임.
        voucherService.useVoucher(voucher);
        return order;
    }

    public Order createOrder(UUID customerId, List<OrderItem> orderItems) {
        var order = new Order(UUID.randomUUID(), customerId, orderItems);
        return orderRepository.insert(order);
    }
}
