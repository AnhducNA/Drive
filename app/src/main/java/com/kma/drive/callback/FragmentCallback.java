package com.kma.drive.callback;

public interface FragmentCallback {
    void doAnOrder(String order);
    void doAnOrderWithParams(int order, Object ... objects);
    void back();
}