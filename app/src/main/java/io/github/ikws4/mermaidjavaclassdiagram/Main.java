package io.github.ikws4.mermaidjavaclassdiagram;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.utils.SourceRoot;
import com.github.javaparser.utils.SourceRoot.Callback;

public class Main {
  public static void main(String[] args) throws IOException {
    if (args.length < 2) {
      System.out.println("Usage: mjcd <source-root> <output-dir>");
      System.exit(1);
    }

    System.setOut(new PrintStream(new File(args[1])));

    Path path = Paths.get(args[0]);
    SourceRoot sourceRoot = new SourceRoot(path);
    System.out.println("classDiagram");
    Set<String> classNames = new HashSet<>();
    sourceRoot.parse("", new Callback() {
      @Override
      public Result process(Path localPath, Path absolutePath,
          ParseResult<CompilationUnit> result) {
        result.ifSuccessful(cu -> {
          cu.accept(new ClassDiagramDefineVisitor(classNames), "");
        });
        System.out.println();
        return Result.DONT_SAVE;
      }
    });
    sourceRoot.parse("", new Callback() {
      @Override
      public Result process(Path localPath, Path absolutePath,
          ParseResult<CompilationUnit> result) {
        result.ifSuccessful(cu -> {
          cu.accept(new ClassDiagramRelationVisitor(classNames), "");
        });
        System.out.println();
        return Result.DONT_SAVE;
      }
    });
  }
}
