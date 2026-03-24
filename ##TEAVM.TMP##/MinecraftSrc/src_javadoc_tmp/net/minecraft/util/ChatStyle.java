package net.minecraft.util;

import org.json.JSONException;
import org.json.JSONObject;

import net.lax1dude.eaglercraft.v1_8.json.JSONTypeCodec;
import net.lax1dude.eaglercraft.v1_8.json.JSONTypeProvider;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;

/**+
 * This portion of EaglercraftX contains deobfuscated Minecraft 1.8 source code.
 * 
 * Minecraft 1.8.8 bytecode is (c) 2015 Mojang AB. "Do not distribute!"
 * Mod Coder Pack v9.18 deobfuscation configs are (c) Copyright by the MCP Team
 * 
 * EaglercraftX 1.8 patch files (c) 2022-2025 lax1dude, ayunami2000. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */
public class ChatStyle {
	private ChatStyle parentStyle;
	private EnumChatFormatting color;
	private Boolean bold;
	private Boolean italic;
	private Boolean underlined;
	private Boolean strikethrough;
	private Boolean obfuscated;
	private ClickEvent chatClickEvent;
	private HoverEvent chatHoverEvent;
	private String insertion;
	private static final ChatStyle rootStyle = new ChatStyle() {
		public EnumChatFormatting getColor() {
			return null;
		}

		public boolean getBold() {
			return false;
		}

		public boolean getItalic() {
			return false;
		}

		public boolean getStrikethrough() {
			return false;
		}

		public boolean getUnderlined() {
			return false;
		}

		public boolean getObfuscated() {
			return false;
		}

		public ClickEvent getChatClickEvent() {
			return null;
		}

		public HoverEvent getChatHoverEvent() {
			return null;
		}

		public String getInsertion() {
			return null;
		}

		public ChatStyle setColor(EnumChatFormatting color) {
			throw new UnsupportedOperationException();
		}

		public ChatStyle setBold(Boolean boldIn) {
			throw new UnsupportedOperationException();
		}

		public ChatStyle setItalic(Boolean italic) {
			throw new UnsupportedOperationException();
		}

		public ChatStyle setStrikethrough(Boolean strikethrough) {
			throw new UnsupportedOperationException();
		}

		public ChatStyle setUnderlined(Boolean underlined) {
			throw new UnsupportedOperationException();
		}

		public ChatStyle setObfuscated(Boolean obfuscated) {
			throw new UnsupportedOperationException();
		}

		public ChatStyle setChatClickEvent(ClickEvent event) {
			throw new UnsupportedOperationException();
		}

		public ChatStyle setChatHoverEvent(HoverEvent event) {
			throw new UnsupportedOperationException();
		}

		public ChatStyle setParentStyle(ChatStyle parent) {
			throw new UnsupportedOperationException();
		}

		public String toString() {
			return "Style.ROOT";
		}

		public ChatStyle createShallowCopy() {
			return this;
		}

		public ChatStyle createDeepCopy() {
			return this;
		}

		public String getFormattingCode() {
			return "";
		}
	};

	public EnumChatFormatting getColor() {
		return this.color == null ? this.getParent().getColor() : this.color;
	}

	public boolean getBold() {
		return this.bold == null ? this.getParent().getBold() : this.bold.booleanValue();
	}

	public boolean getItalic() {
		return this.italic == null ? this.getParent().getItalic() : this.italic.booleanValue();
	}

	public boolean getStrikethrough() {
		return this.strikethrough == null ? this.getParent().getStrikethrough() : this.strikethrough.booleanValue();
	}

	public boolean getUnderlined() {
		return this.underlined == null ? this.getParent().getUnderlined() : this.underlined.booleanValue();
	}

	public boolean getObfuscated() {
		return this.obfuscated == null ? this.getParent().getObfuscated() : this.obfuscated.booleanValue();
	}

	public boolean isEmpty() {
		return this.bold == null && this.italic == null && this.strikethrough == null && this.underlined == null
				&& this.obfuscated == null && this.color == null && this.chatClickEvent == null
				&& this.chatHoverEvent == null;
	}

	public ClickEvent getChatClickEvent() {
		return this.chatClickEvent == null ? this.getParent().getChatClickEvent() : this.chatClickEvent;
	}

	public HoverEvent getChatHoverEvent() {
		return this.chatHoverEvent == null ? this.getParent().getChatHoverEvent() : this.chatHoverEvent;
	}

	public String getInsertion() {
		return this.insertion == null ? this.getParent().getInsertion() : this.insertion;
	}

	public ChatStyle setColor(EnumChatFormatting enumchatformatting) {
		this.color = enumchatformatting;
		return this;
	}

	public ChatStyle setBold(Boolean obool) {
		this.bold = obool;
		return this;
	}

	public ChatStyle setItalic(Boolean obool) {
		this.italic = obool;
		return this;
	}

	public ChatStyle setStrikethrough(Boolean obool) {
		this.strikethrough = obool;
		return this;
	}

	public ChatStyle setUnderlined(Boolean obool) {
		this.underlined = obool;
		return this;
	}

	public ChatStyle setObfuscated(Boolean obool) {
		this.obfuscated = obool;
		return this;
	}

	public ChatStyle setChatClickEvent(ClickEvent clickevent) {
		this.chatClickEvent = clickevent;
		return this;
	}

	public ChatStyle setChatHoverEvent(HoverEvent hoverevent) {
		this.chatHoverEvent = hoverevent;
		return this;
	}

	public ChatStyle setInsertion(String insertion) {
		this.insertion = insertion;
		return this;
	}

	public ChatStyle setParentStyle(ChatStyle chatstyle) {
		this.parentStyle = chatstyle;
		return this;
	}

	public String getFormattingCode() {
		if (this.isEmpty()) {
			return this.parentStyle != null ? this.parentStyle.getFormattingCode() : "";
		} else {
			StringBuilder stringbuilder = new StringBuilder();
			if (this.getColor() != null) {
				stringbuilder.append(this.getColor());
			}

			if (this.getBold()) {
				stringbuilder.append(EnumChatFormatting.BOLD);
			}

			if (this.getItalic()) {
				stringbuilder.append(EnumChatFormatting.ITALIC);
			}

			if (this.getUnderlined()) {
				stringbuilder.append(EnumChatFormatting.UNDERLINE);
			}

			if (this.getObfuscated()) {
				stringbuilder.append(EnumChatFormatting.OBFUSCATED);
			}

			if (this.getStrikethrough()) {
				stringbuilder.append(EnumChatFormatting.STRIKETHROUGH);
			}

			return stringbuilder.toString();
		}
	}

	private ChatStyle getParent() {
		return this.parentStyle == null ? rootStyle : this.parentStyle;
	}

	public String toString() {
		return "Style{hasParent=" + (this.parentStyle != null) + ", color=" + this.color + ", bold=" + this.bold
				+ ", italic=" + this.italic + ", underlined=" + this.underlined + ", obfuscated=" + this.obfuscated
				+ ", clickEvent=" + this.getChatClickEvent() + ", hoverEvent=" + this.getChatHoverEvent()
				+ ", insertion=" + this.getInsertion() + '}';
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof ChatStyle)) {
			return false;
		} else {
			boolean flag;
			label0: {
				ChatStyle chatstyle = (ChatStyle) object;
				if (this.getBold() == chatstyle.getBold() && this.getColor() == chatstyle.getColor()
						&& this.getItalic() == chatstyle.getItalic()
						&& this.getObfuscated() == chatstyle.getObfuscated()
						&& this.getStrikethrough() == chatstyle.getStrikethrough()
						&& this.getUnderlined() == chatstyle.getUnderlined()) {
					label85: {
						if (this.getChatClickEvent() != null) {
							if (!this.getChatClickEvent().equals(chatstyle.getChatClickEvent())) {
								break label85;
							}
						} else if (chatstyle.getChatClickEvent() != null) {
							break label85;
						}

						if (this.getChatHoverEvent() != null) {
							if (!this.getChatHoverEvent().equals(chatstyle.getChatHoverEvent())) {
								break label85;
							}
						} else if (chatstyle.getChatHoverEvent() != null) {
							break label85;
						}

						if (this.getInsertion() != null) {
							if (this.getInsertion().equals(chatstyle.getInsertion())) {
								break label0;
							}
						} else if (chatstyle.getInsertion() == null) {
							break label0;
						}
					}
				}

				flag = false;
				return flag;
			}

			flag = true;
			return flag;
		}
	}

	public int hashCode() {
		int i = this.color.hashCode();
		i = 31 * i + this.bold.hashCode();
		i = 31 * i + this.italic.hashCode();
		i = 31 * i + this.underlined.hashCode();
		i = 31 * i + this.strikethrough.hashCode();
		i = 31 * i + this.obfuscated.hashCode();
		i = 31 * i + this.chatClickEvent.hashCode();
		i = 31 * i + this.chatHoverEvent.hashCode();
		i = 31 * i + this.insertion.hashCode();
		return i;
	}

	public ChatStyle createShallowCopy() {
		ChatStyle chatstyle = new ChatStyle();
		chatstyle.bold = this.bold;
		chatstyle.italic = this.italic;
		chatstyle.strikethrough = this.strikethrough;
		chatstyle.underlined = this.underlined;
		chatstyle.obfuscated = this.obfuscated;
		chatstyle.color = this.color;
		chatstyle.chatClickEvent = this.chatClickEvent;
		chatstyle.chatHoverEvent = this.chatHoverEvent;
		chatstyle.parentStyle = this.parentStyle;
		chatstyle.insertion = this.insertion;
		return chatstyle;
	}

	public ChatStyle createDeepCopy() {
		ChatStyle chatstyle = new ChatStyle();
		chatstyle.setBold(Boolean.valueOf(this.getBold()));
		chatstyle.setItalic(Boolean.valueOf(this.getItalic()));
		chatstyle.setStrikethrough(Boolean.valueOf(this.getStrikethrough()));
		chatstyle.setUnderlined(Boolean.valueOf(this.getUnderlined()));
		chatstyle.setObfuscated(Boolean.valueOf(this.getObfuscated()));
		chatstyle.setColor(this.getColor());
		chatstyle.setChatClickEvent(this.getChatClickEvent());
		chatstyle.setChatHoverEvent(this.getChatHoverEvent());
		chatstyle.setInsertion(this.getInsertion());
		return chatstyle;
	}

	public static class Serializer implements JSONTypeCodec<ChatStyle, JSONObject> {
		public ChatStyle deserialize(JSONObject jsonobject) throws JSONException {
			ChatStyle chatstyle = new ChatStyle();
			if (jsonobject == null) {
				return null;
			} else {
				if (jsonobject.has("bold")) {
					chatstyle.bold = jsonobject.getBoolean("bold");
				}

				if (jsonobject.has("italic")) {
					chatstyle.italic = jsonobject.getBoolean("italic");
				}

				if (jsonobject.has("underlined")) {
					chatstyle.underlined = jsonobject.getBoolean("underlined");
				}

				if (jsonobject.has("strikethrough")) {
					chatstyle.strikethrough = jsonobject.getBoolean("strikethrough");
				}

				if (jsonobject.has("obfuscated")) {
					chatstyle.obfuscated = jsonobject.getBoolean("obfuscated");
				}

				if (jsonobject.has("color")) {
					chatstyle.color = EnumChatFormatting.getValueByName(jsonobject.getString("color"));
				}

				if (jsonobject.has("insertion")) {
					chatstyle.insertion = jsonobject.getString("insertion");
				}

				if (jsonobject.has("clickEvent")) {
					JSONObject jsonobject1 = jsonobject.getJSONObject("clickEvent");
					if (jsonobject1 != null) {
						String jsonprimitive = jsonobject1.optString("action");
						ClickEvent.Action clickevent$action = jsonprimitive == null ? null
								: ClickEvent.Action.getValueByCanonicalName(jsonprimitive);
						String jsonprimitive1 = jsonobject1.optString("value");
						if (clickevent$action != null && jsonprimitive1 != null
								&& clickevent$action.shouldAllowInChat()) {
							chatstyle.chatClickEvent = new ClickEvent(clickevent$action, jsonprimitive1);
						}
					}
				}

				if (jsonobject.has("hoverEvent")) {
					JSONObject jsonobject2 = jsonobject.getJSONObject("hoverEvent");
					if (jsonobject2 != null) {
						String jsonprimitive2 = jsonobject2.getString("action");
						HoverEvent.Action hoverevent$action = jsonprimitive2 == null ? null
								: HoverEvent.Action.getValueByCanonicalName(jsonprimitive2);
						Object val = jsonobject2.opt("value");
						if (val == null) {
							val = jsonobject2.opt("contents");
							if (val == null) {
								throw new JSONException(
										"JSONObject[\"value\"] or JSONObject[\"contents\"] could not be found");
							}
						}
						IChatComponent ichatcomponent = JSONTypeProvider.deserializeNoCast(val, IChatComponent.class);
						if (hoverevent$action != null && ichatcomponent != null
								&& hoverevent$action.shouldAllowInChat()) {
							chatstyle.chatHoverEvent = new HoverEvent(hoverevent$action, ichatcomponent);
						}
					}
				}

				return chatstyle;
			}
		}

		public JSONObject serialize(ChatStyle chatstyle) {
			if (chatstyle.isEmpty()) {
				return null;
			} else {
				JSONObject jsonobject = new JSONObject();
				if (chatstyle.bold != null) {
					jsonobject.put("bold", chatstyle.bold);
				}

				if (chatstyle.italic != null) {
					jsonobject.put("italic", chatstyle.italic);
				}

				if (chatstyle.underlined != null) {
					jsonobject.put("underlined", chatstyle.underlined);
				}

				if (chatstyle.strikethrough != null) {
					jsonobject.put("strikethrough", chatstyle.strikethrough);
				}

				if (chatstyle.obfuscated != null) {
					jsonobject.put("obfuscated", chatstyle.obfuscated);
				}

				if (chatstyle.color != null) {
					jsonobject.put("color", chatstyle.color.getFriendlyName());
				}

				if (chatstyle.insertion != null) {
					jsonobject.put("insertion", chatstyle.insertion);
				}

				if (chatstyle.chatClickEvent != null) {
					JSONObject jsonobject1 = new JSONObject();
					jsonobject1.put("action", chatstyle.chatClickEvent.getAction().getCanonicalName());
					jsonobject1.put("value", chatstyle.chatClickEvent.getValue());
					jsonobject.put("clickEvent", jsonobject1);
				}

				if (chatstyle.chatHoverEvent != null) {
					JSONObject jsonobject2 = new JSONObject();
					jsonobject2.put("action", chatstyle.chatHoverEvent.getAction().getCanonicalName());
					Object obj = JSONTypeProvider.serialize(chatstyle.chatHoverEvent.getValue());
					if (obj instanceof String) {
						jsonobject2.put("value", (String) obj);
					} else if (obj instanceof JSONObject) {
						jsonobject2.put("value", (JSONObject) obj);
					} else {
						throw new ClassCastException();
					}
					jsonobject.put("hoverEvent", jsonobject2);
				}

				return jsonobject;
			}
		}
	}
}