package scripts;

import org.powerbot.bot.rt4.Con;
import org.powerbot.script.Condition;
import org.powerbot.script.PaintListener;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GeItem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.powerbot.script.rt4.Constants.SKILLS_MAGIC;

@Script.Manifest(name = "LunarTanner", description = "Train magic and make profit!")
public class LunarTanner extends PollingScript<ClientContext> implements PaintListener {

    static int dhideGuiChoice = 0;
    static int hidesTanned = 0;
    static int dhideChoice, leatherChoice;
    private static long startTime;

    static int[] dragonhideID = {-1, 1747, 1749, 1751, 1753};
    static int[] leatherID = {-1, 2509, 2507, 2505, 1745};

    private boolean ready = false;
    private int startingXp;
    private int profit;
    private Font tahoma = new Font("Tahoma", Font.PLAIN, 12);
    private Font tahomaBold = new Font("Tahoma", Font.BOLD, 12);
    private String status;
    private static final GUI gui = new GUI("LunarTanner");
    private static final Object lock = new Object();

    private List<Task> taskList = new ArrayList<Task>();

    @Override
    public void start() {

        if (!ctx.game.loggedIn()) {
            errorMessageBox("Not logged in, start script once logged in.");
            stopScript();
        } else if ((ctx.varpbits.varpbit(439) & 2) != 2) {
            errorMessageBox("Not on Lunar Spellbook!");
            stopScript();
        } else if (ctx.skills.realLevel(SKILLS_MAGIC) < 78) {
            errorMessageBox("You do not have the required magic level to use this script (78).");
            stopScript();
        } else {

            gui.setVisible(true);
            Thread t = new Thread() {
                public void run() {
                    synchronized (lock) {
                        while (gui.isVisible()) {
                            try {
                                lock.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            };

            t.start();

            gui.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    synchronized (lock) {
                        gui.setVisible(false);
                        lock.notify();
                        ready = true;
                    }
                }
            });

            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (dhideGuiChoice == 0) ctx.controller.stop();
            else {
                dhideChoice = dragonhideID[dhideGuiChoice];
                leatherChoice = leatherID[dhideGuiChoice];
                startTime = System.currentTimeMillis();
                int natPrice = new GeItem(561).price;
                int astPrice = new GeItem(9075).price;
                int dhidePrice = new GeItem(dhideChoice).price;
                int leatherPrice = new GeItem(leatherChoice).price;
                profit = leatherPrice - (dhidePrice + (natPrice + (astPrice) * 2) / 5);
                startingXp = ctx.skills.experience(SKILLS_MAGIC);
                ctx.input.speed(2);
                taskList.addAll(Arrays.asList(new Bank(ctx), new OpenBank(ctx), new Tan(ctx)));
            }

        }
    }

    @Override
    public void poll() {

        if (ready) {
            for (Task task : taskList) {
                if (task.activate()) {
                    if (task.status() != null) {
                        status = task.status();
                    }
                    task.execute();
                }
            }
        }
    }

    @Override
    public void repaint(Graphics graphics) {
        final Graphics2D g = (Graphics2D) graphics;

        if (ready) {

            final long currentTime = System.currentTimeMillis();
            final long runtime = currentTime - startTime;
            final long seconds = (runtime / 1000) % 60;
            final long minutes = (runtime / (1000 * 60)) % 60;
            final long hours = (runtime / (1000 * 60 * 60)) % 24;

            final int mouseX = (int) ctx.input.getLocation().getX();
            final int mouseY = (int) ctx.input.getLocation().getY();

            final int currentXp = ctx.skills.experience(SKILLS_MAGIC);
            final int hidesHr = (int) ((hidesTanned * 3600000D) / runtime);
            final int xp = currentXp - startingXp;
            final int xpHr = (int) (((currentXp - startingXp) * 3600000D) / runtime);

            final int totalProfit = profit * hidesTanned;
            final int profitHr = (int) (totalProfit * 3600000D / runtime);

            g.setFont(tahomaBold);
            g.setColor(Color.BLACK);
            g.fillRect(7, 345, 506, 129);
            g.setColor(Color.WHITE);
            g.drawString("LunarTanner v1.00 by Mr Freezerburnt", 12, 362);
            g.setFont(tahoma);
            g.drawString("Status: " + status, 12, 405);
            g.drawString(String.format("Runtime: %02d:%02d:%02d", hours, minutes, seconds), 12, 420);
            g.drawString(String.format("Hides Tanned: %,d (%,d)", hidesTanned, hidesHr), 12, 435);
            g.drawString(String.format("XP Gained: %,d (%,d)", xp, xpHr), 12, 450);
            g.drawString(String.format("Profit Earned: %,d (%,d)", totalProfit, profitHr), 12, 465);
            g.setColor(Color.GREEN);
            g.drawLine(mouseX - 5, mouseY - 5, mouseX + 5, mouseY + 5);
            g.drawLine(mouseX - 5, mouseY + 5, mouseX + 5, mouseY - 5);
        }
    }

    private void errorMessageBox(String message) {
        JOptionPane.showMessageDialog(null, message, "ERROR", JOptionPane.ERROR_MESSAGE);
    }

    private void stopScript() {
        ctx.controller.stop();
    }

}
