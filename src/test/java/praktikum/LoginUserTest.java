package praktikum;

import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;

public class LoginUserTest {
    private User user;
    private UserApi userApi;

    @Before
    public void setUp() {
        userApi = new UserApi();

        String email = UUID.randomUUID() + "@yandex.ru";
        String password = String.valueOf(UUID.randomUUID());
        String name = String.valueOf(UUID.randomUUID());

        user = new User(email, password, name);
        userApi.createUser(user);
    }

    @After
    public void tearDown(){
        String token = userApi.loginUser(user)
                .extract().body().path("accessToken");
        user.setAccessToken(token);

        if (user.getAccessToken() != null) {
            userApi.deleteUser(user);
        }
    }

    @Test
    @DisplayName("Вход зарегистрированного пользователя")
    public void login(){
        userApi.loginUser(user)
                .assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Попытка входа с неверной почтой")
    public void loginWithIncorrectEmail(){
        user.setEmail("incorrectEmail");
        userApi.loginUser(user)
                .assertThat()
                .statusCode(401)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Попытка входа с неверным паролем")
    public void loginWithIncorrectPassword(){
        user.setPassword("incorrectPassword");
        userApi.loginUser(user)
                .assertThat()
                .statusCode(401)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("email or password are incorrect"));
    }
}
