package ru.paimon.deobfuscator.colonialdeobfuscator;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import ru.paimon.deobfuscator.colonialdeobfuscator.transformers.*;

import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

public class MainCtr {
    private boolean str = false,flow = false,bool = false,num = false,accs = false;
    private static JarOutputStream finalOutputStream = null;
    public static Map<String, ClassNode> ToAdd = new HashMap<>();
    private static JarOutputStream outputStream = null;
    public static Map<String, ClassNode> classes = new HashMap<>();
    @FXML
    public TextField output_path;
    @FXML
    public TextField input_path;
    @FXML
    protected void onAddInputPathButtonClick(){
        final FileChooser fileChooser = new FileChooser();
        try{
            File file = fileChooser.showOpenDialog(Main.stage);
            if (file.exists()) {
                String uga = file.getAbsolutePath();
                input_path.setText(uga);
            }
        }catch (Exception ignored){}
    }
    @FXML
    protected void onAddOutputPathButtonClick(){
        final FileChooser fileChooser = new FileChooser();
        try{
            File file = fileChooser.showOpenDialog(Main.stage);
            if (file.exists()) {
                String uga = file.getAbsolutePath();
                output_path.setText(uga);
            }
        }catch (Exception ignored){}
    }
    @FXML
    protected void onStringTransformerButtonClick(){
        str = !str;
    }
    @FXML
    protected void onBooleanTransformerButtonClick(){
        bool = !bool;
    }
    @FXML
    protected void onFlowTransformerButtonClick(){
        flow = !flow;
    }
    @FXML
    protected void onNumberTransformerButtonClick(){
        num = !num;
    }
    @FXML
    protected void onAccessTransformerButtonClick(){
        accs = !accs;
    }
    @FXML
    protected void onDeobfuscateButtonClick() throws IOException {
        File file = new File(input_path.getText());
        if(file.exists()){
            JarFile jarFile = new JarFile(file);
            jarFile.stream().forEach((entry) -> {
                try {
                    if (entry.getName().endsWith(".class")) {
                        ClassReader classReader = new ClassReader(jarFile.getInputStream(entry));
                        ClassNode classNode = new ClassNode();
                        classReader.accept(classNode, 2);
                        classes.put(classNode.name, classNode);
                    } else if (!entry.isDirectory()) {
                        finalOutputStream.putNextEntry(new ZipEntry(entry.getName()));
                        finalOutputStream.write(toByteArray(jarFile.getInputStream(entry)));
                        finalOutputStream.closeEntry();
                    }
                } catch (Exception ignored) {}

            });
            outputStream = new JarOutputStream(new FileOutputStream(output_path.getText()));
            parseInput(input_path.getText());
            classes.values().forEach((classNode) -> {
                for (ClassModifier m : modules()) {
                    m.modify(classNode);
                }

            });
            classes.putAll(ToAdd);
            classes.values().forEach((classNode) -> {
                ClassWriter classWriter = new ClassWriter(1);

                try {
                    classNode.accept(classWriter);
                    JarEntry jarEntry = new JarEntry(classNode.name.concat(".class"));
                    outputStream.putNextEntry(jarEntry);
                    outputStream.write(classWriter.toByteArray());
                } catch (Exception var3) {
                    var3.printStackTrace();
                }

            });
            outputStream.setComment("https://discord.paimonsoft.xyz/");
            outputStream.setComment("https://discord.paimonsoft.xyz/");
            outputStream.close();
        }
    }
    public List<ClassModifier> modules() {
        List<ClassModifier> modifier = new ArrayList<>(List.of());
        if(flow){
            modifier.add(new FlowTransformer());
        }
        if(num){
            modifier.add(new NumberTransformer());
        }
        if(bool){
            modifier.add(new BooleanTransformer());
        }
        if(num && bool){
            modifier.add(new NumberTransformer());
        }
        if(str){
            modifier.add(new StringTransformer());
        }
        if(accs){
            modifier.add(new AccessTransformer());
        }
        modifier.add(new CleanTransformer());
        if(num){
            modifier.add(new FixNumberTransformer());
        }
        return modifier;
    }
    private static void parseInput(String input) {
        JarFile jarFile = null;

        try {
            jarFile = new JarFile(input);
        } catch (IOException var5) {
            var5.printStackTrace();
        }

        JarFile finalJarFile = jarFile;
        jarFile.stream().forEach((entry) -> {
            try {
                if (entry.getName().endsWith(".class")) {
                    ClassReader classReader = new ClassReader(finalJarFile.getInputStream(entry));
                    ClassNode classNode = new ClassNode();
                    classReader.accept(classNode, 2);
                    classes.put(classNode.name, classNode);
                } else if (!entry.isDirectory()) {
                    outputStream.putNextEntry(new ZipEntry(entry.getName()));
                    outputStream.write(toByteArray(finalJarFile.getInputStream(entry)));
                    outputStream.closeEntry();
                }
            } catch (Exception var4) {
                var4.printStackTrace();
            }

        });

        try {
            jarFile.close();
        } catch (IOException var4) {
            var4.printStackTrace();
        }

    }
    private static byte[] toByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte['\uffff'];

        int length;
        while((length = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, length);
        }

        outputStream.flush();
        return outputStream.toByteArray();
    }
}
