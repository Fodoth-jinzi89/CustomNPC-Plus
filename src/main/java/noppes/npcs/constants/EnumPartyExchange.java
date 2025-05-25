package noppes.npcs.constants;

import java.util.ArrayList;

public enum EnumPartyExchange {
    //All: 所有玩家都可以获得奖励
	//Enrolled: 只有接了任务的能获得奖励
    //Valid: 只有接了或者完成了任务的才能获得奖励
	Leader("party.leader"),
	All("party.all"),
	Enrolled("party.enrolled"),
    Valid("party.valid");

	public final String name;

	EnumPartyExchange(String name){
		this.name = name;
	}
	public static String[] names(){
		ArrayList<String> list = new ArrayList<String>();
		for(EnumPartyExchange e : values())
			list.add(e.name);

		return list.toArray(new String[list.size()]);
	}
}
