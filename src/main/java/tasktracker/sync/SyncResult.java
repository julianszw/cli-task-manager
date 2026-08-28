package tasktracker.sync;

public record SyncResult(
        int createdLocal,
        int updatedLocal,
        int deletedLocal,
        int createdRemote,
        int updatedRemote,
        int deletedRemote) {

    public boolean isEmpty() {
        return createdLocal == 0 && updatedLocal == 0 && deletedLocal == 0
                && createdRemote == 0 && updatedRemote == 0 && deletedRemote == 0;
    }
}
