package praktikum;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import static io.restassured.RestAssured.given;

public class UserApi extends SiteURL{
    @Step("Создание пользователя")
    public ValidatableResponse createUser(User user) {
        return RestAssured.given()
                .spec(getSpecification())
                .body(user)
                .post(CREATE_USER)
                .then();
    }

    @Step ("Получение токена")
    private String getToken(User user){
        ValidatableResponse loginResponse = given()
                .spec(getSpecification())
                .body(user)
                .when()
                .post(LOGIN_USER)
                .then()
                .assertThat()
                .statusCode(200);

        String token = loginResponse.extract().path("accessToken");
        if (token == null) {
            throw new IllegalArgumentException("Token is null");
        }
        return token;
    }

    @Step("Авторизация пользователя")
    public ValidatableResponse loginUser(User user) {
        return RestAssured.given()
                .spec(getSpecification())
                .body(user)
                .post(LOGIN_USER)
                .then();
    }

    @Step ("Изменение почты авторизованного пользователя")
    public ValidatableResponse changeEmailWithAuth(User user, String newEmail){
        String token = getToken(user);
        user.setEmail(newEmail);
        return given()
                .spec(getSpecification())
                .header("Authorization", token)
                .body(user)
                .when()
                .patch(USER)
                .then();
    }

    @Step ("Изменение имени авторизованного пользователя")
    public ValidatableResponse changeNameWithAuth(User user, String newName) {
        String token = getToken(user);
        user.setName(newName);
        return given()
                .spec(getSpecification())
                .header("Authorization", token)
                .body(user)
                .when()
                .patch(USER)
                .then();
    }

    @Step ("Изменение почты неавторизованного пользователя")
    public ValidatableResponse changeEmailWithoutAuth(User user, String newEmail){
        user.setEmail(newEmail);
        return given()
                .spec(getSpecification())
                .body(user)
                .when()
                .patch(USER)
                .then();
    }

    @Step ("Изменение имени пнеавторизованного")
    public ValidatableResponse changeNameWithoutAuth(User user, String newName){
        user.setName(newName);
        return given()
                .spec(getSpecification())
                .body(user)
                .when()
                .patch(USER)
                .then();
    }

    @Step ("Удаление пользователя")
    public void deleteUser(User user){
        given()
                .spec(getSpecification())
                .header("accessToken", user.getAccessToken())
                .when()
                .delete(USER)
                .then();
    }
}
