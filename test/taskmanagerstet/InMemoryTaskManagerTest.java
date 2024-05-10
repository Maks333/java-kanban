package taskmanagerstet;

import managers.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    @BeforeEach
    public void beforeEach() {
        super.beforeEach();
        manager = new InMemoryTaskManager();
    }
}
