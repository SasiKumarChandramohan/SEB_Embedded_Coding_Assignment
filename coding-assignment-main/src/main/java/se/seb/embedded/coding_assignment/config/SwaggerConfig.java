package se.seb.embedded.coding_assignment.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("SEB - Embedded Coding Assignment")
                        .description("Transform Transactions into XML")
                        .termsOfService("SEB-Embedded")
                        .contact(new io.swagger.v3.oas.models.info.Contact()
                                .email("sasikumar.csk1994@gmail.com")));
    }
}
