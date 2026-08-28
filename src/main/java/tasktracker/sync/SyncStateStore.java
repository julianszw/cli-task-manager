package tasktracker.sync;

import com.google.gson.Gson;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class SyncStateStore {

    private final Path file;
    private final Gson gson = new Gson();

    public SyncStateStore(Path file) {
        this.file = file;
    }

    public SyncState load() {
        if (!Files.exists(file)) {
            return new SyncState();
        }
        try {
            String json = Files.readString(file);
            if (json.isBlank()) {
                return new SyncState();
            }
            SyncState state = gson.fromJson(json, SyncState.class);
            return state == null ? new SyncState() : state;
        } catch (IOException | RuntimeException e) {
            return new SyncState();
        }
    }

    public void save(SyncState state) {
        try {
            Files.writeString(file, gson.toJson(state));
        } catch (IOException ignored) {
            // Mejor esfuerzo: si falla, la próxima sincronización recomputa el estado.
        }
    }
}
