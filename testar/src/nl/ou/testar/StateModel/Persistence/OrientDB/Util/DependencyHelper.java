package nl.ou.testar.StateModel.Persistence.OrientDB.Util;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.EntityClass;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

public abstract class DependencyHelper {

    /**
     * This method will accept a set of entity classes and return them sorted by dependencies,
     * so that classes that have a superclass dependency on another class will always come after
     * their super class in the array
     * @param entityClassSet a set of entity classes to sort by dependency
     * @return
     */
    public static EntityClass[] sortDependenciesForDeletion(Set<EntityClass> entityClassSet) {
        LinkedList<EntityClass> classQueue = new LinkedList<>(entityClassSet);
        EntityClass[] entityClasses = new EntityClass[entityClassSet.size()];
        int i = 0;
        while (!classQueue.isEmpty()) {
            EntityClass entityClass = classQueue.remove();
            if (entityClass.getSuperClassName() == null) {
                // no dependency on a super class, so we can just insert
                entityClasses[i++] = entityClass;
            }
            else {
                // we need to pay attention to the presence of a super class
                boolean classInserted = false;
                for (int x = 0; x < i; x++) {
                    if (entityClasses[x].getClassName().equals(entityClass.getSuperClassName())) {
                        // super class is already in the array, just add ours to the end then
                        entityClasses[i++] = entityClass;
                        classInserted = true;
                        break;
                    }
                }
                if (!classInserted) {
                    // superclass was not in the array yet, requeue
                    classQueue.addLast(entityClass);
                }
            }
        }
        return entityClasses;
    }

    /**
     * This method will accept a set of orient db classes and return them sorted by dependencies,
     * so that classes that have subclasses will come after their subclasses.
     * @param classes
     * @return
     */
    public static OClass[] sortDependenciesForDeletion(Collection<OClass> classes) {
        LinkedList<OClass> classQueue = new LinkedList<>(classes);
        OClass[] oClasses = new OClass[classes.size()];
        int i = 0;
        while (!classQueue.isEmpty()) {
            OClass oClass = classQueue.remove();

            if (oClass.getName().equals("OSequence")) {
                // special rule for this class: we want it to always be last
                if (classQueue.isEmpty()) {
                    // this was the last class, so add it to the array
                    oClasses[i++] = oClass;
                }
                else {
                    // not the last class yet..requeue
                    classQueue.addLast(oClass);
                }
                continue;
            }

            Collection<OClass> subClasses = oClass.getAllSubclasses();
            if (subClasses.size() == 0) {
                // no subclasses, we simply add it to the array
                oClasses[i++] = oClass;
            }
            else {
                // we need to make sure all the subclasses are in the array before we add this class
                // if that is not the case, we add it to the back of the list
                boolean allSubClassesInserted = true;
                outer: for (OClass subClass : subClasses) {
                    for (int x = 0; x < i; x++) {
                        if (oClasses[x].getName().equals(subClass.getName())) {
                            // subclass is present, good deal
                            continue outer;
                        }
                    }
                    // made it here means this subclass is not present yet
                    allSubClassesInserted = false;
                    break;
                }

                if (!allSubClassesInserted) {
                    classQueue.addLast(oClass);
                }
                else {
                    oClasses[i++] = oClass;
                }
            }
        }
        return oClasses;
    }

}
