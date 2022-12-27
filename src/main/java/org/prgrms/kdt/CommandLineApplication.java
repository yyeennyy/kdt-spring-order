package org.prgrms.kdt;

import org.prgrms.kdt.voucher.FixedAmountVoucher;
import org.prgrms.kdt.voucher.Voucher;
import org.prgrms.kdt.voucher.VoucherService;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class CommandLineApplication {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        // ┌> 위에서 @SpringBootApplication 을 사용했으므로 @ComponentScan이 붙은 AppConfiguration.class는 딱히 필요없다.
        // │  거기에 딱히 명시한 설정도 없고..
        // IoC컨테이너 생성! => Service, Repository를 Bean으로 등록!
        var applicationContext = new AnnotationConfigApplicationContext(ShellConfiguration.class);


        // 프로그램 시작 : 지원 명령어 안내
        System.out.println(String.format("=== Voucher Program ===\n" +
                "Type exit to exit the program.\n" +
                "Type create to create a new voucher.\n" +
                "Type list to list all vouchers\n"));

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        VoucherService voucherService = applicationContext.getBean(VoucherService.class);
//        List<Voucher> vouchers = new ArrayList<>();
        String cmd = reader.readLine();
        String fileName = "voucher_storage.db";
        ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(fileName, true));
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
                        outputStream.writeObject(v);
                    }
                    System.out.println("Voucher created."); break;

                // list 커맨드를 통해 만들어진 바우처를 조회 가능하다.
                // db파일에서 꺼내도록 하자
                case "list":
                    File voucherFile = new File(fileName);
                    if(voucherFile.length() == 0){
                        System.out.println("No voucher exists.");
                        break;
                    }
                    ObjectInputStream fr = new ObjectInputStream(new FileInputStream(fileName));
                    while(true){
                        try {
                            Voucher obj = (Voucher) fr.readObject();
                            System.out.println(String.format("%-25s | id : %s", obj.getClass().getSimpleName(), obj.getVoucherId()));
                        } catch(IOException e){
                            break;
                        }
                    }
                    fr.close();
                    break;
                case "exit":
                    break loop;
            }
            cmd = reader.readLine();
        }
        outputStream.close();
        System.out.println("program exited.");
    }
}

@Configuration  // 얘조차 Component라는 거!
@ComponentScan(basePackages = {"org.prgrms.kdt.order", "org.prgrms.kdt.voucher"})
class ShellConfiguration{

    // ? 이런 구현체도 Configuration에 등록하나? 컴포넌트 아닌 일반 Bean들도?
    // ? 그냥 생성할 일 있으면 new로 부르면 안되나?
    // ? Bean으로 관리되어야 할 이유는?
//    public FixedAmountVoucher fixedAmountVoucher(){
//        return new FixedAmountVoucher(),,,  amount가 들어가야 해서 어째야할지 모르겠다. 이거 아닌 것 같기도
//    }
}
