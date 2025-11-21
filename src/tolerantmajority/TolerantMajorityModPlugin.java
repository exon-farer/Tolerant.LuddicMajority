package tolerantmajority;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.listeners.ColonySizeChangeListener;
import tolerantmajority.campaign.TlmSettingsListener;
import tolerantmajority.campaign.TolerantLuddicMajorityListener;

import lunalib.lunaSettings.LunaSettings;

public class TolerantMajorityModPlugin extends BaseModPlugin {

    @Override
    public void onApplicationLoad() throws Exception {
        if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
            LunaSettings.addSettingsListener(new TlmSettingsListener());
        }
    }

    @Override
    public void onGameLoad(boolean newGame) {
        // Add listener if not present
        if (!Global.getSector().getListenerManager().hasListenerOfClass(TolerantLuddicMajorityListener.class)) {
            Global.getSector().getListenerManager().addListener(new TolerantLuddicMajorityListener());
        }

        // Remove vanilla Luddic Church size-change behavior
        for (ColonySizeChangeListener l :
                Global.getSector().getListenerManager().getListeners(ColonySizeChangeListener.class)) {

            if (l instanceof com.fs.starfarer.api.impl.campaign.intel.events.LuddicChurchHostileActivityFactor) {
                Global.getSector().getListenerManager().removeListener(l);
            }
        }
    }

}
