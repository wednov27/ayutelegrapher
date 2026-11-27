/*
 * This file is part of AyuTelegrapher, a fork of Telegraher.
 * Licensed under the GNU GPL v2 or later, see the repository LICENSE.
 */

package com.ayutelegrapher;

// Build-time switches for removing Telegram features.
public final class Debloat {

    public static final boolean HIDE_STORIES = true;
    public static final boolean HIDE_PREMIUM = true;
    public static final boolean HIDE_ADS = true;

    private Debloat() {
    }
}
