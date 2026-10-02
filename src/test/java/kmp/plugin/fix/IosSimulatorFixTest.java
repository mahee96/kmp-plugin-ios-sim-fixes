//
//  IosSimulatorFixTest.java
//  kmp-plugin-ios-sim-fixes
//
//  Created by Magesh K on 2/10/26.
//

package kmp.plugin.fix;

import com.jetbrains.cidr.execution.XcodeSimulatorsOnlineIndexPreferredPlatformBuildForSdkBuild;
import com.jetbrains.cidr.execution.simulatorSupport.SimulatorDeviceType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IosSimulatorFixTest {

    @Test
    @DisplayName("Verify SimulatorDeviceType intFromNumberOrString handles non-null numbers and null gracefully")
    public void testSimulatorDeviceTypeNullGuard() {
        assertEquals(42, SimulatorDeviceType.intFromNumberOrString(42));
        assertEquals(0, SimulatorDeviceType.intFromNumberOrString(null));
    }

    @Test
    @DisplayName("Verify XcodeSimulatorsOnlineIndexPreferredPlatformBuildForSdkBuild preserves all runtimes")
    public void testSimulatorRuntimeFilterPreservation() {
        XcodeSimulatorsOnlineIndexPreferredPlatformBuildForSdkBuild filter =
                new XcodeSimulatorsOnlineIndexPreferredPlatformBuildForSdkBuild();

        List<String> inputRuntimes = Arrays.asList("iOS-26-5", "iOS-27-0");
        List<String> result = filter.filterRuntimesWithAvailablePlatforms(inputRuntimes);

        assertEquals(2, result.size());
        assertEquals(inputRuntimes, result);
    }
}
