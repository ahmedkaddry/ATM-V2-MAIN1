package ports;

import domain.Movement;
import java.util.List;

public interface MovementPort {
    void save(Movement movement);
    List<Movement> findAll();
}
