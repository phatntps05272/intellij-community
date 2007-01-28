/*
 * Copyright (c) 2000-2004 by JetBrains s.r.o. All Rights Reserved.
 * Use is subject to license terms.
 */
package com.intellij.debugger.engine.evaluation;

import com.intellij.debugger.ui.DebuggerExpressionComboBox;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiCodeFragment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiManager;

/**
 * @author Eugene Zhuravlev
 *         Date: Jun 7, 2005
 */
public class DefaultCodeFragmentFactory implements CodeFragmentFactory {
  private static final class SingletonHolder {
    public static final DefaultCodeFragmentFactory ourInstance = new DefaultCodeFragmentFactory();
  }

  public static DefaultCodeFragmentFactory getInstance() {
    return SingletonHolder.ourInstance;
  }

  public String getDisplayName() {
    //noinspection HardCodedStringLiteral
    return "Java";
  }

  public PsiCodeFragment createCodeFragment(TextWithImports item, PsiElement context, Project project) {
    final PsiElementFactory elementFactory = PsiManager.getInstance(project).getElementFactory();
    final String text = item.getText();

    final PsiCodeFragment fragment;
    if (CodeFragmentKind.EXPRESSION == item.getKind()) {
      final String expressionText = StringUtil.endsWithChar(text, ';')? text.substring(0, text.length() - 1) : text;
      fragment = elementFactory.createExpressionCodeFragment(expressionText, context, null, true);
    }
    else if (CodeFragmentKind.CODE_BLOCK == item.getKind()) {
      fragment = elementFactory.createCodeBlockCodeFragment(text, context, true);
    }
    else {
      fragment = null;
    }

    if (fragment != null) {
      if(item.getImports().length() > 0) {
        fragment.addImportsFromString(item.getImports());
      }
      fragment.setVisibilityChecker(PsiCodeFragment.VisibilityChecker.EVERYTHING_VISIBLE);
      //noinspection HardCodedStringLiteral
      fragment.putUserData(DebuggerExpressionComboBox.KEY, "DebuggerComboBoxEditor.IS_DEBUGGER_EDITOR");
    }

    return fragment;
  }

  public boolean isContextAccepted(PsiElement contextElement) {
    return true; // default factory works everywhere debugger can stop
  }
}
