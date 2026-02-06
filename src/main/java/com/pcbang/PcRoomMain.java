package com.pcbang;

import frame.FrameBase;
import frame.PanelStart;

import java.awt.Font;

import javax.swing.UIManager;

import com.formdev.flatlaf.FlatLightLaf;
public class PcRoomMain {
    
    public static void main(String[] args) {
        FrameBase.getInstance(new PanelStart());
        FlatLightLaf.setup();        
        UIManager.put("defaultFont", new Font("맑은 고딕", Font.PLAIN, 14));
    }
}
