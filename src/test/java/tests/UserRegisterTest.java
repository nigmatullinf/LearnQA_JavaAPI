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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import static io.qameta.allure.SeverityLevel.CRITICAL;
import static io.qameta.allure.SeverityLevel.NORMAL;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Epic("User")
@Feature("Registration")
public class UserRegisterTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    //Ex15: Тесты на метод user
    @Test
    @Severity(NORMAL)
    @DisplayName("Создание пользователя с email без знака @")
    public void userCreateWithoutEmailSign() {
        Map<String, String> regData = new HashMap<>();
        regData.put("email", DataGenerator.getRandomEmailWithoutSign());
        regData = DataGenerator.getRegistrationData(regData);

        Response responsePostRegister = apiCoreRequests
                .createUserPostRequest(regData);

        assertEquals("Invalid email format", responsePostRegister.getBody().asString(), "Json value is not equal to expected value");
        Assertions.assertStatusCode(responsePostRegister, 400);
    }

    //Ex15: Тесты на метод user
    @ParameterizedTest
    @Severity(CRITICAL)
    @DisplayName("Создание пользователя без указания одного из обязательных полей")
    @ValueSource(strings = {"email", "password", "username", "firstName", "lastName"})
    public void userCreateWithEmptyKey(String key) {
        Map<String, String> regData = new HashMap<>();

        switch (key) {
            case "email":
                regData.put("password", "1234");
                regData.put("username", "learnqa");
                regData.put("firstName", "learnqa");
                regData.put("lastName", "learnqa");;
                break;
            case "password":
                regData.put("email", DataGenerator.getRandomEmail());
                regData.put("username", "learnqa");
                regData.put("firstName", "learnqa");
                regData.put("lastName", "learnqa");
                break;
            case "username":
                regData.put("email", DataGenerator.getRandomEmail());
                regData.put("password", "1234");
                regData.put("firstName", "learnqa");
                regData.put("lastName", "learnqa");
                break;
            case "firstName":
                regData.put("email", DataGenerator.getRandomEmail());
                regData.put("password", "1234");
                regData.put("username", "learnqa");
                regData.put("lastName", "learnqa");
                break;
            case "lastName":
                regData.put("email", DataGenerator.getRandomEmail());
                regData.put("password", "1234");
                regData.put("username", "learnqa");
                regData.put("firstName", "learnqa");
                break;
        }

        Response responsePostRegister = apiCoreRequests
                .createUserPostRequest(regData);

        assertEquals("The following required params are missed: " + key, responsePostRegister.getBody().asString(),
                "Unexpected error message: " + responsePostRegister.getBody().asString());
        Assertions.assertStatusCode(responsePostRegister, 400);
    }

    //Ex15: Тесты на метод user
    @Test
    @Severity(NORMAL)
    @DisplayName("Создание пользователя c слишком коротким именем")
    public void userCreateWithShortName() {
        Map<String, String> regData = new HashMap<>();
        regData.put("username", DataGenerator.getShortUsername());
        regData = DataGenerator.getRegistrationData(regData);

        Response responsePostRegister = apiCoreRequests
                .createUserPostRequest(regData);

        assertEquals("The value of 'username' field is too short",
        responsePostRegister.getBody().asString(),
        "Unexpected error message: " + responsePostRegister.getBody().asString());
        Assertions.assertStatusCode(responsePostRegister, 400);
    }

    //Ex15: Тесты на метод user
    @Test
    @Severity(NORMAL)
    @DisplayName("Создание пользователя c очень длинным именем")
    public void userCreateWithEnormousName() {
        Map<String, String> regData = new HashMap<>();
        regData.put("username", DataGenerator.getEnormousUsername());
        regData = DataGenerator.getRegistrationData(regData);

        Response responsePostRegister = apiCoreRequests
                .createUserPostRequest(regData);

        assertEquals("The value of 'username' field is too long",
                responsePostRegister.getBody().asString(),
                "Unexpected error message: " + responsePostRegister.getBody().asString());
        Assertions.assertStatusCode(responsePostRegister, 400);
    }
}
