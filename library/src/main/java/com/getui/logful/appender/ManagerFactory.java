package com.getui.logful.appender;

public interface ManagerFactory<M, T> {
    /**
     * Creates a Manager.
     * 
     * @param filePath The filePath of the entity to manage.
     * @param data The data required to create the entity.
     * @return A Manager for the entity.
     */
    M createManager(String filePath, T data);
}
