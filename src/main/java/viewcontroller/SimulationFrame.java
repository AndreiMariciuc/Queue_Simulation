package viewcontroller;

import simulationmodel.SimulationManager;
import strategy.enums.SelectionPolicy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/**
 * Interfata grafica din care utilizatorul introduce caracteristicile de procesare!
 * folosindu-se de {@code SimulationManager} si de {@code SimulationDrawFrame}
 */
public class SimulationFrame extends JFrame {
    /**
     * Text field-uri pentru a introduce caracteristicile
     */
    private JTextField timeLimit;
    private JTextField minProcessingTime;
    private JTextField maxProcessingTime;
    private JTextField minArrivalTime;
    private JTextField maxArrivalTime;
    private JTextField numberOfServers;
    private JTextField numberOfClients;
    /**
     * Buton pentru a incepe simularea
     */
    private final JButton startSimulation;
    /**
     * CheckBox pentru a selecta strategia: 1 - time strategy, 0 - queue strategy
     */
    private final JCheckBox checkBox;

    public SimulationFrame(String title) {
        super(title);
        setLayout(new GridLayout(8, 2));

        init();

        startSimulation = new JButton("Start the simulation!");
        add(startSimulation);

        checkBox = new JCheckBox("Time(1)/Queue(0) Strategy");
        add(checkBox);

        listenerSimulation();

        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Metoda care se ocupa de parasarea inputului si
     */
    private void listenerSimulation() {
        //EXTRACT FROM UI!!!!!
        startSimulation.addActionListener(event -> {
            int timeLimit1 = 0;
            try {
                timeLimit1 = Integer.parseInt(timeLimit.getText());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Hei nu ai introdus in numar la time limit!");
                return;
            }

            int minPorcessingTime1 = 0;
            try {
                minPorcessingTime1 = Integer.parseInt(minProcessingTime.getText());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Hei nu ai introdus in numar la min processing time!");
                return;
            }

            int maxPorcessingTime1 = 0;
            try {
                maxPorcessingTime1 = Integer.parseInt(maxProcessingTime.getText());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Hei nu ai introdus in numar la max processing time!");
                return;
            }

            int minArrivalTime1 = 0;
            try {
                minArrivalTime1 = Integer.parseInt(minArrivalTime.getText());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Hei nu ai introdus in numar la min arrival time!");
                return;
            }

            int maxArrivalTime1;
            try {
                maxArrivalTime1 = Integer.parseInt(maxArrivalTime.getText());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Hei nu ai introdus in numar la max arrival time!");
                return;
            }

            int noOfServers1;
            try {
                noOfServers1 = Integer.parseInt(numberOfServers.getText());
                if(noOfServers1 == 0)
                    throw new Exception();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Hei nu ai introdus in numar la numarul de Servere!");
                return;
            }

            int noOfClients1;
            try {
                noOfClients1 = Integer.parseInt(numberOfClients.getText());
                if(noOfClients1 == 0)
                    throw new Exception();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Hei nu ai introdus in numar la numarul de clienti!!");
                return;
            }

            try {
                if(maxPorcessingTime1 + maxArrivalTime1 > timeLimit1)
                    throw new Exception("nu poti pune maxProcessingTime + maxArrivalTime > simulationTime");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
                return;
            }

            int finalTimeLimit1 = timeLimit1;
            int finalNoOfClients = noOfClients1;
            int finalNoOfServers = noOfServers1;
            int finalMaxArrivalTime = maxArrivalTime1;
            int finalMinArrivalTime = minArrivalTime1;
            int finalMaxPorcessingTime = maxPorcessingTime1;
            int finalMinPorcessingTime = minPorcessingTime1;
            //porneste simularea utilizand valorile parasate din GUI!
            SwingWorker<Void, String> worker =
                    new SwingWorker<>() {
                        SimulationManager simulationManager;
                        SimulationDrawFrame simulationDrawFrame;
                        Thread thread;

                        @Override
                        protected Void doInBackground() {
                            simulationManager = new SimulationManager(finalTimeLimit1, finalMinPorcessingTime,
                                    finalMaxPorcessingTime, finalMinArrivalTime, finalMaxArrivalTime, finalNoOfServers,
                                    finalNoOfClients, checkBox.isSelected() ? SelectionPolicy.SHORTEST_TIME : SelectionPolicy.SHORTEST_QUEUE);
                            simulationDrawFrame = new SimulationDrawFrame(simulationManager);
                            simulationDrawFrame.addWindowListener(new WindowAdapter() {
                                @Override
                                public void windowClosed(WindowEvent e) {
                                    thread.interrupt();
                                }
                            });
                            thread = new Thread(simulationManager);
                            thread.start();

                            while (simulationManager.getFinished().get() != 1) {
                                simulationDrawFrame.paint(simulationDrawFrame.getGraphics());
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException interruptedException) {
                                    interruptedException.printStackTrace();
                                }
                            }
                            publish(simulationManager.getFinalMessage());
                            return null;
                        }

                        @Override
                        protected void process(List<String> chunks) {
                            JOptionPane.showMessageDialog(simulationDrawFrame, chunks);
                        }

                        @Override
                        protected void done() {
                            this.cancel(true);
                        }
                    };
            //executam workerul
            worker.execute();
        });
    }

    /**
     * face initializarea GUI
     */
    private void init() {
        timeLimit = new JTextField();
        JLabel timeLimitLabel = new JLabel("Simulation Time");
        minProcessingTime = new JTextField();
        JLabel minProcessingTimeLabel = new JLabel("Min Processing Time");
        maxProcessingTime = new JTextField();
        JLabel maxProcessingTimeLabel = new JLabel("Max Processing Time");
        minArrivalTime = new JTextField();
        JLabel minArrivalTimeLabel = new JLabel("Min Arrival Time");
        maxArrivalTime = new JTextField();
        JLabel maxArrivalTimeLabel = new JLabel("Max Arrival Time");
        numberOfClients = new JTextField();
        JLabel numberOfClientsLabel = new JLabel("Number of tasks");
        numberOfServers = new JTextField();
        JLabel numberOfServersLabel = new JLabel("Number of servers");

        add(timeLimitLabel);
        add(timeLimit);

        add(minProcessingTimeLabel);
        add(minProcessingTime);

        add(maxProcessingTimeLabel);
        add(maxProcessingTime);

        add(minArrivalTimeLabel);
        add(minArrivalTime);

        add(maxArrivalTimeLabel);
        add(maxArrivalTime);

        add(numberOfClientsLabel);
        add(numberOfClients);

        add(numberOfServersLabel);
        add(numberOfServers);
    }

    /**
     * Lanseaza aplicatia aici!
     * @param args  nu avem nevoie!
     */
    public static void main(String[] args) {
        new SimulationFrame("Queue Simulator!!");
    }
}
