package it.unibo.assignment_02;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.type.TypeParameter;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class DependencyVisitor extends VoidVisitorAdapter<Object> {

    private final Set<String> dependencySet = new java.util.HashSet<String>();
    private final Set<String> genericsSet = new java.util.HashSet<String>();
    private String className = "";

    /**
     *  Finding a type in a class/interface declaration 
     */			
    public void visit(ClassOrInterfaceDeclaration n, Object arg) {
        super.visit(n, arg);
        n.getExtendedTypes().forEach(e -> dependencySet.add(e.toString()));
        n.getImplementedTypes().forEach(e -> dependencySet.add(e.toString()));
        n.getConstructors().forEach(e -> e.getParameters().forEach(p -> dependencySet.add(p.getTypeAsString())));
        for (TypeParameter typeParameter : n.getTypeParameters()) {
            this.genericsSet.add(typeParameter.getNameAsString());
        }
        this.className = n.getNameAsString();
    }
    
    /**
     *  Package declaration 
     */			
    public void visit(PackageDeclaration n, Object arg) {
        super.visit(n, arg);
    }
    
    /**
     *  Finding a type in a field declaration 
     */			
    public void visit(FieldDeclaration n, Object arg) {
        super.visit(n, arg);
        for (VariableDeclarator vd : n.getVariables()) {
            dependencySet.add(vd.getTypeAsString());
        }
    }
    
    /**
     *  Finding types in methods declaration 
     */			
    public void visit(MethodDeclaration n, Object arg) {
        super.visit(n, arg);
        for (var p: n.getParameters()) {
            dependencySet.add(p.getTypeAsString());
        }
        dependencySet.add(n.getTypeAsString());
    }
    
    /**
     *  Finding type in object creation 
     */			
    public void visit(ObjectCreationExpr n, Object arg) {
        super.visit(n, arg);
        var interfaceOrClassType =  n.getChildNodes().get(0);
        dependencySet.add(interfaceOrClassType.toString());
    }
    
    /**
     *  Finding types in variable declaration 
     */			
    public void visit(VariableDeclarator n, Object arg) {
        super.visit(n, arg);
        dependencySet.add(n.getTypeAsString());
    }

    public Set<String> getSet() {
        List<String> primitive;
        try(InputStream in= Thread.currentThread()
                .getContextClassLoader().getResourceAsStream("ExcludeDependencyFile.json")){
            ObjectMapper mapper = new ObjectMapper();
            primitive = mapper.readValue(in, new TypeReference<List<String>>(){});
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
        return dependencySet.stream()
                .map(e -> e.substring(e.lastIndexOf('.') + 1))
                .flatMap(e -> {
                    if (e.contains("<"))
                        return Stream.of(e.split("<|>"));
                    else
                        return Stream.of(e);
                })
                .flatMap(e -> {
                    if (e.contains(","))
                        return Stream.of(e.split(","));
                    else
                        return Stream.of(e);
                })
                .map(e -> e.replace(" ","")
                        .replace("[","")
                        .replace("]",""))
                .filter(e -> !primitive.contains(e) && !e.isEmpty() && !this.genericsSet.contains(e))
                .collect(java.util.stream.Collectors.toSet());
    }

    public String getClassName() {
        return this.className;
    }
    
}
