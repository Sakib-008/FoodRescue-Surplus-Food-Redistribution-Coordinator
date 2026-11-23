package com.foodrescue.common.config;

import java.io.IOException;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

/**
 * Serves the React single-page app that the Dockerfile copies into classpath:/static, so the whole
 * application is one container (one Render web service, one URL, no CORS).
 * Unknown non-API paths such as /browse or /donor/requests fall back to index.html for client-side routing.
 * REST controllers are matched before this handler, and unknown /api paths still return a JSON 404.
 */
@Configuration
public class SpaWebConfig implements WebMvcConfigurer {

    private static final Resource INDEX = new ClassPathResource("static/index.html");

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String path, Resource location) throws IOException {
                        Resource requested = location.createRelative(path);
                        if (requested.exists() && requested.isReadable()) {
                            return requested;
                        }
                        boolean backendPath = path.startsWith("api/") || path.startsWith("actuator/")
                                || path.startsWith("v3/") || path.contains(".");
                        return backendPath || !INDEX.exists() ? null : INDEX;
                    }
                });
    }
}
