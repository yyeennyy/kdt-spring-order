package org.prgrms.kdt.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "kdt")  //이 어노테이션은 스프링부트꺼라? AppConfiguraion 설정파일에다가 Enable!~~~써줘야함
//필드 접근이 되나까..
public class OrderProperties implements InitializingBean {  // 이 값들을 간단히 확인가능한 방법. 그냥 InitializingBean 구현하면 되겠지

    private final static Logger logger = LoggerFactory.getLogger(OrderProperties.class);




//    @Value("v1.1.1")   // 이렇게 원하는값을 필드에 주입시킬 수 있다.
//    private String version;
//
//    @Value("0")
//    private int minimumOrderAmount;
//
//    @Value("d, a, b")
//    private List<String> supportVendors;
//


    // Value를 Property에서 읽어오자! 키를 주면됨. ${}를 통해서.
//    @Value("${kdt.version2:v0.0.0}")   // 이렇게 원하는값을 필드에 주입시킬 수 있다.  //키를통해 못 찾으면 이값을 넣으라고 콜론을통해 설정가능
    private String version;

//    @Value("${kdt.minimum-order-amount}")
    private int minimumOrderAmount;

//    @Value("${kdt.support-vendors}")
    private List<String> supportVendors;

    private String description;


    // system 환경변수 갖고오기
    @Value("${JAVA_HOME}")
    private String javaHome;


    @Override
    public void afterPropertiesSet() throws Exception {
        logger.debug("[OrderProperties] version => {}", version);
        logger.debug("[OrderProperties] minimumOrderAmount => {}", minimumOrderAmount);
        logger.debug("[OrderProperties] supportVendors => {}", supportVendors);
        logger.debug("[OrderProperties] javaHome => {}", javaHome);
        logger.debug("[OrderProperties] javaHome => {}", javaHome);
    }




    // @ConfigurationProperties를 쓰기 위한 getter, setter 코드

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getMinimumOrderAmount() {
        return minimumOrderAmount;
    }

    public void setMinimumOrderAmount(int minimumOrderAmount) {
        this.minimumOrderAmount = minimumOrderAmount;
    }

    public List<String> getSupportVendors() {
        return supportVendors;
    }

    public void setSupportVendors(List<String> supportVendors) {
        this.supportVendors = supportVendors;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
