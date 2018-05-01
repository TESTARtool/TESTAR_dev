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


/*
 *  @author Sebastian Bauersfeld
 */
package org.fruit.alayer.devices;

public enum KBKeys {
	// Retrieved from NativeKeyEvent in jnativehook.jar
	KEY_FIRST(2400),
	KEY_LAST(2402),
	KEY_TYPED(2400),
	KEY_PRESSED(2401),
	KEY_RELEASED(2402),
	KEY_LOCATION_UNKNOWN(0),
	KEY_LOCATION_STANDARD(1),
	KEY_LOCATION_LEFT(2),
	KEY_LOCATION_RIGHT(3),
	KEY_LOCATION_NUMPAD(4),
	VK_ESCAPE(1),
	VK_F1(59),
	VK_F2(60),
	VK_F3(61),
	VK_F4(62),
	VK_F5(63),
	VK_F6(64),
	VK_F7(65),
	VK_F8(66),
	VK_F9(67),
	VK_F10(68),
	VK_F11(87),
	VK_F12(88),
	VK_F13(91),
	VK_F14(92),
	VK_F15(93),
	VK_F16(99),
	VK_F17(100),
	VK_F18(101),
	VK_F19(102),
	VK_F20(103),
	VK_F21(104),
	VK_F22(105),
	VK_F23(106),
	VK_F24(107),
	VK_BACKQUOTE(41),
	VK_1(2),
	VK_2(3),
	VK_3(4),
	VK_4(5),
	VK_5(6),
	VK_6(7),
	VK_7(8),
	VK_8(9),
	VK_9(10),
	VK_0(11),
	VK_MINUS(12),
	VK_EQUALS(13),
	VK_BACKSPACE(14),
	VK_TAB(15),
	VK_CAPS_LOCK(58),
	VK_A(30),
	VK_B(48),
	VK_C(46),
	VK_D(32),
	VK_E(18),
	VK_F(33),
	VK_G(34),
	VK_H(35),
	VK_I(23),
	VK_J(36),
	VK_K(37),
	VK_L(38),
	VK_M(50),
	VK_N(49),
	VK_O(24),
	VK_P(25),
	VK_Q(16),
	VK_R(19),
	VK_S(31),
	VK_T(20),
	VK_U(22),
	VK_V(47),
	VK_W(17),
	VK_X(45),
	VK_Y(21),
	VK_Z(44),
	VK_OPEN_BRACKET(26),
	VK_CLOSE_BRACKET(27),
	VK_BACK_SLASH(43),
	VK_SEMICOLON(39),
	VK_QUOTE(40),
	VK_ENTER(28),
	VK_COMMA(51),
	VK_PERIOD(52),
	VK_SLASH(53),
	VK_SPACE(57),
	VK_PRINTSCREEN(3639),
	VK_SCROLL_LOCK(70),
	VK_PAUSE(3653),
	VK_INSERT(3666),
	VK_DELETE(3667),
	VK_HOME(3655),
	VK_END(3663),
	VK_PAGE_UP(3657),
	VK_PAGE_DOWN(3665),
	VK_UP(57416),
	VK_LEFT(57419),
	VK_CLEAR(57420),
	VK_RIGHT(57421),
	VK_DOWN(57424),
	VK_NUM_LOCK(69),
	VK_SEPARATOR(83),
	VK_SHIFT(42),
	VK_CONTROL(29),
	VK_ALT(56),
	VK_META(3675),
	VK_CONTEXT_MENU(3677),
	VK_POWER(57438),
	VK_SLEEP(57439),
	VK_WAKE(57443),
	VK_MEDIA_PLAY(57378),
	VK_MEDIA_STOP(57380),
	VK_MEDIA_PREVIOUS(57360),
	VK_MEDIA_NEXT(57369),
	VK_MEDIA_SELECT(57453),
	VK_MEDIA_EJECT(57388),
	VK_VOLUME_MUTE(57376),
	VK_VOLUME_UP(57392),
	VK_VOLUME_DOWN(57390),
	VK_APP_MAIL(57452),
	VK_APP_CALCULATOR(57377),
	VK_APP_MUSIC(57404),
	VK_APP_PICTURES(57444),
	VK_BROWSER_SEARCH(57445),
	VK_BROWSER_HOME(57394),
	VK_BROWSER_BACK(57450),
	VK_BROWSER_FORWARD(57449),
	VK_BROWSER_STOP(57448),
	VK_BROWSER_REFRESH(57447),
	VK_BROWSER_FAVORITES(57446),
	VK_KATAKANA(112),
	VK_UNDERSCORE(115),
	VK_FURIGANA(119),
	VK_KANJI(121),
	VK_HIRAGANA(123),
	VK_YEN(125),
	VK_SUN_HELP(65397),
	VK_SUN_STOP(65400),
	VK_SUN_PROPS(65398),
	VK_SUN_FRONT(65399),
	VK_SUN_OPEN(65396),
	VK_SUN_FIND(65406),
	VK_SUN_AGAIN(65401),
	VK_SUN_UNDO(65402),
	VK_SUN_COPY(65404),
	VK_SUN_INSERT(65405),
	VK_SUN_CUT(65403),
	VK_UNDEFINED(0),

	VK_ARROBA(64),
	VK_EXCLAMATION_MARK(33),
	VK_NUMPAD0(11),
	VK_NUMPAD1(2),
	VK_NUMPAD2(3),
	VK_NUMPAD3(4),
	VK_NUMPAD4(5),
	VK_NUMPAD5(6),
	VK_NUMPAD6(7),
	VK_NUMPAD7(8),
	VK_NUMPAD8(9),
	VK_NUMPAD9(10);

	private final int code;
	private KBKeys(int code){ this.code = code; }
	public int code(){ return code; }
	
	// by urueda
	public static boolean contains(String s) {
		try {
			KBKeys.valueOf(s);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}
	
}
