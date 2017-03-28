package scripts;

import org.powerbot.script.Condition;
import org.powerbot.script.PaintListener;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GeItem;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.powerbot.script.rt4.Constants.SKILLS_MAGIC;

@Script.Manifest(
        name = "LunarTanner",
        properties = "author = Mr Freezerburnt; topic = 1330105;",
        description = "Train magic and make profit!"
)

public class LunarTanner extends PollingScript<ClientContext> implements PaintListener {

    static int dhideGuiChoice, hidesTanned, dhideChoice, leatherChoice;
    private static long startTime;
    private static int[] dragonhideID = {0, 1747, 1749, 1751, 1753};
    private static int[] leatherID = {0, 2509, 2507, 2505, 1745};

    private int startingXp;
    private int profit;
    private Font tahoma = new Font("Tahoma", Font.PLAIN, 12);
    private Font tahomaBold = new Font("Tahoma", Font.BOLD, 12);
    private String status, dhideChoiceString;
    private GUI gui = new GUI("LunarTanner v1.00");

    private List<Task> taskList = new ArrayList<Task>();

    public LunarTanner() throws IOException, URISyntaxException {
    }

    @Override
    public void start() {

        if (!ctx.game.loggedIn()) {
            errorMessageBox("Not logged in, start script once logged in.");
            ctx.controller.stop();
        } else if ((ctx.varpbits.varpbit(439) & 2) != 2) {
            errorMessageBox("Not on Lunar Spellbook!");
            ctx.controller.stop();
        } else if (ctx.skills.realLevel(SKILLS_MAGIC) < 78) {
            errorMessageBox("You do not have the required magic level to use this script (78).");
            ctx.controller.stop();
        } else {

            gui.setVisible(true);

            while (gui.isVisible()) {
                Condition.sleep(1000);
            }

            if (dhideGuiChoice == 0) {
                ctx.controller.stop();
            } else {
                taskList.addAll(Arrays.asList(new Bank(ctx), new OpenBank(ctx), new Tan(ctx)));
                dhideChoice = dragonhideID[dhideGuiChoice];
                leatherChoice = leatherID[dhideGuiChoice];
                dhideChoiceString = GUI.dhideBoxOptions[dhideGuiChoice];
                startTime = System.currentTimeMillis();
                int natPrice = new GeItem(561).price;
                int astPrice = new GeItem(9075).price;
                int dhidePrice = new GeItem(dhideChoice).price;
                int leatherPrice = new GeItem(leatherChoice).price;
                profit = leatherPrice - (dhidePrice + (natPrice + (astPrice) * 2) / 5);
                startingXp = ctx.skills.experience(SKILLS_MAGIC);
                ctx.input.speed(2);
            }

        }
    }

    @Override
    public void poll() {

        for (Task task : taskList) {
            if (task.activate()) {
                if (task.status() != null) {
                    status = task.status();
                }
                task.execute();
            }
        }
    }

    @Override
    public void repaint(Graphics graphics) {
        final Graphics2D g = (Graphics2D) graphics;

        BufferedImage img;

        if (dhideGuiChoice != 0) {

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

            try {
                URL url = new URL("http://i.imgur.com/LQDj2Hj.png");
                img = ImageIO.read(url);
                g.drawImage(img, 187, 371, null);
            } catch (IOException exp) {
                exp.printStackTrace();
            }

            g.setColor(Color.WHITE);
            g.drawString("LunarTanner v1.00 by Mr Freezerburnt", 12, 362);
            g.setFont(tahoma);
            g.drawString("Status: " + status, 12, 405);
            g.drawString(String.format("Runtime: %02d:%02d:%02d", hours, minutes, seconds), 12, 420);
            g.drawString(String.format("Hides Tanned (%s): %,d (%,d)", dhideChoiceString, hidesTanned, hidesHr), 12, 435);
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
}
