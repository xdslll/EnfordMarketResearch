package com.enford.market.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 16/2/8.
 */
public final class InputMethodUtil {

    /**
     * 隐藏键盘
     */
    public static void hideKeyBoard(Context _context, EditText searchText) {
        InputMethodManager immDefault = (InputMethodManager) _context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        immDefault.hideSoftInputFromWindow(searchText.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 隐藏键盘
     * @param _context
     */
    public static void hideKeyBoard(Activity _context) {
        View view = _context.getWindow().getDecorView();
        InputMethodManager immDefault = (InputMethodManager) _context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        immDefault.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 判断软键盘是否显示
     */
    public static boolean isKeyBoardShow(Context _context) {
        InputMethodManager immDefault = (InputMethodManager) _context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        return immDefault.isActive();
    }
    /**
     * 设置标准
     */
    public static void setKeyBoardFlag(Context _context){
        InputMethodManager imm = (InputMethodManager) _context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 显示键盘
     */
    public static void showKeyBoard(Activity _context) {//, EditText searchText) {
        InputMethodManager immDefault = (InputMethodManager) _context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        //immDefault.showSoftInputFromInputMethod(searchText.getWindowToken(), 0);
        View view = _context.getWindow().getDecorView();
        immDefault.showSoftInputFromInputMethod(view.getWindowToken(), InputMethodManager.SHOW_FORCED);
    }

}
