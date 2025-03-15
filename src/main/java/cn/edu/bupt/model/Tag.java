package cn.edu.bupt.model;

/**
 * Tag
 */
public class Tag {

    private static TransactionManager TM = TransactionManager.getInstance();

    private final String name;

    public Tag(String name) {
        if(!TM.Tags.contains(name)) {
            this.name = name;
            TM.Tags.add(this);
            TM.tagRegistry.put(name, this);
        }
        else {
            this.name = "";
        }
    }

    public String getName() { return name; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag)) return false;
        return name.equals(((Tag) o).name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}

