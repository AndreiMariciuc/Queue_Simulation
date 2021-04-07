package simulationmodel;

/**
 * Clasa care modeleaza caracteristicle unui task
 */
public class Task {
    /**
     * Caracteristicile unui task care trebuie procesat:
     * <p>id - unic</p>
     * <p>arrivalTime - timp initial de pornire</p>
     * <p>processingTime - timp de procesare</p>
     * <p>finishTime - timp de finalizare in cadrul simularii</p>
     * @see SimulationManager
     * @see Server
     */
    private int id;
    private int arrivalTime;
    private int processingTime;
    private int finishTime;

    public Task(int id, int arrivalTime, int processingTime) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.processingTime = processingTime;
    }

    @Override
    public String toString() {
        return "(" + id + ", " + arrivalTime + ", " + processingTime + "); ";
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getFinishTime() {
        return finishTime;
    }

    public int getProcessingTime() {
        return processingTime;
    }

    public void setFinishTime(int finishTime) {
        this.finishTime = finishTime;
    }

    public void setProcessingTime(int processingTime) {
        this.processingTime = processingTime;
    }
}