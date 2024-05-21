package cn.edu.cqwu.model.domain;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author 杨闯
 * 腾讯云上传文件配置类
 */
@Data
@Component
@ConfigurationProperties(prefix = "tencent.cos")
public class Upload {
    private String appId;
    private String secretId;
    private String secretKey;
    private String bucketName;
    private String regionId;
    private String baseUrl;
}
