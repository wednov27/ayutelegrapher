/*
 * This file is part of AyuTelegrapher, a fork of Telegraher.
 * Licensed under the GNU GPL v2 or later, see the repository LICENSE.
 */

package com.ayutelegrapher;

import android.view.View;

import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalFragment;

import java.util.ArrayList;

// Settings page: ghost mode, message filters, chat-list reactions.
public class AyuSettingsActivity extends UniversalFragment {

    private static final int ID_GHOST = 1;
    private static final int ID_REACTIONS = 2;
    private static final int ID_FILTER_BASE = 10;

    private static final String[] FILTER_KEYS = {
            AyuPrefs.HIDE_VOICE, AyuPrefs.HIDE_ROUND_VIDEO, AyuPrefs.HIDE_DICE, AyuPrefs.HIDE_STICKERS
    };
    private static final String[] FILTER_LABELS = {
            "Voice messages", "Round videos", "Dice 🎲 🎯 🏀 ⚽ 🎳 🎰", "Stickers"
    };

    @Override
    protected CharSequence getTitle() {
        return "AyuTelegrapher";
    }

    @Override
    protected void fillItems(ArrayList<UItem> items, UniversalAdapter adapter) {
        items.add(UItem.asCheck(ID_GHOST, "Ghost mode").setChecked(GhostMode.isEnabled()));
        items.add(UItem.asShadow("No typing, online, read receipts or story views are sent."));

        items.add(UItem.asHeader("Hide in groups and channels"));
        for (int i = 0; i < FILTER_KEYS.length; i++) {
            items.add(UItem.asCheck(ID_FILTER_BASE + i, FILTER_LABELS[i]).setChecked(AyuPrefs.get(FILTER_KEYS[i])));
        }
        items.add(UItem.asShadow("Private chats and your own messages are never hidden. Applies when a chat is opened."));

        items.add(UItem.asCheck(ID_REACTIONS, "Hide reactions in chat list").setChecked(AyuPrefs.get(AyuPrefs.HIDE_CHATLIST_REACTIONS)));
        items.add(UItem.asShadow("No heart badge and no \"Reacted\" preview. Reactions inside chats stay."));
    }

    @Override
    protected void onClick(UItem item, View view, int position, float x, float y) {
        final boolean on;
        if (item.id == ID_GHOST) {
            on = !GhostMode.isEnabled();
            GhostMode.setEnabled(on);
        } else if (item.id == ID_REACTIONS) {
            on = !AyuPrefs.get(AyuPrefs.HIDE_CHATLIST_REACTIONS);
            AyuPrefs.set(AyuPrefs.HIDE_CHATLIST_REACTIONS, on);
            getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, MessagesController.UPDATE_MASK_ALL);
        } else if (item.id >= ID_FILTER_BASE && item.id < ID_FILTER_BASE + FILTER_KEYS.length) {
            final String key = FILTER_KEYS[item.id - ID_FILTER_BASE];
            on = !AyuPrefs.get(key);
            AyuPrefs.set(key, on);
        } else {
            return;
        }
        if (view instanceof TextCheckCell) {
            ((TextCheckCell) view).setChecked(on);
        }
    }

    @Override
    protected boolean onLongClick(UItem item, View view, int position, float x, float y) {
        return false;
    }
}
