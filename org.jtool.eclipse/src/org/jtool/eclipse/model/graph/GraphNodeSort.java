/*
 *  Copyright 2014, Katsuhisa Maruyama (maru@jtool.org)
 */

package org.jtool.eclipse.model.graph;

/**
 * Constant values indicating the sort of nodes of CFGs and PDGs.
 * @author Katsuhisa Maruyama
 */
public enum GraphNodeSort {
    
    classEntry,        // CFGClassEntry (TypeDeclaration, AnonymousClassDeclaration)
    interfaceEntry,    // CFGClassEntry (TypeDeclaration, AnonymousClassDeclaration)
    enumEntry,         // CFGClassEntry (EnumDeclaration)
    methodEntry,       // CFGMethodEntry (MethodDeclaration)
    constructorEntry,  // CFGMethodEntry (MethodDeclaration)
    initializerEntry,  // CFGInitializerEntry (Initializer)
    fieldEntry,        // CFGFieldEntry (VariableDeclarationFragment/FieldDeclaration)
    enumConstantEntry, // CFGEnumConstantEntry (EnumConstantDeclaration)
    
    classExit,         // CFGExit
    interfaceExit,     // CFGExit
    enumExit,          // CFGExit
    methodExit,        // CFGExit
    constructorExit,   // CFGExit
    initializerExit,   // CFGExit
    fieldExit,         // CFGExit
    enumConstantExit,  // CFGExit
    
    assignment,        // CFGStatement (Assignment)
    methodCall,        // CFGMethodInvocation (MethodInvocation/SuperMethodInvocation)
    constructorCall,   // CFGMethodInvocation (ConstructorInvocation/SuperConstructorInvocation)
    instanceCreation,  // CFGMethodInvocation (InstanceCreation)
    fieldDeclaration,  // CFGStatement (VariableDeclarationFragment)
    localDeclaration,  // CFGStatement (VariableDeclarationFragment)
    
    assertSt,          // CFGStatement (AssertStatement)
    breakSt,           // CFGStatement (BreakStatement)
    continueSt,        // CFGStatement (ContinueStatement)
    doSt,              // CFGStatement (DoStatement)
    forSt,             // CFGStatement (ForStatement)    
    ifSt,              // CFGStatement (IfStatement)
    returnSt,          // CFGStatement (ReturnStatement)
    switchCaseSt,      // CFGStatement (SwitchCase)
    switchDefaultSt,   // CFGStatement (SwitchCase)
    whileSt,           // CFGStatement (WhileStatement)
    emptySt,           // CFGStatement (EmptyStatement)
    
    labelSt,           // CFGStatement (Identifier in LabeledStatement)
    switchSt,          // CFGStatement (SwitchStatement)
    synchronizedSt,    // CFGStatement (SynchronizedStatement)
    throwSt,           // CFGStatement (ThrowStatement)
    trySt,             // CFGStatement (TryStatement)
    catchSt,           // CFGStatement (CatchClause in TryStatement)
    finallySt,         // CFGStatement (Block in TryStatement)
    
    formalIn,          // CFGParameter
    formalOut,         // CFGParameter
    actualIn,          // CFGParameter
    actualOut,         // CFGParameter
    
    merge,             // CFGMerge (for merge)
    dummy;             // CFGDummy (for dummy)
}
