package noppes.npcs.items;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.Server;
import noppes.npcs.blocks.tiles.TileBook;
import noppes.npcs.client.gui.player.GuiScenarioBook;
import noppes.npcs.client.gui.player.GuiScenarioBook2;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.constants.EnumPacketClient;

public class ItemScenarioBook extends ItemNpcInterface {
	public String playername;

	public ItemScenarioBook() {
		setCreativeTab(CustomItems.tabMisc);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (!world.isRemote)
			Server.sendData((EntityPlayerMP)player, EnumPacketClient.OPEN_SCENARIO_BOOK, stack.writeToNBT(new NBTTagCompound()));
			//openGui(stack, player);
		return stack;
	}
	
	@SideOnly(Side.CLIENT)
	public void openGui(ItemStack book, EntityPlayer author)
	{
		Minecraft.getMinecraft().displayGuiScreen(new GuiScenarioBook2(author, book));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean whatsthis) {
		if (player != null) {
			this.playername = player.getDisplayName();
		} else {
			this.playername = "Douglas Adams";
		}
		NBTTagCompound bookTag = stack.getTagCompound();
		if (bookTag != null) {
			this.playername = bookTag.getString("author");
		} else {
			NBTTagCompound newbooktag = new NBTTagCompound();
			newbooktag.setString("author", this.playername);
			stack.setTagCompound(newbooktag);
		}
		list.add(StatCollector.translateToLocal("scenarioBook.by") + " " + this.playername);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		NBTTagCompound tags = stack.getTagCompound();
		if (tags != null) {
			if (tags.getBoolean("signed")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int getItemEnchantability() {
		return 1;
	}
	
	/**
	 * 验证给定的 NBTTagCompound 是否包含有效的书籍页面数据。
	 * 
	 * 合法条件：
	 * - NBT 非空
	 * - 包含键 "pages"，且其类型为 NBTTagList（类型 9）
	 * - 每一项必须是 NBTTagString（类型 8）
	 * - 每页内容非空，长度不超过 256 字符
	 *
	 * @param tagCompound 要验证的书籍 NBT 数据
	 * @return 如果合法返回 true，否则返回 false
	 */
	public static boolean isValidBookData(NBTTagCompound tagCompound) {
	    if (tagCompound == null) {
	        return false;
	    }

	    // "pages" 键存在且为 List 类型（9）
	    if (!tagCompound.hasKey("pages", 9)) {
	        return false;
	    }

	    // 获取 "pages" 列表，元素类型应为 8（String）
	    NBTTagList pages = tagCompound.getTagList("pages", 8);

	    for (int i = 0; i < pages.tagCount(); ++i) {
	        String page = pages.getStringTagAt(i);

	        // 检查是否为空或超长
	        if (page == null || page.length() > 256) {
	            return false;
	        }
	    }

	    return true;
	}
	
	/**
	 * 验证给定的 NBTTagCompound 是否是一个有效的已签名书籍（Written Book）标签数据。
	 * 
	 * 合法条件包括：
	 * - 拥有合法的页面数据（调用 isValidBookData）
	 * - 包含 "title"（类型 8，字符串）且长度不超过 16
	 * - 包含 "author"（类型 8，字符串）
	 *
	 * @param tagCompound 要验证的书籍标签 NBT 数据
	 * @return 如果合法返回 true，否则返回 false
	 */
	public static boolean isValidSignedBookData(NBTTagCompound tagCompound) {
	    // 检查 pages 字段是否合法
	    if (!isValidBookData(tagCompound)) {
	        return false;
	    }

	    // 必须有 title 且是字符串类型（8）
	    if (!tagCompound.hasKey("title", 8)) {
	        return false;
	    }

	    String title = tagCompound.getString("title");

	    // 标题不为 null 且不超过 16 个字符，且必须包含 author 字段（字符串）
	    return title != null && title.length() <= 16 && tagCompound.hasKey("author", 8);
	}



}
