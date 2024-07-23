package com.vmo.management_fresher.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "Fresher Management Api",
                description = "Fresher Management System",
                summary = "This app-api will add,delete,create,update,get",
                termsOfService = "Terms of service",
                contact = @Contact(
                        name = "lucpv",
                        email = "lucpv508@gmail.com"
                ),
                license = @License(
                        name = "Your License No"
                ),
                version = "v1"
        ),
        servers = {
                @Server(
                        description = "Dev",
                        url = "http://localhost:8081"
                ),
                @Server(
                        description = "Test",
                        url = "http://localhost:8081"
                )
        },
        security = @SecurityRequirement(
                name = "authBearer"
        )
)
@SecurityScheme(
        name = "authBearer",
        in = SecuritySchemeIn.HEADER,
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer",
        description = "JWT auth description"
)
public class OpenApiConfig {
}
