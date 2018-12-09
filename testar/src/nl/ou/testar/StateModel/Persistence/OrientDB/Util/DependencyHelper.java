package nl.ou.testar.StateModel.Persistence.OrientDB.Util;

import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.EntityClass;

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
    public static EntityClass[] sortDependencies(Set<EntityClass> entityClassSet) {
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

}
