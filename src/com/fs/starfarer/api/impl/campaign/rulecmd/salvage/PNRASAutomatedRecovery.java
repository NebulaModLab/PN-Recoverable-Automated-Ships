package com.fs.starfarer.api.impl.campaign.rulecmd.salvage;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.procgen.SalvageEntityGenDataSpec;
import com.fs.starfarer.api.impl.campaign.procgen.themes.SalvageEntityGeneratorOld;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial;
import com.fs.starfarer.api.impl.campaign.terrain.DebrisFieldTerrainPlugin;
import com.fs.starfarer.api.util.Misc;

import java.util.List;
import java.util.Map;

public class PNRASAutomatedRecovery extends BaseCommandPlugin {

    protected CampaignFleetAPI playerFleet;
    protected SectorEntityToken entity;
    protected FactionAPI playerFaction;
    protected FactionAPI entityFaction;
    protected TextPanelAPI text;
    protected OptionPanelAPI options;
    protected SalvageEntityGenDataSpec spec;
    protected CargoAPI cargo;
    protected MemoryAPI memory;
    protected InteractionDialogAPI dialog;
    private DebrisFieldTerrainPlugin debris;
    private Map<String, MemoryAPI> memoryMap;

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {

        this.dialog = dialog;
        this.memoryMap = memoryMap;

        String command = params.get(0).getString(memoryMap);
        if (command == null) return false;

        memory = getEntityMemory(memoryMap);

        entity = dialog.getInteractionTarget();

        String specId = entity.getCustomEntityType();
        if (specId == null || entity.getMemoryWithoutUpdate().contains(MemFlags.SALVAGE_SPEC_ID_OVERRIDE)) {
            specId = entity.getMemoryWithoutUpdate().getString(MemFlags.SALVAGE_SPEC_ID_OVERRIDE);
        }
        spec = SalvageEntityGeneratorOld.getSalvageSpec(specId);

        text = dialog.getTextPanel();
        options = dialog.getOptionPanel();

        playerFleet = Global.getSector().getPlayerFleet();
        cargo = playerFleet.getCargo();

        playerFaction = Global.getSector().getPlayerFaction();
        entityFaction = entity.getFaction();

        Object test = entity.getMemoryWithoutUpdate().get(MemFlags.SALVAGE_DEBRIS_FIELD);
        if (test instanceof DebrisFieldTerrainPlugin) {
            debris = (DebrisFieldTerrainPlugin) test;
        }

        if (command.equals("showCost")) {
            if (debris == null) {
//                showCost();
            } else {
//                showCostDebrisField();
            }
//        } else if (command.equals("performSalvage")) {
//            performSalvage();
//        } else if (command.equals("descDebris")) {
//            showDebrisDescription();
//        } else if (command.equals("checkAccidents")) {
//            checkAccidents();
//        } else if (command.equals("demolish")) {
//            demolish();
        } else if (command.equals("canBeMadeRecoverable")) {
            return canBeMadeRecoverable(entity);
        } else if (command.equals("showRecoverable")) {
            showRecoverable(entity);
        }

        return true;

    }

    public boolean canBeMadeRecoverable(SectorEntityToken entity) {
        Misc.getAllowedRecoveryTags().add(Tags.AUTOMATED_RECOVERABLE);
        if (entity.getCustomPlugin() instanceof DerelictShipEntityPlugin) {

            if (Misc.getSalvageSpecial(entity) instanceof ShipRecoverySpecial.ShipRecoverySpecialData) {
                return false;
            }
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
        Misc.getAllowedRecoveryTags().add(Tags.AUTOMATED_RECOVERABLE);
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
}
