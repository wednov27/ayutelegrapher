/*
 * This file is part of AyuTelegrapher, a fork of Telegraher.
 * Licensed under the GNU GPL v2 or later, see the repository LICENSE.
 */

package com.ayutelegrapher;

import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC;

// Hides chosen message types in groups and channels. Switches in AyuPrefs.
public final class MediaFilter {


    // RecyclerView item type for a hidden message: an empty 1px row.
    public static final int HIDDEN_VIEW_TYPE = -1000;

    private MediaFilter() {
    }

    public static boolean shouldHide(MessageObject msg, TLRPC.Chat chat) {
        if (chat == null || msg == null || msg.messageOwner == null || msg.isOut()) {
            return false; // private chats, and own messages, stay untouched
        }
        return (msg.isVoice() && AyuPrefs.get(AyuPrefs.HIDE_VOICE))
                || (msg.isRoundVideo() && AyuPrefs.get(AyuPrefs.HIDE_ROUND_VIDEO))
                || (msg.isDice() && AyuPrefs.get(AyuPrefs.HIDE_DICE))
                || ((msg.isSticker() || msg.isAnimatedSticker() || msg.isVideoSticker()) && AyuPrefs.get(AyuPrefs.HIDE_STICKERS));
    }
}
