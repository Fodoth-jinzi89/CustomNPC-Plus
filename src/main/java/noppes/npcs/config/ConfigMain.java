package noppes.npcs.config;

import cpw.mods.fml.common.FMLLog;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import noppes.npcs.CustomNpcs;
import noppes.npcs.config.legacy.LegacyConfig;
import org.apache.logging.log4j.Level;

import java.io.File;

public class ConfigMain
{
    public static Configuration config;

    public final static String GENERAL = "General";
    public final static String NPC = "NPC";
    public final static String UPDATE = "Update";
    public final static String PARTY = "PARTY";

    /**
     *  General Main Properties
     **/

    public static Property EnableUpdateCheckerProperty;
    public static boolean EnableUpdateChecker = true;

    public static Property DisableExtraBlockProperty;
    public static boolean DisableExtraBlock = false;

    public static Property DisableExtraItemsProperty;
    public static boolean DisableExtraItems = false;

    public static Property DisableEnchantsProperty;
    public static boolean DisableEnchants = false;

    public static Property GunsEnabledProperty;
    public static boolean GunsEnabled = true;

    public static Property EnchantStartIdProperty;
    public static int EnchantStartId = 100;

    public static Property LeavesDecayEnabledProperty;
    public static boolean LeavesDecayEnabled = true;

    public static Property VineGrowthEnabledProperty;
    public static boolean VineGrowthEnabled = true;

    public static Property IceMeltsEnabledProperty;
    public static boolean IceMeltsEnabled = true;

    public static Property SoulStoneAnimalsProperty;
    public static boolean SoulStoneAnimals = true;

    public static Property SoulStoneVillagersProperty;
    public static boolean SoulStoneVillagers = false;

    public static Property SoulStoneNPCsProperty;
    public static boolean SoulStoneNPCs = false;

    public static Property SoulStoneFriendlyNPCsProperty;
    public static boolean SoulStoneFriendlyNPCs = false;
    
    public static Property SoulStoneFollowerNPCsProperty;
    public static boolean SoulStoneFollowerNPCs = true;
    
    public static Property SoulStoneRegainProperty;
    public static boolean SoulStoneRegain = false;

    public static Property DatFormatProperty;
    public static boolean DatFormat = false;

    public static Property MarketDatFormatProperty;
    public static boolean MarketDatFormat = false;

    public static Property PartiesEnabledProperty;
    public static boolean PartiesEnabled;

    public static Property PartyFriendlyFireEnabledProperty;
    public static boolean PartyFriendlyFireEnabled;

    public static Property DefaultMinPartySizeProperty;
    public static int DefaultMinPartySize = 1;

    public static Property DefaultMaxPartySizeProperty;
    public static int DefaultMaxPartySize = 4;
    

    /**
     *  General NPC Properties
     **/

    public static Property OpsOnlyProperty;
    public static boolean OpsOnly = false;
    
    public static Property DropsOnlyPlayerKillProperty;
    public static boolean DropsOnlyPlayerKill = false;

    public static Property NpcNavRangeProperty;
    public static int NpcNavRange = 32;
    
    public static Property NpcTrackRangeProperty;
    public static int NpcTrackRange = 64;
    
    public static Property NpcUpdateIntervalProperty;
    public static int NpcUpdateInterval = 3;

    public static Property NpcSizeLimitProperty;
    public static int NpcSizeLimit = 100;

    public static Property DefaultInteractLineProperty;
    public static String DefaultInteractLine = "Hello @p";

    public static Property ChunkLoadersProperty;
    public static int ChunkLoaders = 20;

    public static Property DialogImageLimitProperty;
    public static int DialogImageLimit = 10;

    public static Property SkinOverlayLimitProperty;
    public static int SkinOverlayLimit = 10;

    public static Property NpcUseOpCommandsProperty;
    public static boolean NpcUseOpCommands = true;

    public static Property HitBoxScaleMaxProperty;
    public static int HitBoxScaleMax = 15;

    /**
     *  Update Properties
     **/

    public static Property TrackedQuestUpdateFrequencyProperty;
    public static int TrackedQuestUpdateFrequency = 5;

    public static void init(File configFile)
    {
        config = new Configuration(configFile);

        try
        {
            config.load();

            // General
            EnableUpdateCheckerProperty = config.get(GENERAL, "Enables Update Checker", true);
            EnableUpdateChecker = EnableUpdateCheckerProperty.getBoolean(true);

            DisableExtraBlockProperty = config.get(GENERAL, "Disable Extra Blocks", false);
            DisableExtraBlock = DisableExtraBlockProperty.getBoolean(false);

            DisableExtraItemsProperty = config.get(GENERAL, "Disable Extra Items", false);
            DisableExtraItems = DisableExtraItemsProperty.getBoolean(false);

            GunsEnabledProperty = config.get(GENERAL, "Guns Enabled", true, "Set to false if you want to disable guns");
            GunsEnabled = GunsEnabledProperty.getBoolean(true);

            DisableEnchantsProperty = config.get(GENERAL, "Disable Enchants", false);
            DisableEnchants = DisableEnchantsProperty.getBoolean(false);

            EnchantStartIdProperty = config.get(GENERAL, "Enchant Start ID", 100, "Start ID for enchants. IDs can only range from 0-256");
            EnchantStartId = EnchantStartIdProperty.getInt(100);

            LeavesDecayEnabledProperty = config.get(GENERAL, "Leaves Decay Enabled", true);
            LeavesDecayEnabled = LeavesDecayEnabledProperty.getBoolean(true);

            VineGrowthEnabledProperty = config.get(GENERAL, "Vine Growth Enabled", true);
            VineGrowthEnabled = VineGrowthEnabledProperty.getBoolean(true);

            IceMeltsEnabledProperty = config.get(GENERAL, "Ice Melt Enabled", true);
            IceMeltsEnabled = IceMeltsEnabledProperty.getBoolean(true);

            SoulStoneAnimalsProperty = config.get(GENERAL, "Normal players can use soulstone on Animals", true);
            SoulStoneAnimals = SoulStoneAnimalsProperty.getBoolean(true);

            SoulStoneVillagersProperty = config.get(GENERAL, "Normal players can use soulstone on Villagers", true);
            SoulStoneVillagers = SoulStoneVillagersProperty.getBoolean(true);

            SoulStoneNPCsProperty = config.get(GENERAL, "Normal players can use soulstone on NPCs", false);
            SoulStoneNPCs = SoulStoneNPCsProperty.getBoolean(false);

            SoulStoneFriendlyNPCsProperty = config.get(GENERAL, "Normal players can use soulstone on Friendly NPCs", false);
            SoulStoneFriendlyNPCs = SoulStoneFriendlyNPCsProperty.getBoolean(false);
            
            SoulStoneFollowerNPCsProperty = config.get(GENERAL, "Normal players can use soulstone on Follower NPCs", true);
            SoulStoneFollowerNPCs = SoulStoneFollowerNPCsProperty.getBoolean(true);
            
            SoulStoneRegainProperty = config.get(GENERAL, "Regains an empty soulstone when a filled soulstone is used by normal players", false);
            SoulStoneRegain = SoulStoneRegainProperty.getBoolean(false);

            DatFormatProperty = config.get(GENERAL, "Dat Format for PlayerData", false, "You need to use '/kamkeel config playerdata' to convert existing playerdata to new format.");
            DatFormat = DatFormatProperty.getBoolean(false);

            MarketDatFormatProperty = config.get(GENERAL, "Dat Format for Market", false, "You need to use '/kamkeel config market' to convert existing market to new format.");
            MarketDatFormat = MarketDatFormatProperty.getBoolean(false);

            // NPC
            NpcNavRangeProperty = config.get(NPC, "NPC Navigation Range", 32, "Navigation search range for NPCs. Not recommended to increase if you have a slow pc or on a server. Minimum of 16, maximum of 96.");
            NpcNavRange = NpcNavRangeProperty.getInt(32);
            
            NpcTrackRangeProperty = config.get(NPC, "NPC Track Range", 64, "Track range for onLivingUpdate() for NPCs. Not recommended to increase if you have a slow pc or on a server. Default of 64");
            NpcTrackRange = NpcTrackRangeProperty.getInt(64);
            
            NpcUpdateIntervalProperty = config.get(NPC, "NPC Update Interval", 3, "Update interval for onLivingUpdate() for NPCs. Lower values may cause lags.");
            NpcUpdateInterval = NpcUpdateIntervalProperty.getInt(3);

            OpsOnlyProperty = config.get(NPC, "Only Ops Edit NPCs", false, "Only ops can create and edit npcs");
            OpsOnly = OpsOnlyProperty.getBoolean(false);
            
            DropsOnlyPlayerKillProperty = config.get(NPC, "NPC Only Drops Item When Killed By Players", false, "NPCs only drops item when killed by players.");
            DropsOnlyPlayerKill = DropsOnlyPlayerKillProperty.getBoolean(false);

            NpcUseOpCommandsProperty = config.get(NPC, "NPC Use Op Commands", true, "Set to true if you want the dialog command option to be able to use op commands like tp etc");
            NpcUseOpCommands = NpcUseOpCommandsProperty.getBoolean(true);

            DefaultInteractLineProperty = config.get(NPC, "Default Interaction Line", "Hello @p", "Default interact line. Leave empty to not have one");
            DefaultInteractLine = DefaultInteractLineProperty.getString();

            ChunkLoadersProperty = config.get(NPC, "Chunk Loader Limit", 20, "Number of chunk loading npcs that can be active at the same time");
            ChunkLoaders = ChunkLoadersProperty.getInt(20);

            DialogImageLimitProperty = config.get(NPC, "Dialog Image Limit", 10, "The maximum number of images any dialog can hold.");
            DialogImageLimit = DialogImageLimitProperty.getInt(10);

            NpcSizeLimitProperty = config.get(NPC, "NPC Size Limit", 100, "Size limit for NPCs. Default 100, larger sizes may cause lag on clients and servers that can't take it!");
            NpcSizeLimit = NpcSizeLimitProperty.getInt(100);

            SkinOverlayLimitProperty = config.get(NPC, "Skin Overlay Limit", 10, "The maximum number of overlays any npc/player can hold.");
            SkinOverlayLimit = SkinOverlayLimitProperty.getInt(10);

            HitBoxScaleMaxProperty = config.get(NPC, "HitBox Scale Limit", 15, "The maximum scale factor for a custom hitbox");
            HitBoxScaleMax = HitBoxScaleMaxProperty.getInt(15);

            //PARTY
            PartiesEnabledProperty = config.get(PARTY, "Parties Enabled", true, "Determines whether the party system is enabled or not.");
            PartiesEnabled = PartiesEnabledProperty.getBoolean();

            PartyFriendlyFireEnabledProperty = config.get(PARTY, "Party Friendly Fire", true, "Determines whether friendly fire can be toggled in parties.");
            PartyFriendlyFireEnabled = PartyFriendlyFireEnabledProperty.getBoolean();

            DefaultMinPartySizeProperty = config.get(PARTY, "Default Min Party Size", 1, "When creating a new Quest sets the default min party size");
            DefaultMinPartySize = DefaultMinPartySizeProperty.getInt(4);

            DefaultMaxPartySizeProperty = config.get(PARTY, "Default Max Party Size", 4, "When creating a new Quest sets the default max party size");
            DefaultMaxPartySize = DefaultMaxPartySizeProperty.getInt(4);

            // Update
            TrackedQuestUpdateFrequencyProperty = config.get(UPDATE, "Tracked Quest Update Frequency", 5, "How often in seconds to update a players tracked quest. [Only applies to Item Quest currently]");
            TrackedQuestUpdateFrequency = TrackedQuestUpdateFrequencyProperty.getInt(5);

            // Convert to Legacy
            if(CustomNpcs.legacyExist){
                EnableUpdateChecker = LegacyConfig.EnableUpdateChecker;
                EnableUpdateCheckerProperty.set(EnableUpdateChecker);

                DisableExtraBlock = LegacyConfig.DisableExtraBlock;
                DisableExtraBlockProperty.set(DisableExtraBlock);

                DisableExtraItems = LegacyConfig.DisableExtraItems;
                DisableExtraItemsProperty.set(DisableExtraItems);

                DisableEnchants = LegacyConfig.DisableEnchants;
                DisableEnchantsProperty.set(DisableEnchants);

                GunsEnabled = LegacyConfig.GunsEnabled;
                GunsEnabledProperty.set(GunsEnabled);

                EnchantStartId = LegacyConfig.EnchantStartId;
                EnchantStartIdProperty.set(EnchantStartId);

                LeavesDecayEnabled = LegacyConfig.LeavesDecayEnabled;
                LeavesDecayEnabledProperty.set(LeavesDecayEnabled);

                VineGrowthEnabled = LegacyConfig.VineGrowthEnabled;
                VineGrowthEnabledProperty.set(VineGrowthEnabled);

                IceMeltsEnabled = LegacyConfig.IceMeltsEnabled;
                IceMeltsEnabledProperty.set(IceMeltsEnabled);

                SoulStoneAnimals = LegacyConfig.SoulStoneAnimals;
                SoulStoneAnimalsProperty.set(SoulStoneAnimals);

                SoulStoneVillagers = LegacyConfig.SoulStoneVillagers;
                SoulStoneVillagersProperty.set(SoulStoneVillagers);

                SoulStoneNPCs = LegacyConfig.SoulStoneNPCs;
                SoulStoneNPCsProperty.set(SoulStoneNPCs);

                SoulStoneFriendlyNPCs = LegacyConfig.SoulStoneFriendlyNPCs;
                SoulStoneFriendlyNPCsProperty.set(SoulStoneFriendlyNPCs);

                OpsOnly = LegacyConfig.OpsOnly;
                OpsOnlyProperty.set(OpsOnly);

                NpcNavRange = LegacyConfig.NpcNavRange;
                NpcNavRangeProperty.set(NpcNavRange);

                NpcSizeLimit = LegacyConfig.NpcSizeLimit;
                NpcSizeLimitProperty.set(NpcSizeLimit);

                DefaultInteractLine = LegacyConfig.DefaultInteractLine;
                DefaultInteractLineProperty.set(DefaultInteractLine);

                ChunkLoaders = LegacyConfig.ChunkLoaders;
                ChunkLoadersProperty.set(ChunkLoaders);

                DialogImageLimit = LegacyConfig.DialogImageLimit;
                DialogImageLimitProperty.set(DialogImageLimit);

                SkinOverlayLimit = LegacyConfig.SkinOverlayLimit;
                SkinOverlayLimitProperty.set(SkinOverlayLimit);

                NpcUseOpCommands = LegacyConfig.NpcUseOpCommands;
                NpcUseOpCommandsProperty.set(NpcUseOpCommands);
            }

            if (NpcNavRange < 16) {
                NpcNavRange = 16;
            }
            if (NpcNavRange > 96) {
                NpcNavRange = 96;
            }

            if(NpcSizeLimit < 1){
                NpcSizeLimit = 1;
            }

            if (DialogImageLimit < 0) {
                DialogImageLimit = 0;
            }

            if (SkinOverlayLimit < 0) {
                SkinOverlayLimit = 0;
            }
        }
        catch (Exception e)
        {
            FMLLog.log(Level.ERROR, e, "CNPC+ has had a problem loading its main configuration");
        }
        finally
        {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }
}
