package com.runicsoft.bencolapp.seguridad.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )
                .exceptionHandling(exception ->
                        exception
                                .authenticationEntryPoint(authenticationEntryPoint)
                                .accessDeniedHandler(accessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> auth

                        // AUTH
                        .requestMatchers(
                                HttpMethod.POST,
                                "/bencol.agua/auth/login"
                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/bencol.agua/auth/me"
                        ).authenticated()

                        // USUARIOS
                        .requestMatchers(
                                "/bencol.agua/usuarios/**"
                        ).hasRole("ADMIN")

                        // CLIENTES
                        .requestMatchers(
                                HttpMethod.GET,
                                "/bencol.agua/clientes/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "VENTAS",
                                "COBRANZAS"
                        )

                        .requestMatchers(
                                HttpMethod.POST,
                                "/bencol.agua/clientes/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "VENTAS"
                        )

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/bencol.agua/clientes/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "VENTAS"
                        )

                        // PRODUCTOS
                        .requestMatchers(
                                HttpMethod.GET,
                                "/bencol.agua/productos/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "VENTAS",
                                "ALMACEN",
                                "COMPRAS"
                        )

                        .requestMatchers(
                                HttpMethod.POST,
                                "/bencol.agua/productos/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "ALMACEN"
                        )

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/bencol.agua/productos/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "ALMACEN"
                        )

                        // PRECIOS ESPECIALES
                        .requestMatchers(
                                HttpMethod.GET,
                                "/bencol.agua/precios-clientes/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "VENTAS"
                        )

                        .requestMatchers(
                                HttpMethod.POST,
                                "/bencol.agua/precios-clientes/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "VENTAS"
                        )

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/bencol.agua/precios-clientes/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "VENTAS"
                        )

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/bencol.agua/precios-clientes/**"
                        ).hasRole("ADMIN")

                        // VENTAS
                        .requestMatchers(
                                HttpMethod.GET,
                                "/bencol.agua/ventas/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "VENTAS",
                                "COBRANZAS"
                        )

                        .requestMatchers(
                                HttpMethod.POST,
                                "/bencol.agua/ventas/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "VENTAS"
                        )

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/bencol.agua/ventas/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "VENTAS"
                        )

                        // INVENTARIO
                        .requestMatchers(
                                HttpMethod.GET,
                                "/bencol.agua/inventarios/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "ALMACEN",
                                "COMPRAS"
                        )

                        .requestMatchers(
                                HttpMethod.POST,
                                "/bencol.agua/inventarios/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "ALMACEN"
                        )

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/bencol.agua/inventarios/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "ALMACEN"
                        )

                        // ENVASES
                        /*
                         * SALDO INICIAL
                         *
                         * Se utiliza únicamente para registrar la situación
                         * inicial real de los envases de un cliente.
                         *
                         * Ejemplo:
                         * cliente nuevo en el sistema que ya poseía
                         * 30 bidones antes de utilizar Bencol App.
                         *
                         * Por seguridad solo ADMIN puede realizarlo.
                         */
                        .requestMatchers(
                                HttpMethod.POST,
                                "/bencol.agua/envases/saldo-inicial"
                        ).hasRole("ADMIN")

                        /*
                         * CONSULTAS DE ENVASES
                         */
                        .requestMatchers(
                                HttpMethod.GET,
                                "/bencol.agua/envases/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "VENTAS",
                                "ALMACEN"
                        )

                        /*
                         * MOVIMIENTOS DE ENVASES
                         *
                         * PRESTAMO
                         * COMPRA
                         * DEVOLUCION
                         * INTERCAMBIO
                         * CONVERSION_COMPRA
                         *
                         * AJUSTE continúa controlado internamente
                         * por EnvaseServiceImpl.
                         */
                        .requestMatchers(
                                HttpMethod.POST,
                                "/bencol.agua/envases/movimientos"
                        ).hasAnyRole(
                                "ADMIN",
                                "VENTAS",
                                "ALMACEN"
                        )

                        // CUENTAS POR COBRAR
                        .requestMatchers(
                                HttpMethod.GET,
                                "/bencol.agua/finanzas/cuentas/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "COBRANZAS",
                                "CAJA"
                        )

                        .requestMatchers(
                                HttpMethod.POST,
                                "/bencol.agua/finanzas/cuentas/pagos"
                        ).hasAnyRole(
                                "ADMIN",
                                "COBRANZAS",
                                "CAJA"
                        )

                        // CUENTAS POR PAGAR
                        .requestMatchers(
                                HttpMethod.GET,
                                "/bencol.agua/finanzas/cuentas-pagar/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "COMPRAS",
                                "CAJA"
                        )

                        .requestMatchers(
                                HttpMethod.POST,
                                "/bencol.agua/finanzas/cuentas-pagar/pagos"
                        ).hasAnyRole(
                                "ADMIN",
                                "COMPRAS",
                                "CAJA"
                        )

                        // CAJA
                        .requestMatchers(
                                HttpMethod.GET,
                                "/bencol.agua/caja/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "CAJA"
                        )

                        .requestMatchers(
                                HttpMethod.POST,
                                "/bencol.agua/caja/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "CAJA"
                        )

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/bencol.agua/caja/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "CAJA"
                        )

                        // EGRESOS
                        .requestMatchers(
                                HttpMethod.GET,
                                "/bencol.agua/egresos/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "CAJA"
                        )

                        .requestMatchers(
                                HttpMethod.POST,
                                "/bencol.agua/egresos/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "CAJA"
                        )

                        // PROVEEDORES
                        .requestMatchers(
                                HttpMethod.GET,
                                "/bencol.agua/proveedores/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "COMPRAS"
                        )

                        .requestMatchers(
                                HttpMethod.POST,
                                "/bencol.agua/proveedores/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "COMPRAS"
                        )

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/bencol.agua/proveedores/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "COMPRAS"
                        )

                        // COMPRAS
                        .requestMatchers(
                                HttpMethod.GET,
                                "/bencol.agua/compras/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "COMPRAS",
                                "ALMACEN"
                        )

                        .requestMatchers(
                                HttpMethod.POST,
                                "/bencol.agua/compras/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "COMPRAS"
                        )

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/bencol.agua/compras/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "COMPRAS"
                        )

                        // REPORTES
                        .requestMatchers(
                                HttpMethod.GET,
                                "/bencol.agua/reportes/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "VENTAS",
                                "CAJA",
                                "ALMACEN",
                                "COBRANZAS",
                                "COMPRAS"
                        )

                        // CUALQUIER OTRO ENDPOINT
                        .anyRequest()
                        .denyAll()
                )

                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(
                                        jwtAuthenticationConverter()
                                )
                        )
                );

        return http.build();
    }

    @Bean
    public Converter<Jwt, ? extends AbstractAuthenticationToken>
    jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authorities = new JwtGrantedAuthoritiesConverter();

        authorities.setAuthoritiesClaimName("rol");
        authorities.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authorities);
        return converter;
    }
}