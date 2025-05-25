package noppes.npcs.client;


import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import mantle.books.BookData;
import mantle.books.BookDataStore;
import mantle.client.MProxyClient;
import mantle.client.SmallFontRenderer;
import mantle.client.pages.ContentsTablePage;
import mantle.lib.client.MantleClientRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntityFlameFX;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import noppes.npcs.*;
import noppes.npcs.api.IWorld;
import noppes.npcs.blocks.tiles.*;
import noppes.npcs.client.controllers.*;
import noppes.npcs.client.fx.EntityElementalStaffFX;
import noppes.npcs.client.fx.EntityEnderFX;
import noppes.npcs.client.fx.EntityRainbowFX;
import noppes.npcs.client.gui.*;
import noppes.npcs.client.gui.custom.GuiCustom;
import noppes.npcs.client.gui.global.*;
import noppes.npcs.client.gui.mainmenu.*;
import noppes.npcs.client.gui.player.*;
import noppes.npcs.client.gui.player.companion.GuiNpcCompanionInv;
import noppes.npcs.client.gui.player.companion.GuiNpcCompanionStats;
import noppes.npcs.client.gui.player.companion.GuiNpcCompanionTalents;
import noppes.npcs.client.gui.questtypes.GuiNpcQuestTypeItem;
import noppes.npcs.client.gui.roles.*;
import noppes.npcs.client.gui.script.GuiScriptBlock;
import noppes.npcs.client.gui.script.GuiScriptGlobal;
import noppes.npcs.client.gui.script.GuiScriptItem;
import noppes.npcs.client.model.*;
import noppes.npcs.client.pages.NovelChapterPage;
import noppes.npcs.client.pages.NovelContentsPage;
import noppes.npcs.client.pages.NovelDedicatePage;
import noppes.npcs.client.pages.NovelTextPage;
import noppes.npcs.client.pages.NpcBookData;
import noppes.npcs.client.renderer.*;
import noppes.npcs.client.renderer.blocks.*;
import noppes.npcs.client.renderer.items.CustomItemRenderer;
import noppes.npcs.client.renderer.items.ScriptedBlockItemRenderer;
import noppes.npcs.config.ConfigClient;
import noppes.npcs.config.ConfigMain;
import noppes.npcs.config.StringCache;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.containers.*;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.*;
import noppes.npcs.entity.data.ModelData;
import noppes.npcs.entity.data.ModelPartData;
import noppes.npcs.items.ItemScripted;
import org.lwjgl.input.Keyboard;
import org.w3c.dom.Document;

import tconstruct.client.tabs.InventoryTabCustomNpc;
import tconstruct.client.tabs.InventoryTabVanilla;
import tconstruct.client.tabs.TabRegistry;

import static mantle.lib.CoreRepo.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ClientProxy extends CommonProxy {
	public static KeyBinding NPCButton;

	public static FontContainer Font;
	
    public static Document curiousIncident;
    public static ManualInfo manualData;
    public static SmallFontRenderer smallFontRenderer;
    
    public void registerRenderer() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.getTextureManager() == null) logger.error("Vanilla texture manager is null!");
        if (mc.renderEngine == null) logger.error("Vanilla render engine is null!");
        smallFontRenderer = new NpcBookFontRenderer(
                mc.gameSettings,
                new ResourceLocation("minecraft:textures/font/ascii.png"),
                mc.renderEngine,
                false);
    }
    
    public void readManuals() {
        initManualIcons();
        initManualRecipes();
        initManualPages();
        readNPCManuals();
    }
    
    private void readNPCManuals() {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        String currentLanguage = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
        Document curiousIncident_cl = readManual("/assets/customnpcs/manuals/" + currentLanguage + "/curious_incident.xml", dbFactory);
        curiousIncident = curiousIncident_cl != null ? curiousIncident_cl : readManual("/assets/customnpcs/manuals/en_US/curious_incident.xml", dbFactory);
        manualData = new ManualInfo();
    }
    
    Document readManual(String location, DocumentBuilderFactory dbFactory) {
        try {
            InputStream stream = CustomNpcs.class.getResourceAsStream(location);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(stream);
            doc.getDocumentElement().normalize();
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public void initManualIcons() {
        MantleClientRegistry.registerManualIcon("torch", new ItemStack(Blocks.torch));
        MantleClientRegistry.registerManualIcon("sapling", new ItemStack(Blocks.sapling));
        MantleClientRegistry.registerManualIcon("workbench", new ItemStack(Blocks.crafting_table));
        MantleClientRegistry.registerManualIcon("coal", new ItemStack(Items.coal));

        MantleClientRegistry.registerManualIcon("woodplanks", new ItemStack(Blocks.planks));
        MantleClientRegistry.registerManualIcon("stoneblock", new ItemStack(Blocks.stone));
        MantleClientRegistry.registerManualIcon("ironingot", new ItemStack(Items.iron_ingot));
        MantleClientRegistry.registerManualIcon("flint", new ItemStack(Items.flint));
        MantleClientRegistry.registerManualIcon("cactus", new ItemStack(Blocks.cactus));
        MantleClientRegistry.registerManualIcon("bone", new ItemStack(Items.bone));
        MantleClientRegistry.registerManualIcon("obsidian", new ItemStack(Blocks.obsidian));
        MantleClientRegistry.registerManualIcon("netherrack", new ItemStack(Blocks.netherrack));
    }
    
    public void initManualRecipes() {}
    void initManualPages() {
    	MProxyClient.registerManualPage("novel_contents", NovelContentsPage.class);
    	MProxyClient.registerManualPage("novel_dedicate", NovelDedicatePage.class);
    	MProxyClient.registerManualPage("novel_chapter", NovelChapterPage.class);
    	MProxyClient.registerManualPage("novel_text", NovelTextPage.class);
    }

	public void load() {
		//registerRenderer();
		readManuals();
		Font = new FontContainer(ConfigClient.FontType, ConfigClient.FontSize);
		createFolders();
		CustomNpcs.Channel.register(new PacketHandlerClient());
		CustomNpcs.ChannelPlayer.register(new PacketHandlerPlayer());
		new MusicController();
		new ScriptSoundController();

		RenderingRegistry.registerEntityRenderingHandler(EntityNpcPony.class, new RenderNPCPony());
		RenderingRegistry.registerEntityRenderingHandler(EntityNpcCrystal.class, new RenderNpcCrystal(new ModelNpcCrystal(0.5F)));
		RenderingRegistry.registerEntityRenderingHandler(EntityNpcDragon.class, new RenderNpcDragon(new ModelNpcDragon(0.0F), 0.5F));
		RenderingRegistry.registerEntityRenderingHandler(EntityNpcSlime.class, new RenderNpcSlime(new ModelNpcSlime(16), new ModelNpcSlime(0), 0.25F));
		RenderingRegistry.registerEntityRenderingHandler(EntityProjectile.class, new RenderProjectile());

		RenderingRegistry.registerEntityRenderingHandler(EntityCustomNpc.class, new RenderCustomNpc());

		RenderingRegistry.registerEntityRenderingHandler(EntityNPCGolem.class, new RenderNPCHumanMale(
				new ModelNPCGolem(0), new ModelNPCGolem(1F), new ModelNPCGolem(0.5F)));
		FMLCommonHandler.instance().bus().register(new ClientTickHandler());

		ClientRegistry.bindTileEntitySpecialRenderer(TileBlockAnvil.class, new BlockCarpentryBenchRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileMailbox.class, new BlockMailboxRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileScripted.class, new BlockScriptedRenderer());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(CustomItems.scripted), new ScriptedBlockItemRenderer());
        RenderingRegistry.registerBlockHandler(new BlockBorderRenderer());

		if(!ConfigMain.DisableExtraBlock){
			ClientRegistry.bindTileEntitySpecialRenderer(TileBanner.class, new BlockBannerRenderer());
			ClientRegistry.bindTileEntitySpecialRenderer(TileWallBanner.class, new BlockWallBannerRenderer());
			ClientRegistry.bindTileEntitySpecialRenderer(TileTallLamp.class, new BlockTallLampRenderer());
			ClientRegistry.bindTileEntitySpecialRenderer(TileChair.class, new BlockChairRenderer());
			ClientRegistry.bindTileEntitySpecialRenderer(TileWeaponRack.class, new BlockWeaponRackRenderer());
			ClientRegistry.bindTileEntitySpecialRenderer(TileCrate.class, new BlockCrateRenderer());
			ClientRegistry.bindTileEntitySpecialRenderer(TileCouchWool.class, new BlockCouchWoolRenderer());
			ClientRegistry.bindTileEntitySpecialRenderer(TileCouchWood.class, new BlockCouchWoodRenderer());
			ClientRegistry.bindTileEntitySpecialRenderer(TileTable.class, new BlockTableRenderer());
			ClientRegistry.bindTileEntitySpecialRenderer(TileCandle.class, new BlockCandleRenderer());
			ClientRegistry.bindTileEntitySpecialRenderer(TileLamp.class, new BlockLampRenderer());
			ClientRegistry.bindTileEntitySpecialRenderer(TileStool.class, new BlockStoolRenderer());
			ClientRegistry.bindTileEntitySpecialRenderer(TileBigSign.class, new BlockBigSignRenderer());
			ClientRegistry.bindTileEntitySpecialRenderer(TileBarrel.class, new BlockBarrelRenderer());
			ClientRegistry.bindTileEntitySpecialRenderer(TileCampfire.class, new BlockCampfireRenderer());
			ClientRegistry.bindTileEntitySpecialRenderer(TileTombstone.class, new BlockTombstoneRenderer());
			ClientRegistry.bindTileEntitySpecialRenderer(TileShelf.class, new BlockShelfRenderer());
			ClientRegistry.bindTileEntitySpecialRenderer(TileSign.class, new BlockSignRenderer());
			ClientRegistry.bindTileEntitySpecialRenderer(TileBeam.class, new BlockBeamRenderer());
			ClientRegistry.bindTileEntitySpecialRenderer(TileBook.class, new BlockBookRenderer());
			ClientRegistry.bindTileEntitySpecialRenderer(TilePedestal.class, new BlockPedestalRenderer());
			RenderingRegistry.registerBlockHandler(new BlockBloodRenderer());
		}
		Minecraft mc = Minecraft.getMinecraft();

		NPCButton = new KeyBinding("CustomNPC+", Keyboard.KEY_L, "key.categories.gameplay");

		ClientRegistry.registerKeyBinding(NPCButton);
		mc.gameSettings.loadOptions();

		new PresetController(CustomNpcs.Dir);

		if(ConfigMain.EnableUpdateChecker){
			VersionChecker checker = new VersionChecker();
			checker.start();
		}

		ClientCloneController.Instance = new ClientCloneController();
		ClientTagMapController.Instance = new ClientTagMapController();

		MinecraftForge.EVENT_BUS.register(new ClientEventHandler());

		if(ConfigClient.InventoryGuiEnabled){
			MinecraftForge.EVENT_BUS.register(new TabRegistry());
			if (TabRegistry.getTabList().isEmpty()){
				TabRegistry.registerTab(new InventoryTabVanilla());
			}
			TabRegistry.registerTab(new InventoryTabCustomNpc());
		}
	}

	public FakePlayer getCommandPlayer(IWorld world) {
		return (FakePlayer)(new EntityCustomNpc(world.getMCWorld())).getFakePlayer();
	}

	private void createFolders() {
		File file = new File(CustomNpcs.Dir,"assets/customnpcs");
		if(!file.exists())
			file.mkdirs();

		File check = new File(file,"sounds");
		if(!check.exists())
			check.mkdir();

		File json = new File(file, "sounds.json");
		if(!json.exists()){
			try {
				json.createNewFile();
				BufferedWriter writer = new BufferedWriter(new FileWriter(json));
				writer.write("{\n\n}");
				writer.close();
			} catch (IOException e) {
			}
		}

		check = new File(file,"textures");
		if(!check.exists())
			check.mkdir();

		File cache = new File(check,"cache");
		if(!cache.exists())
			cache.mkdir();
		((IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager()).registerReloadListener(new CustomNpcResourceListener());
	}


	@Override
	public PlayerData getPlayerData(EntityPlayer player) {
		if (player.getUniqueID() == Minecraft.getMinecraft().thePlayer.getUniqueID()) {
            if(ClientCacheHandler.playerData != null){
                if (ClientCacheHandler.playerData.player != player) {
                    ClientCacheHandler.playerData.player = player;
                }

                return ClientCacheHandler.playerData;
            }
		}
        return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {

		if (ID > EnumGuiType.values().length)
			return null;
		EnumGuiType gui = EnumGuiType.values()[ID];
		EntityNPCInterface npc = NoppesUtil.getLastNpc();
		Container container =  this.getContainer(gui, player, x, y, z, npc);
		return getGui(npc, gui, container, x, y, z);
	}



	private GuiScreen getGui(EntityNPCInterface npc, EnumGuiType gui, Container container, int x, int y, int z) {
		if (gui == EnumGuiType.MainMenuDisplay) {
			if (npc != null)
				return new GuiNpcDisplay(npc);
			else
				Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Unable to find npc"));
		} else if (gui == EnumGuiType.MainMenuStats)
			return new GuiNpcStats(npc);

		else if (gui == EnumGuiType.MainMenuInv)
			return new GuiNPCInv(npc, (ContainerNPCInv) container);

		else if (gui == EnumGuiType.MainMenuAdvanced)
			return new GuiNpcAdvanced(npc);

		else if (gui == EnumGuiType.QuestReward)
			return new GuiNpcQuestReward(npc, (ContainerNpcQuestReward) container);

		else if (gui == EnumGuiType.QuestItem)
			return new GuiNpcQuestTypeItem(npc, (ContainerNpcQuestTypeItem) container);

		else if (gui == EnumGuiType.MovingPath)
			return new GuiNpcPather(npc);

		else if (gui == EnumGuiType.ManageFactions)
			return new GuiNPCManageFactions(npc);

		else if (gui == EnumGuiType.ManageTags)
			return new GuiNPCManageTags(npc);

		else if (gui == EnumGuiType.ManageAnimations) {
			EntityCustomNpc animNpc;
			boolean save;
			if (npc != null) {
				animNpc = new EntityCustomNpc(npc.worldObj);
				animNpc.copyDataFrom(npc, true);
				animNpc.display.showName = 1;
				animNpc.display.showBossBar = 0;
				save = true;
			} else {
				animNpc = new EntityCustomNpc(Minecraft.getMinecraft().theWorld);
				animNpc.display.texture = "customnpcs:textures/entity/humanmale/AnimationBody.png";
				save = false;
			}
			return new GuiNPCManageAnimations(animNpc, save);
		}
		else if (gui == EnumGuiType.ManageLinked)
			return new GuiNPCManageLinkedNpc(npc);

		else if (gui == EnumGuiType.ManageTransport)
			return new GuiNPCManageTransporters(npc);

		else if (gui == EnumGuiType.ManageRecipes)
			return new GuiNpcManageRecipes(npc, (ContainerManageRecipes) container);

		else if (gui == EnumGuiType.ManageDialogs)
			return new GuiNPCManageDialogs(npc);

		else if (gui == EnumGuiType.ManageQuests)
			return new GuiNPCManageQuest(npc);

		else if (gui == EnumGuiType.ManageBanks)
			return new GuiNPCManageBanks(npc, (ContainerManageBanks) container);

		else if (gui == EnumGuiType.MainMenuGlobal)
			return new GuiNPCGlobalMainMenu(npc);

		else if (gui == EnumGuiType.MainMenuAI)
			return new GuiNpcAI(npc);

		else if (gui == EnumGuiType.PlayerFollowerHire)
			return new GuiNpcFollowerHire(npc, (ContainerNPCFollowerHire) container);

		else if (gui == EnumGuiType.PlayerFollower)
			return new GuiNpcFollower(npc, (ContainerNPCFollower) container);

		else if (gui == EnumGuiType.PlayerTrader)
			return new GuiNPCTrader(npc, (ContainerNPCTrader) container);

		else if (gui == EnumGuiType.PlayerBankSmall || gui == EnumGuiType.PlayerBankUnlock || gui == EnumGuiType.PlayerBankUprade || gui == EnumGuiType.PlayerBankLarge)
			return new GuiNPCBankChest(npc, (ContainerNPCBankInterface) container);

		else if (gui == EnumGuiType.PlayerTransporter)
			return new GuiTransportSelection(npc);

		else if (gui == EnumGuiType.Script)
			return new GuiScript(npc);

		else if (gui == EnumGuiType.ScriptItem)
			return new GuiScriptItem();

		else if (gui == EnumGuiType.PlayerAnvil)
			return new GuiNpcCarpentryBench((ContainerCarpentryBench) container);

		else if (gui == EnumGuiType.SetupFollower)
			return new GuiNpcFollowerSetup(npc, (ContainerNPCFollowerSetup) container);

		else if (gui == EnumGuiType.SetupItemGiver)
			return new GuiNpcItemGiver(npc, (ContainerNpcItemGiver) container);

		else if (gui == EnumGuiType.SetupTrader)
			return new GuiNpcTraderSetup(npc, (ContainerNPCTraderSetup) container);

		else if (gui == EnumGuiType.SetupTransporter)
			return new GuiNpcTransporter(npc);

		else if (gui == EnumGuiType.SetupBank)
			return new GuiNpcBankSetup(npc);

		else if (gui == EnumGuiType.NpcRemote && Minecraft.getMinecraft().currentScreen == null)
			return new GuiNpcRemoteEditor();

		else if (gui == EnumGuiType.ScriptEvent && Minecraft.getMinecraft().currentScreen == null)
			return new GuiScriptGlobal();

		else if (gui == EnumGuiType.PlayerMailman)
			return new GuiMailmanWrite((ContainerMail) container, x == 1, y == 1);

		else if (gui == EnumGuiType.PlayerMailbox)
			return new GuiMailbox();

		else if (gui == EnumGuiType.MerchantAdd)
			return new GuiMerchantAdd();

		else if (gui == EnumGuiType.Crate)
			return new GuiCrate((ContainerCrate) container);

		else if(gui == EnumGuiType.NpcDimensions)
			return new GuiNpcDimension();

		else if(gui == EnumGuiType.Border)
			return new GuiBorderBlock(x, y, z);

		else if(gui == EnumGuiType.BigSign)
			return new GuiBigSign(x, y, z);

		else if (gui == EnumGuiType.RedstoneBlock)
			return new GuiNpcRedstoneBlock(x, y, z);

		else if(gui == EnumGuiType.MobSpawner)
			return new GuiNpcMobSpawner(x, y, z);

		else if(gui == EnumGuiType.MobSpawnerMounter)
			return new GuiNpcMobSpawnerMounter(x, y, z);

		else if (gui == EnumGuiType.Waypoint)
			return new GuiNpcWaypoint(x, y, z);

		else if (gui == EnumGuiType.Companion)
			return new GuiNpcCompanionStats(npc);

		else if (gui == EnumGuiType.CompanionTalent)
			return new GuiNpcCompanionTalents(npc);

		else if (gui == EnumGuiType.CompanionInv)
			return new GuiNpcCompanionInv(npc, (ContainerNPCCompanion) container);

		else if (gui == EnumGuiType.CustomGui)
			return new GuiCustom((ContainerCustomGui)container);

		else if (gui == EnumGuiType.ScriptBlock)
			return new GuiScriptBlock(x,y,z);

        else if (gui == EnumGuiType.GlobalRemote)
            return new GuiNPCGlobalMainMenu(null);

		return null;
	}

	@Override
	public void openGui(int i, int j, int k, EnumGuiType gui, EntityPlayer player) {
		Minecraft minecraft = Minecraft.getMinecraft();
		if(minecraft.thePlayer != player)
			return;

		GuiScreen guiscreen = getGui(null, gui, null, i, j, k);


		if (guiscreen != null) {
			minecraft.displayGuiScreen(guiscreen);
		}
	}

	@Override
	public void openGui(EntityNPCInterface npc, EnumGuiType gui) {
		openGui(npc, gui, 0, 0, 0);
	}

	public void openGui(EntityNPCInterface npc, EnumGuiType gui, int x, int y, int z) {
		Minecraft minecraft = Minecraft.getMinecraft();

		Container container = this.getContainer(gui, minecraft.thePlayer, x, y, z, npc);
		GuiScreen guiscreen = getGui(npc, gui,container, x, y, z);

		if (guiscreen != null) {
			minecraft.displayGuiScreen(guiscreen);
		}
	}

	public void openGui(EntityPlayer player, Object guiscreen) {
		Minecraft minecraft = Minecraft.getMinecraft();
		if(!player.worldObj.isRemote || !(guiscreen instanceof GuiScreen))
			return;

		if (guiscreen != null) {
			minecraft.displayGuiScreen((GuiScreen)guiscreen);
		}

	}

	@Override
	public void spawnParticle(EntityLivingBase player, String string, Object... ob) {
		if(string.equals("Spell")){
			int color = (Integer) ob[0];
			int number = (Integer) ob[1];
			for(int i = 0; i < number; i++){
				Random rand = player.worldObj.rand;
				double x = (rand.nextDouble() - 0.5D) * (double)player.width;
				double y = player.getEyeHeight();
				double z = (rand.nextDouble() - 0.5D) * (double)player.width;

				double f = (rand.nextDouble() - 0.5D) * 2D;
				double f1 =  -rand.nextDouble();
				double f2 = (rand.nextDouble() - 0.5D) * 2D;

				Minecraft.getMinecraft().effectRenderer.addEffect(new EntityElementalStaffFX(player, x, y, z, f, f1, f2, color));
			}
		}
		else if(string.equals("ModelData")){
			ModelData data = (ModelData) ob[0];
			ModelPartData particles = (ModelPartData) ob[1];
			EntityCustomNpc npc = (EntityCustomNpc) player;
			Minecraft minecraft =  Minecraft.getMinecraft();
			double height = npc.getYOffset() + data.getBodyY();
			Random rand = npc.getRNG();
			if(particles.type == 0){
				for(int i = 0; i< 2; i++){
					EntityEnderFX fx = new EntityEnderFX(npc, (rand.nextDouble() - 0.5D) * (double)player.width, (rand.nextDouble() * (double)player.height) - height - 0.25D, (rand.nextDouble() - 0.5D) * (double)player.width, (rand.nextDouble() - 0.5D) * 2D, -rand.nextDouble(), (rand.nextDouble() - 0.5D) * 2D, particles);
					minecraft.effectRenderer.addEffect(fx);
				}

			}
			else if(particles.type == 1){
				for(int i = 0; i < 2; i++){
					double x = player.posX + (rand.nextDouble() - 0.5D) * 0.9;
					double y = (player.posY + rand.nextDouble() * 1.9) - 0.25D - height;
					double z = player.posZ + (rand.nextDouble() - 0.5D) * 0.9;


					double f = (rand.nextDouble() - 0.5D) * 2D;
					double f1 =  -rand.nextDouble();
					double f2 = (rand.nextDouble() - 0.5D) * 2D;

					minecraft.effectRenderer.addEffect(new EntityRainbowFX(player.worldObj, x, y, z, f, f1, f2));
				}
			}
		}
	}
	private ModelSkirtArmor model = new ModelSkirtArmor();

	public ModelBiped getSkirtModel() {
		return model;
	}

	public boolean hasClient() {
		return true;
	}

	public EntityPlayer getPlayer() {
		return Minecraft.getMinecraft().thePlayer;
	}


	@Override
	public void registerItem(Item item) {
		if (item instanceof ItemScripted) {
			MinecraftForgeClient.registerItemRenderer(item, new CustomItemRenderer());
		} else {
			MinecraftForgeClient.registerItemRenderer(item, new NpcItemRenderer());
		}
	}

	public static void bindTexture(ResourceLocation location) {
		try{
			if(location == null)
				return;
			TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
			if(location != null)
				texturemanager.bindTexture((ResourceLocation) location);
		}
		catch(NullPointerException ex){

		}
		catch(ReportedException ex){

		}
	}

	@Override
	public void spawnParticle(String particle, double x, double y, double z,
							  double motionX, double motionY, double motionZ, float scale) {

		RenderGlobal render = Minecraft.getMinecraft().renderGlobal;

		EntityFX fx = render.doSpawnParticle(particle, x, y, z, motionX, motionY, motionZ);
		if(fx == null)
			return;

		if (particle.equals("flame")){
			ObfuscationReflectionHelper.setPrivateValue(EntityFlameFX.class, (EntityFlameFX)fx, scale, 0);
		}
		else if (particle.equals("smoke")){
			ObfuscationReflectionHelper.setPrivateValue(EntitySmokeFX.class, (EntitySmokeFX)fx, scale, 0);
		}
	}

	@Override
	public String getAchievementDesc(Achievement achievement) {
		return achievement.getDescription();
	}

	@Override
	public boolean isGUIOpen(){
		return Minecraft.getMinecraft().currentScreen != null;
	}

	public static class FontContainer {
		private StringCache textFont = null;
		public boolean useCustomFont = true;

		private FontContainer(){

		}

		public FontContainer(String fontType, int fontSize) {
			textFont = new StringCache();
			textFont.setDefaultFont("Arial", fontSize, true);
			useCustomFont = !fontType.equalsIgnoreCase("minecraft");
			try {
				if(!useCustomFont || fontType.isEmpty() || fontType.equalsIgnoreCase("default"))
					textFont.setCustomFont(new ResourceLocation("customnpcs","OpenSans.ttf"), fontSize, true);
				else
					textFont.setDefaultFont(fontType, fontSize, true);
			} catch (Exception e) {
				LogWriter.info("Failed loading font so using Arial");
			}
		}

		public int height(){
			if(useCustomFont)
				return textFont.fontHeight;
			return Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
		}

		public int width(String text){
			if(useCustomFont)
				return textFont.getStringWidth(text);
			return Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
		}

		public FontContainer copy() {
			FontContainer font = new FontContainer();
			font.textFont = textFont;
			font.useCustomFont = useCustomFont;
			return font;
		}

		public void drawString(String text, int x, int y, int color) {
			if(useCustomFont){
				textFont.renderString(text, x, y, color, true);
				textFont.renderString(text, x, y, color, false);
			}
			else{
				Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(text, x, y, color);
			}
		}
		
		public void drawStringWithoutShadow(String text, int x, int y, int color) {
			if(useCustomFont){
				textFont.renderString(text, x, y, color, true);
				textFont.renderString(text, x, y, color, false);
			}
			else{
				Minecraft.getMinecraft().fontRenderer.drawString(text, x, y, color);
			}
		}

		public String getName() {
			if(!useCustomFont)
				return "Minecraft";
			return textFont.usedFont().getFontName();
		}
	}
	
	public class ManualInfo {

	    public ManualInfo() {
	        Side side = FMLCommonHandler.instance().getEffectiveSide();

	        System.out.println("[ManualInfo] Effective side: " + side.name());

	        ResourceLocation bookLeft = new ResourceLocation("customnpcs", "textures/gui/bookleftdog.png");
	        ResourceLocation bookRight = new ResourceLocation("customnpcs", "textures/gui/bookrightdog.png");
	        BookData book = new NpcBookData(bookLeft, bookRight);
	        String unlocName = "curious_incident";
	        String toolTip = "\u00a7o" + StatCollector.translateToLocal("customnpcs.manual.curious_incident.tooltip");
	        Document xmlDoc = (side == Side.CLIENT ? ClientProxy.curiousIncident : null);
	        String itemImage = "customnpcs:curious_incident";

	        System.out.println("[ManualInfo] Initializing manual:");
	        System.out.println("  - unlocName: " + unlocName);
	        System.out.println("  - toolTip: " + toolTip);
	        System.out.println("  - itemImage: " + itemImage);
	        System.out.println("  - XML Document present: " + (xmlDoc != null));

	        initManual(book, unlocName, toolTip, xmlDoc, itemImage);
	    }

	    public BookData initManual(BookData data, String unlocName, String toolTip, Document xmlDoc, String itemImage) {
	        data.unlocalizedName = unlocName;
	        data.toolTip = unlocName;
	        data.modID = "customnpcs";
	        data.itemImage = new ResourceLocation(data.modID, itemImage);
	        data.doc = xmlDoc;

	        System.out.println("[ManualInfo] Registering book data:");
	        System.out.println("  - data.unlocalizedName: " + data.unlocalizedName);
	        System.out.println("  - data.modID: " + data.modID);
	        System.out.println("  - data.itemImage: " + data.itemImage);
	        System.out.println("  - XML Document present: " + (data.doc != null));

	        BookDataStore.addBook(data);

	        return data;
	    }
	}

}
