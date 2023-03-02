package lib;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.Header;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class ApiCoreRequests {

    @Step("Login user")
    public Response loginUser(String email, String password) {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", email);
        authData.put("password", password);
        return given()
                .filter(new AllureRestAssured())
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();
    }

    @Step("Make a GET-request with token and cookie")
    public Response makeGetUserInfoByIdRequest(String userId, String token, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .cookie("auth_sid", cookie)
                .get("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();
    }

    @Step("Make an authorization GET-request with token and cookie")
    public Response makeGetAuthorizationRequest(String token, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .cookie("auth_sid", cookie)
                .get("https://playground.learnqa.ru/api/user/auth")
                .andReturn();
    }

    @Step("Make a GET-request with auth cookie only")
    public Response makeGetRequestWithCookie(String url, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .cookie("auth_sid", cookie)
                .get(url)
                .andReturn();
    }

    @Step("Make a GET-request with token only")
    public Response makeGetRequestWithToken(String url, String token) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .get(url)
                .andReturn();
    }

    @Step("Make a POST-request with email and password")
    public Response createUserPostRequest(Map<String,String> authData) {
        return given()
                .filter(new AllureRestAssured())
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();
    }

    @Step("Make update user PUT-request with cookie and header")
    public Response makeUserEditRequestWithCookieAndToken(String userId, String cookie, String header, Map<String,String> authData) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", header))
                .cookie("auth_sid", cookie)
                .body(authData)
                .put("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();
    }

    @Step("Make update user PUT-request without cookie and header")
    public Response makeUserEditRequestWithoutCookieAndToken(String userId, Map<String,String> authData) {
        return given()
                .filter(new AllureRestAssured())
                .body(authData)
                .put("https://playground.learnqa.ru/api/user/"+ userId)
                .andReturn();
    }

    @Step("Make a user DELETE-request with cookie and header")
    public Response makeUserDeleteRequest(String userId, String cookie, String header) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", header))
                .cookie("auth_sid", cookie)
                .delete("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();
    }

}
