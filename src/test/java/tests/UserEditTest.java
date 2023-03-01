package tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.qameta.allure.SeverityLevel.BLOCKER;
import static io.qameta.allure.SeverityLevel.NORMAL;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Epic("User")
@Feature("Edit user")
public class UserEditTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    //Ex17: Негативные тесты на PUT
    @Test
    @Severity(BLOCKER)
    @DisplayName("Изменение данных пользователя неавторизованным пользователем")
    public void testChangeDataByUnauthorizedUser() {
        Map<String, String> newData = DataGenerator.getRegistrationData();

        Response editUser = apiCoreRequests
                .makePutRequestWithoutCookieAndToken("https://playground.learnqa.ru/api/user/1", newData);

        assertEquals("Auth token not supplied", editUser.getBody().asString(),
                "Unexpected server response: " + editUser.getBody().asString());
    }

    //Ex17: Негативные тесты на PUT
    @Test
    @Severity(BLOCKER)
    @DisplayName("Изменение данных пользователя другим пользователем")
    public void testChangeDataByAnotherUser() {
        //CREATE
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuthFirst = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        Map<String, String> userData2 = DataGenerator.getRegistrationData();

        Response responseCreateAuthSecond = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData2);

        String userIdSecond = responseCreateAuthSecond.jsonPath().getString("id");

        //LOGIN);
        Response responseGetAuth = apiCoreRequests
                .loginUser(userData.get("email"), userData.get("password"));

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        //EDIT SECOND ACCOUNT
        Map<String, String> newData = new HashMap<>();
        newData.put("username", "newUsername");
        newData.put("firstName", "newFirstName");
        newData.put("lastName", "newLastName");
        newData.put("email", DataGenerator.getRandomEmail());
        newData.put("password", "newPassword");

        Response editUser = apiCoreRequests
                .makePutRequestWithCookieAndToken("https://playground.learnqa.ru/api/user/" + userIdSecond,
                        cookie, header, newData);

        //CHECK SECOND ACCOUNT
        Response responseGetAuth2 = apiCoreRequests
                .loginUser(userData2.get("email"), userData2.get("password"));

        String header2 = this.getHeader(responseGetAuth2, "x-csrf-token");
        String cookie2 = this.getCookie(responseGetAuth2, "auth_sid");

        Response responseSecondUserData = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/" + userIdSecond, header2, cookie2);

        assertEquals(userData2.get("username"), responseSecondUserData.jsonPath().get("username"), "Unexpected username in second account");
        assertEquals(userData2.get("firstName"), responseSecondUserData.jsonPath().get("firstName"),"Unexpected firstName in second account");
        assertEquals(userData2.get("lastName"), responseSecondUserData.jsonPath().get("lastName"),"Unexpected lastName in second account");
        assertEquals(userData2.get("email"), responseSecondUserData.jsonPath().get("email"),"Unexpected email in second account");
    }

    //Ex17: Негативные тесты на PUT
    @Test
    @Severity(NORMAL)
    @DisplayName("Изменение email пользователя на невалидный")
    public void testChangeEmailToInvalid() {
        //CREATE
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateUser = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        //LOGIN
        Response responseGetAuth = apiCoreRequests
                .loginUser(userData.get("email"), userData.get("password"));

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        //EDIT
        String userId = responseCreateUser.jsonPath().getString("id");

        Map<String, String> newData = new HashMap<>();
        newData.put("email", DataGenerator.getRandomEmailWithoutSign());

        Response editUser = apiCoreRequests
                .makePutRequestWithCookieAndToken("https://playground.learnqa.ru/api/user/" + userId,
                        cookie, header, newData);

        assertEquals("Invalid email format", editUser.getBody().asString(), "Unexpected server response: " + editUser.getBody().asString());
    }

    //Ex17: Негативные тесты на PUT
    @Test
    @Severity(NORMAL)
    @DisplayName("Изменение имени пользователя на слишком короткое")
    public void testChangeNameToShort() {
        //CREATE
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateUser = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        //LOGIN
        Response responseGetAuth = apiCoreRequests
                .loginUser(userData.get("email"), userData.get("password"));

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        //EDIT
        String userId = responseCreateUser.jsonPath().getString("id");

        Map<String, String> newData = new HashMap<>();
        newData.put("username", DataGenerator.getShortUsername());

        Response editUser = apiCoreRequests
                .makePutRequestWithCookieAndToken("https://playground.learnqa.ru/api/user/" + userId,
                        cookie, header, newData);

        assertEquals("{\"error\":\"Too short value for field username\"}", editUser.getBody().asString(), "Unexpected server response: " + editUser.getBody().asString());
    }
}
