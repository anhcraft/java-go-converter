package modules;

import dev.anhcraft.jvmkit.utils.ReflectionUtil;
import dev.anhcraft.jvmkit.utils.StringUtil;
import org.apache.commons.text.CaseUtils;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.function.Consumer;

public class JavaClassToGoStruct implements Consumer<Class<?>> {
    private String toCamelCase(String s) {
        s = s.replaceFirst("^[^A-Za-z]+", "");
        int i = 0;
        StringBuilder sb = new StringBuilder();
        for(; i < s.length() - 1; i++){
            if(Character.isLowerCase(s.charAt(i)) && Character.isUpperCase(s.charAt(i+1))) {
                sb.append(s.charAt(i));
                sb.append("_");
                sb.append(s.charAt(i+1));
                i++;
            } else {
                sb.append(s.charAt(i));
            }
        }
        s = sb.append(s.charAt(i)).toString();
        sb = new StringBuilder();
        for(String str : s.split("_")){
            if(str.length() > 0) {
                sb.append(Character.toUpperCase(str.charAt(0)));
                if(str.length() > 1){
                    sb.append(str.substring(1).toLowerCase());
                }
            }
        }
        return sb.toString();
    }

    @Override
    public void accept(Class<?> aClass) {
        System.out.printf("type %s struct {\n", aClass.getSimpleName());
        Arrays.stream(ReflectionUtil.getAllFields(aClass))
                .filter(f -> !Modifier.isStatic(f.getModifiers()))
                .forEach(f -> {
                    String prettyName = toCamelCase(f.getName());
                    String type = f.getType().getSimpleName();
                    if (long.class.equals(f.getType()) || Long.class.equals(f.getType())) {
                        type = "int64";
                    } else if (int.class.equals(f.getType()) || Integer.class.equals(f.getType())) {
                        type = "int";
                    } else if (short.class.equals(f.getType()) || Short.class.equals(f.getType())) {
                        type = "int16";
                    } else if (byte.class.equals(f.getType()) || Byte.class.equals(f.getType())) {
                        type = "int8";
                    } else if (boolean.class.equals(f.getType()) || Boolean.class.equals(f.getType())) {
                        type = "bool";
                    } else if (float.class.equals(f.getType()) || Float.class.equals(f.getType())) {
                        type = "float32";
                    } else if (double.class.equals(f.getType()) || Double.class.equals(f.getType())) {
                        type = "float64";
                    } else if (char.class.equals(f.getType()) || Character.class.equals(f.getType())) {
                        type = "rune";
                    } else if (String.class.equals(f.getType())) {
                        type = "string";
                    }
                    System.out.printf("  %s %s `json:\"%s\"`\n", prettyName, type, f.getName());
        });
        System.out.println("}");
    }
}
