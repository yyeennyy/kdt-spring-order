package mission;

import org.prgrms.kdt.voucher.Voucher;
import org.prgrms.kdt.voucher.VoucherService;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.*;

public class CommandLineApplication {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        // ┌> 위에서 @SpringBootApplication 을 사용했으므로 @ComponentScan이 붙은 AppConfiguration.class는 딱히 필요없다.
        // │  거기에 딱히 명시한 설정도 없고..
        // IoC컨테이너 생성! => Service, Repository를 Bean으로 등록!
        var applicationContext = new AnnotationConfigApplicationContext(ShellConfiguration.class);




        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        VoucherService voucherService = applicationContext.getBean(VoucherService.class);
//        List<Voucher> vouchers = new ArrayList<>();

        // 현재 app에서 사용할 input, output stream을 아예 생성해두기 (사용할 때마다 생성하지 않고)
        // ㄴ> 이러면 문제점 : read할 때 그동안의 모든데이터를(=처음부터 끝까지) 읽는게 아니라 마지막 읽은시점부터 끝까지 읽는다.
        String fileName = "mission/voucher_storage.ser";
        ObjectOutputStream os = null;
        ObjectInputStream is = null;



        // 프로그램 시작 : 지원 명령어 안내
        System.out.println(String.format("=== Voucher Program ===\n" +
                "Type exit to exit the program.\n" +
                "Type create to create a new voucher.\n" +
                "Type list to list all vouchers\n"));
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
                        os = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileName, true)));
                        os.writeObject(v);
                        os.flush();
                        os.close();
                        System.out.println("Voucher created."); break;
                    }
                    break;

                // list 커맨드를 통해 만들어진 바우처를 조회 가능하다.
                case "list":
                    FileInputStream fis = new FileInputStream(fileName);
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    while(true){
                        try {
                            is = new ObjectInputStream(bis);
                            Voucher obj = (Voucher) is.readObject();
                            System.out.println(String.format("%-25s | id : %s", obj.getClass().getSimpleName(), obj.getVoucherId()));
                        } catch(EOFException e){
                            // 만약 new ObjectInputStream()할 때 발생한 EOFException이면 읽을거리가 없다.
                            // objectInputStream에서 읽을 게 없으면 header가 안생기나? is가 null인데, readStreamHeader에서 EOFException이 일어나서 이쪽으로 오네..
                            if(is == null){
                                System.out.println("empty!");
                                break;
                            }
                            is.close(); // 아 이거 닫으면 안에있는 bis같은것도 닫히나봐!!
                            break;  // 이때 is.close()는?
                        }
                    }
                    break;
                case "exit":
                    break loop;
            }
            cmd = reader.readLine();
        }
        is.close();
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
//        return new FixedAmountVoucher(),,,  amount가 들어가야 해서 어째야할지 모르겠다. 이거 아닌 것 같다.
//    }
}


// 전혀 쓸일없을거같은데 지금나는?..
//class AppendableObjectOutputStream extends ObjectOutputStream{
//
//    public AppendableObjectOutputStream(OutputStream out) throws IOException {
//        super(out);
//    }
//
//    @Override
//    protected void writeStreamHeader() throws IOException {
//        // make it do nothing!!!
//    }
//}