package tolerantmajority.campaign;

import com.fs.starfarer.api.Global;
import lunalib.lunaSettings.LunaSettings;
import lunalib.lunaSettings.LunaSettingsListener;

public class TlmSettingsListener implements LunaSettingsListener {

    // Store current toggle values
    public static boolean enablePollution;
    public static boolean enableHeavyIndustry;
    public static boolean enableNoRural;
    public static boolean enableUninhabitable;
    public static boolean enableKato;

    // --- Heavy industry exemption ---
    public static String heavyIndustryExemption;

    // Initialize values
    public TlmSettingsListener() {
        reloadSettings();
    }

    @Override
    public void settingsChanged(String modID) {
        if (!modID.equals("tolerant_luddic_majority")) return;
        reloadSettings();
    }

    private void reloadSettings() {
        enablePollution = LunaSettings.getBoolean("tolerant_luddic_majority", "tlm_enablePollutionNegation");
        enableHeavyIndustry = LunaSettings.getBoolean("tolerant_luddic_majority", "tlm_enableHeavyIndustryNegation");
        enableNoRural = LunaSettings.getBoolean("tolerant_luddic_majority", "tlm_enableNoRuralNegation");
        enableUninhabitable = LunaSettings.getBoolean("tolerant_luddic_majority", "tlm_enableUninhabitableNegation");
        enableKato = LunaSettings.getBoolean("tolerant_luddic_majority", "tlm_enableKatoNegation");

        heavyIndustryExemption = LunaSettings.getString("tolerant_luddic_majority", "tlm_heavyIndustryExemption");
    }
}
