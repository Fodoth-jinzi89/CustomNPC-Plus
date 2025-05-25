package noppes.npcs.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.client.pages.JumpBackButton;
import noppes.npcs.client.pages.NextChapterButton;
import noppes.npcs.client.pages.NovelContentsPage.GuiButtonJump;
import noppes.npcs.client.pages.NpcBookData;
import noppes.npcs.client.pages.NpcBookPage;
import noppes.npcs.client.pages.PrevChapterButton;
import noppes.npcs.constants.EnumPlayerPacket;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mantle.books.BookData;
import mantle.client.MProxyClient;
import mantle.client.RenderItemCopy;
import mantle.client.SmallFontRenderer;
import mantle.client.gui.GuiManual;
import mantle.client.gui.TurnPageButton;
import mantle.client.pages.BookPage;

@SideOnly(Side.CLIENT)
public class GuiNpcManual extends GuiManual {

	ItemStack itemstackBook;
	Document manual;
	public RenderItemCopy renderitem = new RenderItemCopy();
	public int bookImageWidth = 206;
	public int bookImageHeight = 200;
	int bookTotalPages = 1;
	int currentPage;
	int maxPages;
	public int numTitlePages;
	public int numContentPages;
	public int numTextPages;
	int localWidth;
	int localWidthLeft;
	byte localHeight = 8;
	NpcBookData npcBookData;
	List<Integer> chapterList = new ArrayList<Integer>();

	private TurnPageButton buttonNextPage;
	private TurnPageButton buttonPreviousPage;
	private JumpBackButton buttonJumpBack;
	private NextChapterButton buttonNextChapter;
	private PrevChapterButton buttonPrevChapter;
	private static ResourceLocation bookRight;// = new ResourceLocation("mantle", "textures/gui/bookright.png");
	private static ResourceLocation bookLeft;// = new ResourceLocation("mantle", "textures/gui/bookleft.png");

	BookPage pageLeft;
	BookPage pageRight;

	public SmallFontRenderer fonts = ClientProxy.smallFontRenderer;

	@SuppressWarnings("rawtypes")
	public List buttonListDefault = new ArrayList();
	// public FontRenderer fonts = Minecraft.getMinecraft().fontRenderer;

	public GuiNpcManual(ItemStack stack, NpcBookData data) {

		super(stack, data);
		this.mc = Minecraft.getMinecraft();
		this.itemstackBook = stack;
		this.currentPage = 0; // Stack page
		this.manual = data.getDoc();
//		if (data.font != null)
//			this.fonts = data.font;
		bookLeft = data.leftImage;
		bookRight = data.rightImage;
		this.npcBookData = data;

		// renderitem.renderInFrame = true;
	}

	public void setZLevel(float f) {
		this.zLevel = f;
	}

	public float getZLevel() {
		return this.zLevel;
	}

	public void incZLevel(float f) {
		this.zLevel += f;
	}

	/*
	 * @Override public void setWorldAndResolution (Minecraft minecraft, int w, int
	 * h) { this.guiParticles = new GuiParticle(minecraft); this.mc = minecraft;
	 * this.width = w; this.height = h; this.buttonList.clear(); this.initGui(); }
	 */

	@Override
	@SuppressWarnings("unchecked")
	public void initGui() {
		this.updateTotalAndChapter();
		this.localWidth = (this.width / 2);
		this.localWidthLeft = (this.width / 2) - this.bookImageWidth;
		if (this.itemstackBook != null && this.itemstackBook.hasTagCompound()) {

			this.currentPage = this.itemstackBook.getTagCompound().getInteger("currentPage");
		}
		this.updatePage();

		int centerX = localWidth;
		int centerY = 180;
		int chapterBasePage = this.numTitlePages + this.numContentPages;

		// 翻页按钮
		this.buttonList.add(this.buttonNextPage = new TurnPageButton(101, centerX + bookImageWidth - 50, centerY, true,
				npcBookData));

		this.buttonList.add(this.buttonPreviousPage = new TurnPageButton(102, centerX - bookImageWidth + 24, centerY,
				false, npcBookData));

		// 跳转目录按钮（JumpBackButton）在最底部中央偏右
		this.buttonList.add(this.buttonJumpBack = new JumpBackButton(103, centerX - 5, centerY, false, npcBookData,
				chapterBasePage));

		// 章节导航按钮（居中偏左和偏右）
		this.buttonList.add(this.buttonPrevChapter = new PrevChapterButton(104, centerX - 29, centerY, false,
				npcBookData, chapterBasePage));

		this.buttonList.add(this.buttonNextChapter = new NextChapterButton(105, centerX + 19, centerY, false,
				npcBookData, chapterBasePage));

		// 最后更新按钮可见性
		this.updateButtonVisibility();

	}

	@SuppressWarnings("unchecked")
	public void addButton(GuiButton button) {
		this.buttonList.add(button);
	}

	private void updateTotalAndChapter() {
		// 清空旧的章节数据
		chapterList.clear();
		numTitlePages = 0;
		numContentPages = 0;
		numTextPages = 0;

		// 获取所有页面节点
		NodeList pageList = manual.getElementsByTagName("page");
		this.maxPages = pageList.getLength();

		boolean inContentSection = false;
		boolean contentEnded = false;

		for (int i = 0; i < pageList.getLength(); i++) {
			Element pageElement = (Element) pageList.item(i);
			String typeAttr = pageElement.getAttribute("type");

			boolean isContentPage = "novel_contents".equals(typeAttr);

			// 分类页面
			if (isContentPage) {
				inContentSection = true;
				numContentPages++;
			} else if (!inContentSection) {
				numTitlePages++;
			} else {
				contentEnded = true;
				numTextPages++;
			}

			// 章节检测
			NodeList children = pageElement.getChildNodes();
			for (int k = 0; k < children.getLength(); k++) {
				String nodeName = children.item(k).getNodeName();
				if ("chapter".equals(nodeName)) {
					int chapterPage = (i % 2 == 1) ? i - 1 : i;
					if (!chapterList.contains(chapterPage)) {
						chapterList.add(chapterPage);
					}
					break; // 每页只添加一次
				}
			}
		}
	}

	private void updateButtonVisibility() {
		// 翻页按钮可见性
		buttonPreviousPage.visible = currentPage > 1;
		buttonNextPage.visible = currentPage < maxPages - 2;

		// 判断是否在正文页
		boolean inTextSection = currentPage >= numTitlePages + numContentPages;

		// 跳转目录按钮：仅在正文中可见
		buttonJumpBack.visible = inTextSection;

		// 初始化章节按钮为不可见
		buttonPrevChapter.visible = false;
		buttonNextChapter.visible = false;

		if (inTextSection) {
			for (int i = 0; i < chapterList.size(); i++) {
				int chapterPage = chapterList.get(i);

				if (chapterPage < currentPage) {
					buttonPrevChapter.visible = true;
				} else if (chapterPage > currentPage) {
					buttonNextChapter.visible = true;
					break; // 找到第一个后续章节即可
				}
			}
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.enabled) {
			updateCurrentPage();
			changePage(button);
			initButtonList();
			updateButtonVisibility();
			updatePage();
		}
	}

	private void changePage(GuiButton button) {
		int buttonId = button.id;

		// 处理跳转按钮（<=100 是 jump 类型按钮）
		if (buttonId <= 100) {
			if (button instanceof GuiButtonJump) {
				setCurrentPage(((GuiButtonJump) button).jumpPage);
			}
			return; // 跳转后不继续执行下面逻辑
		}

		switch (buttonId) {
		case 101: // 下一页
			setCurrentPage(this.currentPage + 2);
			break;

		case 102: // 上一页
			setCurrentPage(this.currentPage - 2);
			break;

		case 103: // 跳转到目录首页
			setCurrentPage(numTitlePages);
			break;

		case 104: // 上一章节
			int previous = -1;
			for (int page : chapterList) {
				if (page < currentPage) {
					previous = page;
				} else {
					break;
				}
			}
			if (previous != -1) {
				setCurrentPage(previous);
			}
			break;

		case 105: // 下一章节
			for (int page : chapterList) {
				if (page > currentPage) {
					setCurrentPage(page);
					break;
				}
			}
			break;

		default:
			break;
		}
	}

	private void updateCurrentPage() {
		if (maxPages % 2 == 1) {
			if (currentPage > maxPages)
				currentPage = maxPages;
		} else {
			if (currentPage >= maxPages)
				currentPage = maxPages - 2;
		}

		if (currentPage % 2 == 1)
			currentPage--;
		if (currentPage < 0)
			currentPage = 0;
	}

	void updatePage() {
		this.updateCurrentPage();

		NodeList nList = manual.getElementsByTagName("page");

		pageLeft = loadPage(nList.item(currentPage), 0);
		pageRight = loadPage(nList.item(currentPage + 1), 1);
	}

	private BookPage loadPage(Node node, int side) {
		if (node == null || node.getNodeType() != Node.ELEMENT_NODE)
			return null;

		Element element = (Element) node;
		Class<? extends BookPage> clazz = MProxyClient.getPageClass(element.getAttribute("type"));
		if (clazz == null)
			return null;

		try {
			BookPage page = clazz.newInstance();
			page.init(this, side);
			page.readPageFromXML(element);
			if (page instanceof NpcBookPage) {
				((NpcBookPage) page).addButtons(this.localWidthLeft, this.localHeight, side);
			}
			return page;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(bookRight);
		this.drawTexturedModalRect(localWidth, localHeight, 0, 0, this.bookImageWidth, this.bookImageHeight);

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(bookLeft);
		this.drawTexturedModalRect(localWidthLeft, localHeight, 256 - this.bookImageWidth, 0, this.bookImageWidth,
				this.bookImageHeight);

		for (int k = 0; k < this.buttonList.size(); ++k) {
			((GuiButton) this.buttonList.get(k)).drawButton(mc, par1, par2);
		}

		// 渲染页面内容
		if (pageLeft != null)
			pageLeft.renderBackgroundLayer(localWidthLeft + 16, localHeight + 12);
		if (pageRight != null)
			pageRight.renderBackgroundLayer(localWidthLeft + 220, localHeight + 12);
		if (pageLeft != null)
			pageLeft.renderContentLayer(localWidthLeft + 16, localHeight + 12, npcBookData.isTranslatable);
		if (pageRight != null)
			pageRight.renderContentLayer(localWidthLeft + 220, localHeight + 12, npcBookData.isTranslatable);

		// 仅在正文部分绘制页码

		FontRenderer font = this.mc.fontRenderer;
		int bottomY = localHeight + bookImageHeight - 26;

		// 计算起始页码（正文页码从 1 开始）
		int pageNumberLeft = currentPage - numTitlePages - numContentPages + 1;
		int pageNumberRight = pageNumberLeft + 1;
		if (currentPage >= numTitlePages + numContentPages) {
			// 左页码
			String leftPageStr = String.valueOf(pageNumberLeft);
			int leftPageTextWidth = font.getStringWidth(leftPageStr);
			int leftPageCenterX = localWidthLeft + (bookImageWidth / 2) - (leftPageTextWidth / 2);
			font.drawString(leftPageStr, leftPageCenterX, bottomY, 0x000000);
		}
		// 右页码
		if (currentPage + 1 >= numTitlePages + numContentPages && currentPage + 1 < maxPages) { // 防止越界
			String rightPageStr = String.valueOf(pageNumberRight);
			int rightPageTextWidth = font.getStringWidth(rightPageStr);
			int rightPageCenterX = localWidthLeft + bookImageWidth + (bookImageWidth / 2) - (rightPageTextWidth / 2);
			font.drawString(rightPageStr, rightPageCenterX, bottomY, 0x000000);
		}
	}

	public int getCurrentPage() {
		return currentPage;
	}

	@SuppressWarnings("unchecked")
	public void initButtonList() {
		this.buttonList.clear();
		this.buttonList.add(this.buttonPreviousPage);
		this.buttonList.add(this.buttonNextPage);
		this.buttonList.add(this.buttonJumpBack);
		this.buttonList.add(this.buttonPrevChapter);
		this.buttonList.add(this.buttonNextChapter);
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
		this.updateCurrentPage();
		// System.out.println("Set Current Page = " + this.currentPage);

	}

	public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height, int texWidth,
			int texHeight) {
		float uScale = 1.0F / texWidth;
		float vScale = 1.0F / texHeight;

		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();

		tessellator.addVertexWithUV(x, y + height, this.zLevel, textureX * uScale, (textureY + height) * vScale);
		tessellator.addVertexWithUV(x + width, y + height, this.zLevel, (textureX + width) * uScale,
				(textureY + height) * vScale);
		tessellator.addVertexWithUV(x + width, y, this.zLevel, (textureX + width) * uScale, textureY * vScale);
		tessellator.addVertexWithUV(x, y, this.zLevel, textureX * uScale, textureY * vScale);

		tessellator.draw();
	}

	@Override
	public void onGuiClosed() {
		NoppesUtilPlayer.sendData(EnumPlayerPacket.CloseMantleBook, this.currentPage);
	}

}
