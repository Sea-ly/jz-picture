package com.jz.jzpicture.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Description: 全局跨域配置
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.config
 * @Project: jz-picture
 * @Date: 2025/2/2  22:57
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true)
                // 明确指定允许的源，而不是使用通配符
                .allowedOrigins(
                        "http://localhost:5173",  // 本地前端地址
                        "http://117.72.112.21",  // 云服务器本地前端地址
                        "http://117.72.112.21:80",  // 云服务器本地前端地址
                        "http://www.jcclubly.top"     // 域名
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("*");
    }
}
