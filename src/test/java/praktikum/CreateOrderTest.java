package praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

public class CreateOrderTest {
    private final OrderApi orderApi = new OrderApi();
    private final UserApi userApi = new UserApi();
    private User user;

    @Before
    public void setUp() {
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
    @DisplayName("Оформление заказа")
    public void orderWithIngredientsAndAuth() {
        ValidatableResponse loginResult = userApi
                .loginUser(user)
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true));

        String token = loginResult.extract().path("accessToken");

        String ingredient1 = orderApi.getIngredientsData().extract().path("data[1]._id");
        String ingredient2 = orderApi.getIngredientsData().extract().path("data[2]._id");
        String ingredient3 = orderApi.getIngredientsData().extract().path("data[3]._id");
        List<String> ingredients = List.of(ingredient1, ingredient2, ingredient3);
        Order order = new Order(ingredients);
        ValidatableResponse orderResult = orderApi.makeOrder(token, order)
                .assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true));

        List<String> finIngredients = orderResult
                .extract()
                .path("order.ingredients._id");

        assertThat("Ингредиенты не совпадают",
                finIngredients, containsInAnyOrder(ingredients.toArray()));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void orderWithoutIngredientsWithAuth(){
        ValidatableResponse validatableResponse = userApi
                .loginUser(user)
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true));
        String token = validatableResponse
                .extract()
                .path("accessToken");
        orderApi.makeOrderWithoutIngredients(token)
                .assertThat()
                .statusCode(400)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Cозданиe заказа с неверным хешем ингредиентов")
    public void orderWithIncorrectIngredientsHash(){
        ValidatableResponse loginResult = userApi
                .loginUser(user)
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true));

        String token = loginResult.extract().path("accessToken");
        orderApi.getIngredientsData()
                .assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true));

        String ingredient1 = String.valueOf(UUID.randomUUID());
        String ingredient2 = String.valueOf(UUID.randomUUID());
        String ingredient3 = String.valueOf(UUID.randomUUID());
        List<String> ingredients = List.of(ingredient1, ingredient2, ingredient3);
        Order order = new Order(ingredients);
        orderApi.makeOrder(token, order)
                .assertThat()
                .statusCode(500);
    }

    @Test
    @DisplayName("Создание заказа неавторизованным пользователем")
    public void orderWithIngredientsAndWithoutAuth(){
        String ingredient1 = orderApi.getIngredientsData().extract().path("data[1]._id");
        String ingredient2 = orderApi.getIngredientsData().extract().path("data[2]._id");
        String ingredient3 = orderApi.getIngredientsData().extract().path("data[3]._id");
        List<String> ingredients = List.of(ingredient1, ingredient2, ingredient3);
        orderApi.makeOrderWithoutAuth(ingredients)
                .assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true));
    }
}
