import sbt._
import Keys._

/** sbt-wartremover's key definitions */
package object wartremover {
  val wartremoverErrors = settingKey[Seq[Wart]]("List of Warts that will be reported as compilation errors.")
  val wartremoverWarnings = settingKey[Seq[Wart]]("List of Warts that will be reported as compilation warnings.")
  val wartremoverExcluded = taskKey[Seq[File]]("List of files to be excluded from all checks.")
  val wartremoverClasspaths = taskKey[Seq[String]]("List of classpaths for custom Warts")

  lazy val wartremoverSettings: Seq[Def.Setting[_]] = Seq(
    wartremoverErrors := Seq.empty,
    wartremoverWarnings := Seq.empty,
    wartremoverExcluded := Seq.empty,
    wartremoverClasspaths := Seq.empty,

    addCompilerPlugin("org.wartremover" %% "wartremover" % Wart.PluginVersion)
  ) ++ inScope(This)(Seq(
    derive {
      val option = scalacOption("traverser") _
      scalacOptions := reset(option, scalacOptions.value) ++ (wartremoverErrors.value.distinct map (_.clazz.toString) map option)
    },
    derive {
      val option = scalacOption("only-warn-traverser") _
      scalacOptions := reset(option, scalacOptions.value) ++ (wartremoverWarnings.value.distinct filterNot (wartremoverErrors.value contains _) map (_.clazz.toString) map option)
    },
    derive {
      val option = scalacOption("excluded") _
      scalacOptions := reset(option, scalacOptions.value) ++ (wartremoverExcluded.value.distinct map (_.getAbsolutePath) map option)
    },
    derive {
      val option = scalacOption("cp") _
      scalacOptions := reset(option, scalacOptions.value) ++ (wartremoverClasspaths.value.distinct map option)
    }
  ))

  private[wartremover] def scalacOption(key: String)(value: String) =
    s"-P:wartremover:$key:$value"

  private[wartremover] def reset(option: String => String, options: Seq[String]) =
    options filterNot (_.startsWith(option("")))

  // Workaround for typelevel/wartremover#123
  private[wartremover] def derive[T](s: Setting[T]): Setting[T] = {
    try {
      Def derive s
    } catch {
      case _: LinkageError =>
        import scala.language.reflectiveCalls
        Def.asInstanceOf[{def derive[T](setting: Setting[T], allowDynamic: Boolean, filter: Scope => Boolean, trigger: AttributeKey[_] => Boolean, default: Boolean): Setting[T]}]
          .derive(s, false, _ => true, _ => true, false)
    }
  }
}
