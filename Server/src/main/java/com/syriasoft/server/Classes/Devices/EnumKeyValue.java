package com.syriasoft.server.Classes.Devices;

import java.util.ArrayList;
import java.util.List;

public class EnumKeyValue {
    public List<enumUnit> enums;

    public EnumKeyValue(Object[] keys,Object[] values) {
        try {
            enums = new ArrayList<>();
            for (int i = 0; i < keys.length; i++) {
                enums.add(new enumUnit(keys[i].toString(), values[i].toString()));
            }
        }
        catch (Exception e) {

        }
    }

    public EnumKeyValue(List<enumUnit> enums) {
        this.enums = enums;
    }

    int getNext(String currentFan) {
        int index = 0;
        for (int i=0;i<enums.size();i++) {
            if (enums.get(i).key.equals(currentFan)) {
                index = i;
            }
        }
        if (index == enums.size()-1) {
            return 0;
        }
        else {
            return index+1;
        }
    }

    public static class enumUnit {
        public String key;
        public String value;

        public enumUnit(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}

