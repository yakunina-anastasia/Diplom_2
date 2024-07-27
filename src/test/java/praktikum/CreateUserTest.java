package praktikum;

import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;

public class CreateUserTest {
    private User user;
    private UserApi userApi;

    @Before
    public void setUp(){
        userApi = new UserApi();

        String email = UUID.randomUUID() + "@yandex.ru";
        String password = String.valueOf(UUID.randomUUID());
        String name = String.valueOf(UUID.randomUUID());

        user = new User(email, password, name);
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
    @DisplayName("Создание пользователя")
    public void createUser(){
        userApi.createUser(user)
                .assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Попытка регистрации без имени")
    public void registrationWithoutName(){
        user.setName(null);
        userApi.createUser(user)
                .assertThat()
                .statusCode(403)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Попытка регистрации без почты")
    public void registrationWithoutEmail(){
        user.setEmail(null);
        userApi.createUser(user)
                .assertThat()
                .statusCode(403)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Попытка регистрации без пароля")
    public void registrationWithoutPassword(){
        user.setPassword(null);
        userApi.createUser(user)
                .assertThat()
                .statusCode(403)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Попыка зарегистрироваться второй раз")
    public void secondRegistration(){
        userApi.createUser(user);

        userApi.createUser(user)
                .assertThat()
                .statusCode(403)
                .and()
                .body("success", equalTo(false));
    }
}
