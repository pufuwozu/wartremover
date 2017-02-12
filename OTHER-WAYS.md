# Other ways of using WartRemover

Apart from [using the sbt plugin](/README.md) to set it up for your project, WartRemover can also be used in the following ways:

* as a command-line tool,
* as a compiler plugin with *manually* provided `scalac` options,
* to derive macros.

## Command-line

Compile the command-line tool via `sbt core/assembly` and then use the provided `wartremover` shell script:

    $ sbt core/assembly
    
      ...
    
    $ ./wartremover -traverser org.wartremover.warts.Unsafe core/src/main/scala/wartremover/Plugin.scala
    core/src/main/scala/wartremover/Plugin.scala:15: error: var is disabled
      private[this] var traversers: List[WartTraverser] = List.empty
                        ^

## Compiler plugin (manually)

Add the following to `build.sbt`:

```scala
resolvers += Resolver.sonatypeRepo("releases")

addCompilerPlugin("org.wartremover" %% "wartremover" % "2.0.0")

scalacOptions += "-P:wartremover:traverser:org.wartremover.warts.Unsafe"
```

By default, WartRemover generates compile-time errors. If you want to be warned only, use an `only-warn-traverser`:

```scala
scalacOptions += "-P:wartremover:only-warn-traverser:org.wartremover.warts.Unsafe"
```

If you don't want to perform the checks in some file, you can use:

```scala
scalacOptions += "-P:wartremover:excluded:ABSOLUTE_PATH_TO_THE_FILE"
```

The `excluded` option accepts a colon-separated list of absolute paths to files to ignore.

To use your custom `WartTraverser`, you'll need to provide a classpath where it can be found:

```scala
scalacOptions += "-P:wartremover:cp:someUrl"
```

## Apache Maven

You can use WartRemover in Maven by employing it as a compilerPlugin to scala-maven-plugin:

```xml
<build>
        <plugins>
            <plugin>
                <groupId>net.alchim31.maven</groupId>
                <artifactId>scala-maven-plugin</artifactId>
                <version>3.2.1</version>
                <configuration>
                    <compilerPlugins>
                        <compilerPlugin>
                            <groupId>org.wartremover</groupId>
                            <artifactId>wartremover_2.11</artifactId>
                            <version>1.2.0</version>
                        </compilerPlugin>
                    </compilerPlugins>
                    <args>
                        <arg>-P:wartremover:only-warn-traverser:org.wartremover.warts.Unsafe</arg>
                    </args>
                </configuration>
            </plugin>
        </plugins>
    </build>  
```

See the notes on the compiler plugin above for options to pass as `<arg>`s.

## Macros

You can make any wart into a macro, like so:

    scala> import language.experimental.macros
    import language.experimental.macros

    scala> import org.wartremover.warts.Unsafe
    import org.wartremover.warts.Unsafe

    scala> def safe(expr: Any) = macro Unsafe.asMacro
    safe: (expr: Any)Any

    scala> safe { null }
    <console>:10: error: null is disabled
                  safe { null }
                         ^

