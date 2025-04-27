package it.unibo.assignment_02;

import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.Set;
import java.util.stream.Stream;

public class DependencyVisitor extends VoidVisitorAdapter<Object> {

    Set<String> set = new java.util.HashSet<String>();
    
    /**
     *  Finding a type in a class/interface declaration 
     */			
    public void visit(ClassOrInterfaceDeclaration n, Object arg) {
        super.visit(n, arg);
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
            set.add(vd.getTypeAsString());
        }
    }
    
    /**
     *  Finding types in methods declaration 
     */			
    public void visit(MethodDeclaration n, Object arg) {
        super.visit(n, arg);
        for (var p: n.getParameters()) {
            set.add(p.getTypeAsString());
        }
        set.add(n.getTypeAsString());
    }
    
    /**
     *  Finding type in object creation 
     */			
    public void visit(ObjectCreationExpr n, Object arg) {
        super.visit(n, arg);
        var interfaceOrClassType =  n.getChildNodes().get(0);
        set.add(interfaceOrClassType.toString());
    }
    
    /**
     *  Finding types in variable declaration 
     */			
    public void visit(VariableDeclarator n, Object arg) {
        super.visit(n, arg);
        set.add(n.getTypeAsString());
    }

    public Set<String> getSet() {
        Set<String> primitive = Set.of("byte", "short", "int", "long", "float", "double", "boolean", "char", "void", "var");
        return set.stream()
                .map(e -> e.substring(e.lastIndexOf('.') + 1))
                .flatMap(e -> {
                    if (!e.contains("<"))
                        return Stream.of(e);
                    else
                        return Stream.of(e.split("<|>"));
                })
                .filter(e -> !primitive.contains(e))
                .collect(java.util.stream.Collectors.toSet());
    }
    
}
