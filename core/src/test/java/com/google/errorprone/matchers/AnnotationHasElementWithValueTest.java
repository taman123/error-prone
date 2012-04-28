/*
 * Copyright 2012 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.errorprone.matchers;

import com.google.errorprone.Scanner;
import com.google.errorprone.VisitorState;
import com.sun.source.tree.AnnotationTree;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.google.errorprone.matchers.Matchers.stringLiteral;
import static org.junit.Assert.assertTrue;

/**
 * @author alexeagle@google.com (Alex Eagle)
 */
public class AnnotationHasElementWithValueTest extends CompilerBasedTest {
  @Before public void setUp() throws IOException {
    writeFile("Thing.java",
        "public @interface Thing {",
        "  String stuff();",
        "}");
  }

  @Test
  public void testMatches() throws IOException {
    writeFile("A.java",
        "@Thing(stuff=\"y\")",
        "public class A {}");
    assertCompiles(annotationMatches(true, new AnnotationHasElementWithValue("stuff", stringLiteral("y"))));
  }
  
  @Test public void notMatches() throws IOException {
    writeFile("A.java",
        "@Thing(stuff=\"n\")",
        "public class A{}");
    assertCompiles(annotationMatches(false, new AnnotationHasElementWithValue("stuff", stringLiteral("y"))));
    assertCompiles(annotationMatches(false, new AnnotationHasElementWithValue("other", stringLiteral("n"))));
  }
  
  @Test public void arrayValuedElement() throws IOException {
    writeFile("A.java",
        "@SuppressWarnings({\"unchecked\",\"fallthrough\"})",
        "public class A{}");
    assertCompiles(annotationMatches(true, new AnnotationHasElementWithValue("value", stringLiteral("unchecked"))));
  }

  private Scanner annotationMatches(final boolean shouldMatch, final AnnotationHasElementWithValue toMatch) {
    return new Scanner() {
      @Override
      public Void visitAnnotation(AnnotationTree node, VisitorState visitorState) {
        assertTrue(node.toString(), !shouldMatch ^ toMatch.matches(node, visitorState));
        return super.visitAnnotation(node, visitorState);
      }
    };
  }
}
