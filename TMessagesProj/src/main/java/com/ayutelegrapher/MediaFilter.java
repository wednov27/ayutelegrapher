/*
 * This file is part of AyuTelegrapher, a fork of Telegraher.
 * Licensed under the GNU GPL v2 or later, see the repository LICENSE.
 */

package com.ayutelegrapher;

import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC;

// Hides chosen message types in groups and channels. Fixed at build time.
public final class MediaFilter {

    public static final boolean HIDE_VOICE = true;
    public static final boolean HIDE_ROUND_VIDEO = true;
    public static final boolean HIDE_DICE = true; // 🎲 🎯 🏀 ⚽ 🎳 🎰

    // RecyclerView item type for a hidden message: an empty 1px row.
    public static final int HIDDEN_VIEW_TYPE = -1000;

    private MediaFilter() {
    }

    public static boolean shouldHide(MessageObject msg, TLRPC.Chat chat) {
        if (chat == null || msg == null || msg.messageOwner == null || msg.isOut()) {
            return false; // private chats, and own messages, stay untouched
        }
        return (HIDE_VOICE && msg.isVoice())
                || (HIDE_ROUND_VIDEO && msg.isRoundVideo())
                || (HIDE_DICE && msg.isDice());
    }
}
