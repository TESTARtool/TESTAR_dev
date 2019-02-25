package org.fruit.alayer.webdriver.enums;

import org.fruit.Util;
import org.fruit.alayer.Role;
import org.fruit.alayer.Roles;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class WdRoles {
  private WdRoles() {
  }

  private final static Map<String, Role> tagToRole = Util.newHashMap();

  public static final Role
    WdWidget = from("-1", "WdWidget", Roles.Widget),
    WdA = from("a", "WdA", WdWidget, Roles.Control),
    WdABBR = from("abbr", "WdABBR", WdWidget, Roles.Control),
    WdACRONYM = from("acronym", "WdACRONYM", WdWidget, Roles.Control),
    WdADDRESS = from("address", "WdADDRESS", WdWidget, Roles.Control),
    WdAPPLET = from("applet", "WdAPPLET", WdWidget, Roles.Control),
    WdAREA = from("area", "WdAREA", WdWidget, Roles.Control),
    WdARTICLE = from("article", "WdARTICLE", WdWidget, Roles.Control),
    WdASIDE = from("aside", "WdASIDE", WdWidget, Roles.Control),
    WdAUDIO = from("audio", "WdAUDIO", WdWidget, Roles.Control),
    WdB = from("b", "WdB", WdWidget, Roles.Control),
    WdBASE = from("base", "WdBASE", WdWidget, Roles.Control),
    WdBASEFONT = from("basefont", "WdBASEFONT", WdWidget, Roles.Control),
    WdBDI = from("bdi", "WdBDI", WdWidget, Roles.Control),
    WdBDO = from("bdo", "WdBDO", WdWidget, Roles.Control),
    WdBIG = from("big", "WdBIG", WdWidget, Roles.Control),
    WdBLOCKQUOTE = from("blockquote", "WdBLOCKQUOTE", WdWidget, Roles.Control),
    WdBODY = from("body", "WdBODY", WdWidget, Roles.Control),
    WdBR = from("br", "WdBR", WdWidget, Roles.Control),
    WdBUTTON = from("button", "WdBUTTON", WdWidget, Roles.Control),
    WdCANVAS = from("canvas", "WdCANVAS", WdWidget, Roles.Control),
    WdCAPTION = from("caption", "WdCAPTION", WdWidget, Roles.Control),
    WdCENTER = from("center", "WdCENTER", WdWidget, Roles.Control),
    WdCITE = from("cite", "WdCITE", WdWidget, Roles.Control),
    WdCODE = from("code", "WdCODE", WdWidget, Roles.Control),
    WdCOL = from("col", "WdCOL", WdWidget, Roles.Control),
    WdCOLGROUP = from("colgroup", "WdCOLGROUP", WdWidget, Roles.Control),
    WdDATALIST = from("datalist", "WdDATALIST", WdWidget, Roles.Control),
    WdDD = from("dd", "WdDD", WdWidget, Roles.Control),
    WdDEL = from("del", "WdDEL", WdWidget, Roles.Control),
    WdDETAILS = from("details", "WdDETAILS", WdWidget, Roles.Control),
    WdDFN = from("dfn", "WdDFN", WdWidget, Roles.Control),
    WdDIR = from("dir", "WdDIR", WdWidget, Roles.Control),
    WdDIV = from("div", "WdDIV", WdWidget, Roles.Control),
    WdDL = from("dl", "WdDL", WdWidget, Roles.Control),
    WdDT = from("dt", "WdDT", WdWidget, Roles.Control),
    WdEM = from("em", "WdEM", WdWidget, Roles.Control),
    WdEMBED = from("embed", "WdEMBED", WdWidget, Roles.Control),
    WdFIELDSET = from("fieldset", "WdFIELDSET", WdWidget, Roles.Control),
    WdFIGCAPTION = from("figcaption", "WdFIGCAPTION", WdWidget, Roles.Control),
    WdFIGURE = from("figure", "WdFIGURE", WdWidget, Roles.Control),
    WdFONT = from("font", "WdFONT", WdWidget, Roles.Control),
    WdFOOTER = from("footer", "WdFOOTER", WdWidget, Roles.Control),
    WdFORM = from("form", "WdFORM", WdWidget, Roles.Control),
    WdFRAME = from("frame", "WdFRAME", WdWidget, Roles.Control),
    WdFRAMESET = from("frameset", "WdFRAMESET", WdWidget, Roles.Control),
    WdH1 = from("h1", "WdH1", WdWidget, Roles.Control),
    WdH2 = from("h2", "WdH2", WdWidget, Roles.Control),
    WdH3 = from("h3", "WdH3", WdWidget, Roles.Control),
    WdH4 = from("h4", "WdH4", WdWidget, Roles.Control),
    WdH5 = from("h5", "WdH5", WdWidget, Roles.Control),
    WdH6 = from("h6", "WdH6", WdWidget, Roles.Control),
    WdHEAD = from("head", "WdHEAD", WdWidget, Roles.Control),
    WdHEADER = from("header", "WdHEADER", WdWidget, Roles.Control),
    WdHGROUP = from("hgroup", "WdHGROUP", WdWidget, Roles.Control),
    WdHR = from("hr", "WdHR", WdWidget, Roles.Control),
    WdHTML = from("html", "WdHTML", WdWidget, Roles.Control),
    WdI = from("i", "WdI", WdWidget, Roles.Control),
    WdIFRAME = from("iframe", "WdIFRAME", WdWidget, Roles.Control),
    WdIMG = from("img", "WdIMG", WdWidget, Roles.Control),
    WdINPUT = from("input", "WdINPUT", WdWidget, Roles.Control),
    WdINS = from("ins", "WdINS", WdWidget, Roles.Control),
    WdKBD = from("kbd", "WdKBD", WdWidget, Roles.Control),
    WdKEYGEN = from("keygen", "WdKEYGEN", WdWidget, Roles.Control),
    WdLABEL = from("label", "WdLABEL", WdWidget, Roles.Control),
    WdLEGEND = from("legend", "WdLEGEND", WdWidget, Roles.Control),
    WdLI = from("li", "WdLI", WdWidget, Roles.Control),
    WdLINK = from("link", "WdLINK", WdWidget, Roles.Control),
    WdMAP = from("map", "WdMAP", WdWidget, Roles.Control),
    WdMARK = from("mark", "WdMARK", WdWidget, Roles.Control),
    WdMENU = from("menu", "WdMENU", WdWidget, Roles.Control),
    WdMETA = from("meta", "WdMETA", WdWidget, Roles.Control),
    WdMETER = from("meter", "WdMETER", WdWidget, Roles.Control),
    WdNAV = from("nav", "WdNAV", WdWidget, Roles.Control),
    WdNOFRAMES = from("noframes", "WdNOFRAMES", WdWidget, Roles.Control),
    WdNOSCRIPT = from("noscript", "WdNOSCRIPT", WdWidget, Roles.Control),
    WdOBJECT = from("object", "WdOBJECT", WdWidget, Roles.Control),
    WdOL = from("ol", "WdOL", WdWidget, Roles.Control),
    WdOPTGROUP = from("optgroup", "WdOPTGROUP", WdWidget, Roles.Control),
    WdOPTION = from("option", "WdOPTION", WdWidget, Roles.Control),
    WdOUTPUT = from("output", "WdOUTPUT", WdWidget, Roles.Control),
    WdP = from("p", "WdP", WdWidget, Roles.Control),
    WdPARAM = from("param", "WdPARAM", WdWidget, Roles.Control),
    WdPRE = from("pre", "WdPRE", WdWidget, Roles.Control),
    WdPROGRESS = from("progress", "WdPROGRESS", WdWidget, Roles.Control),
    WdQ = from("q", "WdQ", WdWidget, Roles.Control),
    WdRP = from("rp", "WdRP", WdWidget, Roles.Control),
    WdRT = from("rt", "WdRT", WdWidget, Roles.Control),
    WdRUBY = from("ruby", "WdRUBY", WdWidget, Roles.Control),
    WdS = from("s", "WdS", WdWidget, Roles.Control),
    WdSAMP = from("samp", "WdSAMP", WdWidget, Roles.Control),
    WdSCRIPT = from("script", "WdSCRIPT", WdWidget, Roles.Control),
    WdSECTION = from("section", "WdSECTION", WdWidget, Roles.Control),
    WdSELECT = from("select", "WdSELECT", WdWidget, Roles.Control),
    WdSMALL = from("small", "WdSMALL", WdWidget, Roles.Control),
    WdSOURCE = from("source", "WdSOURCE", WdWidget, Roles.Control),
    WdSPAN = from("span", "WdSPAN", WdWidget, Roles.Control),
    WdSTRIKE = from("strike", "WdSTRIKE", WdWidget, Roles.Control),
    WdSTRONG = from("strong", "WdSTRONG", WdWidget, Roles.Control),
    WdSTYLE = from("style", "WdSTYLE", WdWidget, Roles.Control),
    WdSUB = from("sub", "WdSUB", WdWidget, Roles.Control),
    WdSUMMARY = from("summary", "WdSUMMARY", WdWidget, Roles.Control),
    WdSUP = from("sup", "WdSUP", WdWidget, Roles.Control),
    WdTABLE = from("table", "WdTABLE", WdWidget, Roles.Control),
    WdTBODY = from("tbody", "WdTBODY", WdWidget, Roles.Control),
    WdTD = from("td", "WdTD", WdWidget, Roles.Control),
    WdTEXTAREA = from("textarea", "WdTEXTAREA", WdWidget, Roles.Control),
    WdTFOOT = from("tfoot", "WdTFOOT", WdWidget, Roles.Control),
    WdTH = from("th", "WdTH", WdWidget, Roles.Control),
    WdTHEAD = from("thead", "WdTHEAD", WdWidget, Roles.Control),
    WdTIME = from("time", "WdTIME", WdWidget, Roles.Control),
    WdTITLE = from("title", "WdTITLE", WdWidget, Roles.Control),
    WdTR = from("tr", "WdTR", WdWidget, Roles.Control),
    WdTRACK = from("track", "WdTRACK", WdWidget, Roles.Control),
    WdTT = from("tt", "WdTT", WdWidget, Roles.Control),
    WdU = from("u", "WdU", WdWidget, Roles.Control),
    WdUL = from("ul", "WdUL", WdWidget, Roles.Control),
    WdVAR = from("var", "WdVAR", WdWidget, Roles.Control),
    WdVIDEO = from("video", "WdVIDEO", WdWidget, Roles.Control),
    WdWBR = from("wbr", "WdWBR", WdWidget, Roles.Control),
    WdUnknown = from("-2", "WdUnknown", WdWidget, Roles.Control);

  private static Role from(String tag, String name, Role... inheritFrom) {
    Role ret = Role.from(name, inheritFrom);
    tagToRole.put(tag, ret);
    return ret;
  }

  public static Role fromTypeId(String tag) {
    Role ret = tagToRole.get(tag);
    return (ret == null) ? WdUnknown : ret;
  }

  public static Collection<Role> rolesSet() {
    return tagToRole.values();
  }

  // TODO Check if correct
  public static Role[] nativeClickableRoles() {
    return new Role[]{
        WdA, WdBUTTON, WdINPUT, WdLINK, WdMENU, WdOPTION, WdSELECT, WdDATALIST
    };
  }

  // TODO Or make separate roles, e.g. WdINPUT_RADIO?
  // TODO date- and number-fields needs work, only partially clickable
  // TODO Idem for WdDATALIST
  public static List<String> clickableInputTypes() {
    return Arrays.asList("submit", "reset", "button", "radio", "checkbox",
        "color", "date", "datetime-local", "month", "number");
  }

  // TODO Check if correct
  public static Role[] nativeTypeableRoles() {
    return new Role[]{WdINPUT, WdTEXTAREA, WdDATALIST};
  }

  public static List<String> typeableInputTypes() {
    return Arrays.asList("text", "password", "email",
        "date", "datetime-local", "month", "number");
  }
}
