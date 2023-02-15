import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class ExerciseSecondTest {

    //Ex5: Парсинг JSON
    @Test
    public void getMessage() {

        JsonPath response = RestAssured
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();


        String message = response.get("messages[1].message");
        System.out.println(message);
    }

    //Ex6: Редирект
    @Test
    public void doRedirect() {

        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .get("https://playground.learnqa.ru/api/long_redirect")
                .andReturn();

        String locationAnswer = response.getHeader("Location");
        System.out.println(locationAnswer);

    }

    //Ex7: Долгий редирект
    @Test
    public void countRedirects() {

        int counter = 0;
        int status = 0;
        String location = "https://playground.learnqa.ru/api/long_redirect";


        while (status != 200) {

            Response response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .get(location)
                    .andReturn();
            status = response.getStatusCode();

            counter++;

            location = response.getHeader("Location");
        }

        System.out.println(counter);

    }

    //Ex8: Токены
    @Test
    public void doTokens() {

        JsonPath parse = RestAssured
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

        String token = parse.get("token");
        Integer estimate = parse.get("seconds");

        Map<String, String> params = new HashMap<>();
        params.put("token", token);

        JsonPath response = RestAssured
                .given()
                .params(params)
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();
        String isStatus = response.get("status");
        String notReadyAlert = "Job is NOT ready";
        String readyAlert = "Job is ready";


        //Проверка валидности статуса о не готовности job
        if (isStatus.equals(notReadyAlert)) {
            System.out.println("Статус 'Job is NOT ready' совпадает");
        } else {
            System.out.println("Статус 'Job is NOT ready' не совпадает");
        }

        //Ожидание окончания времени до завершения job
        try {
            TimeUnit.SECONDS.sleep(estimate + 1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        JsonPath responseFinal = RestAssured
                .given()
                .params(params)
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();
        String isFinalStatus = responseFinal.get("status");
        String result = responseFinal.get("result");

        //Проверяем, что поле status валидное
        if (isFinalStatus.equals(readyAlert)) {
            System.out.println("Статус 'Job is ready' верен");
        } else {
            System.out.println("Ошибка! Статус не 'Job is ready'");
        }

        //Проверяем поле result
        if (result.isEmpty()) {
            System.out.println("Поле result пустое");
        } else {
            System.out.println("Значение в поле result = " + result);
        }
    }

    //Ex9: Подбор пароля
    @Test
    public void passwordHacking() throws IOException {

        String login = "super_admin";
        List<String> lines = Files.readAllLines(Paths.get("src/test/resources/passwrds.txt"), StandardCharsets.UTF_8);
        String isAuthSuccess = "You are authorized";

        for(String line: lines) {
            Map<String, String> data = new HashMap<>();
            data.put("login", login);
            data.put("password", line);

            Response responseAccess = RestAssured
                    .given()
                    .body(data)
                    .when()
                    .post("https://playground.learnqa.ru/ajax/api/get_secret_password_homework")
                    .andReturn();

            Map<String,String> responseCookie = responseAccess.getCookies();

            Response responseCheckCookie = RestAssured
                    .given()
                    .cookies(responseCookie)
                    .when()
                    .get("https://playground.learnqa.ru/ajax/api/check_auth_cookie")
                    .andReturn();

            if (isAuthSuccess.equals(responseCheckCookie.getBody().asString())) {
                System.out.println(line);
                System.out.println(responseCheckCookie.getBody().asString());
                break;
            }
        }
    }
}
