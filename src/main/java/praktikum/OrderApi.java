package praktikum;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import static io.restassured.RestAssured.given;
import io.restassured.response.ValidatableResponse;
import java.util.List;
import java.util.Map;

public class OrderApi extends SiteURL {
    @Step("Получение данных об ингредиентах")
    public ValidatableResponse getIngredientsData() {
        return given()
                .contentType(ContentType.JSON)
                .when()
                .get(INGREDIENTS)
                .then();
    }

    @Step("Создание заказа с авторизацией и ингредиентами")
    public ValidatableResponse makeOrder(String accessToken, Order order){
        return given()
                .header("Authorization", accessToken)
                .spec(getSpecification())
                .body(order)
                .when()
                .post(ORDERS)
                .then();
    }

    @Step("Получение заказов авторизованного пользователя")
    public ValidatableResponse getOrdersAuth(String accessToken){
        return given()
                .header("Authorization", accessToken)
                .spec(getSpecification())
                .when()
                .get(ORDERS)
                .then();
    }

    @Step("Создание заказа без ингредиентов")
    public ValidatableResponse makeOrderWithoutIngredients(String accessToken){
        return given()
                .header("Authorization", accessToken)
                .spec(getSpecification())
                .body("")
                .when()
                .post(ORDERS)
                .then();
    }

    @Step("Создание заказа без авторизации")
    public ValidatableResponse makeOrderWithoutAuth(List<String> ingredients){
        Map<String, Object> requestMap = Map.of("ingredients", ingredients);
        return given()
                .spec(getSpecification())
                .body(requestMap)
                .when()
                .post(ORDERS)
                .then();
    }

    @Step("Получение заказов неавторизованного пользователя")
    public ValidatableResponse getOrdersWithoutAuth(){
        return given()
                .spec(getSpecification())
                .when()
                .get(ORDERS)
                .then();
    }
}
