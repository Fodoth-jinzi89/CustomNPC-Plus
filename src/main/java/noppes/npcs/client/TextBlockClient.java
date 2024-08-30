package noppes.npcs.client;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import noppes.npcs.NoppesStringUtils;
import noppes.npcs.TextBlock;
import noppes.npcs.controllers.data.Dialog;

public class TextBlockClient extends TextBlock{
	private ChatStyle style;
	public int color = 0xe0e0e0;
	public int titleColor = 0xe0e0e0;
	public int titlePos = 0;
	private String name;
	private ICommandSender sender;

	public TextBlockClient(ICommandSender sender, Dialog dialog, Object... obs) {
		this(dialog.text, dialog.textWidth, false, obs);
		this.color = dialog.color;
		this.titleColor = dialog.titleColor;
		this.titlePos = dialog.titlePos;
		this.sender = sender;
	}

	public TextBlockClient(String name, String text, int lineWidth, int color, Object... obs) {
		this(text, lineWidth, false, obs);
		this.color = color;
		this.name = name;
	}
	
	public String getName(){
		if(sender != null)
			return sender.getCommandSenderName();
		return name;
	}
	
	/*
	 * public TextBlockClient(String text, int lineWidth, boolean mcFont, Object...
	 * obs){ style = new ChatStyle(); text = NoppesStringUtils.formatText(text,
	 * obs);
	 * 
	 * String line = ""; text = text.replace("\n", " \n "); text =
	 * text.replace("\r", " \r "); String[] words = text.split(" ");
	 * 
	 * FontRenderer font = Minecraft.getMinecraft().fontRenderer; for(String word :
	 * words){ if(word.isEmpty()) continue; if(word.length() == 1){ char c =
	 * word.charAt(0); if(c == '\r' || c == '\n'){ addLine(line); line = "";
	 * continue; } } String newLine; if(line.isEmpty()) newLine = word; else newLine
	 * = line + " " + word;
	 * 
	 * if((mcFont?font.getStringWidth(newLine): ClientProxy.Font.width(newLine)) >
	 * lineWidth){ addLine(line); line = word.trim(); } else{ line = newLine; } }
	 * if(!line.isEmpty()) addLine(line); }
	 */
	public TextBlockClient(String text, int lineWidth, boolean mcFont, Object... obs) {
	    style = new ChatStyle();
	    text = NoppesStringUtils.formatText(text, obs);
	    ArrayList<String> lines = new ArrayList<>();

	    if (text == null || text.isEmpty()) {
	        addLine("");
	        return;
	    }

	    lines.add(""); // 初始化第一行
	    FontRenderer font = Minecraft.getMinecraft().fontRenderer;

	    for (int i = 0; i < text.length(); i++) {
	        char c = text.charAt(i);
	        String currentChar = String.valueOf(c);

	        // 处理换行符
	        if (c == '\r' || c == '\n') {
	            addLine(lines.get(lines.size() - 1));
	            lines.add(""); // 新建一行
	            continue;
	        }

	        // 当前行的内容加上当前字符后的宽度
	        String currentLine = lines.get(lines.size() - 1);
	        int newWidth = mcFont ? font.getStringWidth(currentLine + currentChar) 
	                              : ClientProxy.Font.width(currentLine + currentChar);

	        // 如果超出行宽限制，换行
	        if (newWidth > lineWidth) {
	            addLine(currentLine); // 将当前行添加到 lines
	            lines.add(currentChar); // 新建一行并添加当前字符
	        } else {
	            // 否则，添加到当前行的末尾
	            lines.set(lines.size() - 1, currentLine + currentChar);
	        }

	        // 如果已经处理到最后一个字符，确保将当前行添加到 lines 中
	        if (i == text.length() - 1) {
	            addLine(lines.get(lines.size() - 1));
	        }
	    }
	}

	private void addLine(String text){
		ChatComponentText line = new ChatComponentText(text);
		line.setChatStyle(style);
		lines.add(line);
	}
}
