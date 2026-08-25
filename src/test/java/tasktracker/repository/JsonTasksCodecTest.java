package tasktracker.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import tasktracker.model.Task;
import tasktracker.model.TaskStatus;

class JsonTasksCodecTest {

    @Test
    void encodeProducesJsonArrayWithIdTitleAndStatus() {
        Task task = new Task("Comprar leche");
        task.setId(7);

        String json = JsonTasksCodec.encode(List.of(task));

        assertEquals("[{\"id\":7,\"title\":\"Comprar leche\",\"status\":\"PENDING\"}]", json);
    }

    @Test
    void encodeEscapesSpecialCharacters() {
        Task task = new Task("Dijo \"hola\" y\nadiós \\");
        task.setId(1);

        String json = JsonTasksCodec.encode(List.of(task));

        assertEquals("[{\"id\":1,\"title\":\"Dijo \\\"hola\\\" y\\nadiós \\\\\",\"status\":\"PENDING\"}]", json);
    }

    @Test
    void decodeParsesValidTasks() {
        String json = "[{\"id\":1,\"title\":\"A\",\"status\":\"PENDING\"},"
                + "{\"id\":2,\"title\":\"B\",\"status\":\"COMPLETED\"}]";

        List<Task> tasks = JsonTasksCodec.decode(json);

        assertEquals(2, tasks.size());
        assertEquals(1, tasks.get(0).getId());
        assertEquals("A", tasks.get(0).getTitle());
        assertEquals(TaskStatus.PENDING, tasks.get(0).getStatus());
        assertEquals(2, tasks.get(1).getId());
        assertEquals(TaskStatus.COMPLETED, tasks.get(1).getStatus());
    }

    @Test
    void decodeEmptyArrayReturnsEmptyList() {
        assertTrue(JsonTasksCodec.decode("[]").isEmpty());
    }

    @Test
    void decodeHandlesWhitespaceAndEscapes() {
        String json = "[ { \"id\" : 3 , \"title\" : \"Linea\\n2\" , \"status\" : \"COMPLETED\" } ]";

        List<Task> tasks = JsonTasksCodec.decode(json);

        assertEquals(1, tasks.size());
        assertEquals(3, tasks.get(0).getId());
        assertEquals("Linea\n2", tasks.get(0).getTitle());
        assertEquals(TaskStatus.COMPLETED, tasks.get(0).getStatus());
    }

    @Test
    void roundTripPreservesTasks() {
        Task first = new Task("Tarea con \"comillas\" y \\ barra");
        first.setId(10);
        Task second = new Task("Segunda");
        second.setId(42);
        second.markCompleted();

        List<Task> decoded = JsonTasksCodec.decode(JsonTasksCodec.encode(List.of(first, second)));

        assertEquals(2, decoded.size());
        assertEquals(10, decoded.get(0).getId());
        assertEquals("Tarea con \"comillas\" y \\ barra", decoded.get(0).getTitle());
        assertEquals(TaskStatus.PENDING, decoded.get(0).getStatus());
        assertEquals(42, decoded.get(1).getId());
        assertEquals(TaskStatus.COMPLETED, decoded.get(1).getStatus());
    }

    @Test
    void decodeRejectsMalformedJson() {
        assertThrows(JsonParseException.class, () -> JsonTasksCodec.decode("{no es json"));
    }

    @Test
    void decodeRejectsMissingField() {
        assertThrows(JsonParseException.class,
                () -> JsonTasksCodec.decode("[{\"id\":1,\"title\":\"A\"}]"));
    }

    @Test
    void decodeRejectsUnknownStatus() {
        assertThrows(JsonParseException.class,
                () -> JsonTasksCodec.decode("[{\"id\":1,\"title\":\"A\",\"status\":\"DONE\"}]"));
    }

    @Test
    void decodeRejectsTrailingContent() {
        assertThrows(JsonParseException.class,
                () -> JsonTasksCodec.decode("[{\"id\":1,\"title\":\"A\",\"status\":\"PENDING\"}] extra"));
    }

    @Test
    void decodeRejectsNotArray() {
        assertThrows(JsonParseException.class, () -> JsonTasksCodec.decode("{\"id\":1}"));
    }
}
