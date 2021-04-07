package simulationmodel;

import file.WriteInFile;
import strategy.enums.SelectionPolicy;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *Această clasă este cea care se va ocupa cu lansarea simulării și cu comunicarea cu interfața grafică.
 * Pe lângă caracteristicile de simulare și rezultatele simulării, apar și alte varibile instanța care
 * sunt descrise mai jos.
 */
public class SimulationManager implements Runnable {
    /**
     * Pentru semnalarea altor threaduri
     * <p>finished - s-a terminat simularea</p>
     * <p>currentTime - timpul curent al simularii</p>
     */
    AtomicInteger finished;
    AtomicInteger currentTime;
    /**
     * Caracteristicile de simulare citite din UI
     */
    private int timeLimit = 30;
    private int minProcessingTime = 4;
    private int maxProcessingTime = 7;
    private int minArrivalTime = 2;
    private int maxArrivalTime = 30;
    private int numberOfClients = 20;
    public SelectionPolicy selectionPolicy = SelectionPolicy.SHORTEST_QUEUE;
    /**
     * Entitate responsabila cu managementul cozii si distribuirea clientilor
     * @see Scheduler
     */
    private Scheduler scheduler;
    /**
     * Lista cu taskurile generate random
     */
    private List<Task> generatedTasks;
    /**
     * Rezultatele simularii
     */
    private int peakHourTime, peakHourTasks;
    private boolean isProcessing;
    private float averageWaitingTime;
    private float averageServiceTime;
    private String writeToFile = "";

    public SimulationManager(int timeLimit, int minProcessingTimp, int maxProcessingTime, int minArrivalTime, int maxArrivalTime, int numberOfServers, int numberOfClients, SelectionPolicy selectionPolicy) {
        this.timeLimit = timeLimit;
        this.minProcessingTime = minProcessingTimp;
        this.maxProcessingTime = maxProcessingTime;
        this.minArrivalTime = minArrivalTime;
        this.maxArrivalTime = maxArrivalTime;
        this.numberOfClients = numberOfClients;
        finished = new AtomicInteger(0);
        int MAX_TASK_PER_SERVER = 100;
        scheduler = new Scheduler(numberOfServers, MAX_TASK_PER_SERVER);
        this.selectionPolicy = selectionPolicy;
        scheduler.changeStrategy(selectionPolicy);
        generateRandomTasks();
        currentTime = new AtomicInteger(1);
    }

    /**
     * Generează random un set de taskuri în funcție de caracteristicile de simulare.
     */
    private void generateRandomTasks() {
        generatedTasks = new CopyOnWriteArrayList<>();
        Random random = new Random();

        for (int i = 0; i < numberOfClients; i++) {
            generatedTasks.add(new Task(i + 1,
                    random.nextInt(maxArrivalTime - minArrivalTime) + minArrivalTime,
                    random.nextInt(maxProcessingTime - minProcessingTime) + minProcessingTime));
        }

        Collections.sort(generatedTasks, Comparator.comparingInt(Task::getArrivalTime));
    }

    private void update(int currentTime) {
        for (var task : generatedTasks) {
            if (task.getArrivalTime() == currentTime) {
                scheduler.dispatchTask(task);//1
                generatedTasks.remove(task);
            }
        }
    }

    /**
     * Metoda are dublă responsabilitate, să afișeze evenimentele curente din servere si sa intoarca o variabila care sa exprime
     * daca in servere exista activitate sau nu
     * @param currentTime timpul curent al simularii
     * @return true dacă cel puțin un server are de lucru taskuri, altfel false.
     */
    private boolean showLogAndIsProcessing(int currentTime) {
        boolean isProcessing = false;
        writeToFile += "Time " + currentTime + "\nWaiting clients: ";
        System.out.println("Time " + currentTime + "\nWaiting clients: ");
        System.out.println(generatedTasks);
        writeToFile += generatedTasks.toString() + "\n";
        List<Server> servers = scheduler.getServers();
        int sumOfTasksProcessed = 0;
        //afisez in fisierul text si in consola starea curenta a serverelor
        for (int i = 0; i < servers.size(); i++) {
            sumOfTasksProcessed = sumOfTasksProcessed + servers.get(i).getTasks().size();
            //afisare
            System.out.println("Queue " + (i + 1) + ":");
            writeToFile += "Queue " + (i + 1) + ":\n";
            if (!scheduler.isServerOccupied(i)) {
                writeToFile += "closed\n";
                System.out.println("closed");
            } else {
                for (var client : servers.get(i).getTasks()) {
                    System.out.print(client);
                    writeToFile += client.toString();
                    isProcessing = true;
                }
                writeToFile += "\n";
                System.out.println();
            }
        }
        //calculez peak hour
        if (sumOfTasksProcessed > peakHourTasks) {
            peakHourTasks = sumOfTasksProcessed;
            peakHourTime = currentTime;
        }
        return isProcessing;
    }

    /**
     *  Afișează rezultatele simulării
     */
    private void showSimulationResult() {
        List<Server> servers = scheduler.getServers();

        float sumAverageWaitingTime = 0;
        float sumAverageServiceTime = 0;

        for (var server : servers) {
            if (server.getNoOfTasksResolved().get() != 0) {
                sumAverageWaitingTime += server.getWaitingTimeServer().get() * 1.0f / server.getNoOfTasksResolved().get();
                sumAverageServiceTime += server.getServiceTimeServer().get() * 1.0f / server.getNoOfTasksResolved().get();
            }
        }

        averageWaitingTime = sumAverageWaitingTime / servers.size();
        averageServiceTime = sumAverageServiceTime / servers.size();
        writeToFile += "Average waiting time= " + averageWaitingTime + "\n" +
                "Average service time= " + averageServiceTime + "\n" +
                "Peak hour is " + peakHourTime + " when " + peakHourTasks + " were processed!"
                + "remaining unprocessed status:" + isProcessing;
        System.out.println("Average waiting time= " + averageWaitingTime);
        System.out.println("Average service time= " + averageServiceTime);
        System.out.println("Peak hour was " + peakHourTime + " when " + peakHourTasks + " tasks were processed!");
    }

    /**
     * Aici se ruleaza simularea propriu-zisa
     */
    @Override
    public void run() {
        isProcessing = false;

        while (currentTime.get() < timeLimit && (isProcessing || !generatedTasks.isEmpty())) { //continui daca am timp necesar si daca ori n am terminat lista de taskuri sau inca procesez undeva!
            //update si vezi daca se proceseaza!
            update(currentTime.get());

            //afisare!
            isProcessing = showLogAndIsProcessing(currentTime.get());

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            currentTime.addAndGet(1);
        }

        showSimulationResult();
        //intrerup threadurile! dar nu stiu sigur daca trebuie!
        scheduler.interruptThreads();
        //scriem in fisier!
        String FILE_NAME = "sim_log.txt";
        new WriteInFile(FILE_NAME, writeToFile);
        finished.addAndGet(1);
        System.out.println("AM AJUNS LA FINAL!");
    }

    public String getFinalMessage() {
        return "Final Time: " + currentTime + "\n" +
                "Average waiting time= " + averageWaitingTime + "\n" +
                "Average service time= " + averageServiceTime + "\n" +
                "Peak hour was " + peakHourTime + " when " + peakHourTasks + " tasks were processed!" + "\n"
                + "remaining unprocessed status:" + isProcessing;
    }

    public AtomicInteger getFinished() {
        return finished;
    }

    public AtomicInteger getCurrentTime() {
        return currentTime;
    }

    public List<Server> getServers() {
        return scheduler.getServers();
    }

    public List<Task> getGeneratedServers() {
        return generatedTasks;
    }
//doar daca vreau sa rulez in consola!!
//    public static void main(String[] args) {
//        SimulationManager simulator = new SimulationManager();
//        Thread thread = new Thread(simulator);
//        thread.start();
//        try {
//            thread.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
}
