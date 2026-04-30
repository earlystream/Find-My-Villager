package com.earlystream.tradecompass.mixin;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityGlowAccessor {
    @Invoker("setSharedFlag")
    void tradecompass$setSharedFlag(int flagIndex, boolean value);
}
