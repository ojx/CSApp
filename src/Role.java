public class Role {
    private static int ALL_ACCESS_MIN_INDEX = 6;
    private int id;
    private String name;

    public Role(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean hasAllCourseAccess() {
        return id >= ALL_ACCESS_MIN_INDEX;
    }
}
