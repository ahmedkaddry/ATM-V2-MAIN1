package ports;

import domain.Client;
import java.util.List;

public interface ClientPort {
    Client findByUsername(String username);
    void update(Client client);
    List<Client> findAll();
}
