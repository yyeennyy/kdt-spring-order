package org.prgrms.kdt.voucher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.UUID;

@Service
public class VoucherService {

    private VoucherRepository voucherRepository;

    @Autowired
    public void setVoucherRepository(VoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
    }


//    public VoucherService(VoucherRepository voucherRepository) {
//        this.voucherRepository = voucherRepository;
//    }

    public Voucher getVoucher(UUID voucherId) {
        return voucherRepository.findById(voucherId)
                .orElseThrow(() -> new RuntimeException(MessageFormat.format("Can not find a voucher for {0}", voucherId)));
    }

    public void useVoucher(Object voucher) {
    }




    // ? practice (just use memory)
    public FixedAmountVoucher createFixedAmountVoucher(long amount){
        return new FixedAmountVoucher(UUID.randomUUID(), amount);
    }
    public PercentDiscountVoucher createPercentDiscountVoucher(long percent){
        return new PercentDiscountVoucher(UUID.randomUUID(), percent);
    }


}