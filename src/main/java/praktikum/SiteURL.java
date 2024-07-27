package praktikum;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class SiteURL {
    public static final String HOME_PAGE = "https://stellarburgers.nomoreparties.site";
    public static final String INGREDIENTS = "https://stellarburgers.nomoreparties.site/api/ingredients";
    public static final String ORDERS = "/api/orders";
    public static final String CREATE_USER = "/api/auth/register";
    public static final String LOGIN_USER = "/api/auth/login";
    public static final String USER = "/api/auth/user";

    protected RequestSpecification getSpecification() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri(HOME_PAGE)
                .build();
    }
}
