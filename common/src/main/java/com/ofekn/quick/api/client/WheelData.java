package com.ofekn.quick.api.client;

import java.util.List;

// TODO javadoc
public interface WheelData {
    List<IWheelOption> options();
    Status status();

    enum Status {
        SELECTING,
        FINISH_SELECT,
        CANCEL
    }
}
