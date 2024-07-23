package com.vmo.management_fresher.configuration.security;

import com.vmo.management_fresher.base.constant.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Autowired
    private CustomJwtDecoder customJwtDecoder;

    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    private final String[] SWAGGER_ENDPOINTS = {
            "/v3/api-docs", "/v3/api-docs/**", "/swagger-resources", "/swagger-resources/**",
            "/configuration/**", "/swagger-ui/**", "/swagger-ui.html", "/webjars/swagger-ui/**",
    };

    private final String[] ADMIN_POST_ENDPOINTS = {
            "/account/registration", "/center", "/center/group-two-center", "/employee", "/employee-center",
            "/import/fresher-to-center", "/position", "/programming-language", "/role",
    };

    private final String[] ADMIN_PUT_ENDPOINTS = {
            "/center/{id}", "/employee-center/{id}", "/position/{name}", "/programming-language/{name}", "/role/{name}",
    };

    private final String[] ADMIN_PATCH_ENDPOINTS = {
            "/account/{id}/add-role-account",
    };

    private final String[] ADMIN_DELETE_ENDPOINTS = {
            "/account/{id}", "/center/{id}", "/employee/{id}", "/position/{name}", "/programming-language/{name}", "/role/{name}",
    };

    private final String[] ADMIN_GET_ENDPOINTS = {
            "/account/get-all", "/center/get-all", "/center/search", "/employee/get-all", "/employee/search", "/import/download-error-file/{fileName}",
            "/position/{name}", "/position/get-all", "/programming-language/{name}", "/programming-language/get-all", "/role/{name}", "/role/get-all",
    };

    private final String[] DIRECTOR_POST_ENDPOINTS = {
            "/assessment/upload-file", "/assessment-fresher/assessment-assign-fresher",
    };

    private final String[] DIRECTOR_PUT_ENDPOINTS = {};

    private final String[] DIRECTOR_PATCH_ENDPOINTS = {
            "/assessment-fresher/{id}/update-point-language",
    };

    private final String[] DIRECTOR_DELETE_ENDPOINTS = {
            "/assessment/delete-file/{id}", "/assessment-fresher/{id}", "/employee-center/{id}",
    };

    private final String[] DIRECTOR_GET_ENDPOINTS = {
            "/assessment/get-by-centerId/{centerId}", "/dashboard/number-freshers-by-center", "/dashboard/freshers-by-point", "/dashboard/freshers-by-avg",
    };

    private final String[] PUBLIC_POST_ENDPOINTS = {
            "/auth/login", "forgot-password/verify-email", "forgot-password/change-password"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity.authorizeHttpRequests(request -> request.requestMatchers(SWAGGER_ENDPOINTS).permitAll()
                .requestMatchers(HttpMethod.POST, PUBLIC_POST_ENDPOINTS).permitAll()

                .requestMatchers(HttpMethod.POST, ADMIN_POST_ENDPOINTS).hasRole(Constant.ADMIN_ROLE)
                .requestMatchers(HttpMethod.PUT, ADMIN_PUT_ENDPOINTS).hasRole(Constant.ADMIN_ROLE)
                .requestMatchers(HttpMethod.PATCH, ADMIN_PATCH_ENDPOINTS).hasRole(Constant.ADMIN_ROLE)
                .requestMatchers(HttpMethod.DELETE, ADMIN_DELETE_ENDPOINTS).hasRole(Constant.ADMIN_ROLE)
                .requestMatchers(HttpMethod.GET, ADMIN_GET_ENDPOINTS).hasRole(Constant.ADMIN_ROLE)

                .requestMatchers(HttpMethod.POST, DIRECTOR_POST_ENDPOINTS).hasAnyRole(Constant.ADMIN_ROLE, Constant.DIRECTOR_ROLE)
                .requestMatchers(HttpMethod.PUT, DIRECTOR_PUT_ENDPOINTS).hasAnyRole(Constant.ADMIN_ROLE, Constant.DIRECTOR_ROLE)
                .requestMatchers(HttpMethod.PATCH, DIRECTOR_PATCH_ENDPOINTS).hasAnyRole(Constant.ADMIN_ROLE, Constant.DIRECTOR_ROLE)
                .requestMatchers(HttpMethod.DELETE, DIRECTOR_DELETE_ENDPOINTS).hasAnyRole(Constant.ADMIN_ROLE, Constant.DIRECTOR_ROLE)
                .requestMatchers(HttpMethod.GET, DIRECTOR_GET_ENDPOINTS).hasAnyRole(Constant.ADMIN_ROLE, Constant.DIRECTOR_ROLE)

                .anyRequest()
                .authenticated());

        httpSecurity.exceptionHandling(exceptionHandlingConfigurer ->
                exceptionHandlingConfigurer.accessDeniedHandler(customAccessDeniedHandler));

        httpSecurity.oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwtConfigurer ->
                                jwtConfigurer.decoder(customJwtDecoder)
                                        .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
        );

        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        return httpSecurity.build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter(){
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
//        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(10);
    }

}
