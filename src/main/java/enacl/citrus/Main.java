package enacl.citrus;

import enacl.citrus.commands.LimitTP;
import enacl.citrus.terrain.TerrainGeneration;
import net.kyori.adventure.text.Component;
import net.minestom.server.Auth;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerGameModeChangeEvent;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.ping.Status;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Random;

public class Main {
    static String VERSION = "v0.0.0-alpha1 (a1)";
    static void main() throws IOException {
        // ----- Get logo for use later
        File logoFile = new File(System.getProperty("user.dir") + "\\citrus.png");
        byte[] logoBytes = Files.readAllBytes(logoFile.toPath());

        // ----- Init the server -----
        // It may be useful to use Velocity in the future
        MinecraftServer server = MinecraftServer.init(new Auth.Online());


        // ----- Create the instance -----
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();


        // ----- Update Lighting -----
        instanceContainer.setChunkSupplier(LightingChunk::new);


        // ----- Terrain generation -----
        // Refer to the class TerrainGeneration for more info.
        // Get the seed
        Random seedGenerator = new Random();
        long seed = seedGenerator.nextLong();
        // Generate
        instanceContainer.setGenerator(unit -> TerrainGeneration.generate(unit, seed));


        // ----- Register events -----
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();

        // Event on player join
        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            final Player player = event.getPlayer(); // Init the player
            event.setSpawningInstance(instanceContainer); // Set what instance to spawn in
            player.setRespawnPoint(new Pos(0, 320, 0)); // Set spawn coords
            player.setGameMode(GameMode.CREATIVE);
        });

        globalEventHandler.addListener(PlayerGameModeChangeEvent.class, event -> {
            final Player player = event.getPlayer();
            player.setGameMode(event.getNewGameMode());
        });

        globalEventHandler.addListener(ServerListPingEvent.class, event -> {
            int onlinePlayers = MinecraftServer.getConnectionManager().getOnlinePlayerCount();
            event.setStatus(Status.builder()
                            .description(Component.text("A Citrus server! " + VERSION))
                            .versionInfo(Status.VersionInfo.DEFAULT)
                            .favicon(logoBytes)
                            .playerInfo(Status.PlayerInfo.builder()
                                    .onlinePlayers(onlinePlayers)
                                    .maxPlayers(20)
                                    .build())
                            .build()
           );
        });

        // ----- Command registering -----
        MinecraftServer.getCommandManager().register(new LimitTP());

        // ----- Start the server on the local device at the standard MC port -----
        // TODO: Allow port and address to be configured.
        server.start("0.0.0.0", 25565);
    }
}
