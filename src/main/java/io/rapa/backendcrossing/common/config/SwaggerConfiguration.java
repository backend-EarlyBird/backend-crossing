package io.rapa.backendcrossing.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "백엔드의 숲",
                version = "0.0.1",
                description = "백엔드의 숲 API 명세"
        )
)
public class SwaggerConfiguration {
    @Bean
    public OpenAPI openAPI(){
        return new OpenAPI()
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        "Bearer Authentication",
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                );
    }

    @Bean
    public GroupedOpenApi authApi(){
        return GroupedOpenApi.builder()
                .pathsToMatch("/api/v1/auth/**")
                .group("인증")
                .build();
    }

    @Bean
    public GroupedOpenApi userApi(){
        return GroupedOpenApi.builder()
                .pathsToMatch("/api/v1/users/**")
                .pathsToExclude("/api/v1/users/me/friends/**")
                .pathsToExclude("/api/v1/users/me/npcs/**")
                .group("유저")
                .build();
    }

    @Bean
    public GroupedOpenApi itemApi(){
        return GroupedOpenApi.builder()
                .pathsToMatch("/api/v1/items/**")
                .group("아이템")
                .build();
    }

    @Bean
    public GroupedOpenApi friendRequestApi(){
        return GroupedOpenApi.builder()
                .pathsToMatch("/api/v1/users/me/friends/**")
                .group("친구요청")
                .build();
    }

    @Bean
    public GroupedOpenApi inventoryApi(){
        return GroupedOpenApi.builder()
                .pathsToMatch("/api/v1/users/me/inventory/**")
                .group("인벤토리")
                .build();
    }

    @Bean
    public GroupedOpenApi profileApi(){
        return GroupedOpenApi.builder()
                .pathsToMatch("/api/v1/users/me/profile/**")
                .group("프로필")
                .build();
    }

    @Bean
    public GroupedOpenApi walletApi(){
        return GroupedOpenApi.builder()
                .pathsToMatch("/api/v1/users/me/wallet/**")
                .group("지갑")
                .build();
    }

    @Bean
    public GroupedOpenApi npcApi(){
        return GroupedOpenApi.builder()
                .pathsToMatch("/api/v1/npcs/**")
                .pathsToMatch("/api/v1/users/me/npcs/**")
                .group("NPC")
                .build();
    }


}
