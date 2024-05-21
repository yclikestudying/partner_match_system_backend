package cn.edu.cqwu.config;

import cn.edu.cqwu.model.domain.Upload;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

@Component
@Configuration
public class TencentCosConfig {
    @Resource
    private Upload upload;

    @Bean
    @Primary
    public COSClient getCoSClient4Picture() {
        //初始化用户身份信息
        COSCredentials cosCredentials = new BasicCOSCredentials(upload.getSecretId(),upload.getSecretKey());
        //设置bucket区域，
        //clientConfig中包含了设置region
        ClientConfig clientConfig = new ClientConfig(new Region(upload.getRegionId()));
        //生成cos客户端
        COSClient cosClient = new COSClient(cosCredentials,clientConfig);
        return  cosClient;
    }
}

