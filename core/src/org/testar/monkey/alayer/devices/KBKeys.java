/*
 **************************************************************************************************
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


/*
 *  @author Sebastian Bauersfeld
 */
package org.testar.monkey.alayer.devices;

public enum KBKeys
{
  KEY_FIRST(2400, 400),
  KEY_LAST(2402, 402),
  KEY_TYPED(2400, 400),
  KEY_PRESSED(2401, 401),
  KEY_RELEASED(2402, 402),
  KEY_LOCATION_UNKNOWN(0, 0),
  KEY_LOCATION_STANDARD(1, 1),
  KEY_LOCATION_LEFT(2, 2),
  KEY_LOCATION_RIGHT(3, 3),
  KEY_LOCATION_NUMPAD(4, 4),
  VK_ESCAPE(1, 27),
  VK_F1(59, 112),
  VK_F2(60, 113),
  VK_F3(61, 114),
  VK_F4(62, 115),
  VK_F5(63, 116),
  VK_F6(64, 117),
  VK_F7(65, 118),
  VK_F8(66, 119),
  VK_F9(67, 120),
  VK_F10(68, 121),
  VK_F11(87, 122),
  VK_F12(88, 123),
  VK_F13(91, 61440),
  VK_F14(92, 61441),
  VK_F15(93, 61442),
  VK_F16(99, 61443),
  VK_F17(100, 61444),
  VK_F18(101, 61445),
  VK_F19(102, 61446),
  VK_F20(103, 61447),
  VK_F21(104, 61448),
  VK_F22(105, 61449),
  VK_F23(106, 61450),
  VK_F24(107, 61451),
  VK_BACKQUOTE(41, 192),
  VK_1(2, 49),
  VK_2(3, 50),
  VK_3(4, 51),
  VK_4(5, 52),
  VK_5(6, 53),
  VK_6(7, 54),
  VK_7(8, 55),
  VK_8(9, 56),
  VK_9(10, 57),
  VK_0(11, 48),
  VK_MINUS(12, 45),
  VK_EQUALS(13, 61),
  VK_BACKSPACE(14, 8),
  VK_TAB(15, 9),
  VK_CAPS_LOCK(58, 20),
  VK_A(30, 65),
  VK_B(48, 66),
  VK_C(46, 67),
  VK_D(32, 68),
  VK_E(18, 69),
  VK_F(33, 70),
  VK_G(34, 71),
  VK_H(35, 72),
  VK_I(23, 73),
  VK_J(36, 74),
  VK_K(37, 75),
  VK_L(38, 76),
  VK_M(50, 77),
  VK_N(49, 78),
  VK_O(24, 79),
  VK_P(25, 80),
  VK_Q(16, 81),
  VK_R(19, 82),
  VK_S(31, 83),
  VK_T(20, 84),
  VK_U(22, 85),
  VK_V(47, 86),
  VK_W(17, 87),
  VK_X(45, 88),
  VK_Y(21, 89),
  VK_Z(44, 90),
  VK_OPEN_BRACKET(26, 91),
  VK_CLOSE_BRACKET(27, 93),
  VK_BACK_SLASH(43, 92),
  VK_SEMICOLON(39, 59),
  VK_QUOTE(40, 222),
  VK_ENTER(28, 10),
  VK_COMMA(51, 44),
  VK_PERIOD(52, 46),
  VK_SLASH(53, 47),
  VK_SPACE(57, 32),
  VK_PRINTSCREEN(3639, 154),
  VK_SCROLL_LOCK(70, 145),
  VK_PAUSE(3653, 19),
  VK_INSERT(3666, 155),
  VK_DELETE(3667, 127),
  VK_HOME(3655, 36),
  VK_END(3663, 35),
  VK_PAGE_UP(3657, 33),
  VK_PAGE_DOWN(3665, 34),
  VK_UP(57416, 38),
  VK_LEFT(57419, 37),
  VK_CLEAR(57420, 12),
  VK_RIGHT(57421, 39),
  VK_DOWN(57424, 40),
  VK_NUM_LOCK(69, 144),
  VK_SEPARATOR(83, 108),
  VK_SHIFT(42, 16),
  VK_CONTROL(29, 17),
  VK_ALT(56, 18),
  VK_META(3675, 157),
  VK_CONTEXT_MENU(3677, 525),
  VK_POWER(57438, -1),
  VK_SLEEP(57439, -1),
  VK_WAKE(57443, -1),
  VK_MEDIA_PLAY(57378, -1),
  VK_MEDIA_STOP(57380, -1),
  VK_MEDIA_PREVIOUS(57360, -1),
  VK_MEDIA_NEXT(57369, -1),
  VK_MEDIA_SELECT(57453, -1),
  VK_MEDIA_EJECT(57388, -1),
  VK_VOLUME_MUTE(57376, -1),
  VK_VOLUME_UP(57392, -1),
  VK_VOLUME_DOWN(57390, -1),
  VK_APP_MAIL(57452, -1),
  VK_APP_CALCULATOR(57377, -1),
  VK_APP_MUSIC(57404, -1),
  VK_APP_PICTURES(57444, -1),
  VK_BROWSER_SEARCH(57445, -1),
  VK_BROWSER_HOME(57394, -1),
  VK_BROWSER_BACK(57450, -1),
  VK_BROWSER_FORWARD(57449, -1),
  VK_BROWSER_STOP(57448, -1),
  VK_BROWSER_REFRESH(57447, -1),
  VK_BROWSER_FAVORITES(57446, -1),
  VK_KATAKANA(112, 241),
  VK_UNDERSCORE(115, 95),
  VK_FURIGANA(119, -1),
  VK_KANJI(121, 25),
  VK_HIRAGANA(123, 242),
  VK_YEN(125, -1),
  VK_SUN_HELP(65397, -1),
  VK_SUN_STOP(65400, -1),
  VK_SUN_PROPS(65398, -1),
  VK_SUN_FRONT(65399, -1),
  VK_SUN_OPEN(65396, -1),
  VK_SUN_FIND(65406, -1),
  VK_SUN_AGAIN(65401, -1),
  VK_SUN_UNDO(65402, -1),
  VK_SUN_COPY(65404, -1),
  VK_SUN_INSERT(65405, -1),
  VK_SUN_CUT(65403, -1),
  VK_UNDEFINED(0, 0),

  VK_NUMPAD0(11, 96),
  VK_NUMPAD1(2, 97),
  VK_NUMPAD2(3, 98),
  VK_NUMPAD3(4, 99),
  VK_NUMPAD4(5, 100),
  VK_NUMPAD5(6, 101),
  VK_NUMPAD6(7, 102),
  VK_NUMPAD7(8, 103),
  VK_NUMPAD8(9, 104),
  VK_NUMPAD9(10, 105),
  VK_ASTERISK(42, 42),
  VK_ARROBA(64, 64),
  VK_EXCLAMATION_MARK(33, 33),
  VK_BEGIN(65368, 65368),
  VK_WINDOWS(524, 524);

  private final int scanCode;
  private final int keyCode;

  private KBKeys (int scanCode, int keyCode)
  {
    this.scanCode = scanCode;
    this.keyCode = keyCode;
  }

  public int code ()
  {
    return keyCode;
  }

  public int scanCode ()
  {
    return scanCode;
  }

  public static boolean contains (String s)
  {
    try {
      KBKeys.valueOf(s);
      return true;
    }
    catch (IllegalArgumentException e) {
      return false;
    }
  }
}
