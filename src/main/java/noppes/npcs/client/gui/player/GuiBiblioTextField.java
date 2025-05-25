package noppes.npcs.client.gui.player;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ChatAllowedCharacters;

import java.nio.charset.StandardCharsets;

import org.lwjgl.opengl.GL11;

/**
 * This is total a copy paste of the GuiTextField class. I tried extending the
 * class and overriding the drawText method becasue I wanted text with no
 * shadows, but that failed. So I did a big copy/paste and am editing tiny bits
 * to suit my need.
 * 
 * @author Mojang
 *
 */
public class GuiBiblioTextField extends Gui {

	private final FontRenderer fontRenderer;

	private final int xPos;

	private final int yPos;

	private final int width;

	private final int height;

	private String text = "";

	private int maxStringLength = 32;

	private int cursorCounter;

	private boolean enableBackgroundDrawing = true;

	private boolean canLoseFocus = true;

	private boolean isFocused;

	private boolean isEnabled = true;

	private int lineScrollOffset;

	private int cursorPosition;

	private int selectionEnd;

	private int enabledColor = 14737632;

	private int disabledColor = 7368816;

	private boolean visible = true;

	@SideOnly(Side.CLIENT)
	public GuiBiblioTextField(FontRenderer par1FontRenderer, int par2, int par3, int par4, int par5) {
		this.fontRenderer = par1FontRenderer;
		this.xPos = par2;
		this.yPos = par3;
		this.width = par4;
		this.height = par5;
	}

	/**
	 * Increments the cursor counter
	 */
	public void updateCursorCounter() {
		++this.cursorCounter;
	}

	/**
	 * Sets the text of the textbox.
	 */
//	public void setText(String par1Str) {
//		if (par1Str.length() > this.maxStringLength) {
//			this.text = par1Str.substring(0, this.maxStringLength);
//		} else {
//			this.text = par1Str;
//		}
//
//		this.setCursorPositionEnd();
//	}
	public void setText(String input) {
	    if (this.fontRenderer == null) {
	        // 兜底措施：未初始化 fontRenderer 时 fallback
	        this.text = input.length() > this.maxStringLength ? input.substring(0, this.maxStringLength) : input;
	    } else {
	        // 按像素宽度裁剪输入文本
	        this.text = this.fontRenderer.trimStringToWidth(input, this.maxStringLength);
	    }

	    // 移动光标到结尾
	    //this.setCursorPositionEnd();
	}


	/**
	 * Returns the text beign edited on the textbox.
	 */
	public String getText() {
		return this.text;
	}

	/**
	 * @return returns the text between the cursor and selectionEnd
	 */
//	public String getSelectedtext() {
//		int i = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
//		int j = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
//		return this.text.substring(i, j);
//	}
	/**
	 * 获取当前选中的文本内容。
	 * 如果未选择任何内容，则返回空字符串。
	 */
	public String getSelectedtext() {
	    // 规范化选区范围：确保 i 是起点，j 是终点
	    int i = Math.min(this.cursorPosition, this.selectionEnd);
	    int j = Math.max(this.cursorPosition, this.selectionEnd);

	    // 边界保护：确保不超出文本范围
	    i = Math.max(0, Math.min(i, this.text.length()));
	    j = Math.max(0, Math.min(j, this.text.length()));

	    // 若无选中区域，返回空串
	    if (i == j) {
	        return "";
	    }

	    // 返回选中部分
	    return this.text.substring(i, j);
	}


	/**
	 * replaces selected text, or inserts text at the position on the cursor
	 */
//	public void writeText(String par1Str) {
//		String s1 = "";
//		String s2 = ChatAllowedCharacters.filerAllowedCharacters(par1Str);
//		int i = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
//		int j = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
//		int k = this.maxStringLength - this.text.length() - (i - this.selectionEnd);
//		boolean flag = false;
//
//		if (this.text.length() > 0) {
//			s1 = s1 + this.text.substring(0, i);
//		}
//
//		int l;
//
//		if (k < s2.length()) {
//			s1 = s1 + s2.substring(0, k);
//			l = k;
//		} else {
//			s1 = s1 + s2;
//			l = s2.length();
//		}
//
//		if (this.text.length() > 0 && j < this.text.length()) {
//			s1 = s1 + this.text.substring(j);
//		}
//
//		this.text = s1;
//		this.moveCursorBy(i - this.selectionEnd + l);
//	}
	public void writeText(String par1Str) {
		if (!this.isEnabled) return;

		// 过滤非法字符
		String filteredInput = ChatAllowedCharacters.filerAllowedCharacters(par1Str);
		if (filteredInput.isEmpty()) return;

		int selStart = Math.min(this.cursorPosition, this.selectionEnd);
		int selEnd = Math.max(this.cursorPosition, this.selectionEnd);

		String before = this.text.substring(0, selStart);
		String after = this.text.substring(selEnd);

		// 计算原始文本在显示上的长度（全角 = 2，半角 = 1）
		int beforeLength = getVisualLength(before);
		int afterLength = getVisualLength(after);
		int currentLength = beforeLength + afterLength;

		// 计算插入内容的最大可用显示宽度
		int maxInsertLength = this.maxStringLength - currentLength;
		if (maxInsertLength <= 0) return;

		// 剪裁插入内容，使其不超过显示宽度
		StringBuilder inserted = new StringBuilder();
		int insertWidth = 0;
		for (int i = 0; i < filteredInput.length(); i++) {
			char ch = filteredInput.charAt(i);
			int width = isFullWidth(ch) ? 2 : 1;

			if (insertWidth + width > maxInsertLength) break;

			inserted.append(ch);
			insertWidth += width;
		}

		// 拼接文本
		this.text = before + inserted + after;

		// 设置光标位置：原位置 + 插入的实际字符数
		int newCursorPos = selStart + inserted.length();
		this.setCursorPosition(newCursorPos);
	}

	
	public static int getVisualLength(String str) {
		int length = 0;
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			length += isFullWidth(c) ? 2 : 1;
		}
		return length;
	}
	
	private static boolean isFullWidth(char c) {
	    return String.valueOf(c).getBytes(StandardCharsets.UTF_8).length > 1;
	}




//	/**
//	 * Deletes the specified number of words starting at the cursor position.
//	 * Negative numbers will delete words left of the cursor.
//	 */
//	public void deleteWords(int par1) {
//		if (this.text.length() != 0) {
//			if (this.selectionEnd != this.cursorPosition) {
//				this.writeText("");
//			} else {
//				this.deleteFromCursor(this.getNthWordFromCursor(par1) - this.cursorPosition);
//			}
//		}
//	}
//
//	/**
//	 * delete the selected text, otherwsie deletes characters from either side of
//	 * the cursor. params: delete num
//	 */
//	public void deleteFromCursor(int par1) {
//		if (this.text.length() != 0) {
//			if (this.selectionEnd != this.cursorPosition) {
//				this.writeText("");
//			} else {
//				boolean flag = par1 < 0;
//				int j = flag ? this.cursorPosition + par1 : this.cursorPosition;
//				int k = flag ? this.cursorPosition : this.cursorPosition + par1;
//				String s = "";
//
//				if (j >= 0) {
//					s = this.text.substring(0, j);
//				}
//
//				if (k < this.text.length()) {
//					s = s + this.text.substring(k);
//				}
//
//				this.text = s;
//
//				if (flag) {
//					this.moveCursorBy(par1);
//				}
//			}
//		}
//	}
	
	public void deleteWords(int par1) {
		if (this.text.length() != 0) {
			if (this.selectionEnd != this.cursorPosition) {
				this.writeText(""); // 删除选中内容
			} else {
				int target = this.getNthWordFromCursor(par1);
				int delta = target - this.cursorPosition;
				this.deleteFromCursor(delta); // 删除指定词数
			}
		}
	}
	
	public void deleteFromCursor(int par1) {
		if (this.text.isEmpty()) return;

		if (this.selectionEnd != this.cursorPosition) {
			this.writeText(""); // 优先删除选中内容
			return;
		}

		int start = this.cursorPosition;
		int end = start + par1;

		if (par1 < 0) {
			start = this.cursorPosition + par1;
			end = this.cursorPosition;
		}

		// 边界保护
		start = Math.max(0, start);
		end = Math.min(this.text.length(), end);

		// 避免截断格式符号 §x
		start = adjustDeleteBoundary(start, true);
		end = adjustDeleteBoundary(end, false);

		String before = this.text.substring(0, start);
		String after = this.text.substring(end);
		this.text = before + after;

		if (par1 < 0) {
			this.setCursorPosition(start);
		}
	}
	
	/**
	 * 若位置在§字符后，回退1确保不截断格式符
	 * 若位置是§，则跳过§x两个字符
	 */
	private int adjustDeleteBoundary(int index, boolean reverse) {
		if (reverse && index > 0 && this.text.charAt(index - 1) == '\u00a7') {
			return index - 1;
		}
		if (!reverse && index < this.text.length() - 1 && this.text.charAt(index) == '\u00a7') {
			return index + 2;
		}
		return index;
	}




	/**
	 * see @getNthNextWordFromPos() params: N, position
	 */
	public int getNthWordFromCursor(int n) {
	    return getNthWordFromPos(n, this.getCursorPosition());
	}


	/**
	 * gets the position of the nth word. N may be negative, then it looks
	 * backwards. params: N, position
	 */
	public int getNthWordFromPos(int n, int pos) {
	    return getWordBoundary(n, pos);
	}


	/**
	 * 支持中英文混合文本的光标跳转。
	 * 将每个非空字符视为一个“词”，包括中文、英文字母、标点等。
	 *
	 * @param wordCount  要跳过的“词”数量（可为负）
	 * @param startPos   当前光标位置
	 * @return           新光标位置
	 */
	public int getWordBoundary(int wordCount, int startPos) {
	    int len = text.length();
	    int pos = startPos;

	    if (wordCount == 0 || len == 0) {
	        return pos;
	    }

	    // 向左
	    if (wordCount < 0) {
	        for (int i = 0; i < -wordCount; i++) {
	            if (pos == 0) break;
	            pos = getPrevWordPos(pos);
	        }
	    }
	    // 向右
	    else {
	        for (int i = 0; i < wordCount; i++) {
	            if (pos >= len) break;
	            pos = getNextWordPos(pos);
	        }
	    }

	    return pos;
	}
	
	/**
	 * 获取下一个“词”的起始位置，支持中英文混合。
	 */
	private int getNextWordPos(int pos) {
	    int len = text.length();
	    if (pos >= len) return len;

	    char current = text.charAt(pos);
	    CharacterType currentType = getCharType(current);

	    pos++;
	    while (pos < len && getCharType(text.charAt(pos)) == currentType) {
	        pos++;
	    }

	    return pos;
	}

	/**
	 * 获取上一个“词”的起始位置，支持中英文混合。
	 */
	private int getPrevWordPos(int pos) {
	    if (pos <= 0) return 0;

	    pos--;
	    char current = text.charAt(pos);
	    CharacterType currentType = getCharType(current);

	    while (pos > 0 && getCharType(text.charAt(pos - 1)) == currentType) {
	        pos--;
	    }

	    return pos;
	}
	
	private enum CharacterType {
	    LETTER,     // 英文单词、数字
	    CJK,        // 中文字符（CJK统一表意符号）
	    WHITESPACE, // 空白
	    OTHER       // 其他字符（标点等）
	}

	private CharacterType getCharType(char c) {
	    if (Character.isWhitespace(c)) {
	        return CharacterType.WHITESPACE;
	    } else if (Character.isLetterOrDigit(c)) {
	        return CharacterType.LETTER;
	    } else if (isCJK(c)) {
	        return CharacterType.CJK;
	    } else {
	        return CharacterType.OTHER;
	    }
	}

	private boolean isCJK(char c) {
	    // 中日韩统一表意文字范围
	    Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
	    return block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS ||
	           block == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS ||
	           block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A ||
	           block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B;
	}




	/**
	 * Moves the text cursor by a specified number of characters and clears the
	 * selection
	 */
	public void moveCursorBy(int offset) {
	    int newPos = this.selectionEnd + offset;
	    this.setCursorPosition(newPos);
	}


	/**
	 * sets the position of the cursor to the provided index
	 */
	public void setCursorPosition(int position) {
	    // 防止越界
	    if (position < 0) {
	        position = 0;
	    } else if (position > this.text.length()) {
	        position = this.text.length();
	    }

	    this.cursorPosition = position;
	    this.setSelectionPos(position); // 保持光标和选区一致
	}


	/**
	 * sets the cursors position to the beginning
	 */
	public void setCursorPositionZero() {
		this.setCursorPosition(0);
	}

	/**
	 * sets the cursors position to after the text
	 */
	public void setCursorPositionEnd() {
		this.setCursorPosition(this.text.length());
	}

	/**
	 * Call this method from you GuiScreen to process the keys into textbox.
	 */
//	public boolean textboxKeyTyped(char par1, int par2) {
//		if (this.isEnabled && this.isFocused) {
//			switch (par1) {
//			case 1:
//				this.setCursorPositionEnd();
//				this.setSelectionPos(0);
//				return true;
//			case 3:
//				GuiScreen.setClipboardString(this.getSelectedtext());
//				return true;
//			case 22:
//				this.writeText(GuiScreen.getClipboardString());
//				return true;
//			case 24:
//				GuiScreen.setClipboardString(this.getSelectedtext());
//				this.writeText("");
//				return true;
//			default:
//				switch (par2) {
//				case 14:
//					if (GuiScreen.isCtrlKeyDown()) {
//						this.deleteWords(-1);
//					} else {
//						this.deleteFromCursor(-1);
//					}
//
//					return true;
//				case 199:
//					if (GuiScreen.isShiftKeyDown()) {
//						this.setSelectionPos(0);
//					} else {
//						this.setCursorPositionZero();
//					}
//
//					return true;
//				case 203:
//					if (GuiScreen.isShiftKeyDown()) {
//						if (GuiScreen.isCtrlKeyDown()) {
//							this.setSelectionPos(this.getNthWordFromPos(-1, this.getSelectionEnd()));
//						} else {
//							this.setSelectionPos(this.getSelectionEnd() - 1);
//						}
//					} else if (GuiScreen.isCtrlKeyDown()) {
//						this.setCursorPosition(this.getNthWordFromCursor(-1));
//					} else {
//						this.moveCursorBy(-1);
//					}
//
//					return true;
//				case 205:
//					if (GuiScreen.isShiftKeyDown()) {
//						if (GuiScreen.isCtrlKeyDown()) {
//							this.setSelectionPos(this.getNthWordFromPos(1, this.getSelectionEnd()));
//						} else {
//							this.setSelectionPos(this.getSelectionEnd() + 1);
//						}
//					} else if (GuiScreen.isCtrlKeyDown()) {
//						this.setCursorPosition(this.getNthWordFromCursor(1));
//					} else {
//						this.moveCursorBy(1);
//					}
//
//					return true;
//				case 207:
//					if (GuiScreen.isShiftKeyDown()) {
//						this.setSelectionPos(this.text.length());
//					} else {
//						this.setCursorPositionEnd();
//					}
//
//					return true;
//				case 211:
//					if (GuiScreen.isCtrlKeyDown()) {
//						this.deleteWords(1);
//					} else {
//						this.deleteFromCursor(1);
//					}
//
//					return true;
//				default:
//					if (ChatAllowedCharacters.isAllowedCharacter(par1)) {
//						this.writeText(Character.toString(par1));
//						return true;
//					} else {
//						return false;
//					}
//				}
//			}
//		} else {
//			return false;
//		}
//	}
	public boolean textboxKeyTyped(char par1, int par2) {
		if (!this.isEnabled || !this.isFocused) return false;

		switch (par1) {
			case 1: // Ctrl+A 全选
				this.setCursorPositionEnd();
				this.setSelectionPos(0);
				return true;

			case 3: // Ctrl+C 复制
				GuiScreen.setClipboardString(this.getSelectedtext());
				return true;

			case 22: // Ctrl+V 粘贴
				this.writeText(GuiScreen.getClipboardString());
				return true;

			case 24: // Ctrl+X 剪切
				GuiScreen.setClipboardString(this.getSelectedtext());
				this.writeText("");
				return true;

			default:
				switch (par2) {
					case 14: // Backspace
						if (GuiScreen.isCtrlKeyDown()) {
							this.deleteWords(-1);
						} else {
							this.deleteFromCursor(-1);
						}
						return true;

					case 199: // Home
						if (GuiScreen.isShiftKeyDown()) {
							this.setSelectionPos(0);
						} else {
							this.setCursorPositionZero();
						}
						return true;

					case 203: // Left arrow
						if (GuiScreen.isShiftKeyDown()) {
							if (GuiScreen.isCtrlKeyDown()) {
								this.setSelectionPos(this.getNthWordFromPos(-1, this.getSelectionEnd()));
							} else {
								this.setSelectionPos(this.getSelectionEnd() - 1);
							}
						} else if (GuiScreen.isCtrlKeyDown()) {
							this.setCursorPosition(this.getNthWordFromCursor(-1));
						} else {
							this.moveCursorBy(-1);
						}
						return true;

					case 205: // Right arrow
						if (GuiScreen.isShiftKeyDown()) {
							if (GuiScreen.isCtrlKeyDown()) {
								this.setSelectionPos(this.getNthWordFromPos(1, this.getSelectionEnd()));
							} else {
								this.setSelectionPos(this.getSelectionEnd() + 1);
							}
						} else if (GuiScreen.isCtrlKeyDown()) {
							this.setCursorPosition(this.getNthWordFromCursor(1));
						} else {
							this.moveCursorBy(1);
						}
						return true;

					case 207: // End
						if (GuiScreen.isShiftKeyDown()) {
							this.setSelectionPos(this.text.length());
						} else {
							this.setCursorPositionEnd();
						}
						return true;

					case 211: // Delete
						if (GuiScreen.isCtrlKeyDown()) {
							this.deleteWords(1);
						} else {
							this.deleteFromCursor(1);
						}
						return true;

					default:
						// 处理文本输入
						if (ChatAllowedCharacters.isAllowedCharacter(par1)) {
							String insert = Character.toString(par1);
							this.writeText(insert);
							return true;
						}
						return false;
				}
		}
	}


	/**
	 * Args: x, y, buttonClicked
	 */
	public boolean mouseClicked(int mouseX, int mouseY, int button) {
	    // 判断点击是否在文本框范围内
	    boolean withinBounds = mouseX >= this.xPos && mouseX < this.xPos + this.width
	                        && mouseY >= this.yPos && mouseY < this.yPos + this.height;

	    if (this.canLoseFocus) {
	        this.setFocused(this.isEnabled && withinBounds);
	    }

	    if (this.isFocused && button == 0) { // 鼠标左键
	        int relativeX = mouseX - this.xPos;

	        if (this.enableBackgroundDrawing) {
	            relativeX -= 4; // 减去内边距
	        }

	        // 当前可视区内的文本
	        String visibleText = this.fontRenderer.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());

	        // 逐字符判断哪个位置最接近点击点
	        int pos = this.lineScrollOffset;
	        int accumWidth = 0;

	        for (int i = 0; i < visibleText.length(); i++) {
	            char ch = visibleText.charAt(i);
	            int charWidth = this.fontRenderer.getCharWidth(ch);

	            if (accumWidth + charWidth / 2 >= relativeX) {
	                break;
	            }

	            accumWidth += charWidth;
	            pos++;
	        }

	        this.setCursorPosition(pos);
	        return true;
	    }

	    return false;
	}


	/**
	 * Draws the textbox
	 */
//	public void drawTextBox() {
//		if (this.getVisible()) {
//			// GL11.glPushMatrix();
//			// GL11.glScalef(0.8f, 0.8f, 0.8f);
//			if (this.getEnableBackgroundDrawing()) {
//				drawRect(this.xPos - 1, this.yPos - 1, this.xPos + this.width + 1, this.yPos + this.height + 1,
//						-6250336);
//				drawRect(this.xPos, this.yPos, this.xPos + this.width, this.yPos + this.height, -16777216);
//			}
//
//			int i = this.isEnabled ? this.enabledColor : this.disabledColor;
//			int j = this.cursorPosition - this.lineScrollOffset;
//			int k = this.selectionEnd - this.lineScrollOffset;
//			String s = this.fontRenderer.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
//			boolean flag = j >= 0 && j <= s.length();
//			boolean flag1 = this.isFocused && this.cursorCounter / 6 % 2 == 0 && flag;
//			int l = this.enableBackgroundDrawing ? this.xPos + 4 : this.xPos;
//			int i1 = this.enableBackgroundDrawing ? this.yPos + (this.height - 8) / 2 : this.yPos;
//			int j1 = l;
//
//			if (k > s.length()) {
//				k = s.length();
//			}
//
//			if (s.length() > 0) {
//				String s1 = flag ? s.substring(0, j) : s;
//				j1 = this.fontRenderer.drawString(s1, l, i1, i); // drawString
//				// this.fontRenderer.dr
//			}
//
//			boolean flag2 = this.cursorPosition < this.text.length() || this.text.length() >= this.getMaxStringLength();
//			int k1 = j1;
//
//			if (!flag) {
//				k1 = j > 0 ? l + this.width : l;
//			} else if (flag2) {
//				k1 = j1 - 1;
//				--j1;
//			}
//
//			if (s.length() > 0 && flag && j < s.length()) {
//				this.fontRenderer.drawString(s.substring(j), j1, i1, i);
//			}
//
//			if (flag1) {
//				if (flag2) {
//					Gui.drawRect(k1, i1 - 1, k1 + 1, i1 + 1 + this.fontRenderer.FONT_HEIGHT, -3092272);
//				} else {
//					this.fontRenderer.drawString("_", k1, i1, i);
//				}
//			}
//
//			if (k != j) {
//				int l1 = l + this.fontRenderer.getStringWidth(s.substring(0, k));
//				this.drawCursorVertical(k1, i1 - 1, l1 - 1, i1 + 1 + this.fontRenderer.FONT_HEIGHT);
//			}
//			// GL11.glPopMatrix();
//		}
//	}
	
	public void drawTextBox() {
	    if (!this.getVisible()) return;

	    // 背景绘制
	    if (this.getEnableBackgroundDrawing()) {
	        drawRect(this.xPos - 1, this.yPos - 1, this.xPos + this.width + 1, this.yPos + this.height + 1, -6250336);
	        drawRect(this.xPos, this.yPos, this.xPos + this.width, this.yPos + this.height, -16777216);
	    }

	    int color = this.isEnabled ? this.enabledColor : this.disabledColor;
	    int drawX = this.enableBackgroundDrawing ? this.xPos + 4 : this.xPos;
	    int drawY = this.enableBackgroundDrawing ? this.yPos + (this.height - 8) / 2 : this.yPos;

	    int cursorIdx = this.cursorPosition - this.lineScrollOffset;
	    int selectionIdx = this.selectionEnd - this.lineScrollOffset;

	    String visibleText = this.fontRenderer.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());

	    boolean cursorInVisibleRange = cursorIdx >= 0 && cursorIdx <= visibleText.length();
	    boolean drawCursor = this.isFocused && this.cursorCounter / 6 % 2 == 0 && cursorInVisibleRange;

	    // ------- 文本绘制 -------
	    int textX = drawX;

	    if (visibleText.length() > 0) {
	        String beforeCursor = cursorInVisibleRange ? visibleText.substring(0, cursorIdx) : visibleText;
	        textX = this.fontRenderer.drawString(beforeCursor, drawX, drawY, color);
	    }

	    // ------- 以下逻辑仅在控件聚焦时处理光标与选区 -------
	    if (this.isFocused) {
	        boolean cursorBeyondText = this.cursorPosition < this.text.length() || this.text.length() >= this.getMaxStringLength();
	        int cursorX = textX;

	        // 光标位置修正（聚焦状态下才进行）
	        if (!cursorInVisibleRange) {
	            cursorX = cursorIdx > 0 ? drawX + this.width : drawX;
	        } else if (cursorBeyondText) {
	            cursorX = textX - 1;
	            textX -= 1;
	        }

	        // 绘制光标
	        if (drawCursor) {
	            if (cursorBeyondText) {
	                Gui.drawRect(cursorX, drawY - 1, cursorX + 1, drawY + 1 + this.fontRenderer.FONT_HEIGHT, -3092272);
	            } else {
	                this.fontRenderer.drawString("_", cursorX, drawY, color);
	            }
	        }

	        // 绘制选区
	        if (selectionIdx != cursorIdx) {
	            int selStartX = drawX + this.fontRenderer.getStringWidth(visibleText.substring(0, Math.min(cursorIdx, selectionIdx)));
	            int selEndX = drawX + this.fontRenderer.getStringWidth(visibleText.substring(0, Math.max(cursorIdx, selectionIdx)));
	            this.drawCursorVertical(selStartX, drawY - 1, selEndX, drawY + 1 + this.fontRenderer.FONT_HEIGHT);
	        }

	        // 绘制光标后的文本（只有聚焦时才需要）
	        if (visibleText.length() > 0 && cursorInVisibleRange && cursorIdx < visibleText.length()) {
	            this.fontRenderer.drawString(visibleText.substring(cursorIdx), textX, drawY, color);
	        }
	    } else {
	        // 非聚焦状态：绘制全部文本（不做截断）
	        if (cursorIdx < visibleText.length()) {
	            this.fontRenderer.drawString(visibleText.substring(cursorIdx), textX, drawY, color);
	        }
	    }
	}



	/**
	 * draws the vertical line cursor in the textbox
	 */
	private void drawCursorVertical(int par1, int par2, int par3, int par4) {
		int i1;

		if (par1 < par3) {
			i1 = par1;
			par1 = par3;
			par3 = i1;
		}

		if (par2 < par4) {
			i1 = par2;
			par2 = par4;
			par4 = i1;
		}

		Tessellator tessellator = Tessellator.instance;
        GL11.glColor4f(0.0F, 0.0F, 255.0F, 255.0F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_COLOR_LOGIC_OP);
        GL11.glLogicOp(GL11.GL_OR_REVERSE);
        tessellator.startDrawingQuads();
        tessellator.addVertex((double)par1, (double)par4, 0.0D);
        tessellator.addVertex((double)par3, (double)par4, 0.0D);
        tessellator.addVertex((double)par3, (double)par2, 0.0D);
        tessellator.addVertex((double)par1, (double)par2, 0.0D);
        tessellator.draw();
        GL11.glDisable(GL11.GL_COLOR_LOGIC_OP);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	@Deprecated
	public void setMaxStringLength(int par1) {
		this.maxStringLength = par1;
		// System.out.println(formcount);
		if ((this.text.length()) > par1) {
			this.text = this.text.substring(0, par1);
		}
	}

	/**
	 * returns the maximum number of character that can be contained in this textbox
	 */
	public int getMaxStringLength() {
		return this.maxStringLength;
	}

	/**
	 * returns the current position of the cursor
	 */
	public int getCursorPosition() {
		return this.cursorPosition;
	}

	/**
	 * get enable drawing background and outline
	 */
	public boolean getEnableBackgroundDrawing() {
		return this.enableBackgroundDrawing;
	}

	/**
	 * enable drawing background and outline
	 */
	public void setEnableBackgroundDrawing(boolean par1) {
		this.enableBackgroundDrawing = par1;
	}

	/**
	 * Sets the text colour for this textbox (disabled text will not use this
	 * colour)
	 */
	public void setTextColor(int par1) {
		this.enabledColor = par1;
	}

	public void setDisabledTextColour(int par1) {
		this.disabledColor = par1;
	}

	/**
	 * setter for the focused field
	 */
	public void setFocused(boolean par1) {
		if (par1 && !this.isFocused) {
			this.cursorCounter = 0;
		}

		this.isFocused = par1;
	}

	/**
	 * getter for the focused field
	 */
	public boolean isFocused() {
		return this.isFocused;
	}

	public void setEnabled(boolean par1) {
		this.isEnabled = par1;
	}

	/**
	 * the side of the selection that is not the cursor, maye be the same as the
	 * cursor
	 */
	public int getSelectionEnd() {
		return this.selectionEnd;
	}

	/**
	 * returns the width of the textbox depending on if the the box is enabled
	 */
	public int getWidth() {
		return this.getEnableBackgroundDrawing() ? this.width - 8 : this.width;
	}

	/**
	 * Sets the position of the selection anchor (i.e. position the selection was
	 * started at)
	 */
	/*
	 * public void setSelectionPos(int par1) { int j = this.text.length();
	 * 
	 * if (par1 > j) { par1 = j; }
	 * 
	 * if (par1 < 0) { par1 = 0; }
	 * 
	 * this.selectionEnd = par1;
	 * 
	 * if (this.fontRenderer != null) { if (this.lineScrollOffset > j) {
	 * this.lineScrollOffset = j; }
	 * 
	 * int k = this.getWidth(); String s =
	 * this.fontRenderer.trimStringToWidth(this.text.substring(this.lineScrollOffset
	 * ), k); int l = s.length() + this.lineScrollOffset;
	 * 
	 * if (par1 == this.lineScrollOffset) { this.lineScrollOffset -=
	 * this.fontRenderer.trimStringToWidth(this.text, k, true).length(); }
	 * 
	 * if (par1 > l) { this.lineScrollOffset += par1 - l; } else if (par1 <=
	 * this.lineScrollOffset) { this.lineScrollOffset -= this.lineScrollOffset -
	 * par1; }
	 * 
	 * if (this.lineScrollOffset < 0) { this.lineScrollOffset = 0; }
	 * 
	 * if (this.lineScrollOffset > j) { this.lineScrollOffset = j; } } }
	 */
	public void setSelectionPos(int par1) {
		int textLength = this.text.length();
		par1 = Math.max(0, Math.min(par1, textLength));
		this.selectionEnd = par1;

		if (this.fontRenderer != null) {
			// 确保 lineScrollOffset 合法
			if (this.lineScrollOffset > textLength) {
				this.lineScrollOffset = textLength;
			}

			int boxWidth = this.getWidth();
			int visualWidth = 0;
			int visibleChars = 0;

			// 计算从 lineScrollOffset 开始，最多能显示多少字符（考虑全角宽度）
			for (int i = this.lineScrollOffset; i < textLength; i++) {
				char c = this.text.charAt(i);
				int charWidth = isFullWidth(c) ? fontRenderer.getCharWidth('好') : fontRenderer.getCharWidth(c); // '好' 为典型中文宽度
				if (visualWidth + charWidth > boxWidth) break;
				visualWidth += charWidth;
				visibleChars++;
			}

			int visibleEnd = this.lineScrollOffset + visibleChars;

			// 滚动处理，使光标在可视范围内
			/*
			 * if (par1 < this.lineScrollOffset) { this.lineScrollOffset = par1; } else if
			 * (par1 > visibleEnd) { // 滚动到让 par1 成为可见末尾 int backwardWidth = 0; int
			 * newOffset = par1; while (newOffset > 0 && backwardWidth < boxWidth) { char c
			 * = this.text.charAt(newOffset - 1); backwardWidth += isFullWidth(c) ?
			 * fontRenderer.getCharWidth('好') : fontRenderer.getCharWidth(c); if
			 * (backwardWidth > boxWidth) break; newOffset--; } this.lineScrollOffset =
			 * newOffset; }
			 */

			// 边界保护
			if (this.lineScrollOffset < 0) {
				this.lineScrollOffset = 0;
			}
			if (this.lineScrollOffset > textLength) {
				this.lineScrollOffset = textLength;
			}
		}
	}
	
	


	/**
	 * if true the textbox can lose focus by clicking elsewhere on the screen
	 */
	public void setCanLoseFocus(boolean par1) {
		this.canLoseFocus = par1;
	}

	/**
	 * @return {@code true} if this textbox is visible
	 */
	public boolean getVisible() {
		return this.visible;
	}

	/**
	 * Sets whether or not this textbox is visible
	 */
	public void setVisible(boolean par1) {
		this.visible = par1;
	}
	
	/**
	 * 设置文本内容，自动限制不超过文本框宽度（以像素为单位）
	 * 
	 * @param field           要设置的文本框（GuiBiblioTextField 或 TextField）
	 * @param text            用户输入的完整文本
	 * @param maxPixelWidth   文本框最大宽度（单位：像素）
	 * @param fontRenderer    当前使用的 FontRenderer（例如 fontRendererObj）
	 */
	public void setTextWithLimit(String text, int maxPixelWidth, FontRenderer fontRenderer) {
	    if (text == null || text.isEmpty()) {
	        this.setText("");
	        return;
	    }

	    StringBuilder limitedText = new StringBuilder();
	    int totalWidth = 0;

	    for (int i = 0; i < text.length(); i++) {
	        String currentChar = String.valueOf(text.charAt(i));
	        int charWidth = fontRenderer.getStringWidth(limitedText.toString() + currentChar);

	        if (charWidth > maxPixelWidth) {
	            break; // 超过宽度，停止添加
	        }

	        limitedText.append(currentChar);
	        totalWidth = charWidth;
	    }

	    this.setText(limitedText.toString());
	}

}
