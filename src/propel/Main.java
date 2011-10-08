package propel;

import lombok.Action;
import lombok.Actions.Action0;
import lombok.ExtensionMethod;
import propel.core.functional.projections.Projections;

@ExtensionMethod(Projections.class)
public class Main
{

  /**
   * @param args
   */
  public static void main(String[] args)
  {
    
    String abc=null;
    abc.orElse();
    // Action0 act = test();
    
  }
  
  //@Action
  public static void doNothing() {
  }
  
  @Action
  public static void doSomething(String a) {
   System.out.println(a+"+something");
  }
  
}
