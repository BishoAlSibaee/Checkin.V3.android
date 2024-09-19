package com.example.hotelservicesstandalone.Classes.Devices;

import java.util.ArrayList;
import java.util.List;

public class EnumKeyValue {
    List<enumUnit> enums;

    public EnumKeyValue(Object[] keys,Object[] values) {
        enums = new ArrayList<>();
        for (int i=0;i< keys.length;i++) {
            enums.add(new enumUnit(keys[i].toString(),values[i].toString()));
        }
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
}

class enumUnit {
    public String key;
    public String value;

    enumUnit(String key,String value) {
        this.key = key;
        this.value = value;
    }
}