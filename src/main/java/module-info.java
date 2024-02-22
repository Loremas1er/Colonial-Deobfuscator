module ru.paimon.deobfuscator.colonialdeobfuscator {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires org.objectweb.asm;
    requires org.objectweb.asm.tree;
    requires org.objectweb.asm.tree.analysis;
    requires org.objectweb.asm.util;
    requires jdk.unsupported;
    requires org.apache.commons.io;
    opens ru.paimon.deobfuscator.colonialdeobfuscator to javafx.fxml;
    exports ru.paimon.deobfuscator.colonialdeobfuscator;
}