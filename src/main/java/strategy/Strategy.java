package strategy;

import simulationmodel.Server;
import simulationmodel.Task;

import java.util.List;

/**
 *Functionalitatea pe care trebuie s-o aiba orice clasa care implementeaza o strategie
 */

public interface Strategy {
    void addTask(List<Server> servers, Task t);
}
