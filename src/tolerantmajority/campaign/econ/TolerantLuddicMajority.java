package tolerantmajority.campaign.econ;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketImmigrationModifier;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.econ.impl.ConstructionQueue.ConstructionQueueItem;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.People;
import com.fs.starfarer.api.impl.campaign.intel.events.LuddicChurchHostileActivityFactor;
import com.fs.starfarer.api.impl.campaign.population.PopulationComposition;
import com.fs.starfarer.api.loading.IndustrySpecAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import tolerantmajority.campaign.TlmSettingsListener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TolerantLuddicMajority extends BaseMarketConditionPlugin implements MarketImmigrationModifier {

    public static float STABILITY = 1f;
    public static float IMMIGRATION_BASE = 5f;

    public static float PRODUCTION_BASE_RURAL = 1f;
    public static Map<String, Integer> PRODUCTION_OVERRIDES = new LinkedHashMap<>();

    public static int BONUS_MULT_DEFEATED_EXPEDITION = 2;

    // -------------------------
    // Utility methods
    // -------------------------

    private static boolean hasRuralIndustry(MarketAPI market) {
        for (Industry ind : market.getIndustries()) {
            if (ind.getSpec().hasTag(Industries.TAG_RURAL)) return true;
        }
        if (market.getConstructionQueue() != null) {
            for (ConstructionQueueItem item : market.getConstructionQueue().getItems()) {
                IndustrySpecAPI spec = Global.getSettings().getIndustrySpec(item.id);
                if (spec != null && spec.hasTag(Industries.TAG_RURAL)) {
                    return true;
                }
                break; // only check the first
            }
        }
        return false;
    }

    private static String hasHeavyIndustry(MarketAPI market) {
        for (Industry ind : market.getIndustries()) {
            if (ind.getSpec().hasTag(Industries.TAG_INDUSTRIAL)) {

                // Skip mining if exemption is Mining or Tech-Mining
                if ("Mining".equals(TlmSettingsListener.heavyIndustryExemption) && ind.getSpec().hasTag(Industries.MINING)) {
                    continue;
                }
                // Skip mining and tech-mining if exemption is Tech-Mining
                if ("Tech-Mining".equals(TlmSettingsListener.heavyIndustryExemption) &&
                        (ind.getSpec().hasTag(Industries.MINING) || ind.getSpec().hasTag(Industries.TECHMINING))) {
                    continue;
                }

                if (!ind.getSpec().hasTag(Industries.MINING) && !ind.getSpec().hasTag(Industries.TECHMINING)) {
                    return ind.getCurrentName();
                }
            }
        }

        if (market.getConstructionQueue() != null) {
            for (ConstructionQueueItem item : market.getConstructionQueue().getItems()) {
                IndustrySpecAPI spec = Global.getSettings().getIndustrySpec(item.id);
                if (spec != null && spec.hasTag(Industries.TAG_INDUSTRIAL)) {

                    if ("Mining".equals(TlmSettingsListener.heavyIndustryExemption) && spec.hasTag(Industries.MINING)) {
                        continue;
                    }
                    if ("Tech-Mining".equals(TlmSettingsListener.heavyIndustryExemption) &&
                            (spec.hasTag(Industries.MINING) || spec.hasTag(Industries.TECHMINING))) {
                        continue;
                    }

                    if (!spec.hasTag(Industries.MINING) && !spec.hasTag(Industries.TECHMINING)) {
                        return spec.getName();
                    }
                }
                break; // only check first in queue
            }
        }

        return null;
    }


    private static List<String> getAllRuralIndustryNames() {
        List<String> names = new ArrayList<>();
        for (IndustrySpecAPI spec : Global.getSettings().getAllIndustrySpecs()) {
            if (spec.hasTag(Industries.TAG_RURAL)) {
                names.add(spec.getName());
            }
        }
        return names;
    }

    private static List<String> getAllHeavyIndustryNames() {
        List<String> names = new ArrayList<>();
        String exemption = TlmSettingsListener.heavyIndustryExemption;

        for (IndustrySpecAPI spec : Global.getSettings().getAllIndustrySpecs()) {
            if (!spec.hasTag(Industries.TAG_INDUSTRIAL)) continue;

            // Skip exempted industries based on user setting
            if ("Mining".equals(exemption) && spec.hasTag(Industries.MINING)) continue;
            if ("Tech-Mining".equals(exemption) &&
                    (spec.hasTag(Industries.MINING) || spec.hasTag(Industries.TECHMINING))) continue;

            // Include all other heavy industries
            names.add(spec.getName());
        }
        return names;
    }



    public static List<String> getNegatingFactors(MarketAPI market) {
        List<String> reasons = new ArrayList<>();
        boolean codex = Global.CODEX_TOOLTIP_MODE;

        if (codex || (market.isPlayerOwned() && LuddicChurchHostileActivityFactor.isMadeDeal())) {
            reasons.add("A formal pact with the Luddic Church suppresses Luddic migration to the colony.");
        }

        if (TlmSettingsListener.enableKato) {
            if (codex || (market.getAdmin() != null && People.DARDAN_KATO.equals(market.getAdmin().getId()))) {
                reasons.add("Kato's policies actively suppress the Luddic faithful.");
            }
        }

        if (TlmSettingsListener.enableUninhabitable) {
            if (codex || !market.hasCondition(Conditions.HABITABLE)) {
                reasons.add("The colony is not habitable.");
            }
        }

        if (TlmSettingsListener.enableNoRural) {
            if (codex || !hasRuralIndustry(market)) {
                String ruralList = Misc.getAndJoined(getAllRuralIndustryNames());
                reasons.add("The colony has no suitable employment for the faithful. (" + ruralList + ")");
            }
        }

        if (TlmSettingsListener.enablePollution) {
            if (codex || market.hasCondition(Conditions.POLLUTION)) {
                reasons.add("The colony has high pollution levels, making it unsuitable for the faithful.");
            }
        }

        if (TlmSettingsListener.enableHeavyIndustry) {
            String heavy = hasHeavyIndustry(market);
            if (codex || heavy != null) {
                reasons.add("The colony has heavy industrial facilities. (" + (heavy != null ? heavy : Misc.getAndJoined(getAllHeavyIndustryNames())) + ")");
            }
        }

        return reasons;
    }

    public static boolean matchesBonusConditions(MarketAPI market) {
        return getNegatingFactors(market).isEmpty();
    }

    // -------------------------
    // Tooltip
    // -------------------------
    public static void addConditions(TooltipMakerAPI tooltip, MarketAPI market, float opad) {
        List<String> reasons = getNegatingFactors(market);

        // Only show negating factors if there are any
        if (!reasons.isEmpty()) {
            if (market.isPlayerOwned()) {
                tooltip.addPara("The following factors result in these bonuses being negated, and, "
                                + "unless addressed, will result in the \"Luddic Majority\" condition "
                                + "being removed if the colony increases in size:",
                        opad, Misc.getNegativeHighlightColor(), "negated", "removed");
            } else {
                tooltip.addPara("The following factors result in these bonuses being negated:",
                        opad, Misc.getNegativeHighlightColor(), "negated");
            }

            tooltip.setBulletedListMode("    - ");
            for (String reason : reasons) {
                tooltip.addPara(reason, opad);
            }
            tooltip.setBulletedListMode(null);
        }
    }


    // -------------------------
    // Market effects
    // -------------------------

    public void apply(String id) {
        if (!matchesBonusConditions(market)) {
            unapply(id);
            return;
        }

        market.addTransientImmigrationModifier(this);

        int stability = (int) Math.round(STABILITY * getEffectMult());
        if (stability != 0) {
            market.getStability().modifyFlat(id, stability, "Luddic majority");
        }

        float mult = getEffectMult();
        for (Industry ind : market.getIndustries()) {
            if (ind.getSpec().hasTag(Industries.TAG_RURAL) || PRODUCTION_OVERRIDES.containsKey(ind.getId())) {
                int production = (int) Math.round(PRODUCTION_BASE_RURAL * mult);
                if (PRODUCTION_OVERRIDES.containsKey(ind.getId())) {
                    production = (int) Math.round(PRODUCTION_OVERRIDES.get(ind.getId()) * mult);
                }
                if (production != 0) {
                    ind.getSupplyBonusFromOther().modifyFlat(id, production, "Luddic majority");
                }
            }
        }
    }

    public void unapply(String id) {
        market.removeTransientImmigrationModifier(this);
        market.getStability().unmodify(id);

        for (Industry ind : market.getIndustries()) {
            if (ind.getSpec().hasTag(Industries.TAG_RURAL) || PRODUCTION_OVERRIDES.containsKey(ind.getId())) {
                ind.getSupplyBonusFromOther().unmodifyFlat(id);
            }
        }
    }

    public void modifyIncoming(MarketAPI market, PopulationComposition incoming) {
        float bonus = getImmigrationBonus(true);
        if (bonus > 0) {
            incoming.add(Factions.LUDDIC_CHURCH, bonus);
            incoming.getWeight().modifyFlat(getModId(), bonus, "Luddic immigration (Luddic majority)");
        }
    }

    // -------------------------
    // Helpers
    // -------------------------

    public float getImmigrationBonus(boolean withEffectMult) {
        float bonus = IMMIGRATION_BASE * market.getSize();
        if (withEffectMult) bonus *= getEffectMult();
        return bonus;
    }

    public float getEffectMult() {
        if (market.isPlayerOwned() && LuddicChurchHostileActivityFactor.isDefeatedExpedition()) {
            return BONUS_MULT_DEFEATED_EXPEDITION;
        }
        return 1f;
    }
    protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
        super.createTooltipAfterDescription(tooltip, expanded);

        String name = market.getName();
        float opad = 10f;

        tooltip.addPara("A majority of the population of " + name + " are Luddic faithful. "
                + "This may result in a substantial boost to stability and productivity.", opad);

        tooltip.addPara("For colonies outside the core, it may also result in increased population growth, "
                + "from Luddic immigrants seeking to escape the sometimes oppressive influence of the Luddic Church.", opad);

        tooltip.addPara("%s stability", opad, Misc.getHighlightColor(), "+" + (int) STABILITY);

        String ruralList = Misc.getAndJoined(getAllRuralIndustryNames());
        tooltip.addPara("%s production for " + ruralList,
                opad, Misc.getHighlightColor(), "+" + (int) PRODUCTION_BASE_RURAL);

        if (market.isPlayerOwned()) {
            tooltip.addPara("%s population growth",
                    opad, Misc.getHighlightColor(),
                    "+" + (int) getImmigrationBonus(false));
        }

        tooltip.addSpacer(10f);

        // Always use addConditions, which handles codex mode and empty lists correctly
        addConditions(tooltip, market, opad);
    }


    public String getIconName() {
        if (!matchesBonusConditions(market)) {
            return Global.getSettings().getSpriteName("events", "luddic_majority_unhappy");
        }
        return super.getIconName();
    }
}
