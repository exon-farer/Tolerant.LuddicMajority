package tolerantmajority.campaign;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.ColonySizeChangeListener;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import tolerantmajority.campaign.econ.TolerantLuddicMajority;

public class TolerantLuddicMajorityListener implements ColonySizeChangeListener {

    public void reportColonySizeChanged(MarketAPI market, int prevSize) {
        if (!market.isPlayerOwned()) return;

        boolean matches = TolerantLuddicMajority.matchesBonusConditions(market);

        if (market.hasCondition(Conditions.LUDDIC_MAJORITY) && !matches) {
            market.removeCondition(Conditions.LUDDIC_MAJORITY);
        } else if (!market.hasCondition(Conditions.LUDDIC_MAJORITY) && matches) {
            market.addCondition(Conditions.LUDDIC_MAJORITY);
        }
    }
}
