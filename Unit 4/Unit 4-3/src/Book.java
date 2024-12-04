public class Book {
    private final String source;
    private final String bookName;
    private final boolean permission;

    public Book(String source, String bookName, boolean permission) {
        this.source = source;
        this.bookName = bookName;
        this.permission = permission;
    }

    public boolean allowBorrow() {
        return this.permission;
    }

    public String getName() {
        return this.bookName;
    }

    public String getSource() {
        return this.source;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        //result = prime * result + ((source == null) ? 0 : source.hashCode());
        result = prime * result + ((bookName == null) ? 0 : bookName.hashCode());
        result = prime * result + (permission ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Book other = (Book) obj;
        return bookName.equals(other.bookName)
                &&  permission == other.permission;
    }
}
