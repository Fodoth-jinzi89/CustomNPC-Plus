package noppes.npcs.client.pages;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import mantle.client.pages.BookPage;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.StatCollector;
import noppes.npcs.client.NpcBookFontRenderer;
import noppes.npcs.client.gui.GuiNpcManual;

public class NovelChapterPage extends NpcBookPage {
	
	String chapter;
    String npcText;

    @Override
    public void readPageFromXML(Element element) {
        NodeList nodes = element.getElementsByTagName("chapter");
        if (nodes != null) chapter = nodes.item(0).getTextContent();
        nodes = element.getElementsByTagName("text");
        if (nodes != null) npcText = nodes.item(0).getTextContent();
    }

    @Override
    public void renderContentLayer(int localWidth, int localHeight, boolean isTranslatable) {
    	FontRenderer fontRendererObj = npcManual.mc.fontRenderer;
    	if (chapter != null && npcManual != null && npcManual.fonts != null) {
    		boolean isNpc = false;
    		NpcBookFontRenderer ft = null;
    		if(npcManual.fonts instanceof NpcBookFontRenderer) {
    			isNpc = true;
    			ft = (NpcBookFontRenderer)npcManual.fonts;
    		}
            String displayText = isTranslatable ? StatCollector.translateToLocal(chapter) : chapter;
            displayText = "\u00a7l" + displayText;
            int textWidth = npcManual.fonts.getStringWidth(displayText);

            // 计算居中对齐的绘制起点
            int textX = localWidth - 16 + (npcManual.bookImageWidth - textWidth) / 2;
            int textY = localHeight + 4;

            // 绘制文本
            if(isNpc) {
            	fontRendererObj.drawString(
                        displayText,
                        textX,
                        textY,
                        0
                );
            } else {
                npcManual.fonts.drawString(
                        displayText,
                        textX,
                        textY,
                        0
                );
            }


            // 在文字下方绘制一条水平线
            int lineY = textY + 10; // 根据字体高度适当偏移（通常字体高度为 8）
            int lineStartX = localWidth; // 稍微内缩
            int lineEndX = localWidth - 16 + npcManual.bookImageWidth - 18;

            GuiNpcManual.drawRect(lineStartX, lineY, lineEndX, lineY + 1, 0xFF000000); // 黑色实线
            
            if (isTranslatable) npcText = StatCollector.translateToLocal(npcText);
            
            if(isNpc) {
            	fontRendererObj.drawSplitString(npcText, localWidth, lineY + 4, 174, 0);
            } else {
            	manual.fonts.drawSplitString(npcText, localWidth, lineY + 4, 174, 0);
            }
            
    }
    }
}
