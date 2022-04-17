// From blackoutburst's Reflection Utils

package com.gmail.qwerty12944qwerty.pgjigsaw.nms;

import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;

public class NMSChat {
    /**
     * This defines the possible type of chat message
     */
    public enum MessageType {
        CHAT,
        SYSTEM,
        ACTION_BAR;
    }

    /**
     * Send a message to a player
     *
     * @param player the player that will receive the packet
     * @param text the message text
     * @param type the message type
     */
    public static void send(Player player, String text, MessageType type) {
        try {
            final Class<?> chatComponentClass = NMS.getClass("ChatComponentText");

            final Constructor<?> chatComponentText = chatComponentClass.getConstructor(String.class);

            final Object chatPacket = NMS.getClass("PacketPlayOutChat").getDeclaredConstructor().newInstance();

            NMS.setField(chatPacket, "a", chatComponentText.newInstance(text));
            NMS.setField(chatPacket, "b", (byte) type.ordinal());
            NMS.sendPacket(player, chatPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}