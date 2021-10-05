package web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

//@Configuration - присутствует в @EnableWebSecurity, поэтому не требуется
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsService userDetailsService; // сервис, с помощью которого тащим пользователя
    private final SuccessUserHandler successUserHandler; // класс, в котором описана логика перенаправления пользователей по ролям

    public SecurityConfig(@Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService, SuccessUserHandler successUserHandler) {
        this.userDetailsService = userDetailsService;
        this.successUserHandler = successUserHandler;
    }

    @Autowired
    public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder()); // конфигурация для прохождения аутентификации
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //Защита CSRF включена по умолчанию в конфигурации Java. Мы все еще можем отключить его, если нам нужно.
        //http.csrf().disable();
        http.authorizeRequests()
                .antMatchers("/", "/login", "/logout").permitAll() // доступность всем
                .antMatchers("/user/**").access("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')") // разрешаем входить на /user пользователям с ролью User, Admin
                .antMatchers("/admin/**").access("hasAnyRole('ROLE_ADMIN')") // разрешает входить на /admin пользователю с ролью Admin
                .and()
                .formLogin()  // Spring сам подставит свою логин форму
//                .loginProcessingUrl("/j_spring_security_check")
//                .loginPage("/login")
//                .defaultSuccessUrl("/user", true)
//                .failureUrl("/login?error=true")
//                .usernameParameter("name")
//                .passwordParameter("password")
                .successHandler(successUserHandler) // подключаем наш SuccessHandler для перенаправления по ролям
                .and().logout()
                .logoutUrl("/logout") //URL-адрес, запускающий выход из системы (по умолчанию "/ logout").
                .logoutSuccessUrl("/login") //URL-адрес для перенаправления после выхода из системы.
                .and().csrf().disable();
    }

    // Необходимо для шифрования паролей
    // В данном примере не использоваться, отключен
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}

