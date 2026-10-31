package enacl.citrus.terrain;

import de.articdive.jnoise.generators.noisegen.opensimplex.SuperSimplexNoiseGenerator;
import de.articdive.jnoise.modules.octavation.fractal_functions.FractalFunction;
import de.articdive.jnoise.pipeline.JNoise;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;

// import java.util.Random;

public class TerrainGeneration {
    public static void generate(GenerationUnit unit, long seed){


        // ----- Create the generators -----
        // Random random = new Random(seed);
        JNoise octaveNoise1 = JNoise.newBuilder()
                .superSimplex(SuperSimplexNoiseGenerator.newBuilder().setSeed(seed))
                .scale(0.01)
                .octavate(5, 0.3, 2, FractalFunction.FBM, true)
                .build();


        // ----- Get positions to generate -----
        Point start = unit.absoluteStart();
        Point size = unit.size();

        // ----- Block generation loop
        // First pass - height
        for (int xo = 0; xo < size.x(); xo++){
            for (int zo = 0; zo < size.z(); zo++) {
                // Get positions
                int x = (int) (start.x() + xo);
                int z = (int) (start.z() + zo);

                int maxY = 0;

                for (int y = (int) start.y(); y < start.y() + size.y(); y++) {
                    // Get terrain density function
                    double density = 0;
                    // Apply base noise functions
                    density += octaveNoise1.evaluateNoise(x, y, z);

                    density -= (double) (y-100) /70;

                    if (density > 0){
                        unit.modifier().setBlock(x, y, z, Block.STONE);
                        maxY = y;
                    }
                }

                // Now, fill out the grass
                unit.modifier().setBlock(x, maxY, z, Block.GRASS_BLOCK);
                for (int i = 1; i <= 3; i++){
                    unit.modifier().setBlock(x, maxY-i, z, Block.DIRT);
                }
            }
        }
    }
}
