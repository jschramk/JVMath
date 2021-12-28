package com.jschramk.JVMath.runtime.components;

public class JVMathSettings {

  private static int DECIMAL_PLACES = 5;
  private static AngularUnit ANGULAR_UNIT = AngularUnit.RADIANS;


  public enum AngularUnit {
    DEGREES, RADIANS
  }

  public static void setDecimalPlaces(int decimalPlaces) {
    DECIMAL_PLACES = decimalPlaces;
  }

  public static void setAngularUnit(AngularUnit angularUnit) {
    ANGULAR_UNIT = angularUnit;
  }

  public static int getDecimalPlaces() {
    return DECIMAL_PLACES;
  }

  public static AngularUnit getAngularUnit() {
    return ANGULAR_UNIT;
  }
}
