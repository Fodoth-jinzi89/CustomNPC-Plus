package noppes.npcs.client.pages;

import static mantle.lib.CoreRepo.logger;

import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import mantle.client.SmallFontRenderer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class NovelDedicatePage extends NpcBookPage {

	String dedication;
	String text;
	String location;
	ResourceLocation image;

	@Override
	public void readPageFromXML(Element element) {
		NodeList nodes = element.getElementsByTagName("dedication");
		if (nodes != null)
			dedication = nodes.item(0).getTextContent();
		nodes = element.getElementsByTagName("text");
		if (nodes != null)
			text = nodes.item(0).getTextContent();
		nodes = element.getElementsByTagName("location");
		if (nodes != null) {
			location = nodes.item(0).getTextContent();
			image = new ResourceLocation(location);
			if (image == null) {
				logger.warn(nodes.item(0).getTextContent() + " could not be found in the image cache(location)!");
			}
		}

	}

	@Override
	public void renderContentLayer(int localWidth, int localHeight, boolean isTranslatable) {
		
	    if (npcManual == null || npcManual.fonts == null) return;

	    SmallFontRenderer font = npcManual.fonts;

	    String displayDedication = dedication;
	    String displayText = text;
	    if (isTranslatable) {
	        displayDedication = StatCollector.translateToLocal(dedication);
	        displayText = StatCollector.translateToLocal(text);
	    }

	    // 加粗处理
	    String boldDedication = "\u00a7l" + displayDedication;
	    String italicText = "\u00a7o" + displayText;

	    // 高度测量
	    int dedicationHeight = font.listFormattedStringToWidth(boldDedication, 162).size() * font.FONT_HEIGHT;
	    int textHeight = font.listFormattedStringToWidth(displayText, 162).size() * font.FONT_HEIGHT;

	    int imageHeight = 128;
	    int spacing = 0;
	    int totalHeight = imageHeight + spacing + dedicationHeight + spacing + textHeight;

	    // 居中起点
	    int startY = localHeight - 12 + (npcManual.bookImageHeight - totalHeight) / 2;
	    int currentY = startY + imageHeight + spacing;

	    drawCenteredMultiline(font, boldDedication, localWidth - 16, currentY, 162, 0x000000);
	    currentY += dedicationHeight + spacing;

	    drawCenteredMultiline(font, italicText, localWidth - 16, currentY, 162, 0x000000);

	}
	
	

	
	@Override
	public void renderBackgroundLayer(int localWidth, int localHeight) {
	    if (image != null) {
	        npcManual.getMC().getTextureManager().bindTexture(image);
	    }

	    // 居中绘制 128×128 图片
	    int imageWidth = 128;
	    int imageHeight = 128;

	    int imageX = localWidth - 16 + (npcManual.bookImageWidth - imageWidth) / 2;
	    int imageY = localHeight + 4 + (npcManual.bookImageHeight - (imageHeight + 6 + 40 + 6 + 40)) / 2; // 图片开始于整体居中起点

	    npcManual.drawTexturedModalRect(imageX, imageY, 0, 0, imageWidth, imageHeight,imageWidth, imageHeight);
	}
	
	private void drawCenteredMultiline(SmallFontRenderer font, String text, int localWidth, int y, int maxWidth, int color) {
		FontRenderer fontRendererObj = npcManual.mc.fontRenderer;
		List<String> lines = fontRendererObj.listFormattedStringToWidth(text, maxWidth);
	    for (String line : lines) {
	        int textWidth = fontRendererObj.getStringWidth(line);
	        int x = localWidth + (npcManual.bookImageWidth - textWidth) / 2;
	        fontRendererObj.drawString(line, x, y, color);
	        y += font.FONT_HEIGHT;
	    }
	}



}
