package com.crimulus.knock_on_doors;

import com.google.gson.JsonObject;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;

public class KnockOnDoors implements ModInitializer {

    public static final String MODID = "knock_on_doors";

    public static final EasyJsonConfig<Config> CONFIG_HOLDER = new EasyJsonConfig<>(KnockOnDoors.id("main"), FabricLoader.getInstance()::getConfigDir,
            (object) -> {
                var volumeElement = object.get("volume");

                return new Config(volumeElement.getAsFloat());
            },
            () -> {
                var object = new JsonObject();

                object.addProperty("volume", 0.8f);

                return object;
            }
    );

    public void onInitialize() {
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if(!player.isSpectator() && !player.isCreative() && player.getMainHandItem().isEmpty()) {
                var blockState = world.getBlockState(pos);

                float pitch;
                boolean isWooden;

                if (blockState.is(BlockTags.DOORS)) {
                    isWooden = blockState.is(BlockTags.WOODEN_DOORS);
                    pitch = 0.6f;
                } else if (blockState.is(BlockTags.TRAPDOORS)) {
                    isWooden = blockState.is(BlockTags.WOODEN_TRAPDOORS);
                    pitch = 4.0f;
                } else {
                    return InteractionResult.PASS;
                }

                var soundEvent = isWooden
                        ? SoundEvents.ZOMBIE_ATTACK_WOODEN_DOOR
                        : SoundEvents.ZOMBIE_ATTACK_IRON_DOOR;

                world.playSound(null, pos, soundEvent, SoundSource.BLOCKS, CONFIG_HOLDER.instance().volume(), pitch);
            }

            return InteractionResult.PASS;
        });
    }

    public record Config(float volume) {}

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }
}
