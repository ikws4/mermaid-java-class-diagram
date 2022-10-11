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
  public static boolean printField, printMethod, onlyPrintPublic;

  public static void main(String[] args) throws IOException {
    if (args.length < 2) {
      System.out.println("Usage: mjcd -[fmp] <source-path> <output-path>");
      System.out.println("  -f print field");
      System.out.println("  -m print method");
      System.out.println("  -p only print public field and method");
      System.exit(1);
    }

    String opt = args[0];
    String sourcePath = null, outputPath = null;
    if (opt.charAt(0) == '-') {
      if (opt.contains("f")) {
        printField = true;
      }
      if (opt.contains("m")) {
        printMethod = true;
      }
      if (opt.contains("p")) {
        onlyPrintPublic = true;
      }
      sourcePath = args[1];
      outputPath = args[2];
    } else {
      sourcePath = opt;
      outputPath = args[1];
    }

    File outputDir = new File(outputPath);
    if (!outputDir.exists()) {
      if (outputDir.mkdirs() == false) {
        System.out.println("Can't not create output directory: " + outputPath);
        System.exit(1);
      }
    }
    System.setOut(new PrintStream(new File(outputPath + "/" + getLastDirectoryName(sourcePath))));

    Path path = Paths.get(sourcePath);
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
          cu.accept(new ClassDiagramExtendAndImplVisitor(), null);
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

  private static String getLastDirectoryName(String sourcePath) {
    String[] parts = sourcePath.split("/");
    return parts[parts.length - 1];
  }
}
