package com.example.configurations;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@AllArgsConstructor
public class OpenApiConfig {

    private final OpenApiPropertiesConfig environment;


    @Bean
    public OpenAPI customOpenAPI() {

          return new OpenAPI()
                  .components(new Components())
                  .info(new Info()
                  .title("Products")
                  .description("API responsável por gerenciar as informações dos Produtos")
                  .version(environment.getAppVersion()))
                  .tags(List.of(new Tag().name("Products").description("Endpoints responsáveis por gerenciar as informações dos produtos")
                  )
          );
    }
}