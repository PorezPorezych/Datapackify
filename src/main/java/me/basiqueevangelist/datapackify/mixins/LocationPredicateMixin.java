package me.basiqueevangelist.datapackify.mixins;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.DataFixUtils;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.FluidPredicate;
import net.minecraft.predicate.LightPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.feature.StructureFeature;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.function.LongConsumer;

@Mixin(LocationPredicate.class)
public class LocationPredicateMixin {
    @Unique
    @Nullable
    private Identifier structurePiece;

    @Redirect(method = "fromJson", at = @At(value = "NEW", target = "net/minecraft/predicate/entity/LocationPredicate"))
    private static LocationPredicate addStructurePiece(NumberRange.FloatRange x, NumberRange.FloatRange y, NumberRange.FloatRange z, @Nullable RegistryKey<Biome> registryKey, @Nullable StructureFeature<?> feature, @Nullable RegistryKey<World> dimension, @Nullable Boolean smokey, LightPredicate light, BlockPredicate block, FluidPredicate fluid, JsonElement el) {
        LocationPredicate pred = new LocationPredicate(x, y, z, registryKey, feature, dimension, smokey, light, block, fluid);
        JsonObject obj = el.getAsJsonObject();
        ((LocationPredicateMixin)(Object)pred).structurePiece = obj.has("datapackify:structure_piece") ? new Identifier(JsonHelper.getString(obj, "datapackify:structure_piece")) : null;
        return pred;
    }

    @Inject(method = "test(Lnet/minecraft/server/world/ServerWorld;FFF)Z", at = @At("TAIL"), cancellable = true)
    private void checkStructureFeature(ServerWorld world, float x, float y, float z, CallbackInfoReturnable<Boolean> cb) {
        if (cb.getReturnValue() && structurePiece != null) {
            world.getProfiler().push("datapackify:checkStructureFeature");
            BlockPos pos = new BlockPos(x, y, z);

            cb.setReturnValue(false);
            for (Map.Entry<StructureFeature<?>, LongSet> starts : world.getChunk((int)x >> 4, (int)z >> 4, ChunkStatus.STRUCTURE_REFERENCES).getStructureReferences().entrySet()) {
                starts.getValue().forEach((LongConsumer) startPos -> {
                    StructureStart<?> start = world.getChunk(ChunkPos.getPackedX(startPos), ChunkPos.getPackedZ(startPos), ChunkStatus.STRUCTURE_STARTS).getStructureStart(starts.getKey());
                    if (start.getBoundingBox().contains(pos)) {
                        for (StructurePiece piece : start.getChildren()) {
                            if (piece.getBoundingBox().contains(pos) && Registry.STRUCTURE_PIECE.getId(piece.getType()).equals(structurePiece)) {
                                cb.setReturnValue(true);
                            }
                        }
                    }
                });
            }

            world.getProfiler().pop();
        }
    }
}
