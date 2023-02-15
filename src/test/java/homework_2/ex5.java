package homework_2;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;

public class ex5 {
    @Test
    public void getMessage() {

        JsonPath response = RestAssured
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();


        String message = response.get("messages[1].message");
        System.out.println(message);
    }
}
