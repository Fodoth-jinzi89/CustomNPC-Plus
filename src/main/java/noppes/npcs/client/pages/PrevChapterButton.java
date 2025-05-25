package noppes.npcs.client.pages;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class PrevChapterButton extends GuiButton {


	public int ignorePages;
    private static ResourceLocation background;// = new ResourceLocation("tinker", "textures/gui/bookleft.png");

    public PrevChapterButton(int par1, int par2, int par3, boolean par4, NpcBookData data, int ignorePages) {
        super(par1, par2, par3, 11, 12, "");
        background = data.buttonImage;
        this.ignorePages = ignorePages;
    }

    /**
     * Draws this button to the screen.
     */
    public void drawButton(Minecraft par1Minecraft, int par2, int par3) {
        if (this.visible) {
            boolean var4 = par2 >= this.xPosition && par3 >= this.yPosition
                    && par2 < this.xPosition + this.width
                    && par3 < this.yPosition + this.height;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            par1Minecraft.getTextureManager().bindTexture(background);
            int var5 = 0;
            int var6 = 0;

            if (var4) {
                var5 += 11;
            }


            this.drawTexturedModalRect(this.xPosition, this.yPosition, var5, var6, 11, 12);
        }
    }
}