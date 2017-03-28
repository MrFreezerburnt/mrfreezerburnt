package scripts;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;

import java.util.concurrent.Callable;
import java.util.logging.Logger;

public class OpenBank extends Task<ClientContext> {

    OpenBank(ClientContext ctx) {
        super(ctx);
    }

    @Override
    public boolean activate() {
        return ctx.bank.nearest().tile().distanceTo(ctx.players.local()) < 10 && !ctx.bank.opened()
                && ((!ctx.inventory.select().id(LunarTanner.leatherChoice).isEmpty() && ctx.inventory.select().id(LunarTanner.dhideChoice).isEmpty())
                || (ctx.inventory.select().id(LunarTanner.leatherChoice).isEmpty() && ctx.inventory.select().id(LunarTanner.dhideChoice).isEmpty()));
    }

    @Override
    public void execute() {

        final State state = getState();
        if (state == null) return;

        Logger.getLogger("OpenBank.java").info(state.toString() + " detected");

        switch (state) {
            case OPEN_BANK:
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.bank.open();
                    }
                },100, 60);
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
        if (ctx.bank.inViewport()) return State.OPEN_BANK;
        return null;
    }
}
