package org.remast.util;


/**
* Collected methods which allow easy implementation of <code>equals</code>.
*
* Example use case in a class called Car:
* <pre>
public boolean equals(Object that){
  if ( this == that ) return true;
  if ( !(that instanceof Car) ) return false;
  Car thatCar = (Car)that;
  return
    EqualsUtil.areEqual(this.fName, that.fName) &&
    EqualsUtil.areEqual(this.fNumDoors, that.fNumDoors) &&
    EqualsUtil.areEqual(this.fGasMileage, that.fGasMileage) &&
    EqualsUtil.areEqual(this.fColor, that.fColor) &&
    Arrays.equals(this.fMaintenanceChecks, that.fMaintenanceChecks); //array!
}
* </pre>
*
* <em>Arrays are not handled by this class</em>.
* This is because the <code>Arrays.equals</code> methods should be used for
* array fields.
*/
public final class EqualsUtil {

  static public boolean areEqual(boolean aThis, boolean aThat){
    return aThis == aThat;
  }

  static public boolean areEqual(char aThis, char aThat){
    return aThis == aThat;
  }

  static public boolean areEqual(long aThis, long aThat){
    /*
    * Implementation Note
    * Note that byte, short, and int are handled by this method, through
    * implicit conversion.
    */
    return aThis == aThat;
  }

  static public boolean areEqual(float aThis, float aThat){
    return Float.floatToIntBits(aThis) == Float.floatToIntBits(aThat);
  }

  static public boolean areEqual(double aThis, double aThat){
    return Double.doubleToLongBits(aThis) == Double.doubleToLongBits(aThat);
  }

  /**
  * Possibly-null object field.
  *
  * Includes type-safe enumerations and collections, but does not include
  * arrays. See class comment.
  */
  static public boolean areEqual(Object aThis, Object aThat){
    return aThis == null ? aThat == null : aThis.equals(aThat);
  }

  /**
   * Possibly-null object field.
   *
   * Includes type-safe enumerations and collections, but does not include
   * arrays. See class comment.
   */
   static public boolean areEqualIgnoreCase(String aThis, String aThat){
     return aThis == null ? aThat == null : aThis.equalsIgnoreCase(aThat);
   }
}