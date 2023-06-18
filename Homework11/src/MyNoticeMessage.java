import com.oocourse.spec3.main.Group;
import com.oocourse.spec3.main.NoticeMessage;
import com.oocourse.spec3.main.Person;

public class MyNoticeMessage implements NoticeMessage {
    private final String string;
    private final int id;
    private final int socialValue;
    private final int type;
    private final Person person1;
    private final Person person2;
    private final Group group;

    public MyNoticeMessage(int messageId, String noticeString,
                           Person messagePerson1, Person messagePerson2) {
        this.string = noticeString;
        this.id = messageId;
        this.socialValue = string.length();
        this.type = 0;
        this.person1 = messagePerson1;
        this.person2 = messagePerson2;
        this.group = null;

    }

    public MyNoticeMessage(int messageId, String noticeString,
                           Person messagePerson1, Group messageGroup) {
        this.string = noticeString;
        this.id =  messageId;
        this.socialValue = string.length();
        this.type = 1;
        this.person1 = messagePerson1;
        this.person2 = null;
        this.group = messageGroup;
    }

    @Override
    public String getString() {
        return this.string;
    }

    @Override
    public int getType() {
        return this.type;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public int getSocialValue() {
        return this.socialValue;
    }

    @Override
    public Person getPerson1() {
        return this.person1;
    }

    @Override
    public Person getPerson2() {
        return this.person2;
    }

    @Override
    public Group getGroup() {
        return this.group;
    }
}