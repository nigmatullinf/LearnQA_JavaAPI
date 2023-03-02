package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class ExerciseThreeTest {

    //Ex10: Тест на короткую фразу
    @Test
    public void stringCheck() {
        String string = RandomStringUtils.randomAlphabetic(30);

        assertTrue(string.length() > 15, "string length is less than 15 symbols");
    }

    //Ex11: Тест запроса на метод cookie
    @Test
    public void cookieCheck() {
        Response responseCookie = RestAssured
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();

        String actualCookie = responseCookie.getCookie("HomeWork");
        assertEquals("hw_value", actualCookie, "cookies are NOT equal");
    }

    //Ex12: Тест запроса на метод header
    @Test
    public void headerCheck() {

        Date dateNow = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("E, dd MMM yyyy H:mm:ss z", Locale.UK);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        String expectedHeader = "Date="+ dateFormat.format(dateNow) +"\n"+
                "Content-Type=application/json\n" +
                "Content-Length=15\n" +
                "Connection=keep-alive\n" +
                "Keep-Alive=timeout=10\n" +
                "Server=Apache\n" +
                "x-secret-homework-header=Some secret value\n" +
                "Cache-Control=max-age=0\n" +
                "Expires="+ dateFormat.format(dateNow);

        System.out.println(expectedHeader);

        Response responseHeader = RestAssured
                .get("https://playground.learnqa.ru/api/homework_header")
                .andReturn();
        String actualHeader = responseHeader.getHeaders().toString();
        assertEquals(actualHeader, expectedHeader, "headers are NOT equal");
    }

    //Ex13: UserAgent
    @ParameterizedTest
    @MethodSource("pages")
    public void userAgentTest(String userState,
                              String expectedPlatform,
                              String expectedBrowser,
                              String expectedDevice)
    {
        Map<String, String> userAgent = new HashMap<>();
        userAgent.put("user-agent", userState);


        JsonPath response = RestAssured
                .given()
                .headers(userAgent)
                .get("https://playground.learnqa.ru/ajax/api/user_agent_check")
                .jsonPath();

        String actualPlatform = response.get("platform");
        String actualBrowser = response.get("browser");
        String actualDevice = response.get("device");

        assertEquals(expectedPlatform, actualPlatform, "Unexpected response in user agent: "+ userState + ". Key 'platform'" );
        assertEquals(expectedBrowser, actualBrowser, "Wrong user agent response: " + userState + ". Key 'browser'");
        assertEquals(expectedDevice, actualDevice, "Unexpected user agent response in user agent: "+ userState + ". Key 'device'");

    }

    static Stream<Arguments> pages() {
        return Stream.of(
                arguments(
                        "Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30",
                        "Mobile", "No","Android"),
                arguments(
                        "Mozilla/5.0 (iPad; CPU OS 13_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/91.0.4472.77 Mobile/15E148 Safari/604.1",
                        "Mobile", "Chrome", "iOS"),
                arguments(
                        "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)",
                        "Googlebot", "Unknown", "Unknown"),
                arguments(
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36 Edg/91.0.100.0",
                        "Web", "Chrome", "No"),
                arguments(
                        "Mozilla/5.0 (iPad; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1",
                        "Mobile", "No", "iPhone")
                );}
}
