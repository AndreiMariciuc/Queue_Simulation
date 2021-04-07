package viewcontroller;

import simulationmodel.Server;
import simulationmodel.SimulationManager;
import simulationmodel.Task;

import java.util.List;
import javax.swing.*;
import java.awt.*;

/**
 *  Clasa care este utilizata pentru a desena starea dintr-un anumit timp al simularii
 *  utilizand {@code SimulationManager}
 */

public class SimulationDrawFrame extends JFrame {
    /**
     * Dimensiunile frame-ului
     * @see JFrame
     */
    private final int WIDTH = 700;
    private final int HIEGHT = 800;
    /**
     * Simulatorul care furnizeaza informatii ce ar trebuie afisate in metoda paint(Graphics g)
     */
    private final SimulationManager simulationManager;

    public SimulationDrawFrame(SimulationManager simulationManager) {
        super("Simulation");
        this.simulationManager = simulationManager;
        pack();
        setSize(WIDTH, HIEGHT);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Desenam grafic in functie de datele primite de la {@code SimulationManager}
     * @param g grafica aferenta framelui in care se deseneaza
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        int MARGIN_Y = 50;
        //draw current time
        g.drawString("Time " + simulationManager.getCurrentTime().toString(), WIDTH - MARGIN_Y, HIEGHT - 20);
        //draw serves start!
        List<Server> serverList = simulationManager.getServers();

        int WIDTH_OF_LIST = 100;
        int widthOfServer = (WIDTH - WIDTH_OF_LIST) / serverList.size();

        List<Task> generatedTasks = simulationManager.getGeneratedServers();

        g.drawString("Generated Tasks", 10, 40);
        g.drawLine(WIDTH_OF_LIST, 0, WIDTH_OF_LIST, HIEGHT);
        int MARGIN_X = 20;
        Point locTasks = new Point(MARGIN_X, MARGIN_X);
        //desenam taskurile generate
        for (int i = 0; i < generatedTasks.size(); i++)
            g.drawString(generatedTasks.get(i).toString(), locTasks.x, locTasks.y * (i + 1) + 40);
        //desenam serverele si taskruile aferente fiecarui task
        for (int i = 0; i < serverList.size(); i++) {
            Point loc = new Point(i * widthOfServer + WIDTH_OF_LIST, MARGIN_Y);

            int RECT_HEIGHT = 50;
            g.drawRect(loc.x, loc.y, widthOfServer, RECT_HEIGHT);
            g.drawString("Server " + i, loc.x + widthOfServer / 2 - MARGIN_X, loc.y + RECT_HEIGHT / 2);
            //obtinem lista de taskuri serverului curent
            List<Task> tasksList = serverList.get(i).getTasks();
            //desenam taskruile aferente aferent serverului i
            for (var j = 0; j < tasksList.size(); j++) {
                if (j == 0) {
                    g.setColor(new Color(124, 173, 122, 190));
                } else {
                    g.setColor(new Color(203, 149, 62));
                }
                g.fillRect(loc.x, (j + 2) * RECT_HEIGHT, widthOfServer, RECT_HEIGHT);
                g.setColor(new Color(0, 0, 0));
                g.drawRect(loc.x, (j + 2) * RECT_HEIGHT, widthOfServer, RECT_HEIGHT);
                g.drawString(tasksList.get(j).toString(), loc.x + widthOfServer / 2 - MARGIN_X, (j + 2) * RECT_HEIGHT + RECT_HEIGHT / 2);
            }
        }
    }
}
