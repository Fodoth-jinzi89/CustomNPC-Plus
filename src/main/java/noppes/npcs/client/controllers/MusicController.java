package noppes.npcs.client.controllers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;


public class MusicController {

	public static MusicController Instance;
    public PositionedSoundRecord playing;
    public ResourceLocation playingResource;
    public Entity playingEntity;

	public MusicController(){
		Instance = this;
	}

	public void stopMusic(){
		SoundHandler handler = Minecraft.getMinecraft().getSoundHandler();
		if(playing != null)
			handler.stopSound(playing);
		handler.stopSounds();
		playingResource = null;
		playingEntity = null;
		playing = null;
	}

	public void playStreaming(String music, Entity entity){
		if(isPlaying(music)){
			return;
		}
		stopMusic();
		playingEntity = entity;
		playingResource = new ResourceLocation(music);
		SoundHandler handler = Minecraft.getMinecraft().getSoundHandler();
        playing = PositionedSoundRecord.func_147675_a(playingResource, (float)entity.posX, (float)entity.posY, (float)entity.posZ);
        handler.playSound(playing);
	}
	
	public void playStreaming(String music, Entity entity, float volume, float pitch, int offsetX, int offsetY, int offsetZ){
		if(isPlaying(music)){
			return;
		}
		stopMusic();
		playingEntity = entity;
		playingResource = new ResourceLocation(music);
		SoundHandler handler = Minecraft.getMinecraft().getSoundHandler();
        playing = new PositionedSoundRecord(playingResource, volume, pitch, (float)entity.posX + offsetX, (float)entity.posY + offsetY, (float)entity.posZ + offsetZ);
        handler.playSound(playing);
	}
	
	public void playMusic(String music, Entity entity) {
		if(isPlaying(music))
			return;
		stopMusic();
		playingResource = new ResourceLocation(music);

		playingEntity = entity;

		SoundHandler handler = Minecraft.getMinecraft().getSoundHandler();
        playing = PositionedSoundRecord.func_147673_a(playingResource);
        handler.playSound(playing);
	}
	


	public boolean isPlaying(String music) {
		ResourceLocation resource = new ResourceLocation(music);
		if(playingResource == null || !playingResource.equals(resource)){
			return false;
		}
    	return Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(playing);
	}

	public void playSound(String music, float x, float y, float z) {
		Minecraft.getMinecraft().theWorld.playSound(x, y, z, music, 1, 1, false);
	}
}
