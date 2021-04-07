package simulationmodel;

import strategy.ConcreteStrategyQueue;
import strategy.ConcreteStrategyTime;
import strategy.enums.SelectionPolicy;
import strategy.Strategy;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Această clasă asigură planificarea serverelor dar și împărțirea taskurilor în funcție de strategia aleasă
 */
public class Scheduler {
    /**
     * Lista de Severe
     * @see Server
     */
    private List<Server> servers;
    /**
     * Stategia cu care face dispatch
     * @see Strategy
     */
    private Strategy strategy;
    /**
     * Lista de thread uri
     */
    private List<Thread> threads;

    //implementare viitoare
    private int maxNoServers;
    private int maxTasksPerServer;

    public Scheduler(int maxNoServers, int maxTasksPerServer) {
        this.maxNoServers = maxNoServers;
        this.maxTasksPerServer = maxTasksPerServer;

        servers = new CopyOnWriteArrayList<>();
        threads = new CopyOnWriteArrayList<>();

        for (int i = 0; i < maxNoServers; i++) {
            servers.add(new Server());
            threads.add(new Thread(servers.get(i)));
            threads.get(i).start();
        }
    }

    /**
     * Schimbă implementarea strategiei
     * @param policy in functie de el se alege strategia de dispatch
     */
    public void changeStrategy(SelectionPolicy policy) {
        if (policy == SelectionPolicy.SHORTEST_QUEUE) {
            strategy = new ConcreteStrategyQueue();
        } else if (policy == SelectionPolicy.SHORTEST_TIME) {
            strategy = new ConcreteStrategyTime();
        }
    }

    /**
     * Adaugă taskul care trebuie procesat către servereul ales după implementarea interfeței {@code Strategy}
     * @param t taskul care trebuie adaugat la servere
     */
    public void dispatchTask(Task t) {
        strategy.addTask(servers, t);
    }

    /**
     * Aceasta executia intrerupe execuția serverelor
     */
    public void interruptThreads() {
        for(var thread: threads)
            thread.interrupt();
    }

    public List<Server> getServers() {
        return servers;
    }

    public boolean isServerOccupied(int i) {
        return servers.get(i).getTasks().size() != 0;
    }

}
