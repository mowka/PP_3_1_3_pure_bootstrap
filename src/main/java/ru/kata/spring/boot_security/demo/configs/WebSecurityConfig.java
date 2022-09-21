package ru.kata.spring.boot_security.demo.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.Arrays;
import java.util.HashSet;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    private final SuccessUserHandler successUserHandler;

    public WebSecurityConfig(SuccessUserHandler successUserHandler) {
        this.successUserHandler = successUserHandler;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/admin/**", "/").hasRole("ADMIN")
                .antMatchers("/user").hasRole("USER")
                .anyRequest().authenticated()
                .and()
                .formLogin().loginPage("/login").usernameParameter("email")
                .successHandler(successUserHandler)
                .permitAll()
                .and()
                .logout()
                .permitAll();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    /*
    сюда отдаём логин+пароль, а он должен сказать существует такой пользователь или нет
    если существует, то его нужно положить в spring security context
    узнает он о существовании через userdetailservice, именно она предоставляет юзеров
     */
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        authenticationProvider.setUserDetailsService(userService);
        return authenticationProvider;
    }


    public class Test1 {

        public void init() {
            Role roleUser = new Role("ROLE_USER");
            Role roleAdmin = new Role("ROLE_ADMIN");
            userService.save(roleUser);
            userService.save(roleAdmin);
            User admin1 = new User("admin1", "$2a$12$Mx4/rjducuZybQEo3pXIy.4g.SS85MT.LZjAnp9vbB/wr.X.wIQPa"
                    , "admin1", "admin1", 1
                    , "admin1@mail.ru", new HashSet<>(Arrays.asList(roleUser, roleAdmin)));
            userService.save(admin1);

            User admin2 = new User("admin2", "$2a$12$JvH4wQ8iZTdVeL3jBYPzm.r4x3gQEShP/mf6Rn3rYG1HhHaQC/IPe"
                    , "admin2", "admin2", 1
                    , "admin2@mail.ru", new HashSet<>(Arrays.asList(roleAdmin)));
            userService.save(admin2);

            User user1 = new User("user1", "$2a$12$ur2oF.Ie7cWN/XMdkmifBeYK7eiZqPZS1r5ir70VE3aGw0LlI6KIi"
                    , "user1", "user1", 1
                    ,"user1@mail.ru", new HashSet<>(Arrays.asList(roleUser)));
            userService.save(user1);

            User user2 = new User("user2", "$2a$12$f9rko62VbLPm6.B.f4/NPeqDgtNhumxrItdG/F0TEC3lIxr2eWo16"
                    , "user2", "user2", 1
                    , "user2@mail.ru", new HashSet<>(Arrays.asList(roleUser)));
            userService.save(user2);
        }

        @Bean(initMethod = "init")
        Test1 test1() {
            return new Test1();
        }
    }

}