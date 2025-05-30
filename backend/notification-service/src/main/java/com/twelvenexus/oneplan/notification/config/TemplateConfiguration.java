package com.twelvenexus.oneplan.notification.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

@Configuration
public class TemplateConfiguration {

  @Bean
  public TemplateEngine emailTemplateEngine() {
    SpringTemplateEngine templateEngine = new SpringTemplateEngine();
    StringTemplateResolver templateResolver = new StringTemplateResolver();
    templateResolver.setTemplateMode(TemplateMode.HTML);
    templateEngine.setTemplateResolver(templateResolver);
    return templateEngine;
  }
}
