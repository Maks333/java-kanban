package taskmanagerstet;

import managers.InMemoryTaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {


    @Override
    @BeforeEach
    public void beforeEach() {
        super.beforeEach();
        manager = new InMemoryTaskManager();
    }
}
