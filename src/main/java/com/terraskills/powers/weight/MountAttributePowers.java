package com.terraskills.powers.weight;

import com.terraskills.powers.skill.SkillRanks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Mule;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Default Pufferfish adapter that grants temporary mount attributes while the owner is riding. */
public final class MountAttributePowers {
    private static final ResourceLocation SPEED = ResourceLocation.fromNamespaceAndPath("terraskillspowers", "mount_skill_speed");
    private static final ResourceLocation ARMOR = ResourceLocation.fromNamespaceAndPath("terraskillspowers", "mount_skill_armor");
    private static final ResourceLocation TOUGHNESS = ResourceLocation.fromNamespaceAndPath("terraskillspowers", "mount_skill_toughness");
    private static final ResourceLocation ARMOR_EFFECTIVENESS = ResourceLocation.fromNamespaceAndPath("terraskillspowers", "mount_armor_effectiveness");
    private static final ResourceLocation TOUGHNESS_EFFECTIVENESS = ResourceLocation.fromNamespaceAndPath("terraskillspowers", "mount_toughness_effectiveness");
    private static final Map<UUID, AbstractHorse> RIDER_MOUNTS = new HashMap<>();

    private MountAttributePowers() {}

    public static void reconcile(ServerPlayer rider) {
        AbstractHorse current = rider.getVehicle() instanceof AbstractHorse horse && horse.getControllingPassenger() == rider ? horse : null;
        AbstractHorse previous = RIDER_MOUNTS.put(rider.getUUID(), current);
        if (previous != null && previous != current) remove(previous);
        if (current == null) {
            RIDER_MOUNTS.remove(rider.getUUID());
            return;
        }

        double speedMultiplier = AnimalWeightApi.mountSpeedMultiplier(SkillRanks.mountSpeed(rider));
        double armor = AnimalWeightApi.mountArmorBonus(SkillRanks.mountArmor(rider));
        double toughness = AnimalWeightApi.mountToughnessBonus(SkillRanks.mountToughness(rider));
        if (current instanceof Horse) {
            speedMultiplier *= AnimalWeightApi.horseSpeedMultiplier(SkillRanks.horseSpeed(rider));
            armor += AnimalWeightApi.horseArmorBonus(SkillRanks.horseArmor(rider));
            toughness += AnimalWeightApi.horseToughnessBonus(SkillRanks.horseToughness(rider));
        } else if (current instanceof Mule) {
            speedMultiplier *= AnimalWeightApi.muleSpeedMultiplier(SkillRanks.muleSpeed(rider));
            armor += AnimalWeightApi.muleArmorBonus(SkillRanks.muleArmor(rider));
            toughness += AnimalWeightApi.muleToughnessBonus(SkillRanks.muleToughness(rider));
        }
        update(current.getAttribute(Attributes.MOVEMENT_SPEED), SPEED, speedMultiplier - 1.0D, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        update(current.getAttribute(Attributes.ARMOR), ARMOR, armor, AttributeModifier.Operation.ADD_VALUE);
        update(current.getAttribute(Attributes.ARMOR_TOUGHNESS), TOUGHNESS, toughness, AttributeModifier.Operation.ADD_VALUE);
        double effectivenessBonus = AnimalWeightApi.horseArmorEffectivenessMultiplier(SkillRanks.horseArmorEffectiveness(rider)) - 1.0D;
        update(current.getAttribute(Attributes.ARMOR), ARMOR_EFFECTIVENESS,
                equippedDefense(current, Attributes.ARMOR) * effectivenessBonus, AttributeModifier.Operation.ADD_VALUE);
        update(current.getAttribute(Attributes.ARMOR_TOUGHNESS), TOUGHNESS_EFFECTIVENESS,
                equippedDefense(current, Attributes.ARMOR_TOUGHNESS) * effectivenessBonus, AttributeModifier.Operation.ADD_VALUE);
    }

    public static void clear(ServerPlayer rider) {
        AbstractHorse previous = RIDER_MOUNTS.remove(rider.getUUID());
        if (previous != null) remove(previous);
    }

    public static void clearAll() { RIDER_MOUNTS.clear(); }

    private static void remove(AbstractHorse horse) {
        remove(horse.getAttribute(Attributes.MOVEMENT_SPEED), SPEED);
        remove(horse.getAttribute(Attributes.ARMOR), ARMOR);
        remove(horse.getAttribute(Attributes.ARMOR_TOUGHNESS), TOUGHNESS);
        remove(horse.getAttribute(Attributes.ARMOR), ARMOR_EFFECTIVENESS);
        remove(horse.getAttribute(Attributes.ARMOR_TOUGHNESS), TOUGHNESS_EFFECTIVENESS);
    }

    private static void update(AttributeInstance attribute, ResourceLocation id, double amount, AttributeModifier.Operation operation) {
        if (attribute == null) return;
        if (amount == 0.0D) attribute.removeModifier(id);
        else attribute.addOrUpdateTransientModifier(new AttributeModifier(id, amount, operation));
    }

    private static void remove(AttributeInstance attribute, ResourceLocation id) {
        if (attribute != null) attribute.removeModifier(id);
    }

    private static double equippedDefense(AbstractHorse horse, net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> wanted) {
        double[] value = {0.0D};
        horse.getItemBySlot(EquipmentSlot.BODY).forEachModifier(EquipmentSlot.BODY, (attribute, modifier) -> {
            if (attribute.equals(wanted) && modifier.operation() == AttributeModifier.Operation.ADD_VALUE) {
                value[0] += modifier.amount();
            }
        });
        return value[0];
    }
}
