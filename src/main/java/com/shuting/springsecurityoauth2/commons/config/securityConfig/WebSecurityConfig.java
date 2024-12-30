package com.shuting.springsecurityoauth2.commons.config.securityConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // 开启基于方法的授权
public class WebSecurityConfig {
//      @Bean
//      public UserDetailsService userDetailsService() {
//        // 创建基于内存的用户信息管理器
//        InMemoryUserDetailsManager userDetailsManager = new InMemoryUserDetailsManager();
//        // 创建UserDetails对象，用于管理用户名、密码、角色、权限等内容，并使用manager来管理
//        userDetailsManager.createUser(
//            // 自定义用户名、密码和角色
//            User.withDefaultPasswordEncoder()
//                .username("shuting")
//                .password("yuqi")
//                .roles("USER")
//                .build());
//        return userDetailsManager;
//      }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
        authorize ->
            authorize
                // 以下两行代码可以注解特定请求的用户访问权限，如果开启基于方法的授权，则不需要在这里授权
                //                .requestMatchers("/user/**")
                //                .hasRole("ADMIN")
                // 对所有请求开启授权保护
                .anyRequest()
                // 对已认证的请求自动授权
                .authenticated());
    http.formLogin(
        form -> {
          form.loginPage("/login") // 自定义登陆页面
              .permitAll() // 允许该前缀跳过认证
              .usernameParameter("username") // 自定义用户名参数名
              .passwordParameter("password") // 自定义密码参数名
              .failureUrl("/login?error") // 自定义错误地址
              // 以上自定义信息必须和login.html中的信息相吻合
              .successHandler(new MyAuthenticationSuccessHandler()) // 自定义登陆成功返回信息
              .failureHandler(new MyAuthenticationFailureHandler()) // 自定义登陆失败返回信息
          ;
        });
    http.logout(logout -> logout.logoutSuccessHandler(new MyLogoutSuccessHandler())); // 注销成功处理
    http.exceptionHandling(
        exception ->
            exception
                .authenticationEntryPoint(new MyAuthenticationEntryPoint())
                .accessDeniedHandler(new MyAccessDeniedHandler())); // 请求未认证的处理
    http.sessionManagement(
        session ->
            session
                // 最多允许1个设备同时在线
                .maximumSessions(1)
                // 后登陆的设备会把先前设备挤掉
                .expiredSessionStrategy(new MySessionInformationExpiredStrategy()));
    // 关闭csrf攻击防御
    http.csrf(csrf -> csrf.disable());
    // 在全局范围内开启前后端服务器的跨域访问
    http.cors(Customizer.withDefaults());

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(4);
  }
}
