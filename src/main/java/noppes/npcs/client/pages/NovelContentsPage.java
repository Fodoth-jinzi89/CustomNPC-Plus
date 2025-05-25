package noppes.npcs.client.pages;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import mantle.client.gui.GuiManual;
import mantle.client.pages.BookPage;
import mantle.lib.client.MantleClientRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import noppes.npcs.client.gui.GuiNpcManual;

public class NovelContentsPage extends NpcBookPage {

    protected List<GuiButtonJump> buttonList = new ArrayList<>();
    protected List<LinkEntry> links = new ArrayList<>();
    String npcText;

    private static class LinkEntry {
        public String text;
        public int jumpPage;
        public int depth;  // 用于记录层级深度

        public LinkEntry(String text, int jumpPage, int depth) {
            this.text = text;
            this.jumpPage = jumpPage;
            this.depth = depth;
        }
    }

    @Override
    public void readPageFromXML(Element element) {
        links.clear();
        npcText = null;

        // 读取标题文本
        NodeList textNodes = element.getElementsByTagName("text");
        if (textNodes != null && textNodes.getLength() > 0) {
            npcText = textNodes.item(0).getTextContent();
        }

        // 获取直接子link节点（而非所有后代）
        NodeList children = element.getChildNodes();
        List<Element> directLinkElements = new ArrayList<>();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node instanceof Element && node.getNodeName().equals("link")) {
                directLinkElements.add((Element) node);
            }
        }

        // 将直接子link节点转换为NodeList以便传入parseLinks，或改为接受List<Element>
        parseLinks(directLinkElements, 0);
    }

    // 修改parseLinks接收List<Element>，简化递归处理
    private void parseLinks(List<Element> linkElements, int depth) {
        for (Element linkElement : linkElements) {
            // 读取当前 link 的 text 和 jump
            String text = "";
            int jump = 0;

            NodeList children = linkElement.getChildNodes();
            for (int j = 0; j < children.getLength(); j++) {
                Node child = children.item(j);
                if (!(child instanceof Element)) continue;

                String nodeName = child.getNodeName();
                String content = child.getTextContent();

                switch (nodeName) {
                    case "text":
                        text = content;
                        break;
                    case "jump":
                        try {
                            jump = Integer.parseInt(content);
                        } catch (NumberFormatException ignored) {
                        }
                        break;
                }
            }

            links.add(new LinkEntry(text, jump, depth));

            // 获取当前 link 元素的直接子 link 节点，递归调用
            NodeList childrenNodes = linkElement.getChildNodes();
            List<Element> childLinks = new ArrayList<>();
            for (int j = 0; j < childrenNodes.getLength(); j++) {
                Node child = childrenNodes.item(j);
                if (child instanceof Element && child.getNodeName().equals("link")) {
                    childLinks.add((Element) child);
                }
            }

            if (!childLinks.isEmpty()) {
                parseLinks(childLinks, depth + 1);
            }
        }
    }


    @Override
    public void addButtons(int localWidth, int localHeight, int side) {
        buttonList.clear();
        for (int i = 0; i < links.size(); i++) {
            LinkEntry link = links.get(i);
            String label = StatCollector.translateToLocal(link.text);
            int yOffset = label.length() > 40 ? 13 : 18;
            int widthAdd = 16 + side * (220 - 16 - (side * 8));
            int heightAdd = 12;
            GuiButtonJump button = new GuiButtonJump(i,
                    localWidth + widthAdd,
                    localHeight + heightAdd + 10 * i + yOffset,
                    180, 10,
                    label,
                    link.jumpPage,
                    link.depth,npcManual.numTitlePages + npcManual.numContentPages);
            npcManual.addButton(button);
            buttonList.add(button);
        }
    }

    @Override
    public void renderContentLayer(int localWidth, int localHeight, boolean isTranslatable) {
    	FontRenderer fontRendererObj = npcManual.mc.fontRenderer;
        if (npcText != null && npcManual != null && npcManual.fonts != null) {
            String displayText = isTranslatable ? StatCollector.translateToLocal(npcText) : npcText;
            displayText = "\u00a7l" + displayText;
            int textWidth = npcManual.fonts.getStringWidth(displayText);

            // 计算居中对齐的绘制起点
            int textX = localWidth - 16 + (npcManual.bookImageWidth - textWidth) / 2;
            int textY = localHeight + 4;

            // 绘制文本
            fontRendererObj.drawString(
                    displayText,
                    textX,
                    textY,
                    0
            );

            if(localWidth > 220) {
            	int lineY = textY + 10; // 根据字体高度适当偏移（通常字体高度为 8）
                int lineStartX = localWidth - 16 + 8; // 稍微内缩
                int lineEndX = localWidth - 16 + npcManual.bookImageWidth - 18;

                GuiNpcManual.drawRect(lineStartX, lineY, lineEndX, lineY + 1, 0xFF000000); // 黑色实线
            } else {
            	int lineY = textY + 10; // 根据字体高度适当偏移（通常字体高度为 8）
                int lineStartX = localWidth; // 稍微内缩
                int lineEndX = localWidth - 16 + npcManual.bookImageWidth - 18 + 8;

                GuiNpcManual.drawRect(lineStartX, lineY, lineEndX, lineY + 1, 0xFF000000); // 黑色实线
            }
            // 在文字下方绘制一条水平线
            
        }
    }




    public static class GuiButtonJump extends GuiButton {
    	public int ignorePages;
        public int jumpPage;
        public int depth;

        public GuiButtonJump(int id, int x, int y, int width, int height, String label, int jumpPage, int depth, int ignorePages) {
            super(id, x, y, width, height, label);
            this.ignorePages = ignorePages;
            this.jumpPage = jumpPage + ignorePages - 1;
            this.depth = depth;
        }
        


        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            if (!this.visible) return;

            FontRenderer fontRenderer = mc.fontRenderer;
            this.field_146123_n = mouseX >= this.xPosition && mouseY >= this.yPosition &&
                    mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            // 背景绘制
            if (this.field_146123_n) {
                drawRect(this.xPosition, this.yPosition,
                        this.xPosition + this.width, this.yPosition + this.height,
                        0x80FFA500); // 半透明橙色
            } else {
                drawRect(this.xPosition, this.yPosition,
                        this.xPosition + this.width, this.yPosition + this.height,
                        0x00000000); // 完全透明
            }

            this.mouseDragged(mc, mouseX, mouseY);

            // 绘制缩进点
            int dotSize = 4;
            int indentPerDepth = 10;
            int baseIndent = 4;
            int dotX = this.xPosition + baseIndent + depth * indentPerDepth;
            int dotY = this.yPosition + (this.height - dotSize) / 2;
            int dotColor = 0xFF000000;
            drawRect(dotX, dotY, dotX + dotSize, dotY + dotSize, dotColor);

            // 主文本绘制位置
            int textOffsetX = dotX + dotSize + 4;
            int textY = this.yPosition + (this.height - 8) / 2;

            // 显示文本
            String displayText = this.displayString;
            if (this.field_146123_n) {
                displayText = EnumChatFormatting.ITALIC + displayText;
            }

            // 页码文本（右对齐绘制）
            String pageText = String.valueOf(this.jumpPage + 1 - this.ignorePages);
            int pageTextWidth = fontRenderer.getStringWidth(pageText);
            int pageX = this.xPosition + this.width - pageTextWidth - 4; // 靠右 4 像素内缩

            // 避免主文本和页码重叠：设置主文本最大宽度
            int maxTextWidth = pageX - textOffsetX - 4; // 与页码保持4像素间隔
            String trimmedText = fontRenderer.trimStringToWidth(displayText, maxTextWidth);

            // 绘制主文本（左侧）
            fontRenderer.drawString(trimmedText, textOffsetX, textY, 0x000000);

            // 绘制页码（右侧）
            fontRenderer.drawString(pageText, pageX, textY, 0x555555); // 深灰色
        }
    }
}
