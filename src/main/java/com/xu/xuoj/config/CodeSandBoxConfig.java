package com.xu.xuoj.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "codesandbox")
@Data
public class CodeSandBoxConfig {

    private String type;
}
