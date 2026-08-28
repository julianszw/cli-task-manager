package tasktracker.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import tasktracker.model.Task;
import tasktracker.model.TaskList;
import tasktracker.model.TaskStatus;

class JsonStoreCodecTest {

    @Test
    void encodeProducesDocumentWithListsAndTasks() {
        TaskList inbox = new TaskList("Inbox");
        inbox.setId(1);
        Task task = new Task("Comprar leche");
        task.setId(7);
        task.setListId(1);

        String json = JsonStoreCodec.encode(List.of(inbox), List.of(task), 0);

        assertEquals("{\"lists\":[{\"id\":1,\"name\":\"Inbox\"}],"
                + "\"tasks\":[{\"id\":7,\"title\":\"Comprar leche\",\"status\":\"PENDING\",\"listId\":1}],"
                + "\"zoom\":0}", json);
    }

    @Test
    void encodeEscapesSpecialCharacters() {
        TaskList list = new TaskList("Mi \"lista\"\n");
        list.setId(1);
        Task task = new Task("Dijo \"hola\" y\nadiós \\");
        task.setId(1);
        task.setListId(1);

        String json = JsonStoreCodec.encode(List.of(list), List.of(task), 0);

        assertEquals("{\"lists\":[{\"id\":1,\"name\":\"Mi \\\"lista\\\"\\n\"}],"
                + "\"tasks\":[{\"id\":1,\"title\":\"Dijo \\\"hola\\\" y\\nadiós \\\\\","
                + "\"status\":\"PENDING\",\"listId\":1}],\"zoom\":0}", json);
    }

    @Test
    void decodeParsesListsAndTasks() {
        String json = "{\"lists\":[{\"id\":1,\"name\":\"Inbox\"},{\"id\":2,\"name\":\"Trabajo\"}],"
                + "\"tasks\":[{\"id\":1,\"title\":\"A\",\"status\":\"PENDING\",\"listId\":1},"
                + "{\"id\":2,\"title\":\"B\",\"status\":\"COMPLETED\",\"listId\":2}]}";

        JsonStoreCodec.Store store = JsonStoreCodec.decode(json);

        assertEquals(2, store.lists().size());
        assertEquals("Inbox", store.lists().get(0).getName());
        assertEquals(1, store.lists().get(0).getId());
        assertEquals(2, store.tasks().size());
        assertEquals(1, store.tasks().get(0).getId());
        assertEquals("A", store.tasks().get(0).getTitle());
        assertEquals(TaskStatus.PENDING, store.tasks().get(0).getStatus());
        assertEquals(1, store.tasks().get(0).getListId());
        assertEquals(TaskStatus.COMPLETED, store.tasks().get(1).getStatus());
        assertEquals(2, store.tasks().get(1).getListId());
    }

    @Test
    void decodeEmptyObjectReturnsEmptyLists() {
        JsonStoreCodec.Store store = JsonStoreCodec.decode("{}");

        assertTrue(store.lists().isEmpty());
        assertTrue(store.tasks().isEmpty());
    }

    @Test
    void decodeEmptyArraysReturnsEmptyLists() {
        JsonStoreCodec.Store store = JsonStoreCodec.decode("{\"lists\":[],\"tasks\":[]}");

        assertTrue(store.lists().isEmpty());
        assertTrue(store.tasks().isEmpty());
    }

    @Test
    void encodeIncludesZoomLevel() {
        String json = JsonStoreCodec.encode(List.of(), List.of(), 2);

        assertEquals("{\"lists\":[],\"tasks\":[],\"zoom\":2}", json);
    }

    @Test
    void decodeParsesZoomLevel() {
        JsonStoreCodec.Store store = JsonStoreCodec.decode("{\"lists\":[],\"tasks\":[],\"zoom\":-2}");

        assertEquals(-2, store.zoom());
    }

    @Test
    void decodeDefaultsZoomToZeroWhenAbsent() {
        JsonStoreCodec.Store store = JsonStoreCodec.decode("{\"lists\":[],\"tasks\":[]}");

        assertEquals(0, store.zoom());
    }

    @Test
    void decodeLegacyFlatArrayMigratesToInbox() {
        String json = "[{\"id\":3,\"title\":\"A\",\"status\":\"PENDING\"},"
                + "{\"id\":7,\"title\":\"B\",\"status\":\"COMPLETED\"}]";

        JsonStoreCodec.Store store = JsonStoreCodec.decode(json);

        assertEquals(1, store.lists().size());
        assertEquals("Inbox", store.lists().get(0).getName());
        assertEquals(1, store.lists().get(0).getId());
        assertEquals(2, store.tasks().size());
        assertEquals(1, store.tasks().get(0).getListId());
        assertEquals(1, store.tasks().get(1).getListId());
        assertEquals(TaskStatus.COMPLETED, store.tasks().get(1).getStatus());
    }

    @Test
    void roundTripPreservesListsAndTasks() {
        TaskList inbox = new TaskList("Inbox");
        inbox.setId(1);
        TaskList work = new TaskList("Trabajo");
        work.setId(2);
        Task first = new Task("Tarea con \"comillas\" y \\ barra");
        first.setId(10);
        first.setListId(2);
        Task second = new Task("Segunda");
        second.setId(42);
        second.setListId(1);
        second.markCompleted();

        String json = JsonStoreCodec.encode(List.of(inbox, work), List.of(first, second), 0);
        JsonStoreCodec.Store decoded = JsonStoreCodec.decode(json);

        assertEquals(2, decoded.lists().size());
        assertEquals("Inbox", decoded.lists().get(0).getName());
        assertEquals("Trabajo", decoded.lists().get(1).getName());
        assertEquals(2, decoded.tasks().size());
        assertEquals(10, decoded.tasks().get(0).getId());
        assertEquals("Tarea con \"comillas\" y \\ barra", decoded.tasks().get(0).getTitle());
        assertEquals(2, decoded.tasks().get(0).getListId());
        assertEquals(TaskStatus.COMPLETED, decoded.tasks().get(1).getStatus());
    }

    @Test
    void decodeRejectsMalformedJson() {
        assertThrows(JsonParseException.class, () -> JsonStoreCodec.decode("{no es json"));
    }

    @Test
    void decodeRejectsNotObjectOrArray() {
        assertThrows(JsonParseException.class, () -> JsonStoreCodec.decode("\"hola\""));
    }

    @Test
    void decodeRejectsTaskMissingListId() {
        assertThrows(JsonParseException.class,
                () -> JsonStoreCodec.decode("{\"lists\":[],\"tasks\":[{\"id\":1,\"title\":\"A\",\"status\":\"PENDING\"}]}"));
    }

    @Test
    void decodeRejectsListMissingField() {
        assertThrows(JsonParseException.class,
                () -> JsonStoreCodec.decode("{\"lists\":[{\"id\":1}],\"tasks\":[]}"));
    }

    @Test
    void decodeRejectsUnknownStatus() {
        assertThrows(JsonParseException.class,
                () -> JsonStoreCodec.decode("{\"lists\":[],\"tasks\":[{\"id\":1,\"title\":\"A\",\"status\":\"DONE\",\"listId\":1}]}"));
    }

    @Test
    void decodeRejectsUnknownTopLevelField() {
        assertThrows(JsonParseException.class,
                () -> JsonStoreCodec.decode("{\"foo\":[]}"));
    }

    @Test
    void decodeRejectsTrailingContent() {
        assertThrows(JsonParseException.class,
                () -> JsonStoreCodec.decode("{\"lists\":[],\"tasks\":[]} extra"));
    }

    @Test
    void roundTripPreservesSyncFields() {
        TaskList inbox = new TaskList("Inbox");
        inbox.setId(1);
        inbox.setRemoteId("list-remote-1");
        inbox.setUpdatedAt(1234L);
        Task task = new Task("Comprar leche");
        task.setId(7);
        task.setListId(1);
        task.setRemoteId("task-remote-7");
        task.setUpdatedAt(5678L);

        String json = JsonStoreCodec.encode(List.of(inbox), List.of(task), 0);
        JsonStoreCodec.Store decoded = JsonStoreCodec.decode(json);

        assertEquals("list-remote-1", decoded.lists().get(0).getRemoteId());
        assertEquals(1234L, decoded.lists().get(0).getUpdatedAt());
        assertEquals("task-remote-7", decoded.tasks().get(0).getRemoteId());
        assertEquals(5678L, decoded.tasks().get(0).getUpdatedAt());
    }

    @Test
    void decodeEntitiesWithoutSyncFieldsLeaveThemEmpty() {
        String json = "{\"lists\":[{\"id\":1,\"name\":\"Inbox\"}],"
                + "\"tasks\":[{\"id\":1,\"title\":\"A\",\"status\":\"PENDING\",\"listId\":1}]}";

        JsonStoreCodec.Store store = JsonStoreCodec.decode(json);

        assertEquals(null, store.lists().get(0).getRemoteId());
        assertEquals(0L, store.lists().get(0).getUpdatedAt());
        assertEquals(null, store.tasks().get(0).getRemoteId());
        assertEquals(0L, store.tasks().get(0).getUpdatedAt());
    }
}
