package org.prgrms.kdt.order;

import java.util.UUID;


// java 14에서 record라는 keyword가 추가되었다.
// 알아서 불변객체로 사용된다
//public record OrderItem(UUID productId,
//                        long productPrice,
//                        long quantity) {
//
//
//}
public class OrderItem {
    public final UUID productId;
    public final long productPrice;
    public final long quantity;

    public OrderItem(UUID productId, long productPrice, int quantity) {
        this.productId = productId;
        this.productPrice = productPrice;
        this.quantity = quantity;
    }

    public UUID getProductId() {
        return productId;
    }

    public long getProductPrice() {
        return productPrice;
    }

    public long getQuantity() {
        return quantity;
    }
}
