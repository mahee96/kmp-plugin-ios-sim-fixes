//
//  SimulatorDeviceType.java
//  kmp-plugin-ios-sim-fixes
//
//  Created by Magesh K on 2/10/26.
//

package com.jetbrains.cidr.execution.simulatorSupport;

public class SimulatorDeviceType {
    public static int intFromNumberOrString(Object o) {
        return ((Number) o).intValue();
    }
}
