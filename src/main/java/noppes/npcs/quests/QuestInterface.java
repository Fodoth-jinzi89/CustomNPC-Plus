package noppes.npcs.quests;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.Server;
import noppes.npcs.api.handler.data.IQuestInterface;
import noppes.npcs.api.handler.data.IQuestObjective;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerQuestData;

import java.util.Vector;

public abstract class QuestInterface implements IQuestInterface {
	public int questId;
	public abstract void writeEntityToNBT(NBTTagCompound compound);
	public abstract void readEntityFromNBT(NBTTagCompound compound);
	public abstract boolean isCompleted(PlayerData player);
	public void handleComplete(EntityPlayer player) {
		PlayerQuestData questData = PlayerDataController.instance.getPlayerData(player).questData;
		if(questData != null && questData.getTrackedQuest() != null){
			if (this.questId == PlayerDataController.instance.getPlayerData(player).questData.getTrackedQuest().getId()) {
				PlayerDataController.instance.getPlayerData(player).questData.untrackQuest();
			}
		}
	}
	public abstract Vector<String> getQuestLogStatus(EntityPlayer player);
	public abstract IQuestObjective[] getObjectives(EntityPlayer var1);
}
