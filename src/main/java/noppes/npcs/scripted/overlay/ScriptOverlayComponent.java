package noppes.npcs.scripted.overlay;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import noppes.npcs.scripted.gui.*;
import noppes.npcs.scripted.interfaces.ICustomGuiComponent;
import noppes.npcs.scripted.interfaces.ICustomOverlayComponent;
import noppes.npcs.scripted.interfaces.IOverlayTexturedRect;

public abstract class ScriptOverlayComponent implements ICustomOverlayComponent {
    int id;
    int posX;
    int posY;
    int alignment = 0;
    int color = 0xFFFFFF;
    float alpha = 1.0F;
    /*  ========ALIGNMENTS========
        0           1           2

        3           4           5

        6           7           8
     */

    public ScriptOverlayComponent(){
    }

    public int getID() {
        return this.id;
    }

    public ICustomOverlayComponent setID(int id) {
        this.id = id;
        return this;
    }

    public int getPosX() {
        return this.posX;
    }

    public int getPosY() {
        return this.posY;
    }

    public ICustomOverlayComponent setPos(int x, int y) {
        this.posX = x;
        this.posY = y;
        return this;
    }

    public int getAlignment() {
        return this.alignment;
    }

    public void setAlignment(int alignment) {
        this.alignment = alignment;
    }

    public int getColor() {
        return this.color;
    }

    public ICustomOverlayComponent setColor(int color) {
        this.color = color;
        return this;
    }

    public float getAlpha() {
        return this.alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public abstract int getType();

    public NBTTagCompound toNBT(NBTTagCompound nbt) {
        nbt.setInteger("id", this.id);
        nbt.setIntArray("pos", new int[]{this.posX, this.posY});
        nbt.setInteger("alignment",this.alignment);
        nbt.setInteger("color", this.color);
        nbt.setFloat("alpha",this.alpha);

        nbt.setInteger("type", this.getType());
        return nbt;
    }

    public ScriptOverlayComponent fromNBT(NBTTagCompound nbt) {
        this.setID(nbt.getInteger("id"));
        this.setPos(nbt.getIntArray("pos")[0], nbt.getIntArray("pos")[1]);
        this.setAlignment(nbt.getInteger("alignment"));
        this.setColor(nbt.getInteger("color"));
        this.setAlpha(nbt.getInteger("alpha"));

        return this;
    }

    public static ScriptOverlayComponent createFromNBT(NBTTagCompound nbt) {
        switch(nbt.getInteger("type")) {
            case 0:
                return (new ScriptOverlayTexturedRect()).fromNBT(nbt);
            case 1:
                return (new ScriptOverlayLabel()).fromNBT(nbt);
            case 2:
                return (new ScriptOverlayLine()).fromNBT(nbt);
            default:
                return null;
        }
    }
}
