/*
 * This file is part of AyuTelegrapher, a fork of Telegraher.
 * Licensed under the GNU GPL v2 or later, see the repository LICENSE.
 *
 * Ghost mode ported from AyuGram for Android (GPL), simplified to one switch.
 */

package com.ayutelegrapher;

import android.content.Context;
import android.content.SharedPreferences;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_account;
import org.telegram.tgnet.tl.TL_stories;

// When on: no typing, always offline, no read receipts, no story views.
public final class GhostMode {

    private static final String PREFS = "ayutelegrapher";
    private static final String KEY_ENABLED = "ghost_mode";

    private static volatile Boolean enabled;

    private GhostMode() {
    }

    private static SharedPreferences prefs() {
        return ApplicationLoader.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static boolean isEnabled() {
        if (enabled == null) {
            enabled = prefs().getBoolean(KEY_ENABLED, false);
        }
        return enabled;
    }

    public static void setEnabled(boolean value) {
        enabled = value;
        prefs().edit().putBoolean(KEY_ENABLED, value).apply();
    }

    // Returns true when the request must not be sent; its callback then gets a fake success.
    public static boolean swallow(TLObject req, RequestDelegate onComplete) {
        if (!isEnabled()) {
            return false;
        }
        if (req instanceof TL_account.updateStatus) {
            ((TL_account.updateStatus) req).offline = true;
            return false;
        }
        TLObject fake;
        if (req instanceof TLRPC.TL_messages_readHistory
                || req instanceof TLRPC.TL_messages_readMessageContents) {
            TLRPC.TL_messages_affectedMessages res = new TLRPC.TL_messages_affectedMessages();
            res.pts = -1; // -1 is ignored by processNewDifferenceParams
            res.pts_count = 0;
            fake = res;
        } else if (req instanceof TLRPC.TL_messages_setTyping
                || req instanceof TLRPC.TL_messages_setEncryptedTyping
                || req instanceof TLRPC.TL_messages_readEncryptedHistory
                || req instanceof TLRPC.TL_messages_readDiscussion
                || req instanceof TLRPC.TL_channels_readHistory
                || req instanceof TLRPC.TL_channels_readMessageContents
                || req instanceof TL_stories.TL_stories_readStories
                || req instanceof TL_stories.TL_stories_incrementStoryViews) {
            fake = new TLRPC.TL_boolTrue();
        } else {
            return false;
        }
        if (onComplete != null) {
            try {
                onComplete.run(fake, null);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        return true;
    }

    // Sending a message marks you online server-side; follow it with an offline status.
    public static RequestDelegate offlineAfterSend(TLObject req, RequestDelegate onComplete, ConnectionsManager cm) {
        if (!isEnabled() || !(req instanceof TLRPC.TL_messages_sendMessage
                || req instanceof TLRPC.TL_messages_sendMedia
                || req instanceof TLRPC.TL_messages_sendMultiMedia
                || req instanceof TLRPC.TL_messages_forwardMessages)) {
            return onComplete;
        }
        return (response, error) -> {
            if (onComplete != null) {
                onComplete.run(response, error);
            }
            TL_account.updateStatus status = new TL_account.updateStatus();
            status.offline = true;
            cm.sendRequest(status, null);
        };
    }
}
