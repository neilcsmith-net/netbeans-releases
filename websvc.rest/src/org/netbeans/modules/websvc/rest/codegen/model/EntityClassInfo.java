/*
 * ClassInfo.java
 *
 * Created on March 27, 2007, 6:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.websvc.rest.codegen.model;

import org.netbeans.modules.websvc.rest.support.*;
import com.sun.source.tree.ClassTree;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;

/**
 *
 * @author PeterLiu
 */
public class EntityClassInfo {
    
    private JavaSource source;
    private String name;
    private String type;
    private String packageName;
    private Collection<FieldInfo> fieldInfos;
    private FieldInfo idFieldInfo;
    
    
    /** Creates a new instance of ClassInfo */
    public EntityClassInfo(JavaSource source) {
        this.source = source;
        this.fieldInfos = new ArrayList<FieldInfo>();
        
        try {
            source.runUserActionTask(new AbstractTask<CompilationController>() {
                public void run(CompilationController controller) throws IOException {
                    ClassTree tree = JavaSourceHelper.getTopLevelClassTree(controller);
                    packageName = controller.getCompilationUnit().getPackageName().toString();     
                    name = tree.getSimpleName().toString();
                    type = packageName + "." + name;
                    
                    TypeElement classElement = JavaSourceHelper.getTopLevelClassElement(controller);
                    List<VariableElement> fields = ElementFilter.fieldsIn(classElement.getEnclosedElements());
                    
                    for (VariableElement field : fields) {
                        FieldInfo fieldInfo = new FieldInfo();
                        
                        fieldInfos.add(fieldInfo);
                        fieldInfo.setName(field.getSimpleName().toString());
                        
                        TypeMirror fieldType = field.asType();
                        
                        if (fieldType.getKind() == TypeKind.DECLARED) {
                            DeclaredType declType = (DeclaredType) fieldType;
                            
                            fieldInfo.setType(declType.asElement().toString());
                      
                            for (TypeMirror arg : declType.getTypeArguments()) {
                                fieldInfo.setTypeArg(arg.toString());
                            }
                        } else {
                            fieldInfo.setType(fieldType.toString());
                        }
                        
                        
                        for (AnnotationMirror annotation : field.getAnnotationMirrors()) {
                            fieldInfo.addAnnotation(annotation.toString());
                        }
                        
                        if (fieldInfo.isId()) {
                            idFieldInfo = fieldInfo;
                        }
                    }
                }
            }, true);
        } catch (IOException ex) {
            
        }
    }
    
    public String getName() {
        return name;
    }
    
    public String getType() {
        return type;
    }
    
    public String getPackageName() {
        return packageName;
    }
    
    public FieldInfo getIdFieldInfo() {
        return idFieldInfo;
    }
    
    public Collection<FieldInfo> getFieldInfos() {
        return fieldInfos;
    }
    
    public class FieldInfo {
        private String name;
        private String type;
        private String typeArg;
        private List<String> annotations;
        
        public FieldInfo() {
            annotations = new ArrayList<String>();
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        public String getType() {
            return type;
        }
        
        public void setTypeArg(String typeArg) {
            this.typeArg = typeArg;
        }
        
        public String getTypeArg() {
            return typeArg;
        }
        
        public void addAnnotation(String annotation) {
            this.annotations.add(annotation);
        }
        
        public boolean isId() {
            return matchAnnotation("@javax.persistence.Id");
        }
        
        public boolean isRelationship() {
            return isOneToOne() || isOneToMany() || isManyToOne() ||
                    isManyToMany();
        }
        
        public boolean isOneToOne() {
            return matchAnnotation("@javax.persistence.OneToOne");
        }
        
        public boolean isOneToMany() {
            return matchAnnotation("@javax.persistence.OneToMany");
        }
        
        public boolean isManyToOne() {
            return matchAnnotation("@javax.persistence.ManyToOne");
        }
        
         public boolean isManyToMany() {
            return matchAnnotation("@javax.persistence.ManyToMany");
        }
         
        private boolean matchAnnotation(String annotation) {
            for (String a : annotations) {
                if (a.startsWith(annotation))
                    return true;
            }
            
            return false;
        }
    }
}
