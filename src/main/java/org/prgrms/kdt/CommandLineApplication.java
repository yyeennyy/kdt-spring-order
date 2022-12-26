package org.prgrms.kdt;

import org.prgrms.kdt.voucher.Voucher;
import org.prgrms.kdt.voucher.VoucherService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CommandLineApplication {

    public static void main(String[] args) throws IOException {
        // IoC컨테이너 생성! => Service, Repository를 Bean으로 등록!
        var applicationContext = new AnnotationConfigApplicationContext(AppConfiguration.class);

        // 프로그램 시작 : 지원 명령어 안내
        System.out.println(String.format("=== Voucher Program ===\n" +
                "Type exit to exit the program.\n" +
                "Type create to create a new voucher.\n" +
                "Type list to list all vouchers\n"));

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        VoucherService voucherService = applicationContext.getBean(VoucherService.class);
        List<Voucher> vouchers = new ArrayList<>();
        String cmd = reader.readLine();
        loop:
        while (true) {
            switch(cmd) {
                // create command를 통해 Voucher를 생성가능하다. (두개 중 선택)
                case "create":
                    System.out.println("What kind of Voucher do you want? (please input number only)");
                    System.out.println("1. PercentDiscountVoucher");
                    System.out.println("2. FixedAmountVoucher");
                    String choice = reader.readLine();
                    Voucher v = null;
                    switch (choice){
                        case "1":
                            System.out.print("Input discount percent : ");
                            long percent = Long.parseLong(reader.readLine());
                            v = voucherService.createPercentDiscountVoucher(percent); break;
                        case "2":
                            System.out.print("Input fixed amount discount : ");
                            long amount = Long.parseLong(reader.readLine());
                            v = voucherService.createFixedAmountVoucher(amount); break;
                    }
                    if (v != null) {
                        vouchers.add(v);
                    }
                    System.out.println("Voucher created."); break;

                // list 커맨드를 통해 만들어진 바우처를 조회 가능하다.
                case "list":
                    if(vouchers.isEmpty()){
                        System.out.println("No voucher exists.");
                        break;
                    }
                    vouchers.stream()
                            .forEach(x -> System.out.println(String.format("%-24s | id:%s", x.getClass().getSimpleName(), x.getVoucherId()))); break;
                case "exit":
                    break loop;
            }
            cmd = reader.readLine();
        }
        System.out.println("program exited.");
    }
}
