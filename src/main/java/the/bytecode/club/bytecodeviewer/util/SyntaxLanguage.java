package the.bytecode.club.bytecodeviewer.util;

import java.io.File;
import java.util.function.BiFunction;
import org.fife.ui.rsyntaxtextarea.FileTypeUtil;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Kalen 'Konloch' Kinloch - http://bytecodeviewer.com  *
 *                                                                         *
 * This program is free software: you can redistribute it and/or modify    *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation, either version 3 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>. *
 ***************************************************************************/

/**
 * @author ThexXTURBOXx
 */
public enum SyntaxLanguage {
    XML(SyntaxConstants.SYNTAX_STYLE_XML,
            (n, c) -> n.endsWith(".xml") || c.startsWith("<?xml") || c.startsWith("<xml")),
    PYTHON(SyntaxConstants.SYNTAX_STYLE_PYTHON, (n, c) -> n.endsWith(".py") || n.endsWith(".python")),
    RUBY(SyntaxConstants.SYNTAX_STYLE_RUBY, (n, c) -> n.endsWith(".rb") || n.endsWith(".ruby")),
    JAVA(SyntaxConstants.SYNTAX_STYLE_JAVA, (n, c) -> n.endsWith(".java")),
    HTML(SyntaxConstants.SYNTAX_STYLE_HTML, (n, c) -> n.endsWith(".html")),
    CSS(SyntaxConstants.SYNTAX_STYLE_CSS, (n, c) -> n.endsWith(".css")),
    PROPERTIES(SyntaxConstants.SYNTAX_STYLE_PROPERTIES_FILE,
            (n, c) -> n.endsWith(".properties") || n.endsWith(".mf") || n.endsWith(".sf")
                    || n.endsWith(".plugin") || n.endsWith(".attachprovider") || n.endsWith(".transportservice")
                    || n.endsWith(".connector")),
    PHP(SyntaxConstants.SYNTAX_STYLE_PHP, (n, c) -> n.endsWith(".php") || c.startsWith("<?php")),
    JS(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT, (n, c) -> n.endsWith(".js")),
    BATCH(SyntaxConstants.SYNTAX_STYLE_WINDOWS_BATCH, (n, c) -> n.endsWith(".bat")),
    SHELL(SyntaxConstants.SYNTAX_STYLE_UNIX_SHELL, (n, c) -> n.endsWith(".sh")),
    C(SyntaxConstants.SYNTAX_STYLE_C, (n, c) -> n.endsWith(".c") || n.endsWith(".h")),
    CPP(SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS, (n, c) -> n.endsWith(".cpp") || n.endsWith(".hpp")),
    SCALA(SyntaxConstants.SYNTAX_STYLE_SCALA, (n, c) -> n.endsWith(".scala")),
    CLOJURE(SyntaxConstants.SYNTAX_STYLE_CLOJURE, (n, c) -> n.endsWith(".clojure")),
    GROOVY(SyntaxConstants.SYNTAX_STYLE_GROOVY, (n, c) -> n.endsWith(".groovy") || n.endsWith(".gradle")),
    LUA(SyntaxConstants.SYNTAX_STYLE_LUA, (n, c) -> n.endsWith(".lua")),
    SQL(SyntaxConstants.SYNTAX_STYLE_SQL, (n, c) -> n.endsWith(".sql")),
    JSON(SyntaxConstants.SYNTAX_STYLE_JSON, (n, c) -> n.endsWith(".json")),
    JSP(SyntaxConstants.SYNTAX_STYLE_JSP, (n, c) -> n.endsWith(".jsp")),
    YAML(SyntaxConstants.SYNTAX_STYLE_YAML, (n, c) -> n.endsWith(".yml") || n.endsWith(".yaml")),
    CS(SyntaxConstants.SYNTAX_STYLE_CSHARP, (n, c) -> n.endsWith(".cs")),
    CSV(SyntaxConstants.SYNTAX_STYLE_CSV, (n, c) -> n.endsWith(".csv")),
    DOCKER(SyntaxConstants.SYNTAX_STYLE_DOCKERFILE, (n, c) -> n.endsWith(".dockerfile")),
    DART(SyntaxConstants.SYNTAX_STYLE_DART, (n, c) -> n.endsWith(".dart")),
    GO(SyntaxConstants.SYNTAX_STYLE_GO, (n, c) -> n.endsWith(".go")),
    HTACCESS(SyntaxConstants.SYNTAX_STYLE_HTACCESS, (n, c) -> n.endsWith(".htaccess")),
    INI(SyntaxConstants.SYNTAX_STYLE_INI, (n, c) -> n.endsWith(".ini")),
    KOTLIN(SyntaxConstants.SYNTAX_STYLE_KOTLIN, (n, c) -> n.endsWith(".kt") || n.endsWith(".kts")),
    LATEX(SyntaxConstants.SYNTAX_STYLE_LATEX, (n, c) -> n.endsWith(".tex")),
    MARKDOWN(SyntaxConstants.SYNTAX_STYLE_MARKDOWN, (n, c) -> n.endsWith(".md")),
    PERL(SyntaxConstants.SYNTAX_STYLE_PERL, (n, c) -> n.endsWith(".pl")),
    TYPESCRIPT(SyntaxConstants.SYNTAX_STYLE_TYPESCRIPT, (n, c) -> n.endsWith(".ts")),
    NONE(SyntaxConstants.SYNTAX_STYLE_NONE, (n, c) -> false);

    public static final SyntaxLanguage[] VALUES = values();

    private static final FileTypeUtil FILE_TYPE_UTIL = FileTypeUtil.get();

    private final BiFunction<String, String, Boolean> criteria;

    private final String syntaxConstant;

    SyntaxLanguage(String syntaxConstant, BiFunction<String, String, Boolean> criteria) {
        this.criteria = criteria;
        this.syntaxConstant = syntaxConstant;
    }

    public boolean isLanguage(String fileName, String content) {
        return criteria.apply(fileName, content);
    }

    public String getSyntaxConstant() {
        return syntaxConstant;
    }

    /**
     * @deprecated See {@link #setLanguage(RSyntaxTextArea, String)}.
     */
    @Deprecated
    public static SyntaxLanguage detectLanguage(String fileName, String content) {
        for (SyntaxLanguage lang : VALUES) {
            if (lang.isLanguage(fileName, content)) {
                return lang;
            }
        }
        return NONE;
    }

    public static void setLanguage(RSyntaxTextArea area, String fileName) {
        String type = FILE_TYPE_UTIL.guessContentType(new File(fileName));
        if (type == null || type.equals(SyntaxConstants.SYNTAX_STYLE_NONE)) {
            type = FILE_TYPE_UTIL.guessContentType(area);
        }
        area.setSyntaxEditingStyle(type);
    }
}
