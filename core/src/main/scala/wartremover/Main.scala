package org.wartremover

import tools.nsc.{Global, Settings}
import tools.nsc.io.VirtualDirectory
import tools.nsc.reporters.{ConsoleReporter, Reporter}

object Main {
  case class WartArgs(traversers: List[String], names: List[String]) {
    def append(o: WartArgs) = WartArgs(this.traversers ++ o.traversers, this.names ++ o.names)
    def addName(name: String) = WartArgs(this.traversers, name :: this.names)
    def addTraverser(traverser: String) = WartArgs(traverser :: this.traversers, this.traversers)
  }
  object WartArgs {
    val empty = WartArgs(List.empty, List.empty)
  }

  def processArgs(args: List[String], processed: WartArgs): WartArgs = args match {
    case "-traverser" :: traverser :: xs => processArgs(xs, processed.addTraverser(traverser))
    case name :: xs => processArgs(xs, processed.addName(name))
    case Nil => processed
  }

  def compile(args: WartArgs, reporter: Option[Reporter] = None) = {
    val settings = new Settings()
    val virtualDirectory = new VirtualDirectory("(memory)", None)
    settings.outputDirs.setSingleOutput(virtualDirectory)
    settings.embeddedDefaults(getClass.getClassLoader)
    settings.pluginOptions.value = args.traversers.map("wartremover:traverser:" ++ _)

    val global = new Global(settings, reporter.getOrElse(new ConsoleReporter(settings))) {
      override protected def loadRoughPluginsList() = List(new Plugin(this))
    }
    val run = new global.Run()
    run.compile(args.names)

    !global.reporter.hasErrors
  }

  def main(args: Array[String]): Unit = {
    val wartArgs = processArgs(args.toList, WartArgs.empty)

    if (wartArgs == WartArgs.empty) {
      System.err.println("usage: wartremover [-traverser qualifiedname ...] [file ...]")
      System.exit(1)
    } else if (wartArgs.traversers == WartArgs.empty.traversers) {
      System.err.println("error: no traverser rules were provided")
      System.exit(1)
    } else if (wartArgs.names == WartArgs.empty.names) {
      System.err.println("error: no Scala files were provided")
      System.exit(1)
    } else if (!compile(wartArgs)) {
      System.exit(1)
    }
  }
}
