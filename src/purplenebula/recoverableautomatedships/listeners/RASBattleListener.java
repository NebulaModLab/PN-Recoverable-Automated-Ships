package purplenebula.recoverableautomatedships.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.AbilityPlugin;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial;
import com.fs.starfarer.api.util.Misc;
import org.lwjgl.input.Keyboard;

public class RASBattleListener implements CampaignEventListener {
    @Override
    public void reportPlayerOpenedMarket(MarketAPI market) {

    }

    @Override
    public void reportPlayerClosedMarket(MarketAPI market) {

    }

    @Override
    public void reportPlayerOpenedMarketAndCargoUpdated(MarketAPI market) {

    }

    @Override
    public void reportEncounterLootGenerated(FleetEncounterContextPlugin plugin, CargoAPI loot) {
        Misc.getAllowedRecoveryTags().add(Tags.AUTOMATED_RECOVERABLE);
    }

    @Override
    public void reportPlayerMarketTransaction(PlayerMarketTransaction transaction) {

    }

    @Override
    public void reportBattleOccurred(CampaignFleetAPI primaryWinner, BattleAPI battle) {

    }

    @Override
    public void reportBattleFinished(CampaignFleetAPI primaryWinner, BattleAPI battle) {

    }

    @Override
    public void reportPlayerEngagement(EngagementResultAPI result) {
        Misc.getAllowedRecoveryTags().add(Tags.AUTOMATED_RECOVERABLE);
    }

    @Override
    public void reportFleetDespawned(CampaignFleetAPI fleet, FleetDespawnReason reason, Object param) {

    }

    @Override
    public void reportFleetSpawned(CampaignFleetAPI fleet) {

    }

    @Override
    public void reportFleetReachedEntity(CampaignFleetAPI fleet, SectorEntityToken entity) {

    }

    @Override
    public void reportFleetJumped(CampaignFleetAPI fleet, SectorEntityToken from, JumpPointAPI.JumpDestination to) {

    }

    public boolean canBeMadeRecoverable(SectorEntityToken entity) {

        if (entity.getCustomPlugin() instanceof DerelictShipEntityPlugin) {

//            if (Misc.getSalvageSpecial(entity) instanceof ShipRecoverySpecial.ShipRecoverySpecialData) {
//                return false;
//            }
            if (entity.hasTag(Tags.UNRECOVERABLE)) {
                return false;
            }

            DerelictShipEntityPlugin plugin = (DerelictShipEntityPlugin) entity.getCustomPlugin();
            ShipVariantAPI variant = plugin.getData().ship.getVariant();
            FleetMemberAPI testMember =
                    Global.getFactory().createFleetMember(
                            FleetMemberType.SHIP,
                            variant
                    );
//            return Misc.isAutomated(variant);
            if (variant != null && !Misc.isUnboardable(testMember)) {
                return true;
            }
        }
        return false;
    }

    public void showRecoverable(SectorEntityToken entity) {

        Object prev = Misc.getSalvageSpecial(entity);
        if (prev != null) {
            Misc.setPrevSalvageSpecial(entity, prev);
        }

        ShipRecoverySpecial.ShipRecoverySpecialData data = new ShipRecoverySpecial.ShipRecoverySpecialData(null);
        DerelictShipEntityPlugin plugin = (DerelictShipEntityPlugin) entity.getCustomPlugin();
        data.addShip(plugin.getData().ship.clone());
        data.storyPointRecovery = true;
        Misc.setSalvageSpecial(entity, data);

        long seed = Misc.getSalvageSeed(entity);
        entity.getMemoryWithoutUpdate().set(MemFlags.SALVAGE_SEED, seed);
    }

    @Override
    public void reportShownInteractionDialog(InteractionDialogAPI dialog) {

        Global.getLogger(getClass()).info("contains(\"$salvageSpecialData\") "+dialog.getInteractionTarget().getMemoryWithoutUpdate().contains("$salvageSpecialData"));

        Misc.getAllowedRecoveryTags().add(Tags.AUTOMATED_RECOVERABLE);
        SectorEntityToken target = dialog.getInteractionTarget();

        Global.getLogger(getClass()).info(
                "canBeMadeRecoverable(): " + canBeMadeRecoverable(target)
        );

        if (target.getCustomPlugin() instanceof DerelictShipEntityPlugin) {

//            dialog.getOptionPanel().addOption(
//                    "Consider whether special measures might be taken to recover this ship",
//                    "salMakeRecoverablePNRAS",
//                    Misc.getStoryOptionColor(),
//                    ""
//            );

            if (canBeMadeRecoverable(target)) {
                showRecoverable(target);
                dialog.getOptionPanel().clearOptions();
                dialog.getOptionPanel().addOption("Explore (PNRAS)","salExplorePNRAS");
//                dialog.getOptionPanel().addOption("Explore (normal)","salExplore");
                dialog.getOptionPanel().addOption("Leave","defaultLeave");
                dialog.getOptionPanel().setShortcut("defaultLeave", Keyboard.KEY_ESCAPE, false, false, false, true);
//                dialog.getOptionPanel().addOption(
//                        "Consider whether special measures might be taken to recover this ship",
//                        "salMakeRecoverable"
//                );

//                Misc.getAllowedRecoveryTags().add(Tags.AUTOMATED_RECOVERABLE);
//
//                DerelictShipEntityPlugin plugin =
//                        (DerelictShipEntityPlugin) target.getCustomPlugin();
//
//                ShipVariantAPI variant = plugin.getData().ship.getVariant();
//
//                variant.removeTag(Tags.VARIANT_UNBOARDABLE);
//
//                FleetMemberAPI testMember =
//                        Global.getFactory().createFleetMember(
//                                FleetMemberType.SHIP,
//                                variant
//                        );
//
//                Global.getLogger(getClass()).info(
//                        "Variant ID: " + testMember.getVariant().getHullVariantId()
//                );
//
//                Global.getLogger(getClass()).info(
//                        "Variant unboardable: " +
//                                testMember.getVariant().hasTag(Tags.VARIANT_UNBOARDABLE)
//                );
//
//                Global.getLogger(getClass()).info(
//                        "Hull unboardable: " +
//                                Misc.isUnboardable(testMember.getHullSpec())
//                );
//
//                Global.getLogger(getClass()).info(
//                        "========================="
//                );
//
//                Global.getLogger(getClass()).info(
//                        "Variant tag: " +
//                                testMember.getVariant().hasTag(Tags.VARIANT_UNBOARDABLE)
//                );
//
//                Global.getLogger(getClass()).info(
//                        "Hull unboardable: " +
//                                Misc.isUnboardable(testMember.getHullSpec())
//                );
//
//                Global.getLogger(getClass()).info(
//                        "Overall unboardable: " +
//                                Misc.isUnboardable(testMember)
//                );
//
//                Global.getLogger(getClass()).info(
//                        "Original variant: " +
//                                System.identityHashCode(plugin.getData().ship.getVariant())
//                );
//
//                Global.getLogger(getClass()).info(
//                        "Test member variant: " +
//                                System.identityHashCode(testMember.getVariant())
//                );
//
//                ShipRecoverySpecial.ShipRecoverySpecialData data = new ShipRecoverySpecial.ShipRecoverySpecialData(null);
//
//                Global.getLogger(getClass()).info(
//                        "Ships: " + data.ships.size()
//                );
//
//                Global.getLogger(getClass()).info(
//                        "Story point recovery: " + data.storyPointRecovery
//                );
//
//                ShipRecoverySpecial.PerShipData original = plugin.getData().ship;
//                ShipRecoverySpecial.PerShipData clone = original.clone();
//
//                clone.variant = original.getVariant(); // If PerShipData exposes this
//                clone.variantId = null;
//
//                Global.getLogger(getClass()).info(
//                        "Original variant: " + plugin.getData().ship.variant
//                );
//
//                Global.getLogger(getClass()).info(
//                        "Cloned variant: " + clone.variant
//                );
//
//                Global.getLogger(getClass()).info(
//                        "Original variant ID: " + plugin.getData().ship.variantId
//                );
//
//                Global.getLogger(getClass()).info(
//                        "Cloned variant ID: " + clone.variantId
//                );
//
//                clone.variant.removeTag(Tags.VARIANT_UNBOARDABLE);
//
//                Global.getLogger(getClass()).info("=== Recovery diagnostic ===");
//
//                Global.getLogger(getClass()).info(
//                        "Clone variant null: " + (clone.variant == null)
//                );
//
//                Global.getLogger(getClass()).info(
//                        "Clone variant ID: " + clone.variantId
//                );
//
//                Global.getLogger(getClass()).info(
//                        "Variant ID: " + clone.variant.getHullVariantId()
//                );
//
//                FleetMemberAPI test = Global.getFactory().createFleetMember(
//                        FleetMemberType.SHIP,
//                        clone.variant
//                );
//
//                Global.getLogger(getClass()).info(
//                        "Created member: " + (test != null)
//                );
//
//                Global.getLogger(getClass()).info(
//                        "Created member variant: " + test.getVariant().getHullVariantId()
//                );
//
//                Global.getLogger(getClass()).info(
//                        "Created member unboardable: " + Misc.isUnboardable(test)
//                );
//
//                data.addShip(clone);
//                data.storyPointRecovery = true;
//
//                Global.getLogger(getClass()).info(
//                        "Recovery data ships: " + data.ships.size()
//                );
//
//                Misc.getAllowedRecoveryTags().add(Tags.AUTOMATED_RECOVERABLE);
//                Misc.setSalvageSpecial(target, data);
//
//                Global.getLogger(getClass()).info(
//                        "Entity salvage special: " +
//                                Misc.getSalvageSpecial(target)
//                );
//
//                Global.getLogger(getClass()).info(
//                        "Entity memory special: " +
//                                target.getMemoryWithoutUpdate().get(
//                                        "$salvageSpecialData"
//                                )
//                );

//                Misc.showRuleDialog(target,"CheckSalvageSpecial");
            }

        }
//        if (target.getCustomPlugin() instanceof DerelictShipEntityPlugin) {
//
//            Misc.getAllowedRecoveryTags().add(
//                    Tags.AUTOMATED_RECOVERABLE
//            );
//
//            DerelictShipEntityPlugin plugin =
//                    (DerelictShipEntityPlugin) target.getCustomPlugin();
//
//            ShipVariantAPI variant = plugin.getData().ship.getVariant();
//            ShipHullSpecAPI hullSpec = variant.getHullSpec();
//
//            Global.getLogger(getClass()).info(
//                    "=== Automated Recovery Debug ==="
//            );
//
//            Global.getLogger(getClass()).info(
//                    "auto_rec allowed: " +
//                            Misc.getAllowedRecoveryTags().contains(
//                                    Tags.AUTOMATED_RECOVERABLE
//                            )
//            );
//
//            Global.getLogger(getClass()).info(
//                    "isUnboardable: " + Misc.isUnboardable(hullSpec)
//            );
//
//            Global.getLogger(getClass()).info(
//                    "Hull ID: " + hullSpec.getHullId()
//            );
//
//            Global.getLogger(getClass()).info(
//                    "Hull tags: " + hullSpec.getTags()
//            );
//
//            Global.getLogger(getClass()).info(
//                    "UNRECOVERABLE: " + target.hasTag(Tags.UNRECOVERABLE)
//            );
//
//            Global.getLogger(getClass()).info(
//                    "Salvage special: " + Misc.getSalvageSpecial(target)
//            );
//        }
//        Global.getLogger(getClass()).info(dialog.getPlugin());
//        Global.getLogger(getClass()).info(dialog.getInteractionTarget());
//        Global.getLogger(getClass()).info(dialog.getInteractionTarget().getName());
//        Global.getLogger(getClass()).info(dialog.getInteractionTarget().getCustomPlugin());
//        Global.getLogger(getClass()).info(dialog.getInteractionTarget().getCustomEntityType());
    }

    @Override
    public void reportPlayerReputationChange(String faction, float delta) {

    }

    @Override
    public void reportPlayerReputationChange(PersonAPI person, float delta) {

    }

    @Override
    public void reportPlayerActivatedAbility(AbilityPlugin ability, Object param) {

    }

    @Override
    public void reportPlayerDeactivatedAbility(AbilityPlugin ability, Object param) {

    }

    @Override
    public void reportPlayerDumpedCargo(CargoAPI cargo) {

    }

    @Override
    public void reportPlayerDidNotTakeCargo(CargoAPI cargo) {

    }

    @Override
    public void reportEconomyTick(int iterIndex) {

    }

    @Override
    public void reportEconomyMonthEnd() {

    }
}
