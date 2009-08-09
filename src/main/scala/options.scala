package optional

import scala.collection._;

case class Options(
  options: Map[String, String],
  args: List[String],
  rawArgs: List[String]
)

object Options
{
  private val ShortOption = """-(\w)""".r
  private val ShortSquashedOption = """-([^-\s])(\w+)""".r
  private val LongOption = """--(\w+)""".r
  private val OptionTerminator = "--"
  private val True = "true";

  /**
   * Take a list of string arguments and parse them into options.
   * Currently the dumbest option parser in the entire world, but
   * oh well.
   */
  def parse(args: String*): Options = {
    import mutable._;
    val optionsStack = new ArrayStack[String];
    val options = new OpenHashMap[String, String];
    val arguments = new ArrayBuffer[String];

    def addOption(name : String) = {
      if(optionsStack.isEmpty) options(name) = True;
      else {
        val next = optionsStack.pop;
        next match {
          case ShortOption(_) | ShortSquashedOption(_) | LongOption(_) | OptionTerminator =>
            optionsStack.push(next);
            options(name) = True;
          case x => options(name) = x;
        }
      }
    }

    optionsStack ++= args.reverse;    
    while(!optionsStack.isEmpty){
      optionsStack.pop match {
        case ShortOption(name) => addOption(name);
        case ShortSquashedOption(name, value) => options(name) = value;
        case LongOption(name) => addOption(name);
        case OptionTerminator => optionsStack.drain(arguments += _);
        case x => arguments += x; 
      }  
    }

    Options(options, arguments.toList, args.toList)
  }
}
