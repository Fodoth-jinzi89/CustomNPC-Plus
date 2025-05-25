package noppes.npcs.client.pages;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import mantle.client.pages.TextPage;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.StatCollector;
import noppes.npcs.client.NpcBookFontRenderer;
import noppes.npcs.client.gui.GuiNpcManual;

public class NovelTextPage extends NpcBookPage{
	
    String npcText;

    @Override
    public void readPageFromXML(Element element) {
        NodeList nodes = element.getElementsByTagName("text");
        if (nodes != null) npcText = nodes.item(0).getTextContent();
    }

    @Override
    public void renderContentLayer(int localWidth, int localHeight, boolean isTranslatable) {
    	FontRenderer fontRendererObj = npcManual.mc.fontRenderer;
    	if (npcText != null && npcManual != null && npcManual.fonts != null) {
    		boolean isNpc = false;
    		NpcBookFontRenderer ft = null;
    		if(npcManual.fonts instanceof NpcBookFontRenderer) {
    			isNpc = true;
    			ft = (NpcBookFontRenderer)npcManual.fonts;
    		}
            
            if (isTranslatable) npcText = StatCollector.translateToLocal(npcText);
            
            if(isNpc) {
            	fontRendererObj.drawSplitString(npcText, localWidth, localHeight, 174, 0);
            } else {
            	manual.fonts.drawSplitString(npcText, localWidth, localHeight, 174, 0);
            }
            
    }
    }

}
