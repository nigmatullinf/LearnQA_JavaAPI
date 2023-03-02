package tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.SeverityLevel.CRITICAL;

@Epic("User")
@Feature("User Details")
public class UserGetTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    //Ex16: Запрос данных другого пользователя
    @Test
    @Severity(CRITICAL)
    @DisplayName("Запрос информации о пользователе другим пользователем")
    public void testGetUserDetailsByAnotherUser() {
        Response responseGetAuth = apiCoreRequests
                .loginUser("vinkotov@example.com", "1234");

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseGetAnotherUser = apiCoreRequests
                .makeGetUserInfoByIdRequest("1", header, cookie);

        String[] unexpectedFields = {"firstName", "lastName", "email"};

        Assertions.assertJsonHasField(responseGetAnotherUser, "username");
        Assertions.assertJsonHasNotFields(responseGetAnotherUser, unexpectedFields);
    }
}
