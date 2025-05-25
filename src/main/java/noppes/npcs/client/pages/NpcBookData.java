package noppes.npcs.client.pages;

import mantle.books.BookData;
import net.minecraft.util.ResourceLocation;

public class NpcBookData extends BookData{
	 public ResourceLocation buttonImage = new ResourceLocation("customnpcs", "textures/gui/npcbook.png");

	 public NpcBookData() {
		 super();
	 }
	 
	 public NpcBookData(ResourceLocation leftImage, ResourceLocation rightImage) {
		 super();
		 this.leftImage = leftImage;
		 this.rightImage = rightImage;
	 }
}
