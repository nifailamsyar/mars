package com.mars.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.temp-dir}")
    private String tempDir;

    @SuppressWarnings("null")
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = !uploadDir.endsWith("/") ? uploadDir + "/" : uploadDir;
        String tempPath = !tempDir.endsWith("/") ? tempDir + "/" : tempDir;

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath);
        registry.addResourceHandler("/temp/**")
                .addResourceLocations("file:" + tempPath);
    }
}