package noppes.npcs.client.gui.player;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.gui.util.GuiNpcTextArea;
import noppes.npcs.client.gui.util.GuiNpcTextField;
import noppes.npcs.client.gui.util.ITextfieldListener;
import noppes.npcs.constants.EnumPlayerPacket;
import noppes.npcs.controllers.data.Quest;

public class GuiScenarioBook2 extends GuiNPCInterface implements ITextfieldListener {
    private static final Logger logger = LogManager.getLogger();
	private static final ResourceLocation bookGuiTextures = new ResourceLocation("textures/gui/book.png");
    private EntityPlayer editingPlayer;
	public int bookImageWidth = 220;
	public int bookImageHeight = 256;
	public int spacing = 30;
	public ItemStack book;
	public boolean isinhand;
    private final boolean bookIsUnsigned;
	private boolean signing = false;
	private boolean bookModified = false;
    private int updateCount;
    private int bookTotalPages = 1;
    private int currentPage;
    private NBTTagList bookPages;
    private String bookTitle = "";
	private GuiNpcTextArea textarea;
    
	public GuiScenarioBook2(EntityPlayer entityPlayer, ItemStack item) {
		super();
        xSize = 220;
        ySize = 256;
        this.editingPlayer = entityPlayer;
        this.book = item;
        this.drawDefaultBackground = false;
        title = "";
        this.bookIsUnsigned = true;
        this.loadPagesFromNBT(item);
	}
	
	protected void loadPagesFromNBT(ItemStack item) {
        if (item.hasTagCompound())
        {
            NBTTagCompound nbttagcompound = item.getTagCompound();
            this.bookPages = nbttagcompound.getTagList("pages", 8);

            if (this.bookPages != null)
            {
                this.bookPages = (NBTTagList)this.bookPages.copy();
                this.bookTotalPages = this.bookPages.tagCount();

                if (this.bookTotalPages < 1)
                {
                    this.bookTotalPages = 1;
                }
            }
        }
        logger.warn("this.bookPages == null" + (this.bookPages == null));
        if (this.bookPages.tagCount() == 0)
        {
            this.bookPages = new NBTTagList();
            this.bookPages.appendTag(new NBTTagString(""));
            this.bookTotalPages = 1;
            logger.warn("this.bookPagesddd" + this.bookPages.tagCount());
        }
	}
	
    public void initGui(){
    	/**
    	 * 初始化书籍 GUI，添加按钮并根据状态设置其可见性和功能。
    	 *
    	 * 按钮 ID 说明：
    	 * 0 - Done：完成/关闭界面
    	 * 1 - Next Page：下一页
    	 * 2 - Previous Page：上一页
    	 * 3 - Sign：进入签名模式（仅未签名书籍）
    	 * 4 - Cancel：取消签名模式
    	 * 5 - Finalize：完成签名（需输入标题）
    	 *
    	 * 实际可见性和启用状态由 updateButtons() 动态控制。
    	 */


        if (this.bookIsUnsigned){
        	this.addButton(new GuiNpcButton(3, guiLeft + 26, guiTop+ 20, 50, 20, StatCollector.translateToLocal("book.signButton")));
        	this.addButton(new GuiNpcButton(0, guiLeft + 26, guiTop+ 40, 50, 20, StatCollector.translateToLocal("gui.done")));
        	this.addButton(new GuiNpcButton(5, guiLeft + 26, guiTop+ 60, 50, 20, StatCollector.translateToLocal("book.finalizeButton")));
        	this.addButton(new GuiNpcButton(4, guiLeft + 26, guiTop+ 80, 50, 20, StatCollector.translateToLocal("gui.cancel")));
            //this.buttonList.add(this.buttonSign = new GuiButton(3, this.width / 2 - 100, 4 + this.bookImageHeight, 98, 20, I18n.format("book.signButton", new Object[0])));
            //this.buttonList.add(this.buttonDone = new GuiButton(0, this.width / 2 + 2, 4 + this.bookImageHeight, 98, 20, I18n.format("gui.done", new Object[0])));
            //this.buttonList.add(this.buttonFinalize = new GuiButton(5, this.width / 2 - 100, 4 + this.bookImageHeight, 98, 20, I18n.format("book.finalizeButton", new Object[0])));
            //this.buttonList.add(this.buttonCancel = new GuiButton(4, this.width / 2 + 2, 4 + this.bookImageHeight, 98, 20, I18n.format("gui.cancel", new Object[0])));
        	textarea = new GuiNpcTextArea(10, this, guiLeft + 80, guiTop + 20, 200, 100, this.getCurrentPageText());
        	textarea.setEnableBackgroundDrawing(false);
        	textarea.setShadow(false);
        	textarea.setColor(0x000000);
        	this.addTextField(textarea);
        }
        else{
        	this.addButton(new GuiNpcButton(0, guiLeft + 26, guiTop+ 40, 50, 20, StatCollector.translateToLocal("gui.done")));
            //this.buttonList.add(this.buttonDone = new GuiButton(0, this.width / 2 - 100, 4 + this.bookImageHeight, 200, 20, I18n.format("gui.done", new Object[0])));
        }

        int i = (this.width - this.bookImageWidth) / 2;
        byte b0 = 2;
        this.addButton(new GuiNpcButton(1, i + 120, b0 + 154, 50, 20, StatCollector.translateToLocal("book.nextPage")));
        this.addButton(new GuiNpcButton(2, i + 38, b0 + 154, 50, 20, StatCollector.translateToLocal("book.prevPage")));
        //this.buttonList.add(this.buttonNextPage = new GuiBook.NextPageButton(1, i + 120, b0 + 154, true));
        //this.buttonList.add(this.buttonPreviousPage = new GuiBook.NextPageButton(2, i + 38, b0 + 154, false));
        
        
        this.updateButtons();
    }
    
    private void updateButtons() {
        this.getButton(1).visible = !this.signing && (this.currentPage < this.bookTotalPages - 1 || this.bookIsUnsigned);
        this.getButton(2).visible = !this.signing && this.currentPage > 0;
        this.getButton(0).visible = !this.bookIsUnsigned || !this.signing;

        if (this.bookIsUnsigned) {
            this.getButton(3).visible = !this.signing;
            this.getButton(4).visible = this.signing;
            this.getButton(5).visible = this.signing;
            this.getButton(5).enabled = this.bookTitle.trim().length() > 0;
        }
    }
    
    private void sendBookToServer(boolean sign){
        if (this.bookIsUnsigned){
            if (this.bookPages != null){

                while (this.bookPages.tagCount() > 1){
                    String s = this.bookPages.getStringTagAt(this.bookPages.tagCount() - 1);

                    if (s.length() != 0)
                        break;

                    this.bookPages.removeTag(this.bookPages.tagCount() - 1);
                }

                if (this.book.hasTagCompound()){
                    NBTTagCompound nbttagcompound = this.book.getTagCompound();
                    nbttagcompound.setTag("pages", this.bookPages);
                }
                else{
                    this.book.setTagInfo("pages", this.bookPages);
                }

                if (sign)
                {
                    this.book.setTagInfo("author", new NBTTagString(this.editingPlayer.getCommandSenderName()));
                    this.book.setTagInfo("title", new NBTTagString(this.bookTitle.trim()));
                    this.book.func_150996_a(Items.written_book);
                }
                
                //System.out.println("send nbt" + book.writeToNBT(new NBTTagCompound()).toString());

                NoppesUtilPlayer.sendData(EnumPlayerPacket.SaveScenarioBook, sign, book.writeToNBT(new NBTTagCompound()));

            }
        }
    }
    
    
    /**
     * 响应按钮点击事件，根据按钮 ID 执行对应的操作：
     * 0 - 完成并发送书本内容（未签名）
     * 1 - 下一页，必要时添加新页
     * 2 - 上一页
     * 3 - 进入签名模式
     * 4 - 取消签名模式
     * 5 - 完成签名并发送书本内容
     */
    @Override
    public void buttonEvent(GuiButton button) {
        if (!button.enabled) {
            return;
        }

        switch (button.id) {
            case 0:
                // Done: 退出界面并提交未签名书本
                this.mc.displayGuiScreen(null);
                this.sendBookToServer(false);
                break;

            case 1:
                // Next Page: 翻到下一页，若书未签名且在末页则自动添加新页
            	if(this.bookIsUnsigned) {
            		this.saveCurrentPage();
            		if (this.currentPage >= this.bookTotalPages - 1) {
            			this.addNewPage();
            		}
            		++this.currentPage;
            	} else if (this.currentPage < this.bookTotalPages - 1) {
            		++this.currentPage;
            	}
                break;

            case 2:
                // Previous Page: 返回上一页
                if(this.bookIsUnsigned) {
                	this.saveCurrentPage();
                }
                if (this.currentPage > 0) {
                    --this.currentPage;
                }
                break;

            case 3:
                // Sign: 进入签名模式，仅在未签名书本中允许
                if (this.bookIsUnsigned) {
                    this.signing = true;
                }
                break;

            case 4:
                // Cancel: 取消签名模式
                if (this.signing) {
                    this.signing = false;
                }
                break;

            case 5:
                // Finalize: 提交签名书本并关闭界面
                if (this.signing) {
                    this.sendBookToServer(true);
                    this.mc.displayGuiScreen(null);
                }
                break;

            default:
                break;
        }

        // 每次点击后更新按钮可见性与状态
        this.updateButtons();
    }
    
    /**
     * 尝试向书本添加一个新页面：
     * - 最多允许 50 页；
     * - 每页内容初始化为空字符串；
     * - 添加新页后更新总页数；
     * - 标记书本已被编辑。
     */
    private void addNewPage() {
        // 如果页面列表存在且未超过最大页数限制（50页）
        if (this.bookPages != null && this.bookPages.tagCount() < 50) {
            // 添加一个新的空白页面
            this.bookPages.appendTag(new NBTTagString(""));

            // 总页数加一
            ++this.bookTotalPages;

            // 标记书本已修改（用于后续保存或提交）
            this.bookModified = true; // 将原 field_146481_r 改为更具语义的 bookModified
        }
    }
    
    /**
     * 处理键盘输入事件，根据当前编辑状态区分普通页面输入与标题输入。
     */
    @Override
	public void keyTyped(char typedChar, int keyCode) {
        super.keyTyped(typedChar, keyCode);

        if (this.bookIsUnsigned) {
            if (this.signing) {
                this.handleTitleInput(typedChar, keyCode); // 原 func_146460_c
            } else {
                //this.handlePageInput(typedChar, keyCode);  // 原 keyTypedInBook
            }
        }
    }
    
    /**
     * 处理书页内容输入，包括字符输入、粘贴、删除、换行等。
     */
    private void handlePageInput(char typedChar, int keyCode) {
        switch (typedChar) {
            case 22: // Ctrl + V（粘贴）
                this.insertText(GuiScreen.getClipboardString());
                return;
            default:
                switch (keyCode) {
                    case 14: // Backspace
                        String content = this.getCurrentPageText();
                        if (!content.isEmpty()) {
                            this.setCurrentPageText(content.substring(0, content.length() - 1));
                        }
                        return;
                    case 28: // Enter
                    case 156: // Numpad Enter
                        this.insertText("\n");
                        return;
                    default:
                        if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                            this.insertText(Character.toString(typedChar));
                        }
                }
        }
    }
    
    /**
     * 在签名模式下处理书籍标题的输入，包括输入、删除、提交。
     */
    private void handleTitleInput(char typedChar, int keyCode) {
        switch (keyCode) {
            case 14: // Backspace
                if (!this.bookTitle.isEmpty()) {
                    this.bookTitle = this.bookTitle.substring(0, this.bookTitle.length() - 1);
                    this.updateButtons();
                }
                return;

            case 28: // Enter
            case 156: // Numpad Enter
                if (!this.bookTitle.isEmpty()) {
                    this.sendBookToServer(true); // 提交签名书籍
                    this.mc.displayGuiScreen(null); // 关闭界面
                }
                return;

            default:
                if (this.bookTitle.length() < 16 && ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                    this.bookTitle += typedChar;
                    this.updateButtons();
                    this.bookModified = true; // 原 field_146481_r
                }
        }
    }
    
    /**
     * 获取当前页面的文本内容。
     */
    private String getCurrentPageText() {
        if (this.bookPages != null && this.currentPage >= 0 && this.currentPage < this.bookPages.tagCount()) {
            return this.bookPages.getStringTagAt(this.currentPage);
        }
        return "";
    }

    /**
     * 设置当前页面的文本内容。
     */
    private void setCurrentPageText(String text) {
    	//logger.warn("this.bookPages" + (this.bookPages != null));
    	//logger.warn("this.currentPage" + (this.currentPage >= 0));
    	//logger.warn("this.currentPage" + (this.currentPage));
    	//logger.warn("this.bookPage" + (this.bookPages.tagCount()));
        if (this.bookPages != null && this.currentPage >= 0 && this.currentPage < this.bookPages.tagCount()) {
            this.bookPages.func_150304_a(this.currentPage, new NBTTagString(text));
            this.bookModified = true; // 原 field_146481_r
			//logger.warn("CurrentPage text is" + this.getCurrentPageText());
        }
    }
    
    /**
     * 向当前页面插入指定文本，若宽度或字符数未超限。
     */
    private void insertText(String newText) {
        String current = this.getCurrentPageText();
        String combined = current + newText;

        // 检查文本宽度与长度限制
        int width = this.fontRendererObj.splitStringWidth(combined + EnumChatFormatting.BLACK + "_", 118);

        if (width <= 118 && combined.length() < 256) {
            this.setCurrentPageText(combined);
        }
    }
    
    /**
     * 渲染书本界面。根据当前是否处于签名模式，展示书页或标题输入框。
     *
     * @param mouseX 鼠标X坐标
     * @param mouseY 鼠标Y坐标
     * @param partialTicks 渲染的时间差值（用于平滑动画）
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // 设置颜色为白色，全不透明
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        // 绑定书本界面贴图
        this.mc.getTextureManager().bindTexture(bookGuiTextures);

        // 计算书本左上角位置
        int left = (this.width - this.bookImageWidth) / 2;
        int top = 2;

        // 绘制书本背景
        this.drawTexturedModalRect(left, top, 0, 0, this.bookImageWidth, this.bookImageHeight);

        String pageText;
        String headerText;
        int textWidth;

        if (this.signing) {
            // ------------------------------
            // 签名模式：输入书名与作者
            // ------------------------------
            pageText = this.bookTitle;

            // 光标闪烁效果
            if (this.bookIsUnsigned) {
                pageText += (this.updateCount / 6 % 2 == 0)
                    ? EnumChatFormatting.BLACK + "_"
                    : EnumChatFormatting.GRAY + "_";
            }

            // 显示 “书名” 提示文本
            headerText = I18n.format("book.editTitle");
            textWidth = this.fontRendererObj.getStringWidth(headerText);
            this.fontRendererObj.drawString(headerText, left + 36 + (116 - textWidth) / 2, top + 32, 0);

            // 显示书名文本
            int titleWidth = this.fontRendererObj.getStringWidth(pageText);
            this.fontRendererObj.drawString(pageText, left + 36 + (116 - titleWidth) / 2, top + 48, 0);

            // 显示作者信息
            String authorLine = I18n.format("book.byAuthor", this.editingPlayer.getCommandSenderName());
            int authorWidth = this.fontRendererObj.getStringWidth(authorLine);
            this.fontRendererObj.drawString(EnumChatFormatting.DARK_GRAY + authorLine, left + 36 + (116 - authorWidth) / 2, top + 58, 0);

            // 显示签名前警告
            String warning = I18n.format("book.finalizeWarning");
            this.fontRendererObj.drawSplitString(warning, left + 36, top + 80, 116, 0);
        } else {
            // ------------------------------
            // 正常编辑模式：展示页码与当前页内容
            // ------------------------------
            headerText = I18n.format("book.pageIndicator", this.currentPage + 1, this.bookTotalPages);
            pageText = "";

            // 获取当前页文本
            if (this.bookPages != null && this.currentPage >= 0 && this.currentPage < this.bookPages.tagCount()) {
                pageText = this.getCurrentPageText();
            }

            // 未签名状态下添加光标（闪烁或方向性下划线）
//            if (this.bookIsUnsigned) {
//                if (this.fontRendererObj.getBidiFlag()) {
//                    pageText += "_";
//                } else {
//                    pageText += (this.updateCount / 6 % 2 == 0)
//                        ? EnumChatFormatting.BLACK + "_"
//                        : EnumChatFormatting.GRAY + "_";
//                }
//            }

            // 渲染页码标题
            textWidth = this.fontRendererObj.getStringWidth(headerText);
            this.fontRendererObj.drawString(headerText, left - textWidth + this.bookImageWidth - 44, top + 16, 0);
            // 渲染书页正文内容
            //this.fontRendererObj.drawSplitString(pageText, left + 36, top + 32, 116, 0);
            
        }

        // 渲染按钮与交互控件
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
	@Override
	public void unFocused(GuiNpcTextField textfield) {
		if(textfield.id == 10)
			//logger.warn("Textfield text is" + textfield.getText());
			this.setCurrentPageText(textfield.getText());
	}
	
    public void updateScreen(){
        super.updateScreen();
        this.updateTextArea();

        ++this.updateCount;
    }
    
    public void updateTextArea() {
        String s = this.bookPages.getStringTagAt(this.currentPage);
        if(!this.getTextField(10).isFocused()) {
        if(s != null) {
        this.getTextField(10).setText(this.bookPages.getStringTagAt(this.currentPage));}
        else {
        	 this.getTextField(10).setText("");
        }}
    }
    @Override
    public boolean doesGuiPauseGame(){
        return false;
    }
    
	@Override
	public void save() {
		this.setCurrentPageText(this.getTextField(10).getText());
	}
	
	public void saveCurrentPage() {
		String s = this.getTextField(10).getText();
        if(s != null) {
        	this.setCurrentPageText(s);
        } else {
        	this.setCurrentPageText("");
        }
        //this.sendBookToServer(false);
	}

}
