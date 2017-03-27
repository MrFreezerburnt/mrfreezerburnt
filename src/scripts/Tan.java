package scripts;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Game;
import org.powerbot.script.rt4.Magic;

import java.util.concurrent.Callable;
import java.util.logging.Logger;

public class Tan extends Task<ClientContext> {

    public Tan(ClientContext ctx) {
        super(ctx);
    }

    public enum LunarSpell implements Magic.MagicSpell {

        TAN_LEATHER(78, 633);

        private final int level, offTexture;

        LunarSpell(final int level, final int offTexture) {
            this.level = level;
            this.offTexture = offTexture;
        }

        @Override
        public int level() {
            return level;
        }

        @Override
        public int texture() {
            return offTexture;
        }

        @Override
        public Magic.Book book() {
            return Magic.Book.LUNAR;
        }
    }

    @Override
    public boolean activate() {
        return !ctx.bank.opened() && ctx.inventory.select().id(LunarTanner.dhideChoice).count() >= 1;
    }

    @Override
    public void execute() {

        final State state = getState();
        if (state == null) return;

        switch (state) {
            case OUT_OF_RUNES:
                ctx.controller.stop();
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.controller.isStopping();
                    }
                },6,100);
                break;
            case USE_SPELL:
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.magic.cast(LunarSpell.TAN_LEATHER);
                    }
                },60,10);
                LunarTanner.hidesTanned += 5;
                break;
            case WAIT:
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.players.local().animation() != 712;
                    }
                }, 100, 6);
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

        int nats = ctx.inventory.select().id(561).peek().stackSize();
        int asts = ctx.inventory.select().id(9075).peek().stackSize();

        if (nats < 2 || asts < 1)
            return State.OUT_OF_RUNES;
        if (ctx.players.local().animation() != 712)
            return State.USE_SPELL;
        if (ctx.players.local().animation() == 712)
            return State.WAIT;

        return null;
    }
}
