//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package noppes.npcs.client.gui.script;

import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.client.Client;
import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.controllers.ScriptContainer;
import noppes.npcs.controllers.data.ForgeDataScript;

import java.util.List;

public class GuiScriptForge extends GuiScriptInterface {
    private ForgeDataScript script = new ForgeDataScript();

    public GuiScriptForge() {
        this.handler = this.script;
        Client.sendData(EnumPacketServer.ScriptForgeGet);
    }

    public void setGuiData(NBTTagCompound compound) {
        if (!compound.hasKey("Tab")) {
            script.setLanguage(compound.getString("ScriptLanguage"));
            script.setEnabled(compound.getBoolean("ScriptEnabled"));
            super.setGuiData(compound);
        } else {
            int tab = compound.getInteger("Tab");
            ScriptContainer container = new ScriptContainer(script);
            container.readFromNBT(compound.getCompoundTag("Script"));
            if (script.getScripts().isEmpty()) {
                for (int i = 0; i < compound.getInteger("TotalScripts"); i++) {
                    script.getScripts().add(new ScriptContainer(script));
                }
            }
            script.getScripts().set(tab,container);
            initGui();
        }
    }

    public void save() {
        super.save();
        List<ScriptContainer> containers = this.script.getScripts();
        for (int i = 0; i < containers.size(); i++) {
            ScriptContainer container = containers.get(i);
            Client.sendData(EnumPacketServer.ScriptForgeSave, i, container.writeToNBT(new NBTTagCompound()));
        }
        NBTTagCompound scriptData = new NBTTagCompound();
        scriptData.setString("ScriptLanguage", this.script.getLanguage());
        scriptData.setBoolean("ScriptEnabled", this.script.getEnabled());
        Client.sendData(EnumPacketServer.ScriptForgeSave, -1, scriptData);
    }
}
