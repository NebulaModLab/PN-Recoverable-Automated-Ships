package purplenebula.recoverableautomatedships;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import purplenebula.recoverableautomatedships.listeners.RASBattleListener;

public class PNRAS_ModPlugin extends BaseModPlugin {

    public void onGameLoad(boolean newGame) {
        // Adds transient battle listener checking for player engagement to allow automated ships to be recovered
        Global.getSector().addTransientListener(new RASBattleListener());
    }

}
