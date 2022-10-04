package com.diamantino.stevevsalex.upgrades;

import com.diamantino.stevevsalex.client.sounds.MovingSound;
import com.diamantino.stevevsalex.entities.base.HelicopterEntity;
import com.diamantino.stevevsalex.entities.base.PlaneEntity;
import com.diamantino.stevevsalex.registries.SVAItems;
import com.diamantino.stevevsalex.registries.SVAUpgrades;
import com.diamantino.stevevsalex.upgrades.base.Upgrade;
import com.diamantino.stevevsalex.utils.MathUtils;
import com.mojang.math.Vector3f;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class RocketBoosterUpgrade extends Upgrade {
    public static final int MAX_THROTTLE = 12;

    public static final int FUEL_PER_GUNPOWDER = 20;

    public int fuel = 0;

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundNBT = new CompoundTag();
        compoundNBT.putInt("fuel", fuel);
        return compoundNBT;
    }

    @Override
    public void deserializeNBT(CompoundTag compoundNBT) {
        fuel = compoundNBT.getInt("fuel");
    }

    @Override
    public void writePacket(FriendlyByteBuf buffer) {
        buffer.writeVarInt(fuel);
    }

    @Override
    public void readPacket(FriendlyByteBuf buffer) {
        fuel = buffer.readVarInt();
    }

    public RocketBoosterUpgrade(PlaneEntity planeEntity) {
        super(SVAUpgrades.ROCKET_BOOSTER_UPGRADE.get(), planeEntity);
    }

    @Override
    public void tick() {
        push();
    }

    @Override
    public void onItemRightClick(PlayerInteractEvent.RightClickItem event) {
        ItemStack itemStack = event.getEntity().getItemInHand(event.getHand());
        if (fuel <= 0) {
            if (itemStack.getItem().equals(Items.GUNPOWDER)) {
               if (!event.getEntity().isCreative()) {
                    itemStack.shrink(1);
               }
                fuel = FUEL_PER_GUNPOWDER;
                if (planeEntity.level.isClientSide) {
                    MovingSound.play(SoundEvents.FIREWORK_ROCKET_LAUNCH, planeEntity);
                }
            }
        }
        push();
    }

    private void push() {
        if (fuel < 0) {
            return;
        }

        --fuel;
        updateClient();

        Vec3 m = planeEntity.getDeltaMovement();
        float pitch = 0;
        Player player = planeEntity.getPlayer();
        if (player != null) {
            if (player.zza > 0.0F) {
                if (planeEntity.isSprinting()) {
                    pitch += 2;
                }
            } else if (player.zza < 0.0F) {
                pitch -= 2;
            }
        }
        if (planeEntity.level.random.nextInt(50) == 0) {
            planeEntity.hurt(DamageSource.ON_FIRE, 1);
        }
        if (planeEntity instanceof HelicopterEntity) {
            pitch = 0;
        }
        planeEntity.setXRot(planeEntity.getXRot() + pitch);
        Vec3 motion = MathUtils.rotationToVector(planeEntity.getYRot(), planeEntity.getXRot(), 0.05);

        planeEntity.setDeltaMovement(m.add(motion));
        if (planeEntity.level.isClientSide) {
            spawnParticle(ParticleTypes.FLAME, new Vector3f(-0.6f, 0f, -1.3f));
            spawnParticle(ParticleTypes.FLAME, new Vector3f(0.6f, 0f, -1.3f));
        }
    }

    public void spawnParticle(ParticleOptions particleData, Vector3f relPos) {
        relPos = new Vector3f(relPos.x(), relPos.y() - 0.3f, relPos.z());
        relPos = planeEntity.transformPos(relPos);
        relPos = new Vector3f(relPos.x(), relPos.y() + 0.9f, relPos.z());
        Vec3 motion = planeEntity.getDeltaMovement();
        double speed = motion.length() / 4;
        planeEntity.level.addParticle(particleData,
                planeEntity.getX() + relPos.x(),
                planeEntity.getY() + relPos.y(),
                planeEntity.getZ() + relPos.z(),
                motion.x * speed,
                (motion.y + 1) * speed,
                motion.z * speed);
    }

    @Override
    public void onRemoved() {
        planeEntity.spawnAtLocation(SVAItems.ROCKET_BOOSTER_UPGRADE.get());
        if (planeEntity.getThrottle() > PlaneEntity.MAX_THROTTLE) {
            planeEntity.setThrottle(PlaneEntity.MAX_THROTTLE);
        }
    }
}