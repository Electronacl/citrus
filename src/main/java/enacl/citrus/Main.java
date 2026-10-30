package enacl.citrus;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.block.Block;

public class Main {
    static void main() {
        // ----- Init the server -----
        MinecraftServer server = MinecraftServer.init();


        // ----- Create the instance -----
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();


        // ----- Terrain generation -----
        // Flat world atm, but hopefully I can get that changed
        instanceContainer.setGenerator(unit -> unit.modifier().fillHeight(-64, -60, Block.GRASS_BLOCK));


        // ----- Register events -----
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();

        // Event on player join
        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            final Player player = event.getPlayer(); // Init the player
            event.setSpawningInstance(instanceContainer); // Set what instance to spawn in
            player.setRespawnPoint(new Pos(0, -58, 0)); // Set spawn coords
        });


        // ----- Start the server on the local device at the standard MC port -----
        // TODO: Allow port and address to be configured.
        server.start("0.0.0.0", 25565);
    }
}
