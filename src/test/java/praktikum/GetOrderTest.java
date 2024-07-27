package praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.UUID;
import static org.hamcrest.CoreMatchers.equalTo;

public class GetOrderTest {
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
    @DisplayName("Получение заказов авторизованным пользователем")
    public void getOrdersWithAuth(){
        ValidatableResponse validatableResponse = userApi
                .loginUser(user)
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true));
        String token = validatableResponse
                .extract()
                .path("accessToken");

        String ingredient1 = orderApi.getIngredientsData().extract().path("data[1]._id");
        String ingredient2 = orderApi.getIngredientsData().extract().path("data[2]._id");
        String Ingredient3 = orderApi.getIngredientsData().extract().path("data[3]._id");
        List<String> ingredients = List.of(ingredient1, ingredient2, Ingredient3);
        Order order = new Order(ingredients);
        orderApi.makeOrder(token, order);
        orderApi.getOrdersAuth(token)
                .assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Получение заказов неавторизованным пользователем")
    public void getOrderWithoutAuth(){
        orderApi.getOrdersWithoutAuth()
                .assertThat()
                .statusCode(401)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("You should be authorised"));
    }
}
