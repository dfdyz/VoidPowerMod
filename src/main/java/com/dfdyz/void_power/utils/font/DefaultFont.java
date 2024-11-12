package com.dfdyz.void_power.utils.font;

import dan200.computercraft.api.lua.LuaException;

public class DefaultFont implements IFontLib{
    public static final DefaultFont Instance = new DefaultFont();

    @Override
    public Font.CharMat get(char ch) throws LuaException {
        return Font.getMat(ch);
    }
}
