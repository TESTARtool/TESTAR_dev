/***************************************************************************************************
*
* Copyright (c) 2013, 2014, 2015, 2016, 2017 Universitat Politecnica de Valencia - www.upv.es
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
* 1. Redistributions of source code must retain the above copyright notice,
* this list of conditions and the following disclaimer.
* 2. Redistributions in binary form must reproduce the above copyright
* notice, this list of conditions and the following disclaimer in the
* documentation and/or other materials provided with the distribution.
* 3. Neither the name of the copyright holder nor the names of its
* contributors may be used to endorse or promote products derived from
* this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
* ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
* LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
* SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
* INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
* CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
* ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
* POSSIBILITY OF SUCH DAMAGE.
*******************************************************************************************************/

/**
 *  @author Sebastian Bauersfeld
 */
package org.fruit.alayer;

import java.io.Serializable;

import org.fruit.Assert;

/**
 * Color objects represent a color and are used together with <code>Pen</code>'s and
 * <code>Canvas</code> objects.
 *
 * @see Pen
 * @see Canvas
 */
public final class Color implements Serializable {
  private static final long serialVersionUID = 7465037502136479951L;

  public static final Color Red = from(255, 0, 0, 255);
  public static final Color DeepPink = from(255, 20, 147, 255);
  public static final Color Aqua = from(0, 128, 255, 255);
  public static final Color Moss = from(0, 128, 64, 255);
  public static final Color Salmon = from(255, 102, 102, 255);
  public static final Color Blue = from(0, 0, 255, 255);
  public static final Color Navy = from(0, 0, 128, 255);
  public static final Color CornflowerBlue = from(100, 149, 237, 255);
  public static final Color SteelBlue = from(70, 130, 180, 255);
  public static final Color BlueViolet = from(138, 43, 226, 255);
  public static final Color Green = from(0, 255, 0, 255);
  public static final Color LimeGreen = from(50, 205, 50, 255);
  public static final Color Yellow = from(255, 255, 0, 255);
  public static final Color Gold = from(255, 215, 0, 255);
  public static final Color White = from(255, 255, 255, 255);
  public static final Color Black = from(0, 0, 0, 255);

  public static Color from(int red, int green, int blue, int alpha) {
    return new Color(red, green, blue, alpha); }

  private final int red, green, blue, alpha, argb32;

  private Color(int red, int green, int blue, int alpha) {
    Assert.isTrue(red >= 0 && green >= 0 && blue >= 0 && alpha >= 0 &&
        red <= 255 && green <= 255 && blue <= 255 && alpha <= 255);
    this.red = red;
    this.green = green;
    this.blue = blue;
    this.alpha = alpha;
    this.argb32 = red + (green << 8) + (blue << 16) + (alpha << 24);
  }

  public int red() {
    return red;
  }
  public int green() {
    return green;
  }
  public int blue() {
    return blue;
  }
  public int alpha() {
    return alpha;
  }
  public int argb32() {
    return argb32;
  }
  public int hashCode() {
    return argb32();
  }

  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o instanceof Color) {
      Color co = (Color) o;
      return co.alpha == alpha && co.red == red && co.green == green && co.blue == blue;
    }
    return false;
  }

  public String toString() {
    return "Color (red: " + red() + " green: " + green() +
      " blue: " + blue() + " alpha: " + alpha() + ")";
  }
}
