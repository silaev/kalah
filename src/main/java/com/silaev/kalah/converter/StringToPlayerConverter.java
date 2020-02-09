package com.silaev.kalah.converter;

import com.silaev.kalah.model.Player;

import java.beans.PropertyEditorSupport;

/**
 * @author Konstantin Silaev on 2/9/2020
 */
public class StringToPlayerConverter extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) {
        setValue(Player.byName(text));
    }
}
