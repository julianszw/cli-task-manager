package tasktracker.repository;

import java.util.ArrayList;
import java.util.List;
import tasktracker.model.Task;
import tasktracker.model.TaskList;
import tasktracker.model.TaskStatus;

final class JsonStoreCodec {

    private static final String DEFAULT_LIST_NAME = "Inbox";

    record Store(List<TaskList> lists, List<Task> tasks) {
    }

    private JsonStoreCodec() {
    }

    static String encode(List<TaskList> lists, List<Task> tasks) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"lists\":[");
        for (int i = 0; i < lists.size(); i++) {
            if (i > 0) {
                sb.append(',');
            }
            TaskList list = lists.get(i);
            sb.append("{\"id\":").append(list.getId());
            sb.append(",\"name\":").append(quote(list.getName()));
            sb.append('}');
        }
        sb.append("],\"tasks\":[");
        for (int i = 0; i < tasks.size(); i++) {
            if (i > 0) {
                sb.append(',');
            }
            Task task = tasks.get(i);
            sb.append("{\"id\":").append(task.getId());
            sb.append(",\"title\":").append(quote(task.getTitle()));
            sb.append(",\"status\":\"").append(task.getStatus().name()).append('"');
            sb.append(",\"listId\":").append(task.getListId());
            sb.append('}');
        }
        sb.append("]}");
        return sb.toString();
    }

    static Store decode(String input) {
        Parser parser = new Parser(input);
        parser.skipWhitespace();
        if (parser.peek() == '[') {
            return decodeLegacy(parser);
        }
        return decodeObject(parser);
    }

    private static Store decodeObject(Parser parser) {
        List<TaskList> lists = new ArrayList<>();
        List<Task> tasks = new ArrayList<>();
        parser.expect('{');
        parser.skipWhitespace();
        if (parser.peek() == '}') {
            parser.next();
            parser.skipWhitespace();
            parser.requireEnd();
            return new Store(lists, tasks);
        }
        while (true) {
            parser.skipWhitespace();
            String key = parser.parseString();
            parser.expect(':');
            parser.skipWhitespace();
            switch (key) {
                case "lists" -> lists.addAll(parser.parseLists());
                case "tasks" -> tasks.addAll(parser.parseTasks(null));
                default -> throw new JsonParseException("Campo desconocido: " + key);
            }
            parser.skipWhitespace();
            char c = parser.next();
            if (c == '}') {
                break;
            }
            if (c != ',') {
                throw new JsonParseException("Se esperaba ',' o '}'");
            }
        }
        parser.skipWhitespace();
        parser.requireEnd();
        return new Store(lists, tasks);
    }

    private static Store decodeLegacy(Parser parser) {
        List<Task> tasks = parser.parseTasks(1L);
        TaskList inbox = new TaskList(DEFAULT_LIST_NAME);
        inbox.setId(1);
        return new Store(List.of(inbox), tasks);
    }

    private static String quote(String value) {
        StringBuilder sb = new StringBuilder(value.length() + 2);
        sb.append('"');
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                case '\b' -> sb.append("\\b");
                case '\f' -> sb.append("\\f");
                default -> {
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        sb.append('"');
        return sb.toString();
    }

    private static final class Parser {

        private final String input;
        private int pos;

        Parser(String input) {
            this.input = input;
        }

        void skipWhitespace() {
            while (pos < input.length() && Character.isWhitespace(input.charAt(pos))) {
                pos++;
            }
        }

        void expect(char expected) {
            skipWhitespace();
            if (pos >= input.length() || input.charAt(pos) != expected) {
                throw new JsonParseException("Se esperaba '" + expected + "'");
            }
            pos++;
        }

        char peek() {
            if (pos >= input.length()) {
                throw new JsonParseException("Fin de entrada inesperado");
            }
            return input.charAt(pos);
        }

        char next() {
            if (pos >= input.length()) {
                throw new JsonParseException("Fin de entrada inesperado");
            }
            return input.charAt(pos++);
        }

        void requireEnd() {
            if (pos < input.length()) {
                throw new JsonParseException("Contenido inesperado tras el documento");
            }
        }

        List<Task> parseTasks(Long defaultListId) {
            List<Task> tasks = new ArrayList<>();
            expect('[');
            skipWhitespace();
            if (peek() == ']') {
                next();
                skipWhitespace();
                return tasks;
            }
            while (true) {
                tasks.add(parseTask(defaultListId));
                skipWhitespace();
                char c = next();
                if (c == ']') {
                    break;
                }
                if (c != ',') {
                    throw new JsonParseException("Se esperaba ',' o ']'");
                }
            }
            skipWhitespace();
            return tasks;
        }

        List<TaskList> parseLists() {
            List<TaskList> lists = new ArrayList<>();
            expect('[');
            skipWhitespace();
            if (peek() == ']') {
                next();
                skipWhitespace();
                return lists;
            }
            while (true) {
                lists.add(parseList());
                skipWhitespace();
                char c = next();
                if (c == ']') {
                    break;
                }
                if (c != ',') {
                    throw new JsonParseException("Se esperaba ',' o ']'");
                }
            }
            skipWhitespace();
            return lists;
        }

        Task parseTask(Long defaultListId) {
            expect('{');
            skipWhitespace();
            Long id = null;
            String title = null;
            TaskStatus status = null;
            Long listId = null;
            if (peek() == '}') {
                next();
            } else {
                while (true) {
                    skipWhitespace();
                    String key = parseString();
                    expect(':');
                    skipWhitespace();
                    switch (key) {
                        case "id" -> id = parseLong();
                        case "title" -> title = parseString();
                        case "status" -> status = parseStatus();
                        case "listId" -> listId = parseLong();
                        default -> throw new JsonParseException("Campo desconocido: " + key);
                    }
                    skipWhitespace();
                    char c = next();
                    if (c == '}') {
                        break;
                    }
                    if (c != ',') {
                        throw new JsonParseException("Se esperaba ',' o '}'");
                    }
                }
            }
            if (listId == null && defaultListId != null) {
                listId = defaultListId;
            }
            if (id == null || title == null || status == null || listId == null) {
                throw new JsonParseException("La tarea requiere los campos id, title, status y listId");
            }
            Task task = new Task(title);
            task.setId(id);
            task.setListId(listId);
            if (status == TaskStatus.COMPLETED) {
                task.markCompleted();
            }
            return task;
        }

        TaskList parseList() {
            expect('{');
            skipWhitespace();
            Long id = null;
            String name = null;
            if (peek() == '}') {
                next();
            } else {
                while (true) {
                    skipWhitespace();
                    String key = parseString();
                    expect(':');
                    skipWhitespace();
                    switch (key) {
                        case "id" -> id = parseLong();
                        case "name" -> name = parseString();
                        default -> throw new JsonParseException("Campo desconocido: " + key);
                    }
                    skipWhitespace();
                    char c = next();
                    if (c == '}') {
                        break;
                    }
                    if (c != ',') {
                        throw new JsonParseException("Se esperaba ',' o '}'");
                    }
                }
            }
            if (id == null || name == null) {
                throw new JsonParseException("La lista requiere los campos id y name");
            }
            TaskList list = new TaskList(name);
            list.setId(id);
            return list;
        }

        String parseString() {
            expect('"');
            StringBuilder sb = new StringBuilder();
            while (true) {
                char c = next();
                if (c == '"') {
                    break;
                }
                if (c == '\\') {
                    char esc = next();
                    switch (esc) {
                        case '"' -> sb.append('"');
                        case '\\' -> sb.append('\\');
                        case '/' -> sb.append('/');
                        case 'b' -> sb.append('\b');
                        case 'f' -> sb.append('\f');
                        case 'n' -> sb.append('\n');
                        case 'r' -> sb.append('\r');
                        case 't' -> sb.append('\t');
                        case 'u' -> sb.append(parseUnicodeEscape());
                        default -> throw new JsonParseException("Secuencia de escape inválida: \\" + esc);
                    }
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }

        TaskStatus parseStatus() {
            String value = parseString();
            try {
                return TaskStatus.valueOf(value);
            } catch (IllegalArgumentException e) {
                throw new JsonParseException("Estado desconocido: " + value);
            }
        }

        long parseLong() {
            int start = pos;
            if (peek() == '-') {
                next();
            }
            if (pos >= input.length() || !Character.isDigit(peek())) {
                throw new JsonParseException("Se esperaba un número");
            }
            while (pos < input.length() && Character.isDigit(peek())) {
                next();
            }
            String number = input.substring(start, pos);
            try {
                return Long.parseLong(number);
            } catch (NumberFormatException e) {
                throw new JsonParseException("Número fuera de rango: " + number);
            }
        }

        char parseUnicodeEscape() {
            int value = 0;
            for (int i = 0; i < 4; i++) {
                char c = next();
                int digit = Character.digit(c, 16);
                if (digit < 0) {
                    throw new JsonParseException("Escape unicode inválido");
                }
                value = value * 16 + digit;
            }
            return (char) value;
        }
    }
}
