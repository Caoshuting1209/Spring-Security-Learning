### Spring Security Learning

#### 1. WebSecurityConfig

##### 1.1 用户信息管理器

###### 基于内存的用户信息管理器

```java
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
 // 直接定义了可以访问资源的用户
      @Bean
      public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager userDetailsManager = new InMemoryUserDetailsManager();
        // 创建UserDetails对象，用于管理用户名、密码、角色、权限等内容，并使用manager来管理
        userDetailsManager.createUser(
            // 自定义用户名、密码和角色
            User.withDefaultPasswordEncoder()
                .username("shuting")
                .password("yuqi")
                .roles("USER")
                .build());
        return userDetailsManager;
      }
}
```

###### 基于数据库的用户信息管理器

```java 
@Component 
public class DBUserDetailsManager implements UserDetailsManager, UserDetailsPasswordService {
  @Autowired private OauthUserMapper oauthUserMapper; //映射数据库
  @Autowired private PasswordEncoder passwordEncoder;

  @Override
  public UserDetails updatePassword(UserDetails user, String newPassword) {
    return null;
  }

  // 从数据库中获取用户信息
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    QueryWrapper<OauthUser> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("username", username);
    OauthUser oauthUser = oauthUserMapper.selectOne(queryWrapper);
    if (oauthUser == null) {
      throw new UsernameNotFoundException(username);
    } else {
      return User.withUsername(oauthUser.getUsername())
          .password(oauthUser.getPassword())
          .disabled(oauthUser.getStatus() == 0)
          .credentialsExpired(oauthUser.getStatus() == 0)
          .accountLocked(oauthUser.getStatus() == 0)
              //用于角色授权
          //.roles("ADMIN")
              //用于基于权限授权，如果和前文的roles同时使用会覆盖roles
          .authorities(oauthUser.getAuthorities().split(","))
          .build();
    }
  }

  // 向数据库插入新用户
  @Override
  public void createUser(UserDetails userDetails) {
    if (userDetails.getUsername() == null) {
      throw new MissingPropertyException("username");
    }
    String password = userDetails.getPassword();
    if (password == null) {
      throw new MissingPropertyException("password");
    }
    QueryWrapper<OauthUser> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("username", userDetails.getUsername());
    if (oauthUserMapper.selectOne(queryWrapper) != null) {
      throw new ResourceAlreadyExistException("username already exists");
    }
    OauthUser oauthUser = new OauthUser();
    oauthUser.setUsername(userDetails.getUsername());
    oauthUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
    oauthUser.setClientId("1df7b0c1-aac0-148a14c7cba8");
    oauthUser.setAuthorities("ROLE_USER,ROLE_API");
    oauthUser.setStatus(1);
    oauthUserMapper.insert(oauthUser);
  }

  @Override
  public void updateUser(UserDetails userDetails) {}

  @Override
  public void deleteUser(String username) {}

  @Override
  public void changePassword(String oldPassword, String newPassword) {}

  @Override
  public boolean userExists(String username) {
    return false;
  }
}
```

##### 1.2 SecurityFilterChain

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // 开启基于方法的授权
public class WebSecurityConfig {
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
        authorize ->
            authorize
                // 以下两行代码可以注解特定请求的用户访问权限，如果开启基于方法的授权，则不需要在这里授权
                //.requestMatchers("/user/**")
                //.hasRole("ADMIN")
                // 以下方法表示对所有请求开启授权保护
                .anyRequest()
                // 对已认证的请求自动授权
                .authenticated());
    http.formLogin(
        form -> {
          form.loginPage("/login") // 自定义登陆页面，可以用controller实现，需要对应的login.html
              .permitAll() // 允许该前缀跳过认证，否则security过滤器无法到达controller层
              .usernameParameter("username") // 自定义用户名参数名，默认为"username"
              .passwordParameter("password") // 自定义密码参数名，默认为"password"
              .failureUrl("/login?error") // 自定义错误地址，默认为"/login?error"
              // 以上自定义信息必须和login.html中的信息相吻合
            
            //以下为自定义登陆成功/失败返回信息，用于前后端分离体系
              .successHandler(new MyAuthenticationSuccessHandler()) //自定义登陆成功返回信息
              .failureHandler(new MyAuthenticationFailureHandler()) //自定义登陆失败返回信息
          ;
        });
    http.logout(logout -> logout.logoutSuccessHandler(new MyLogoutSuccessHandler())); //注销成功处理
    http.exceptionHandling(
        exception ->
            exception
                .authenticationEntryPoint(new MyAuthenticationEntryPoint())//未认证
                .accessDeniedHandler(new MyAccessDeniedHandler())); // 无权限
    http.sessionManagement(
        session ->
            session
                // 最多允许1个设备同时在线
                .maximumSessions(1)
                // 后登陆的设备会把先前设备挤掉
                .expiredSessionStrategy(new MySessionInformationExpiredStrategy()));
    // 关闭csrf攻击防御，可用于test
    http.csrf(csrf -> csrf.disable());
    // 在全局范围内开启前后端服务器的跨域访问
    http.cors(Customizer.withDefaults());
    return http.build();
  }

  //在全局范围内定义密码编码方式，以Bean的形式注解
  //User.withDefaultPasswordEncoder()的方法已经弃用，必须在这里注解
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(4);
  }
}
```



##### 1.3 前后端分离系统的自定义返回信息

###### AuthenticationSuccessHandler

```java
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException, ServletException {
    HashMap result = new HashMap();
    result.put("code", "0");
    result.put("msg", "success login");
    // 返回用户身份信息
    result.put("data", authentication.getPrincipal());

    // 将结果对象转化为json数据(用到fastjson2依赖)
    String json = JSON.toJSONString(result);

    // 将json数据返回给前端
    response.setContentType("application/json;charset=UTF-8");
    response.getWriter().println(json);
  }
}
```



###### AuthenticationFailureHandler

```java
public class MyAuthenticationFailureHandler implements AuthenticationFailureHandler {
  @Override
  public void onAuthenticationFailure(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
      throws IOException, ServletException {
    HashMap result = new HashMap();
    result.put("code", "-1");
    // 获取登陆失败信息
    result.put("msg", exception.getLocalizedMessage());
    String json = JSON.toJSONString(result);
    response.setContentType("application/json;charset=UTF-8");
    response.getWriter().println(json);
  }
}

```



###### LogoutSuccessHandler

```java
public class MyLogoutSuccessHandler implements LogoutSuccessHandler {
  @Override
  public void onLogoutSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException, ServletException {
    HashMap result = new HashMap();
    result.put("code", "0");
    result.put("msg", "success logout");
    String json = JSON.toJSONString(result);
    response.setContentType("application/json;charset=UTF-8");
    response.getWriter().println(json);
  }
}
```



###### AuthenticationEntryPoint（未认证）

```java
public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint {
  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException, ServletException {
    // 当用户访问未经认证的接口时，会自动跳转到登陆界面，此方法用于覆盖这种自动跳转
    HashMap result = new HashMap();
    result.put("code", "-1");
    result.put("msg", authException.getLocalizedMessage());
    String json = JSON.toJSONString(result);
    response.setContentType("application/json;charset=UTF-8");
    response.getWriter().println(json);
  }
}
```



###### AccessDeniedHandler（未授权）

```java
public class MyAccessDeniedHandler implements AccessDeniedHandler {
  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException)
      throws IOException, ServletException {
    HashMap result = new HashMap();
    result.put("code", "-1");
    result.put("msg", "Unauthorized request");

    // 将结果对象转化为json数据
    String json = JSON.toJSONString(result);

    // 将json数据返回给前端
    response.setContentType("application/json;charset=UTF-8");
    response.getWriter().println(json);
  }
}
```



######  SessionInformationExpiredStrategy（连接超时）

```java
public class MySessionInformationExpiredStrategy implements SessionInformationExpiredStrategy {
  @Override
  public void onExpiredSessionDetected(SessionInformationExpiredEvent event)
      throws IOException, ServletException {
    HashMap result = new HashMap();
    result.put("code", "-1");
    result.put("msg", "The account has been logged in from another device");

    // 将结果对象转化为json数据
    String json = JSON.toJSONString(result);

    // 将json数据返回给前端
    HttpServletResponse response = event.getResponse();
    response.setContentType("application/json;charset=UTF-8");
    response.getWriter().println(json);
  }
}
```



##### 1.4 自定义登陆页面

- 引入依赖

  ```xml
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
    <version>3.4.1</version>
  </dependency>
  <dependency>
    <groupId>org.thymeleaf.extras</groupId>
    <artifactId>thymeleaf-extras-springsecurity6</artifactId>
    <version>3.1.3.RELEASE</version>
  </dependency>
  ```

  

- 在controller定义登陆路径

  ```java
  @Controller
  public class LoginController {
    @GetMapping("/login")
    public String login() {
      return "login";//这里的login链接到thymeleaf定义的网页设置
    }
  }
  ```

  

- 在resource -> templete下设置登陆界面

  ```html
  <html xmlns:th="https://www.thymeleaf.org">
  <head>
      <title>Login</title>
  </head>
  <body>
  <h1>Login</h1>
  <div th:if="${param.error}">
      Wrong uesrname and password.</div>
  <form th:action="@{/login}" method="post">
      <!--动态login参数，表单中会自动生成csrf隐藏字段，防止csrf攻击，并且可以自动匹配前缀-->
      <div>
          <!--这里name默认为username，如果需要改动，则在表单登陆设置formLogin中重定义usernameParameter-->
          <input type="text" name="username" placeholder="username"/>
      </div>
      <div>
          <!--这里name默认为password，如果需要改动，则在表单登陆设置formLogin中重定义passwordParameter-->
          <input type="password" name="password" placeholder="password"/>
      </div>
      <input type="submit" value="login"/>
  </form>
  </body>
  </html>
  ```



#### 2. 用户权限相关

```java
@RestController
@RequestMapping("/user")
public class OauthUserController {
  @Autowired private OauthUserServiceImpl oauthUserService;

  // 获取用户列表
  @GetMapping("/list")
  @PreAuthorize("hasAuthority('ROLE_API')") // 在开启方法授权的情况下，这里可以用于校验用户是否被授权该请求
  public List<OauthUser> getOauthUser() {
    return oauthUserService.list();
  }

  // 新增用户
  @PostMapping("/add")
  //在基于方法的授权模式下，没有授权校验默认对所有用户授权
  public void addUser(@RequestBody OauthUser oauthUser) {
    oauthUserService.saveUserDetails(oauthUser);
  }
}

```



#### 3. 用于测试的脚手架（非常好用）

##### 3.1 引入依赖

```xml
<dependency>
    <groupId>com.github.xiaoymin</groupId>
    <artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
    <version>4.5.0</version>
</dependency>
```

##### 3.2 测试方法

- 登陆界面登陆：localhost:8800/（自动跳转至localhost:8800/login）
- 进入测试脚手架： localhost:8800/doc.html
- 进行测试

##### 3.3 特别注意

> 该功能和@ControllerAdvice注解不兼容，测试的时候可以把该注解先注释掉
