package com.kma.drive.callback;

public interface FragmentCallback {
    void doAnOrder(int order);
    void doAnOrderWithParams(int order, Object ... objects);
    void back();
}