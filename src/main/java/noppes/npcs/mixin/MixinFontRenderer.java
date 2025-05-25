package noppes.npcs.mixin;

import com.ibm.icu.text.BreakIterator;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.client.gui.FontRenderer;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.*;

@Mixin({ FontRenderer.class })
public abstract class MixinFontRenderer {

	@Shadow
	private boolean randomStyle;
	@Shadow
	private boolean boldStyle;
	@Shadow
	private boolean strikethroughStyle;
	@Shadow
	private boolean underlineStyle;
	@Shadow
	private boolean italicStyle;
	@Shadow
	private boolean unicodeFlag;

	@Shadow
	private float posX;
	@Shadow
	private float posY;
	@Shadow
	private int textColor;
	@Shadow
	private float alpha;
	@Shadow
	private float red;
	@Shadow
	private float blue;
	@Shadow
	private float green;

	@Shadow
	private int[] colorCode;

	@Shadow
	protected abstract float renderCharAtPos(int charIndex, char character, boolean italic);

	@Shadow
	public int FONT_HEIGHT;
	@Shadow
	private int[] charWidth;
	@Shadow
	private Random fontRandom;

	@ModifyArg(method = "renderString", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;renderStringAtPos(Ljava/lang/String;Z)V"), index = 0)
	private String cleanIllegalCodesBeforeRender(String text) {
		return cleanIllegalFormatCodes(text);
	}

	@Redirect(method = "renderStringAtPos", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/FontRenderer;textColor:I", ordinal = 0, opcode = Opcodes.PUTFIELD))
	private void disableTextColorSet(FontRenderer instance, int value) {
		// Do nothing: 屏蔽 this.textColor = k;
	}

	@Inject(method = "renderStringAtPos", at = @At(value = "INVOKE", target = "Ljava/lang/String;length()I", ordinal = 1))
	private void captureLocal(String text, boolean shadow, CallbackInfo ci, @Share("i") LocalIntRef intRef,
			@Local(ordinal = 0) int index) {
		intRef.set(index);
	}

	@Redirect(method = "renderStringAtPos", at = @At(value = "INVOKE", target = "Ljava/lang/String;length()I", ordinal = 1))
	private int hackSeg(String instance, @Share("i") LocalIntRef intRef) {
		int i = intRef.get() + 1;
		if (i < instance.length()) {
			char c = instance.charAt(i);
			if (!isFormatSpecial(c) && !isFormatColor(c)) {
				return -1;
			}
		}
		return instance.length();
	}

	@Inject(method = "listFormattedStringToWidth", at = @At("HEAD"), cancellable = true)
	private void wrapFormattedStringToWidthAdvanced(String text, int width, CallbackInfoReturnable<List<String>> cir) {
		if (text.isEmpty()) {
			cir.setReturnValue(Collections.singletonList(""));
			return;
		}

		BreakIterator breaker = BreakIterator.getLineInstance();
		breaker.setText(text);

		List<String> result = new ArrayList<>();
		StringBuilder line = new StringBuilder();
		StringBuilder format = new StringBuilder();

		int lineWidth = 0;
		int lastBreak = 0;
		int prevFormatLength = 0;
		boolean bold = false;

		char[] chars = text.toCharArray();
		int[] widths = new int[chars.length];
		String[] formats = new String[chars.length];

		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];

			switch (c) {
			case '\n':
				result.add(line.toString());
				lastBreak = i + 1;
				line.setLength(0);
				line.append(format);
				lineWidth = 0;
				prevFormatLength = format.length();
				continue;

			case '§':
				if (i + 1 < chars.length) {
					char f = chars[i + 1];
					boolean isColor = isFormatColor(f);
					boolean isSpecial = isFormatSpecial(f);

					if (isColor || isSpecial) {
						if (f == 'r' || f == 'R') {
							bold = false;
							format.setLength(0);
						} else if (isColor) {
							bold = false;
							format.setLength(0);
							format.append('§').append(f);
						} else if (f == 'l' || f == 'L') {
							bold = true;
							format.append('§').append(f);
						} else {
							format.append('§').append(f);
						}

						line.append('§').append(f);
						formats[i - lastBreak] = format.toString();
						formats[i - lastBreak + 1] = format.toString();
						widths[i - lastBreak] = lineWidth;
						widths[i - lastBreak + 1] = lineWidth;
						i++; // skip next char
						continue;
					} else {
						// Skip invalid control code
						i++; // skip illegal character after §
						continue;
					}
				}
				break;

			default:
				line.append(c);
				int charWidth = getCharWidth(c);
				lineWidth += bold ? charWidth + 1 : charWidth;
				widths[i - lastBreak] = lineWidth;
				formats[i - lastBreak] = format.toString();
				break;
			}

			if (lineWidth >= width) {
				int breakIndex = breaker.preceding(i + 1);
				if (breakIndex <= lastBreak + 1 || breakIndex == i + 1) {
					result.add(line.substring(0, line.length() - 1));
					lastBreak = i;
					line.setLength(0);
					line.append(format).append(c);
					prevFormatLength = format.length();
					lineWidth = getCharWidth(c);
				} else {
					int relBreak = breakIndex - lastBreak;
					if (line.charAt(relBreak + prevFormatLength - 1) == '§') {
						relBreak++;
					}

					result.add(line.substring(0, relBreak + prevFormatLength));
					String rest = line.substring(relBreak + prevFormatLength);
					String carryFormat = formats[relBreak] != null ? formats[relBreak] : "";

					if (!carryFormat.contains("§r") && !carryFormat.contains("§R")) {
						line.setLength(0);
						line.append(carryFormat).append(rest);
						prevFormatLength = carryFormat.length();
					} else {
						line.setLength(0);
						line.append(rest);
						prevFormatLength = 0;
					}

					lineWidth = lineWidth - widths[relBreak - 1];
					lastBreak += relBreak;
				}
			}
		}

		if (line.length() != 0) {
			result.add(line.toString());
		}

		System.out.println("CNPC Split");
		for (String s : result) {
			System.out.println("---" + s + "---");
		}

		cir.setReturnValue(result);
	}

	@Shadow
	public int getCharWidth(char c0) {
		return 4;
	}

	@Shadow
	private static boolean isFormatColor(char c1) {
		return false;
	}

	@Shadow
	private static boolean isFormatSpecial(char formatChar) {
		return false;
	}

	private static String cleanIllegalFormatCodes(String text) {
		StringBuilder cleaned = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c == '§') {
				if (i + 1 < text.length()) {
					char next = Character.toLowerCase(text.charAt(i + 1));
					if ("0123456789abcdefklmnor".indexOf(next) != -1) {
						cleaned.append(c).append(next);
						i++;
						continue;
					} else {
						// 非法格式码：跳过§和后面一个字符
						i++;
						continue;
					}
				}
			}
			cleaned.append(c);
		}
		return cleaned.toString();
	}

}
