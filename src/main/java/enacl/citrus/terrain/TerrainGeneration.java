package enacl.citrus.terrain;

import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;

public class TerrainGeneration {
    public static void generate(GenerationUnit unit){
        unit.modifier().fillHeight(-64, -60, Block.GRASS_BLOCK);
    }
}
