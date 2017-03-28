package scripts;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Bank.Amount;
import org.powerbot.script.rt4.Game;

import java.util.concurrent.Callable;
import java.util.logging.Logger;

public class Bank extends Task<ClientContext> {

    Bank(ClientContext ctx) {
        super(ctx);
    }

    @Override
    public boolean activate() {
        return ctx.bank.opened();
    }

    @Override
    public void execute() {

        final State state = getState();
        if (state == null) return;

        Logger.getLogger("Bank.java").info(state.toString() + " detected");

        switch (state) {
            case CLOSE_BANK:
                ctx.input.send("{VK_ESCAPE}");
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return !ctx.bank.opened();
                    }
                },6,100);
                ctx.input.move(ctx.widgets.component(218, 113).centerPoint());
                break;
            case EMPTY_INV:
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.bank.depositInventory();
                    }
                },6,100);
                break;
            case WITHDRAW_HIDES:
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.bank.withdraw(ctx.bank.select().id(LunarTanner.dhideChoice).poll().id(), Amount.ALL);
                    }
                },6,100);
                break;
            case NO_DHIDE:
                Logger.getLogger("Bank.java").info("No " + GUI.dhideBoxOptions[LunarTanner.dhideGuiChoice] +
                        " dragonhide detected in inventory or bank, closing script.");
                ctx.controller.stop();
                break;
        }
    }

    @Override
    public String status() {
        State state = getState();
        if (state != null) {
            return state.toString();
        } else {
            return null;
        }
    }

    private State getState() {

        int dhideInInv = ctx.inventory.select().id(LunarTanner.dhideChoice).count();
        int itemsInInv = ctx.inventory.select().count();
        int dhideStackBank = ctx.bank.select().id(LunarTanner.dhideChoice).poll().stackSize();

        if (dhideStackBank == -1 && dhideInInv == 0)
            return State.NO_DHIDE;
        if (itemsInInv > 3 && dhideInInv == 0)
            return State.EMPTY_INV;
        if (itemsInInv == 3 && dhideInInv == 0)
            return State.WITHDRAW_HIDES;
        if (itemsInInv > 3 && dhideInInv > 0)
            return State.CLOSE_BANK;
        return null;
    }
}
