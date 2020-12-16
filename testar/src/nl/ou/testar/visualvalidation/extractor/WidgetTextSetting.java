package nl.ou.testar.visualvalidation.extractor;

import org.testar.settings.ExtendedSettingBase;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

import static java.util.Collections.emptyList;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
class WidgetTextSetting extends ExtendedSettingBase<WidgetTextSetting> {
    public static final boolean IGNORE = true;
    public static final String ANCESTOR_SEPARATOR = "::";

    String role;
    String tag;
    Boolean ignore;
    String ancestor;

    private static WidgetTextSetting CreateRaw(String role, Boolean ignore, String tag, List<String> ancestor) {
        WidgetTextSetting result = new WidgetTextSetting();
        result.role = role;
        result.tag = tag;
        result.ignore = ignore;
        StringBuilder sb = new StringBuilder();
        ancestor.forEach(it -> sb.append(ANCESTOR_SEPARATOR).append(it));
        result.ancestor = sb.toString();
        return result;
    }

    public static WidgetTextSetting CreateIgnoreAncestorBased(String role, List<String> ancestor) {
        return CreateRaw(role, IGNORE, "", ancestor);
    }

    public static WidgetTextSetting CreateIgnore(String role) {
        return CreateRaw(role, IGNORE, "", emptyList());
    }

    public static WidgetTextSetting CreateExtract(String role, String tag) {
        return CreateRaw(role, false, tag, emptyList());
    }

    @Override
    public int compareTo(WidgetTextSetting other) {
        int result = -1;
        if (role.contentEquals(other.role)
                && (tag.contentEquals(other.tag))
                && (ignore.equals(other.ignore)
                && (ancestor.equals(other.ancestor)))
        ) {
            result = 0;
        }
        return result;
    }
}
