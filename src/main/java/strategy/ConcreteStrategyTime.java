package strategy;

import simulationmodel.Server;
import simulationmodel.Task;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Clasa care reprezinta o strategie, implementand functionalitatea {@code Strategy}
 * si care adauga un task in functie de timpul cel mai mic de asteptare din fiecare server
 */
public class ConcreteStrategyTime implements Strategy {
    @Override
    public void addTask(List<Server> servers, Task t) {
        Server server = Collections.min(servers, Comparator.comparingInt(o -> o.getWaitingPeriod().get()));
        server.addTask(t);
    }
}
