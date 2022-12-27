package org.prgrms.kdt.voucher;

import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

public interface VoucherRepository {
    public Optional<Voucher> findById(UUID voucherId);

    public Voucher insert(Voucher voucher);
}
