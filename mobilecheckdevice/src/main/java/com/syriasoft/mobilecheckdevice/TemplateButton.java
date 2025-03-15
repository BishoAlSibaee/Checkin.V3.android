package com.syriasoft.mobilecheckdevice;

import java.util.List;

public class TemplateButton {

    String SwitchName;
    int DP;
    boolean status;

    public TemplateButton(String switchName, int DP) {
        SwitchName = switchName;
        this.DP = DP;
    }

    public TemplateButton(String switchName, int DP,boolean s) {
        SwitchName = switchName;
        this.DP = DP;
        status = s;
    }

    public static int searchTemplateButton(List<TemplateButton> list,TemplateButton TB) {
        for (int i=0;i<list.size();i++) {
            if (list.get(i).SwitchName.equals(TB.SwitchName) && list.get(i).DP == TB.DP) {
                return i;
            }
        }
        return -1;
    }
}
