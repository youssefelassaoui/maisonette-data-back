package gep.ma.maisonette_data_back.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*"); // Autorise toutes les origines
        config.addAllowedMethod("*"); // Autorise toutes les méthodes (GET, POST, etc.)
        config.addAllowedHeader("*"); // Autorise tous les headers
        config.setAllowCredentials(true); // Permet l'envoi des credentials (cookies, authorization headers)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Applique la configuration à toutes les routes
        return new CorsFilter(source);
    }
}
