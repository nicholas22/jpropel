import lombok.ExtensionMethod;
import lombok.val;
import propel.core.utils.Linq;
import static propel.core.functional.predicates.Strings.*;
// import static propel.core.functional.projections.Projections.*;

@ExtensionMethod({Linq.class})
public class Main
{

  /**
   * @param args
   */
  public static void main(String[] args)
  {
    val names = new String[] {"john", "james", "john", "eddie"}.where(startsWith("j")).distinct().all(println());
  }
}
