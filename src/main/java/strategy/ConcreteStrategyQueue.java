package strategy;

import simulationmodel.Server;
import simulationmodel.Task;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
/**
 * Clasa care reprezinta o strategie, implementand functionalitatea {@code Strategy}
 * si care adauga un task in functie de cel mai scurt server = serverul cu cele mai putine taskuri
 */
public class ConcreteStrategyQueue implements Strategy {
    @Override
    public void addTask(List<Server> servers, Task t) {
        Server server = Collections.min(servers, Comparator.comparingInt(o -> o.getTasks().size()));
        server.addTask(t);
    }
}
