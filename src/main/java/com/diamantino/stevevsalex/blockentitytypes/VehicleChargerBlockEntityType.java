package com.diamantino.stevevsalex.blockentitytypes;

import com.diamantino.stevevsalex.registries.SVABlockEntityTypes;
import com.diamantino.stevevsalex.utils.EnergyStorageWithSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class VehicleChargerBlockEntityType extends BlockEntity {

    public final EnergyStorageWithSet energyStorage = new EnergyStorageWithSet(5000000);
    public final LazyOptional<EnergyStorage> energyStorageLazyOptional = LazyOptional.of(() -> energyStorage);

    public VehicleChargerBlockEntityType(BlockPos blockPos, BlockState blockState) {
        super(SVABlockEntityTypes.VEHICLE_CHARGER_TILE.get(), blockPos, blockState);
    }

    public static void tick(VehicleChargerBlockEntityType blockEntity) {
        assert blockEntity.level != null;
        for (Entity entity : blockEntity.level.getEntities(null, new AABB(blockEntity.worldPosition.above()))) {
            entity.getCapability(ForgeCapabilities.ENERGY, Direction.DOWN).ifPresent(entityEnergy ->
                    blockEntity.energyStorage.extractEnergy(entityEnergy.receiveEnergy(blockEntity.energyStorage.extractEnergy(1000, true), false), false));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        compoundTag.putInt("energy", energyStorage.getEnergyStored());
    }

    @Override
    public void load(CompoundTag compoundTag) {
        energyStorage.setEnergy(compoundTag.getInt("energy"));
        super.load(compoundTag);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energyStorageLazyOptional.invalidate();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return energyStorageLazyOptional.cast();
        }
        return super.getCapability(cap, side);
    }
}