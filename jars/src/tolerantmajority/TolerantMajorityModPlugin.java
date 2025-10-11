package tolerantmajority;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import tolerantmajority.campaign.TolerantLuddicMajorityListener;

public class TolerantMajorityModPlugin extends BaseModPlugin {

    @Override
    public void onGameLoad(boolean newGame) {
        if (!Global.getSector().getListenerManager().hasListenerOfClass(TolerantLuddicMajorityListener.class)) {
            Global.getSector().getListenerManager().addListener(new TolerantLuddicMajorityListener());
        }
    }
}
