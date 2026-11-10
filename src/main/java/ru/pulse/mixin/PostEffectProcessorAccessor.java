package ru.pulse.mixin;

import java.util.List;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.PostEffectPass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PostEffectProcessor.class)
public interface PostEffectProcessorAccessor {
   @Accessor("passes")
   List<PostEffectPass> getPasses();
}
