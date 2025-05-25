package noppes.npcs.constants;

import java.util.ArrayList;

public enum EnumPartyRequirements {
    //All: 所有玩家都必须接这个任务
    //Valid: 所有玩家都必须接了或者完成了这个任务
	Leader("party.leader"),
	All("party.all"),
	Valid("party.valid");

	public final String name;

	EnumPartyRequirements(String name){
		this.name = name;
	}
	public static String[] names(){
		ArrayList<String> list = new ArrayList<String>();
		for(EnumPartyRequirements e : values())
			list.add(e.name);

		return list.toArray(new String[list.size()]);
	}
}
