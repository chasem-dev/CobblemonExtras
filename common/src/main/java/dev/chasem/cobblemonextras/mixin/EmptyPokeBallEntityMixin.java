package dev.chasem.cobblemonextras.mixin;

import com.cobblemon.mod.common.entity.pokeball.EmptyPokeBallEntity;
import dev.chasem.cobblemonextras.CobblemonExtras;
import dev.chasem.cobblemonextras.commands.GiveShinyBall;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EmptyPokeBallEntity.class)
public abstract class EmptyPokeBallEntityMixin extends ThrowableProjectile {

    protected EmptyPokeBallEntityMixin(EntityType<? extends ThrowableProjectile> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "onHitBlock", at = @At(value = "HEAD"), cancellable = true)
    private void cobblemonExtras$onHitBlock(BlockHitResult hitResult, CallbackInfo ci) {
        final EmptyPokeBallEntity pokeBallEntity = (EmptyPokeBallEntity) (Object) this;
        CobblemonExtras.INSTANCE.getLogger().info("CobblemonExtras custom onHitBlock!");

        boolean isShinyBall = pokeBallEntity.getTags().contains("shinyBall");
        pokeBallEntity.getTags().forEach(CobblemonExtras.INSTANCE.getLogger()::info);
        if (isShinyBall && pokeBallEntity.getCaptureState() == EmptyPokeBallEntity.CaptureState.NOT) {
            if (!level().isClientSide) {
                discard();
                ci.cancel();
                this.spawnAtLocation(GiveShinyBall.createShinyBall(1));
            }
        }
    }

}
