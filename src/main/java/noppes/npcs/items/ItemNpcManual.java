package noppes.npcs.items;

import java.util.List;
import java.util.Objects;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mantle.books.BookData;
import mantle.books.BookDataStore;
import mantle.client.gui.GuiManual;
import mantle.items.abstracts.CraftingItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.gui.GuiNpcManual;
import noppes.npcs.client.pages.NpcBookData;

public class ItemNpcManual extends CraftingItem {
	static String[] name = new String[] { "curiousIncident" };
	static String[] textureName = new String[] { "npcBookCuriousIncident" };

	public ItemNpcManual() {
		super(name, textureName, "", "customnpcs", CustomItems.tabMisc);
		setUnlocalizedName("npcManual");
		CustomNpcs.proxy.registerItem(this);
		GameRegistry.registerItem(this, "npcManual");
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {

		if (world.isRemote) {
			openBook(stack, world, player);
		}
		return stack;
	}

	@SideOnly(Side.CLIENT)
	public void openBook(ItemStack stack, World world, EntityPlayer player) {
		BookData data = BookDataStore.getBookfromName("customnpcs", getBookName(stack.getItemDamage()));
		if (Objects.nonNull(data) && data instanceof NpcBookData) {
			//player.openGui(CustomNpcs.instance, mantle.client.MProxyClient.manualGuiID, world, 0, 0, 0);
			FMLClientHandler.instance().displayGuiScreen(player, new GuiNpcManual(stack, (NpcBookData) data));
		}
	}

	private static String getBookName(int bookItemDamage) {
		switch (bookItemDamage) {
		case 0:
			return "curious_incident";
		default:
			return "curious_incident";
		}

	}
	
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
        switch (stack.getItemDamage()) {
            case 0:
                list.add(StatCollector.translateToLocal("customnpcs.manual.curious_incident.tooltip." + stack.getItemDamage()));
                break;
            default:
                list.add(StatCollector.translateToLocal("customnpcs.manual.curious_incident.tooltip"));
                break;
        }
    }

}
