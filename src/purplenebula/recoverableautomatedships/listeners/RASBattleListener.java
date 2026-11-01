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
import lunalib.lunaSettings.LunaSettings;
import org.lazywizard.console.Console;
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

        if (Boolean.TRUE.equals(LunaSettings.getBoolean("PN_RecoverableAutomatedShips","pnras_enableDerelictRecoveryBoolean"))) {

//import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin;
//        SectorEntityToken interactionTarget = Global.getSector().getCampaignUI().getCurrentInteractionDialog().getInteractionTarget();
//        boolean isDerelictShipEntityPlugin = interactionTarget.getCustomPlugin() instanceof DerelictShipEntityPlugin;
//        String defaultName = interactionTarget.getCustomEntitySpec().getDefaultName();
//        boolean isDerelictShip = defaultName.equals("Derelict Ship");
//        if (isDerelictShipEntityPlugin && isDerelictShip) {
//            DerelictShipEntityPlugin plugin = (DerelictShipEntityPlugin) interactionTarget.getCustomPlugin();
//            ShipVariantAPI variant = plugin.getData().ship.getVariant();
//            Console.showMessage(variant.getTags());
//            Console.showMessage(variant.getHullSpec().getTags());
//        }

            SectorEntityToken target = dialog.getInteractionTarget();
            if (target == null) return;


            boolean isDerelictShipEntityPlugin = target.getCustomPlugin() instanceof DerelictShipEntityPlugin;
            if (target.getCustomEntitySpec() == null) return;
            String defaultName = target.getCustomEntitySpec().getDefaultName();
            boolean isDerelictShip = defaultName.equals("Derelict Ship");

            if (isDerelictShipEntityPlugin && isDerelictShip) {

                // Grab DSE Plugin
                DerelictShipEntityPlugin plugin = (DerelictShipEntityPlugin) target.getCustomPlugin();
                // Check for NULL values
                if (plugin.getData() == null) return;
                if (plugin.getData().ship == null) return;
                // Grab variant of derelict
                ShipVariantAPI variant = plugin.getData().ship.getVariant();
                // Check if variant's hullspec's tags contains a certain tag
                if (!variant.getHullSpec().getTags().contains(Tags.AUTOMATED_RECOVERABLE)) return;

                Misc.getAllowedRecoveryTags().add(Tags.AUTOMATED_RECOVERABLE);
                if (canBeMadeRecoverable(target)) {
                    showRecoverable(target);
                    dialog.getOptionPanel().clearOptions();
                    dialog.getOptionPanel().addOption("Explore (PNRAS)","salExplorePNRAS");
//                dialog.getOptionPanel().addOption("Explore (normal)","salExplore");
                    dialog.getOptionPanel().addOption("Leave","defaultLeave");
                    dialog.getOptionPanel().setShortcut("defaultLeave", Keyboard.KEY_ESCAPE, false, false, false, true);

                }

            }

        }

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
