package praktikum;

import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;

@DisplayName("Изменение данных пользователя")
public class ChangeUserDataTest {
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
    public void tearDown() {
        String token = userApi.loginUser(user)
                .extract().body().path("accessToken");
        user.setAccessToken(token);

        if (user.getAccessToken() != null) {
            userApi.deleteUser(user);
        }
    }

    @Test
    @DisplayName("Смена почты авторизованного пользователя")
    public void changingEmailWithAuth() {
        String newEmail = UUID.randomUUID() + "@yandex.ru";
        userApi.changeEmailWithAuth(user, newEmail)
                .assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("user.email", equalTo(newEmail));
    }

    @Test
    @DisplayName("Изменение имени авторизованного пользователя")
    public void changingNameWithAuth() {
        String newName = String.valueOf(UUID.randomUUID());
        userApi.changeNameWithAuth(user, newName)
                .assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("user.name", equalTo(newName));
    }

    @Test
    @DisplayName("Смена почты неавторизованного пользователя")
    public void changingEmailWithoutAuth(){
        String newEmail = UUID.randomUUID() + "@yandex.ru";
        userApi.changeEmailWithoutAuth(user, newEmail)
                .assertThat()
                .statusCode(401)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Изменение имени неавторизованного пользователя")
    public void changingUsersNameWithoutAuth() {
        String newName = String.valueOf(UUID.randomUUID());
        userApi.changeNameWithoutAuth(user, newName)
                .assertThat()
                .statusCode(401)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("You should be authorised"));
    }
}
