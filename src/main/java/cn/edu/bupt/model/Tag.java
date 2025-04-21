package cn.edu.bupt.model;

/**
 * Tag
 */
public class Tag {

    private static TransactionManager TM = TransactionManager.getInstance();

    private final String name;

    public Tag(String name) {
        if(TM.Tags.contains(name)) {
            throw new IllegalArgumentException("Tag名称重复: " + name);
        }
        this.name = name;
        TM.Tags.add(this);
        TM.tagRegistry.put(name, this);
    }

    public String getName() { return name; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof Tag) return name.equals(((Tag) o).name);
        if (o instanceof String) return name.equals(((String)o));
        return false;
    }
}

