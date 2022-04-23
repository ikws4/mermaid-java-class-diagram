# mermaid-java-class-diagram
Convert java source code to mermaid class diagram

<img width="255" src="https://user-images.githubusercontent.com/47056144/164866159-ca41cdec-b0b4-444d-acd5-d33c100460c0.png">
<img width="100%" src="https://user-images.githubusercontent.com/47056144/164866427-9398c639-3136-42b1-974c-4a91733a8fa7.png">

The source above is coming from [divotkey/ecs](https://github.com/divotkey/ecs)

# Build and Run

### Build
```bash
./gradlew assemble
unzip app/build/distributions/app.zip -d dist/
```

### Run
```bash
./dist/app/bin/app <source-root> <output-dir>
```
