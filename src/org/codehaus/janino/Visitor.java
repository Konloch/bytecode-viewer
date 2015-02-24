
/*
 * Janino - An embedded Java[TM] compiler
 *
 * Copyright (c) 2001-2010, Arno Unkrig
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *       following disclaimer.
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 *       following disclaimer in the documentation and/or other materials provided with the distribution.
 *    3. The name of the author may not be used to endorse or promote products derived from this software without
 *       specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.codehaus.janino;


/** Basis for the "visitor" pattern as described in "Gamma, Helm, Johnson, Vlissides: Design Patterns". */
public
class Visitor {

    /**
     * The union of {@link ImportVisitor}, {@link TypeDeclarationVisitor}, {@link TypeBodyDeclarationVisitor} and
     * {@link AtomVisitor}.
     */
    public
    interface ComprehensiveVisitor
    extends ImportVisitor, TypeDeclarationVisitor, TypeBodyDeclarationVisitor, BlockStatementVisitor, AtomVisitor,
    ElementValueVisitor { // SUPPRESS CHECKSTYLE WrapAndIndent
    }

    /** The visitor for all kinds of {@link Java.CompilationUnit.ImportDeclaration}s. */
    public
    interface ImportVisitor {
        /** Invoked by {@link Java.CompilationUnit.SingleTypeImportDeclaration#accept(Visitor.ImportVisitor)} */
        void visitSingleTypeImportDeclaration(Java.CompilationUnit.SingleTypeImportDeclaration stid);
        /** Invoked by {@link Java.CompilationUnit.TypeImportOnDemandDeclaration#accept(Visitor.ImportVisitor)} */
        void visitTypeImportOnDemandDeclaration(Java.CompilationUnit.TypeImportOnDemandDeclaration tiodd);
        /** Invoked by {@link Java.CompilationUnit.SingleStaticImportDeclaration#accept(Visitor.ImportVisitor)} */
        void visitSingleStaticImportDeclaration(Java.CompilationUnit.SingleStaticImportDeclaration ssid);
        /** Invoked by {@link Java.CompilationUnit.StaticImportOnDemandDeclaration#accept(Visitor.ImportVisitor)} */
        void visitStaticImportOnDemandDeclaration(Java.CompilationUnit.StaticImportOnDemandDeclaration siodd);
    }

    /** The visitor for all kinds of {@link Java.TypeDeclaration}s. */
    public
    interface TypeDeclarationVisitor {
        /** Invoked by {@link Java.AnonymousClassDeclaration#accept(Visitor.TypeDeclarationVisitor)} */
        void visitAnonymousClassDeclaration(Java.AnonymousClassDeclaration acd);
        /** Invoked by {@link Java.LocalClassDeclaration#accept(Visitor.TypeDeclarationVisitor)} */
        void visitLocalClassDeclaration(Java.LocalClassDeclaration lcd);
        /** Invoked by {@link Java.PackageMemberClassDeclaration#accept(Visitor.TypeDeclarationVisitor)} */
        void visitPackageMemberClassDeclaration(Java.PackageMemberClassDeclaration pmcd);
        /** Invoked by {@link Java.MemberInterfaceDeclaration#accept(Visitor.TypeDeclarationVisitor)} */
        void visitMemberInterfaceDeclaration(Java.MemberInterfaceDeclaration mid);
        /** Invoked by {@link Java.PackageMemberInterfaceDeclaration#accept(Visitor.TypeDeclarationVisitor)} */
        void visitPackageMemberInterfaceDeclaration(Java.PackageMemberInterfaceDeclaration pmid);
        /** Invoked by {@link Java.MemberClassDeclaration#accept(Visitor.TypeDeclarationVisitor)} */
        void visitMemberClassDeclaration(Java.MemberClassDeclaration mcd);
    }

    /** The visitor for all kinds of {@link Java.FunctionDeclarator}s. */
    public
    interface FunctionDeclaratorVisitor {
        /** Invoked by {@link Java.ConstructorDeclarator#accept(Visitor.TypeBodyDeclarationVisitor)} */
        void visitConstructorDeclarator(Java.ConstructorDeclarator cd);
        /** Invoked by {@link Java.MethodDeclarator#accept(Visitor.TypeBodyDeclarationVisitor)} */
        void visitMethodDeclarator(Java.MethodDeclarator md);
    }

    /**
     * The visitor for all kinds of {@link Java.TypeBodyDeclaration}s (declarations that may appear in the body of a
     * type declaration).
     */
    public
    interface TypeBodyDeclarationVisitor extends FunctionDeclaratorVisitor {
        /** Invoked by {@link Java.MemberInterfaceDeclaration#accept(Visitor.TypeBodyDeclarationVisitor)} */
        void visitMemberInterfaceDeclaration(Java.MemberInterfaceDeclaration mid);
        /** Invoked by {@link Java.MemberClassDeclaration#accept(Visitor.TypeBodyDeclarationVisitor)} */
        void visitMemberClassDeclaration(Java.MemberClassDeclaration mcd);
        /** Invoked by {@link Java.Initializer#accept(Visitor.TypeBodyDeclarationVisitor)} */
        void visitInitializer(Java.Initializer i);
        /** Invoked by {@link Java.FieldDeclaration#accept(Visitor.TypeBodyDeclarationVisitor)} */
        void visitFieldDeclaration(Java.FieldDeclaration fd);
    }

    /** The visitor for all kinds of {@link Java.BlockStatement}s (statements that may appear with a block). */
    public
    interface BlockStatementVisitor {
        /** Invoked by {@link Java.Initializer#accept(Visitor.BlockStatementVisitor)} */
        void visitInitializer(Java.Initializer i);
        /** Invoked by {@link Java.FieldDeclaration#accept(Visitor.BlockStatementVisitor)} */
        void visitFieldDeclaration(Java.FieldDeclaration fd);
        /** Invoked by {@link Java.LabeledStatement#accept(Visitor.BlockStatementVisitor)} */
        void visitLabeledStatement(Java.LabeledStatement ls);
        /** Invoked by {@link Java.Block#accept(Visitor.BlockStatementVisitor)} */
        void visitBlock(Java.Block b);
        /** Invoked by {@link Java.ExpressionStatement#accept(Visitor.BlockStatementVisitor)} */
        void visitExpressionStatement(Java.ExpressionStatement es);
        /** Invoked by {@link Java.IfStatement#accept(Visitor.BlockStatementVisitor)} */
        void visitIfStatement(Java.IfStatement is);
        /** Invoked by {@link Java.ForStatement#accept(Visitor.BlockStatementVisitor)} */
        void visitForStatement(Java.ForStatement fs);
        /** Invoked by {@link Java.ForEachStatement#accept(Visitor.BlockStatementVisitor)} */
        void visitForEachStatement(Java.ForEachStatement forEachStatement);
        /** Invoked by {@link Java.WhileStatement#accept(Visitor.BlockStatementVisitor)} */
        void visitWhileStatement(Java.WhileStatement ws);
        /** Invoked by {@link Java.TryStatement#accept(Visitor.BlockStatementVisitor)} */
        void visitTryStatement(Java.TryStatement ts);
        /** Invoked by {@link Java.SwitchStatement#accept(Visitor.BlockStatementVisitor)} */
        void visitSwitchStatement(Java.SwitchStatement ss);
        /** Invoked by {@link Java.SynchronizedStatement#accept(Visitor.BlockStatementVisitor)} */
        void visitSynchronizedStatement(Java.SynchronizedStatement ss);
        /** Invoked by {@link Java.DoStatement#accept(Visitor.BlockStatementVisitor)} */
        void visitDoStatement(Java.DoStatement ds);
        /** Invoked by {@link Java.LocalVariableDeclarationStatement#accept(Visitor.BlockStatementVisitor)} */
        void visitLocalVariableDeclarationStatement(Java.LocalVariableDeclarationStatement lvds);
        /** Invoked by {@link Java.ReturnStatement#accept(Visitor.BlockStatementVisitor)} */
        void visitReturnStatement(Java.ReturnStatement rs);
        /** Invoked by {@link Java.ThrowStatement#accept(Visitor.BlockStatementVisitor)} */
        void visitThrowStatement(Java.ThrowStatement ts);
        /** Invoked by {@link Java.BreakStatement#accept(Visitor.BlockStatementVisitor)} */
        void visitBreakStatement(Java.BreakStatement bs);
        /** Invoked by {@link Java.ContinueStatement#accept(Visitor.BlockStatementVisitor)} */
        void visitContinueStatement(Java.ContinueStatement cs);
        /** Invoked by {@link Java.AssertStatement#accept(Visitor.BlockStatementVisitor)} */
        void visitAssertStatement(Java.AssertStatement as);
        /** Invoked by {@link Java.EmptyStatement#accept(Visitor.BlockStatementVisitor)} */
        void visitEmptyStatement(Java.EmptyStatement es);
        /** Invoked by {@link Java.LocalClassDeclarationStatement#accept(Visitor.BlockStatementVisitor)} */
        void visitLocalClassDeclarationStatement(Java.LocalClassDeclarationStatement lcds);
        /** Invoked by {@link Java.AlternateConstructorInvocation#accept(Visitor.BlockStatementVisitor)} */
        void visitAlternateConstructorInvocation(Java.AlternateConstructorInvocation aci);
        /** Invoked by {@link Java.SuperConstructorInvocation#accept(Visitor.BlockStatementVisitor)} */
        void visitSuperConstructorInvocation(Java.SuperConstructorInvocation sci);
    }

    /** The visitor for all kinds of {@link Java.Atom}s. */
    public
    interface AtomVisitor extends RvalueVisitor, TypeVisitor {
        /** Invoked by {@link Java.Package#accept(Visitor.AtomVisitor)}. */
        void visitPackage(Java.Package p);
    }

    /** The visitor for all kinds of {@link Java.Type}s. */
    public
    interface TypeVisitor {
        /** Invoked by {@link Java.ArrayType#accept(Visitor.TypeVisitor)} */
        void visitArrayType(Java.ArrayType at);
        /** Invoked by {@link Java.BasicType#accept(Visitor.TypeVisitor)} */
        void visitBasicType(Java.BasicType bt);
        /** Invoked by {@link Java.ReferenceType#accept(Visitor.TypeVisitor)} */
        void visitReferenceType(Java.ReferenceType rt);
        /** Invoked by {@link Java.RvalueMemberType#accept(Visitor.TypeVisitor)} */
        void visitRvalueMemberType(Java.RvalueMemberType rmt);
        /** Invoked by {@link Java.SimpleType#accept(Visitor.TypeVisitor)} */
        void visitSimpleType(Java.SimpleType st);
    }

    /** The visitor for all kinds of {@link Java.Rvalue}s. */
    public
    interface RvalueVisitor extends LvalueVisitor {
        /** Invoked by {@link Java.ArrayLength#accept(Visitor.RvalueVisitor)} */
        void visitArrayLength(Java.ArrayLength al);
        /** Invoked by {@link Java.Assignment#accept(Visitor.RvalueVisitor)} */
        void visitAssignment(Java.Assignment a);
        /** Invoked by {@link Java.UnaryOperation#accept(Visitor.RvalueVisitor)} */
        void visitUnaryOperation(Java.UnaryOperation uo);
        /** Invoked by {@link Java.BinaryOperation#accept(Visitor.RvalueVisitor)} */
        void visitBinaryOperation(Java.BinaryOperation bo);
        /** Invoked by {@link Java.Cast#accept(Visitor.RvalueVisitor)} */
        void visitCast(Java.Cast c);
        /** Invoked by {@link Java.ClassLiteral#accept(Visitor.RvalueVisitor)} */
        void visitClassLiteral(Java.ClassLiteral cl);
        /** Invoked by {@link Java.ConditionalExpression#accept(Visitor.RvalueVisitor)} */
        void visitConditionalExpression(Java.ConditionalExpression ce);
        /** Invoked by {@link Java.Crement#accept(Visitor.RvalueVisitor)} */
        void visitCrement(Java.Crement c);
        /** Invoked by {@link Java.Instanceof#accept(Visitor.RvalueVisitor)} */
        void visitInstanceof(Java.Instanceof io);
        /** Invoked by {@link Java.MethodInvocation#accept(Visitor.RvalueVisitor)} */
        void visitMethodInvocation(Java.MethodInvocation mi);
        /** Invoked by {@link Java.SuperclassMethodInvocation#accept(Visitor.RvalueVisitor)} */
        void visitSuperclassMethodInvocation(Java.SuperclassMethodInvocation smi);
        /** Invoked by {@link Java.IntegerLiteral#accept(Visitor.RvalueVisitor)} */
        void visitIntegerLiteral(Java.IntegerLiteral il);
        /** Invoked by {@link Java.FloatingPointLiteral#accept(Visitor.RvalueVisitor)} */
        void visitFloatingPointLiteral(Java.FloatingPointLiteral fpl);
        /** Invoked by {@link Java.BooleanLiteral#accept(Visitor.RvalueVisitor)} */
        void visitBooleanLiteral(Java.BooleanLiteral bl);
        /** Invoked by {@link Java.CharacterLiteral#accept(Visitor.RvalueVisitor)} */
        void visitCharacterLiteral(Java.CharacterLiteral cl);
        /** Invoked by {@link Java.StringLiteral#accept(Visitor.RvalueVisitor)} */
        void visitStringLiteral(Java.StringLiteral sl);
        /** Invoked by {@link Java.NullLiteral#accept(Visitor.RvalueVisitor)} */
        void visitNullLiteral(Java.NullLiteral nl);
        /** Invoked by {@link Java.SimpleConstant#accept(Visitor.RvalueVisitor)} */
        void visitSimpleConstant(Java.SimpleConstant sl);
        /** Invoked by {@link Java.NewAnonymousClassInstance#accept(Visitor.RvalueVisitor)} */
        void visitNewAnonymousClassInstance(Java.NewAnonymousClassInstance naci);
        /** Invoked by {@link Java.NewArray#accept(Visitor.RvalueVisitor)} */
        void visitNewArray(Java.NewArray na);
        /** Invoked by {@link Java.NewInitializedArray#accept(Visitor.RvalueVisitor)} */
        void visitNewInitializedArray(Java.NewInitializedArray nia);
        /** Invoked by {@link Java.NewClassInstance#accept(Visitor.RvalueVisitor)} */
        void visitNewClassInstance(Java.NewClassInstance nci);
        /** Invoked by {@link Java.ParameterAccess#accept(Visitor.RvalueVisitor)} */
        void visitParameterAccess(Java.ParameterAccess pa);
        /** Invoked by {@link Java.QualifiedThisReference#accept(Visitor.RvalueVisitor)} */
        void visitQualifiedThisReference(Java.QualifiedThisReference qtr);
        /** Invoked by {@link Java.ArrayLength#accept(Visitor.RvalueVisitor)} */
        void visitThisReference(Java.ThisReference tr);
    }

    /** The visitor for all kinds of {@link Java.Lvalue}s. */
    public
    interface LvalueVisitor {
        /** Invoked by {@link Java.AmbiguousName#accept(Visitor.LvalueVisitor)} */
        void visitAmbiguousName(Java.AmbiguousName an);
        /** Invoked by {@link Java.ArrayAccessExpression#accept(Visitor.LvalueVisitor)} */
        void visitArrayAccessExpression(Java.ArrayAccessExpression aae);
        /** Invoked by {@link Java.FieldAccess#accept(Visitor.LvalueVisitor)} */
        void visitFieldAccess(Java.FieldAccess fa);
        /** Invoked by {@link Java.FieldAccessExpression#accept(Visitor.LvalueVisitor)} */
        void visitFieldAccessExpression(Java.FieldAccessExpression fae);
        /** Invoked by {@link Java.SuperclassFieldAccessExpression#accept(Visitor.LvalueVisitor)} */
        void visitSuperclassFieldAccessExpression(Java.SuperclassFieldAccessExpression scfae);
        /** Invoked by {@link Java.LocalVariableAccess#accept(Visitor.LvalueVisitor)} */
        void visitLocalVariableAccess(Java.LocalVariableAccess lva);
        /** Invoked by {@link Java.ParenthesizedExpression#accept(Visitor.LvalueVisitor)} */
        void visitParenthesizedExpression(Java.ParenthesizedExpression pe);
    }

    /** The visitor for all kinds of {@link Java.Annotation}s. */
    public
    interface AnnotationVisitor {
        /** Invoked by {@link Java.MarkerAnnotation#accept(Visitor.AnnotationVisitor)} */
        void visitMarkerAnnotation(Java.MarkerAnnotation ma);
        /** Invoked by {@link Java.NormalAnnotation#accept(Visitor.AnnotationVisitor)} */
        void visitNormalAnnotation(Java.NormalAnnotation na);
        /** Invoked by {@link Java.SingleElementAnnotation#accept(Visitor.AnnotationVisitor)} */
        void visitSingleElementAnnotation(Java.SingleElementAnnotation sea);
    }

    /** The visitor for all kinds of {@link Java.ElementValue}s. */
    public
    interface ElementValueVisitor extends RvalueVisitor, AnnotationVisitor {
        /** Invoked by {@link Java.ElementValueArrayInitializer#accept(Visitor.ElementValueVisitor)} */
        void visitElementValueArrayInitializer(Java.ElementValueArrayInitializer evai);
    }

    /** The visitor for all kinds of {@link Java.TypeArgument}s. */
    public
    interface TypeArgumentVisitor {
        /** Invoked by {@link Java.Wildcard#accept(Visitor.TypeArgumentVisitor)} */
        void visitWildcard(Java.Wildcard w);
        /** Invoked by {@link Java.ReferenceType#accept(Visitor.TypeArgumentVisitor)} */
        void visitReferenceType(Java.ReferenceType rt);
        /** Invoked by {@link Java.ArrayType#accept(Visitor.TypeArgumentVisitor)} */
        void visitArrayType(Java.ArrayType arrayType);
    }
}
