package com.dfdyz.void_power.utils.font;

import dan200.computercraft.api.lua.LuaException;

public interface IFontLib {
    Font.CharMat get(char ch) throws LuaException;
}
