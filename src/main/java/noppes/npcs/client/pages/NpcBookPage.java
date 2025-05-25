package noppes.npcs.client.pages;

import org.w3c.dom.Element;

import mantle.client.gui.GuiManual;
import mantle.client.pages.BookPage;
import net.minecraft.client.Minecraft;
import noppes.npcs.client.gui.GuiNpcManual;

public abstract class NpcBookPage extends BookPage{

	public GuiNpcManual npcManual;
	//public int side;

	@Override
    public void init(GuiManual manual, int side) {
		super.init(manual,side);
		if(manual instanceof GuiNpcManual) {
			this.npcManual = (GuiNpcManual) manual;
		}
    }

	public void addButtons(int localWidth, int localHeight, int side) {}
   
}
