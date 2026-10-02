//
//  IosSimulatorFix.java
//  kmp-plugin-ios-sim-fixes
//
//  Created by Magesh K on 2/10/26.
//

package kmp.plugin.fix;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class IosSimulatorFix {
    private static final Logger LOGGER = Logger.getLogger(IosSimulatorFix.class.getName());
    private static final String TARGET_DEVICE_TYPE = "com/jetbrains/cidr/execution/simulatorSupport/SimulatorDeviceType";
    private static final String TARGET_PLATFORM_FILTER = "com/jetbrains/cidr/execution/XcodeSimulatorsOnlineIndexPreferredPlatformBuildForSdkBuild";

    private IosSimulatorFix() {}

    public static void premain(String args, Instrumentation inst) {
        initialize(inst);
    }

    public static void agentmain(String args, Instrumentation inst) {
        initialize(inst);
    }

    private static void initialize(Instrumentation inst) {
        LOGGER.info("[kmp-plugin-ios-sim-fixes] Initializing simulator compatibility fixes");
        inst.addTransformer(new SimulatorFixTransformer(), true);
    }

    private static final class SimulatorFixTransformer implements ClassFileTransformer {
        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                               ProtectionDomain protectionDomain, byte[] classfileBuffer) {
            if (TARGET_DEVICE_TYPE.equals(className)) {
                return patchDeviceType(classfileBuffer);
            }
            if (TARGET_PLATFORM_FILTER.equals(className)) {
                return patchPlatformFilter(classfileBuffer);
            }
            return null;
        }

        private byte[] patchDeviceType(byte[] classfileBuffer) {
            try {
                ClassReader reader = new ClassReader(classfileBuffer);
                ClassWriter writer = new SafeClassWriter(reader, ClassWriter.COMPUTE_FRAMES);

                reader.accept(new ClassVisitor(Opcodes.ASM9, writer) {
                    @Override
                    public MethodVisitor visitMethod(int access, String name, String descriptor,
                                                    String signature, String[] exceptions) {
                        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                        if ("intFromNumberOrString".equals(name) && "(Ljava/lang/Object;)I".equals(descriptor)) {
                            return new MethodVisitor(Opcodes.ASM9, mv) {
                                @Override
                                public void visitCode() {
                                    super.visitCode();
                                    Label nonNullLabel = new Label();
                                    super.visitVarInsn(Opcodes.ALOAD, 0);
                                    super.visitJumpInsn(Opcodes.IFNONNULL, nonNullLabel);
                                    super.visitInsn(Opcodes.ICONST_0);
                                    super.visitInsn(Opcodes.IRETURN);
                                    super.visitLabel(nonNullLabel);
                                }
                            };
                        }
                        return mv;
                    }
                }, 0);

                LOGGER.info("[kmp-plugin-ios-sim-fixes] Patched " + TARGET_DEVICE_TYPE);
                return writer.toByteArray();
            } catch (Throwable t) {
                LOGGER.log(Level.WARNING, "[kmp-plugin-ios-sim-fixes] Failed to patch " + TARGET_DEVICE_TYPE, t);
                return null;
            }
        }

        private byte[] patchPlatformFilter(byte[] classfileBuffer) {
            try {
                ClassReader reader = new ClassReader(classfileBuffer);
                ClassWriter writer = new SafeClassWriter(reader, ClassWriter.COMPUTE_FRAMES);

                reader.accept(new ClassVisitor(Opcodes.ASM9, writer) {
                    @Override
                    public MethodVisitor visitMethod(int access, String name, String descriptor,
                                                    String signature, String[] exceptions) {
                        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                        if ("filterRuntimesWithAvailablePlatforms".equals(name) && "(Ljava/util/List;)Ljava/util/List;".equals(descriptor)) {
                            return new MethodVisitor(Opcodes.ASM9, mv) {
                                @Override
                                public void visitCode() {
                                    super.visitCode();
                                    super.visitVarInsn(Opcodes.ALOAD, 1);
                                    super.visitInsn(Opcodes.ARETURN);
                                }
                            };
                        }
                        return mv;
                    }
                }, 0);

                LOGGER.info("[kmp-plugin-ios-sim-fixes] Patched " + TARGET_PLATFORM_FILTER);
                return writer.toByteArray();
            } catch (Throwable t) {
                LOGGER.log(Level.WARNING, "[kmp-plugin-ios-sim-fixes] Failed to patch " + TARGET_PLATFORM_FILTER, t);
                return null;
            }
        }
    }

    private static final class SafeClassWriter extends ClassWriter {
        SafeClassWriter(ClassReader classReader, int flags) {
            super(classReader, flags);
        }

        @Override
        protected String getCommonSuperClass(String type1, String type2) {
            try {
                return super.getCommonSuperClass(type1, type2);
            } catch (Throwable ignored) {
                return "java/lang/Object";
            }
        }
    }
}
