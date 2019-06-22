package nl.ou.testar.temporal.structure;

        import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;
import org.fruit.alayer.windows.UIATags;

import java.util.HashSet;
import java.util.Set;

//@JsonRootName(value="TemporalProperties")
class SelectedAttributes {

    public Set<Tag<?>> attributes;

    public SelectedAttributes() {
    }

    public Set<Tag<?>> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<Tag<?>> attributes) {
        this.attributes = attributes;
    }



    public void useDefaultOnlyBoolean() {
        for (Tag tag : Tags.tagSet()
        ) {
            if (tag.type() == Boolean.class) {
                attributes.add(tag);
            }

        }
        for (Tag tag : UIATags.tagSet()
        ) {
            if (tag.type() == Boolean.class) {
                attributes.add(tag);
            }

        }
    }
    public void useDefaultAll() {
        for (Tag tag : Tags.tagSet()
        ) {
            attributes.add(tag);
        }
        for (Tag tag : UIATags.tagSet()
        ) {
            attributes.add(tag);
        }
    }

    public void addAttribute(String attrib){

        Set<Tag<?>> tagset =Tags.tagSet();
        tagset.addAll(UIATags.tagSet());
        for (Tag tag : tagset
        ) {
            if ( tag.name() == attrib) {
                attributes.add(tag);
                break;
            }
        }
    }

    public void removeAttribute(String attrib){
        Set<Tag<?>> tagset =Tags.tagSet();
        tagset.addAll(UIATags.tagSet());
        for (Tag tag : tagset
        ) {
            if ( tag.name() == attrib) {
                attributes.remove(tag);
                break;
            }
        }
    }
    private <T> Tag<T>  getTag(String attrib){
        Tag<T> ret=null;
        for (Tag tag : attributes
        ) {
            if ( tag.name() == attrib) {
                ret=tag;
                break;
            }
        }
        return  ret;
    }

    public boolean contains(String attrib){

        if(getTag(attrib)!=null){
            return true;
        }
        return false;
    }

public Set<String> getAPs(String key, String attrib, String value) {
    Set<String> apset = new HashSet<>();
    Tag tag = null;
    tag = getTag(attrib);
    if (tag != null) {
        if (tag.type() == Boolean.class) {
            apset.add(key + "_" + attrib + "__" + (value == "true" ? "1" : "0")); // alternative is to just encode the TRUE  and FALSE is then considered absence
        } else
            if (tag.type() == Double.class ||tag.type() == Long.class||tag.type() == Integer.class) {


        }

    }
    return apset;
}

}