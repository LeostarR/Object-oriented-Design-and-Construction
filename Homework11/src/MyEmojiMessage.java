import com.oocourse.spec3.main.EmojiMessage;
import com.oocourse.spec3.main.Group;
import com.oocourse.spec3.main.Person;

public class MyEmojiMessage implements EmojiMessage {
    private final int emojiId;
    private final int id;
    private final int socialValue;
    private final int type;
    private final Person person1;
    private final Person person2;
    private final Group group;

    public MyEmojiMessage(int messageId, int emojiNumber,
                          Person messagePerson1, Person messagePerson2) {
        this.emojiId = emojiNumber;
        this.id = messageId;
        this.socialValue = emojiId;
        this.type = 0;
        this.person1 = messagePerson1;
        this.person2 = messagePerson2;
        this.group = null;
    }

    public MyEmojiMessage(int messageId, int emojiNumber,
                          Person messagePerson1, Group messageGroup) {
        this.emojiId = emojiNumber;
        this.id = messageId;
        this.socialValue = emojiId;
        this.type = 1;
        this.person1 = messagePerson1;
        this.person2 = null;
        this.group = messageGroup;
    }

    @Override
    public int getEmojiId() {
        return this.emojiId;
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