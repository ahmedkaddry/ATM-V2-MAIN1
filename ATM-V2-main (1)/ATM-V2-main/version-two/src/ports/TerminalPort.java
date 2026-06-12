package ports;

import domain.Terminal;

public interface TerminalPort {
    Terminal loadTerminal();
    void saveTerminal(Terminal terminal);
}
