package org.testar.settings.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Pair;
import org.testar.monkey.RuntimeControlsProtocol;
import org.testar.monkey.alayer.Tag;
import org.testar.settings.Settings;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import static io.restassured.RestAssured.*;


public class TestApi {
    private static BackendMain backend;
    private static File settingsFile;

    @ClassRule
    public static TemporaryFolder sseFolder = new TemporaryFolder();

    @Before
    public void resetSettingsFile() throws IOException {
        System.out.println("Resetting settings file");
        if (settingsFile != null && settingsFile.delete()) {
            settingsFile = sseFolder.newFile("test.settings");
        } else {
            Assert.fail("Could not delete existing settings file");
        }
    }

    @BeforeClass
    public static void setup() throws IOException {
        // Set the base URI for REST-assured
        baseURI = "http://localhost:7070";
        port = 7070;
        backend = new BackendMain(false);
        settingsFile = sseFolder.newFile("test.settings");

        // Temporary fix to resolve different working directory for testing.
        // These should be refactored in the future to avoid hardcoding paths.
        // these fields should be final otherwise
        backend.SSE_FOLDER = Path.of("./target/scripts/settings");
        SettingsPerMode.JSON_PATH = Path.of("./target/scripts/settings/settings-per-mode.json");

        if (backend == null) {
            Assert.fail("Failed to initialize BackendMain");
        }
        System.out.println("Starting API tests with protocol settings folder: " + sseFolder.getRoot());

        String jsonPath = new ObjectMapper().writeValueAsString(settingsFile.getAbsolutePath());
        System.out.println(backend.SSE_FOLDER);
        given()
                .contentType("application/json")
                .body(jsonPath)
                .when()
                .post("/api/conf/protocol").then()
                .log().ifError()
                .statusCode(200);
    }

    @Test
    public void testConfGetEndpoint() throws IOException {

        Settings settings = Settings.loadSettings(new String[]{}, settingsFile.getAbsolutePath());

        Response response = given().when()
                .get("/api/conf").then()
                .log().ifError()
                .statusCode(200).extract().response();

        JsonPath responseJson = response.jsonPath();

        for (Tag<?> tag : settings.tags()) {
            Object expected = settings.get(tag);
            Object actual = Settings.parse(Settings.print(tag, (responseJson.get(tag.name()))), tag);

            assert expected.equals(actual) : "Expected: <" + expected + ">[" + expected.getClass() + "] but got " +
                    "<" + actual + ">[" + actual.getClass() + "] for tag: " + tag.name();
        }
    }

    @Test
    public void testConfPostEndpoint() throws IOException {
        List<Pair<?, ?>> expectedTags = Arrays.asList(
                Pair.from(ConfigTags.Mode, RuntimeControlsProtocol.Modes.Replay),
                Pair.from(ConfigTags.Sequences, 777),
                Pair.from(ConfigTags.TimeToWaitAfterAction, 77.77),
                Pair.from(ConfigTags.FollowLinks, false),
                Pair.from(ConfigTags.WebIgnoredTags, Arrays.asList("test1", "test2", "test3"))
        );

        Map<String, Object> payload = new HashMap<>();
        for (Pair<?, ?> pair : expectedTags) {
            payload.put(pair.left().toString(), pair.right());
        }

        given().header("Content-Type", "application/json")
                .body(payload).when()
                .post("/api/conf").then()
                .log().ifError()
                .statusCode(200);

        Settings actualSettings = Settings.loadSettings(new String[]{}, settingsFile.getAbsolutePath());
        Settings expectedSettings = new Settings(expectedTags, new Properties());

        for (Tag<?> tag : expectedSettings.tags()) {
            Object expected = expectedSettings.get(tag);
            Object actual = actualSettings.get(tag);
            assert expected.equals(actual) : "Expected: <" + expected + ">[" + expected.getClass() + "] but got " +
                    "<" + actual + ">[" + actual.getClass() + "] for tag: " + tag.name();
        }

    }

    @Test
    public void testModeAvailable() {
        List<String> MODES = Arrays.asList("generate", "replay", "spy", "view", "analysismanager");

        for (String mode : MODES) {
            Response response = given()
                    .contentType("application/json")
                    .when()
                    .get("/api/conf/{mode}/available", mode).then()
                    .log().ifError()
                    .statusCode(200)
                    .extract().response();

            JsonPath responseJson = response.jsonPath();
            List<String> actual = responseJson.getList("");

            try {
                Optional<List<String>> expected = SettingsPerMode.getSettingsForMode(mode);
                if (expected.isPresent()) {
                    for (String item : expected.get()) {
                        Assert.assertTrue(actual.contains(item));
                    }
                }
            } catch (IOException e) {
                Assert.fail("Failed to read settings-per-mode.json: " + e.getMessage());
            }
        }
    }

    @Test
    public void testProtocolAvailable() {
        Response response = given()
                .contentType("application/json")
                .when()
                .get("/api/conf/protocol/available").then()
                .log().ifError()
                .statusCode(200)
                .extract().response();

        JsonPath responseJson = response.jsonPath();
        Set<String> expected = new HashSet<>();
        Map<String, String> actual = responseJson.getMap("");
        Path sse_folder = Path.of("./target/scripts/settings");

        try (Stream<Path> expectedDir = Files.list(sse_folder)) {
            expectedDir.forEach(path -> {
                File expectedFile = path.toFile();
                if (expectedFile.isDirectory()) {
                    expected.add(expectedFile.getName());
                }
            });
            Assert.assertEquals(expected, actual.keySet());
        } catch (IOException e) {
            Assert.fail("Failed to read settings folder: " + e.getMessage());
        }

    }

    @AfterClass
    public static void cleanup() {
        System.out.println("Testing completed. Cleaning up...");
        if (backend != null) {
            backend.stop();
        }
    }
}
