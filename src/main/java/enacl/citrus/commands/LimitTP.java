package enacl.citrus.commands;

import net.minestom.server.command.builder.Command;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;

public class LimitTP extends Command {
    public LimitTP(){
        super("toinfinityandinfinity");
        setDefaultExecutor((sender, context) -> {
            if(sender instanceof Player player){
                player.teleport(new Pos( Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY));
                sender.sendMessage("Teleported to 30m, 100, 0");
            }
        });
    }
}
