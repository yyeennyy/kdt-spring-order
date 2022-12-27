package org.prgrms.kdt.order;

import org.prgrms.kdt.voucher.VoucherRepository;
import org.prgrms.kdt.voucher.VoucherService;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.List;

@Service
public class OrderService {
    private final VoucherService voucherService;
    private final OrderRepository orderRepository;
    private final VoucherRepository voucherRepository;

    public OrderService(VoucherService voucherService, OrderRepository orderRepository, VoucherRepository voucherRepository) {
        this.voucherService = voucherService;
        this.orderRepository = orderRepository;
        this.voucherRepository = voucherRepository;
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
