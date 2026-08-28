package tasktracker.sync;

import java.util.HashSet;
import java.util.Set;

public final class SyncState {

    private long lastSync;
    private Set<String> taskIds = new HashSet<>();

    public SyncState() {
    }

    public long getLastSync() {
        return lastSync;
    }

    public void setLastSync(long lastSync) {
        this.lastSync = lastSync;
    }

    public Set<String> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(Set<String> taskIds) {
        this.taskIds = taskIds == null ? new HashSet<>() : taskIds;
    }
}
