package enacl.citrus.terrain;

import de.articdive.jnoise.generators.noisegen.opensimplex.SuperSimplexNoiseGenerator;
import de.articdive.jnoise.generators.noisegen.perlin.PerlinNoiseGenerator;
import de.articdive.jnoise.pipeline.JNoise;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;

import java.util.Random;

public class TerrainGeneration {
    public static void generate(GenerationUnit unit, long seed){


        // ----- Create the generators -----
        Random random = new Random(seed);
        JNoise coarseNoise1 = JNoise.newBuilder()
                .superSimplex(SuperSimplexNoiseGenerator.newBuilder().setSeed(seed))
                .scale(0.01)
                .addModifier(v -> v * 20)
                .build();
        JNoise coarseNoise2 = JNoise.newBuilder()
                .superSimplex(SuperSimplexNoiseGenerator.newBuilder().setSeed(seed))
                .scale(0.02)
                .addModifier(v -> v * 10)
                .build();
        JNoise coarseNoise3 = JNoise.newBuilder()
                .superSimplex(SuperSimplexNoiseGenerator.newBuilder().setSeed(seed))
                .scale(0.05)
                .addModifier(v -> v * 4)
                .build();

        JNoise coarseNoise4 = JNoise.newBuilder()
                .superSimplex(SuperSimplexNoiseGenerator.newBuilder().setSeed(seed))
                .scale(0.02)
                .addModifier(v -> v * 20)
                .build();


        // ----- Get positions to generate -----
        Point start = unit.absoluteStart();
        Point size = unit.size();

        // ----- Block generation loop
        for (int xo = 0; xo < size.x(); xo++){
            for (int zo = 0; zo < size.z(); zo++) {
                // Get positions
                int x = (int) (start.x() + xo);
                int z = (int) (start.z() + zo);

                // Find terrain height with noise
                double height = coarseNoise1.evaluateNoise(x, z);
                height += coarseNoise2.evaluateNoise(x, z);
                height += coarseNoise3.evaluateNoise(x, z);

                // Adjust the height
                //height = height + 70;
                //if (height < -64) height = -60;

                // Static parameters
                int sea_level = 38;

                for (int y = (int) start.y(); y < start.y() + size.y(); y++) {

                    if (coarseNoise4.evaluateNoise(x, z, y) + (height-y) > 0){
                        unit.modifier().setBlock(x, y, z, Block.STONE);
                    }
                    continue;

                    /*if (y == size.y()) {
                        unit.modifier().setBlock(x, y, z, Block.BEDROCK);
                        continue;
                    }
                    if (y <= -64) {
                        unit.modifier().setBlock(x, -64, z, Block.BEDROCK);
                        continue;
                    } else {
                        if (y == Math.round(height)){
                            if (y <= sea_level-1){
                                unit.modifier().setBlock(x, y, z, Block.SAND);
                            }
                            else {
                                unit.modifier().setBlock(x, y, z, Block.GRASS_BLOCK);
                            }
                            continue;
                        }
                        else if (y <= height && y >= height - 3) {
                            unit.modifier().setBlock(x, y, z, Block.DIRT);
                            continue;
                        }
                        else if (y <= height){
                            unit.modifier().setBlock(x, y, z, Block.STONE);
                            continue;
                        }
                        else if (y <= sea_level){
                            unit.modifier().setBlock(x, y, z, Block.WATER);
                            // Underwater terrain
                            if (y == Math.round(height+1)){
                                if (random.nextFloat() <= 0.05){
                                    unit.modifier().setBlock(x, y, z, Block.SEAGRASS);
                                }
                            }
                            continue;
                        }
                        /*else if (y == Math.round(height+1)){
                            float flower_gen = random.nextFloat();
                            if (flower_gen <= 0.05){
                                unit.modifier().setBlock(x, y, z, Block.SHORT_GRASS);

                            }
                            else if (flower_gen <= 0.052){
                                unit.modifier().setBlock(x, y, z, Block.OXEYE_DAISY);
                            }
                            else if (flower_gen <= 0.054){
                                unit.modifier().setBlock(x, y, z, Block.POPPY);
                            }
                            continue;
                        }
                    }*/
                }
            }
        }
    }
}
