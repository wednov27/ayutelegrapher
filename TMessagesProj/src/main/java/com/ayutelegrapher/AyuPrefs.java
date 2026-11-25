/*
 * This file is part of AyuTelegrapher, a fork of Telegraher.
 * Licensed under the GNU GPL v2 or later, see the repository LICENSE.
 */

package com.ayutelegrapher;

import android.content.Context;
import android.content.SharedPreferences;

import org.telegram.messenger.ApplicationLoader;

// Per-phone switches set on the AyuTelegrapher settings page.
public final class AyuPrefs {

    public static final String HIDE_VOICE = "hide_voice";
    public static final String HIDE_ROUND_VIDEO = "hide_round_video";
    public static final String HIDE_DICE = "hide_dice";
    public static final String HIDE_STICKERS = "hide_stickers";
    public static final String HIDE_CHATLIST_REACTIONS = "hide_chatlist_reactions";

    private AyuPrefs() {
    }

    static SharedPreferences prefs() {
        return ApplicationLoader.applicationContext.getSharedPreferences("ayutelegrapher", Context.MODE_PRIVATE);
    }

    private static boolean defaultFor(String key) {
        return HIDE_VOICE.equals(key) || HIDE_ROUND_VIDEO.equals(key) || HIDE_DICE.equals(key);
    }

    public static boolean get(String key) {
        return prefs().getBoolean(key, defaultFor(key));
    }

    public static void set(String key, boolean value) {
        prefs().edit().putBoolean(key, value).apply();
    }
}
