package tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.qameta.allure.SeverityLevel.CRITICAL;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Epic("User")
@Feature("Delete user")
public class UserDeleteTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    //Ex18: Тесты на DELETE
    @Test
    @Severity(CRITICAL)
    @DisplayName("Удаление неудаляемого пользователя")
    public void testDeleteReadonlyUser() {
        //LOGIN
        Response responseGetAuth = apiCoreRequests
                .loginUser("vinkotov@example.com", "1234");
        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseIdUser = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/auth", header, cookie);

        String userId = responseIdUser.jsonPath().getString("user_id");

        //DELETE
        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequest("https://playground.learnqa.ru/api/user/" + userId, cookie, header);

        assertEquals("Please, do not delete test users with ID 1, 2, 3, 4 or 5.", responseDeleteUser.asString(), "Unexpected server response: " + responseDeleteUser.asString());
    }

    @Test
    @Severity(CRITICAL)
    @DisplayName("Успешное удаление пользователя")
    public void testDeleteUser() {
        //CREATE
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateUser = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        //LOGIN
        Response responseGetAuth = apiCoreRequests
                .loginUser(userData.get("email"), userData.get("password"));

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseIdUser = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/auth", header, cookie);

        String userId = responseIdUser.jsonPath().getString("user_id");

        //DELETE
        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequest("https://playground.learnqa.ru/api/user/" + userId, cookie, header);

        //CHECK DELETE TRUE
        Response responseDeletedUser = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/" + userId, header, cookie);

        assertEquals("User not found", responseDeletedUser.getBody().asString(), "User is still exist!");
    }

    @Test
    @Severity(CRITICAL)
    @DisplayName("Удаление пользователя другим пользователем")
    public void testDeleteUserByAnotherUser() {
        //CREATE
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuthFirst = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        Map<String, String> userData2 = DataGenerator.getRegistrationData();

        Response responseCreateAuthSecond = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData2);

        String userIdSecond = responseCreateAuthSecond.jsonPath().getString("id");

        //LOGIN
        Response responseGetAuth = apiCoreRequests
                .loginUser(userData.get("email"), userData.get("password"));

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        //DELETE
        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequest("https://playground.learnqa.ru/api/user/" + userIdSecond, cookie, header);

        //CHECK DELETE
        Response responseDeletedUser = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/" + userIdSecond, header, cookie);
        Assertions.assertJsonHasField(responseDeletedUser, "username");
    }
}
