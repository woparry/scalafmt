import org.scalafmt.Scalafmt
import org.scalafmt.config.ScalafmtRunner
import org.scalafmt.config.ScalafmtConfig
import org.scalafmt.config.ScalafmtConfig.default40
import org.scalafmt.config.Config

package object website {
  def replaceMargin(s: String): String = {
    val buf = new StringBuilder

    for (line <- s.lines) {
      val len = line.length
      var index = 0
      while (index < len && line.charAt(index) <= ' ') {
        index += 1
        buf.append(' ')
      }
      if (index < len) {
        line.charAt(index) match {
          case '#' => buf.append('|')
          case ch => buf.append(ch)
        }
        index += 1
      }
      if (index < len) {
        buf.append(line.substring(index))
      }
      buf.append('\n')
    }
    buf.toString
  }

  def plaintext(code: String): String =
    new StringBuilder()
      .append("```\n")
      .append(code)
      .append("\n```")
      .toString()

  private[this] def scalaCode(code: String): String =
    new StringBuilder()
      .append("```scala\n")
      .append(code)
      .append("\n```")
      .toString()

  /** Prints a formatted Scala code block one using the provided configuration,
    * which is added as a comment on top
    *
    * @param code the unformatted code
    * @param config the config as an HOCON string
    */
  def exampleBlock(code: String, config: String*): Unit = {
    val processedCode = replaceMargin(code).replaceAllLiterally("'''", "\"\"\"")
    val parsedConfig = Config
      .fromHoconString(config.mkString("\n"))
      .get
      .copy(maxColumn = 40, runner = ScalafmtRunner.sbt)
    val formattedCode = Scalafmt.format(processedCode, parsedConfig).get
    val result = new StringBuilder()
      .append(config.mkString("// ", "\n//", ""))
      .append("\n\n")
      .append(formattedCode)
      .toString()

    println(scalaCode(result))
  }

  /** Prints two Scala code block next to each other, one with the original code,
    * the other one formatted using the provided configuration
    *
    * @param code the unformatted code
    * @param config the config to format the code (defaults to `default40`)
    */
  def formatExample(
      code: String,
      config: ScalafmtConfig = default40
  ): Unit = {
    val formatted = Scalafmt.format(code, ScalafmtConfig.default40).get
    println(
      s"""
<div class='scalafmt-pair'>
  <div class='before'>

${scalaCode(code)}

  </div>

  <div class='after'>

${scalaCode(formatted)}

  </div>
</div>
"""
    )
  }

  /** Prints the default value of a property
    *
    * @param selector a function to select the default from the config
    */
  def default[A](selector: ScalafmtConfig => A) = {
    val defaultValue = selector(ScalafmtConfig.default)
    println(s"Default: **$defaultValue**")
  }

}
