package enacl.citrus.terrain;

import de.articdive.jnoise.generators.noisegen.opensimplex.SuperSimplexNoiseGenerator;
import de.articdive.jnoise.modules.octavation.fractal_functions.FractalFunction;
import de.articdive.jnoise.pipeline.JNoise;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import java.util.Random;

public class TerrainGeneration {
    public static void generate(GenerationUnit unit, long seed) {
        // ----- Static constants -----
        int sea_level = 57;

        // ----- Create the generators -----
        Random random = new Random(seed);
        JNoise octaveNoise1 = JNoise.newBuilder()
                .superSimplex(SuperSimplexNoiseGenerator.newBuilder().setSeed(seed))
                .scale(0.01)
                .octavate(5, 0.3, 2, FractalFunction.FBM, true)
                .build();

        JNoise altitudeMap = JNoise.newBuilder()
                .superSimplex(SuperSimplexNoiseGenerator.newBuilder().setSeed(seed-1))
                .scale(0.0002)
                .build();

        JNoise smoothMap = JNoise.newBuilder()
                .superSimplex(SuperSimplexNoiseGenerator.newBuilder().setSeed(seed-2))
                .scale(0.0002)
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

                double altitude = altitudeMap.evaluateNoise(x, z);
                double smoothness = smoothMap.evaluateNoise(x, z);

                for (int y = (int) start.y(); y < start.y() + size.y(); y++) {
                    // Get terrain density function
                    double density = 0;
                    // Apply base noise functions
                    density += octaveNoise1.evaluateNoise(x, y, z);

                    //density -= (double) (y-100) /70;
                    density -= (double) (y-100-(altitude*100)) /(50*(smoothness+1));

                    if (density > 0){
                        unit.modifier().setBlock(x, y, z, Block.STONE);
                        maxY = y;
                    }
                    else {
                        if (y <= sea_level){
                            unit.modifier().setBlock(x, y, z, Block.WATER);
                        }
                    }
                }

                // Now, fill out the grass, dirt, and fill water
                if (maxY > sea_level){
                    unit.modifier().setBlock(x, maxY, z, Block.GRASS_BLOCK);
                    for (int i = 1; i <= (random.nextBoolean() ? 3 : 2); i++){
                        unit.modifier().setBlock(x, maxY-i, z, Block.DIRT);
                    }
                }
                else {
                    unit.modifier().setBlock(x, maxY, z, Block.SAND);
                }

                // Now to add flowers

            }
        }
    }
}
