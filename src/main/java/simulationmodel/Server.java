package simulationmodel;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Clasa care implementează interfața Runnable și care are o coda de taskuri și diferți parametrii definiți sugestiv
 * care vor facilita comunicarea între threaduri și care vor ajuta la calcularea rezultatelor de la finalul simulării
 */
public class Server implements Runnable {
    /**
     * Coada de taskuri care trebuie procesate
     */
    private BlockingQueue<Task> tasks;
    /**
     * Valori ajutatoare care vor contribui la rezultatele simularii
     */
    private AtomicInteger waitingPeriod;
    private AtomicInteger waitingTimeServer;
    private AtomicInteger serviceTimeServer;
    private AtomicInteger noOfTasksResolved;


    public Server() {
        waitingTimeServer = new AtomicInteger(0);
        waitingPeriod = new AtomicInteger(0);
        noOfTasksResolved = new AtomicInteger(0);
        serviceTimeServer = new AtomicInteger(0);
        tasks = new LinkedBlockingQueue<>();
    }

    /**
     * Pune in coda taskul primit ca parametru si-i fixeaza timpul de finalizare!
     * @param newTask - taskul de adaugat in coada
     */
    public void addTask(Task newTask) {
        newTask.setFinishTime(newTask.getArrivalTime() + newTask.getProcessingTime() + waitingPeriod.get());
        tasks.add(newTask);
        waitingPeriod.addAndGet(newTask.getProcessingTime());
        waitingTimeServer.addAndGet(newTask.getFinishTime() - newTask.getArrivalTime() - newTask.getProcessingTime());
        serviceTimeServer.addAndGet(newTask.getFinishTime() - newTask.getArrivalTime());
    }

    @Override
    public void run() {
        while (true) {
            Task t = tasks.peek();

            if(tasks.size() != 0) {
                waitingPeriod.addAndGet(-1);
                if(t.getProcessingTime() - 1 > 0) {
                    t.setProcessingTime(t.getProcessingTime() - 1);
                } else {
                    //adun timpul asteptat de orice task rezolvat!!
                    noOfTasksResolved.addAndGet(1); //numarul de taskuri rezolvate !!
                    tasks.remove(t);
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return;
                //e.printStackTrace();
            }
        }
    }

    public List<Task> getTasks() {
        return new CopyOnWriteArrayList<>(tasks);
    }

    public AtomicInteger getWaitingPeriod() {
        return waitingPeriod;
    }

    public AtomicInteger getWaitingTimeServer() {
        return waitingTimeServer;
    }

    public AtomicInteger getNoOfTasksResolved() {
        return noOfTasksResolved;
    }

    public AtomicInteger getServiceTimeServer() {
        return serviceTimeServer;
    }
}
