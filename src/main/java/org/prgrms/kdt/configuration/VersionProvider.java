package org.prgrms.kdt.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("version.properties")
public class VersionProvider {
    private String version;

    // @Value는 생성자 파라미터에 어노테이션으로 써도 된다.
    public VersionProvider(@Value("${version:v0.0.0}") String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }
}
