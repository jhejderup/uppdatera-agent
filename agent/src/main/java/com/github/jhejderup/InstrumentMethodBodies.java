package com.github.jhejderup;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

public class InstrumentMethodBodies extends ClassVisitor {

  private final String hotClassName;

  public InstrumentMethodBodies(ClassWriter cw, String clazzName) {
    super(Opcodes.ASM5, cw);
    hotClassName = clazzName;
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String desc,
      String signature, String[] exceptions) {

    MethodVisitor mv = super
        .visitMethod(access, name, desc, signature, exceptions);

    return new InstrumentMethod(mv, access, name, desc);

  }

  private class InstrumentMethod extends AdviceAdapter {

    private final String hotMethodName;

    InstrumentMethod(final MethodVisitor mv, final int access,
        final String name, final String desc) {
      super(Opcodes.ASM5, mv, access, name, desc);
      this.hotMethodName = name;
    }

    @Override
    protected void onMethodEnter() {
      visitLdcInsn(
          "L" + hotClassName + "/" + hotMethodName + this.methodDesc + "\n");
      visitMethodInsn(INVOKESTATIC, "com/github/jhejderup/DependencyMethodSet",
          "push", "(Ljava/lang/String;)V", false);
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
      super.visitMaxs(0, 0);
    }
  }
}


